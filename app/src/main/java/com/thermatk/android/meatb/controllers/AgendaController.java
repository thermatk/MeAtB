package com.thermatk.android.meatb.controllers;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bluelinelabs.conductor.Controller;
import com.thermatk.android.meatb.LogConst;
import com.thermatk.android.meatb.R;
import com.thermatk.android.meatb.adapters.AgendaAdapter;
import com.thermatk.android.meatb.data.AgendaEvent;
import com.thermatk.android.meatb.data.ServiceLock;
import com.thermatk.android.meatb.helpers.ServiceHelper;
import com.thermatk.android.meatb.services.AgendaUpdateService;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import eu.davidea.fastscroller.FastScroller;
import eu.davidea.flexibleadapter.common.SmoothScrollLinearLayoutManager;
import eu.davidea.flexibleadapter.items.IFlexible;
import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;
import io.realm.Sort;


public class AgendaController extends Controller implements FastScroller.OnScrollStateChangeListener {

    private boolean doingAsync = false;
    protected Realm realm;
    private View mProgressView;

    private RecyclerView mRecyclerView;
    private FastScroller mFastScroller;
    private View mEmptyView;
    private AgendaAdapter mAdapter;
    private RealmResults<AgendaEvent> eventList;

    @Override
    public void onSaveInstanceState (Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("doingAsync", doingAsync);
    }

    /*@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(savedInstanceState != null) {
            if(savedInstanceState.getBoolean("doingAsync", false)) {
                RetainFragment.setFragment(this);
                doingAsync = true;
            }
        }
    }*/
    public static Date getToday() {

        Calendar c = new GregorianCalendar();
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        return c.getTime();
    }
    public List<IFlexible> formDatabaseList() {
        List<IFlexible> mItems = new ArrayList<>();
        List<AgendaEvent> mItemsRAW;

        eventList = realm.where(AgendaEvent.class).greaterThan("date_start",getToday()).findAllSorted("date_start", Sort.ASCENDING);
        // TODO: BETTER WORKAROUND
        mItemsRAW = realm.copyFromRealm(eventList);
        for (int i=0; i<mItemsRAW.size(); i++)
        {
            mItems.add(mItemsRAW.get(i));
        }


        return mItems;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static int getColorAccent(Context context) {

        int colorAccent = -1;
            int accentAttr = eu.davidea.flexibleadapter.utils.Utils.hasLollipop() ? android.R.attr.colorAccent : R.attr.colorAccent;
            TypedArray androidAttr = context.getTheme().obtainStyledAttributes(new int[] { accentAttr });
            colorAccent = androidAttr.getColor(0, 0xFF009688); //Default: material_deep_teal_500
            androidAttr.recycle();
        return colorAccent;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container) {
        getActivity().setTitle("Agenda");


        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.controller_agenda, container, false);
        realm = Realm.getDefaultInstance();


        mProgressView = rootView.findViewById(R.id.agenda_progress);

        mFastScroller = (FastScroller) rootView.findViewById(R.id.fast_scroller);
        mEmptyView = rootView.findViewById(R.id.empty_view);
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);



        if(doingAsync) {
            showProgress(true);
        } else {
            eventList = realm.where(AgendaEvent.class).greaterThan("date_start",getToday()).findAllSorted("date_start", Sort.ASCENDING);

            if (eventList.size() == 0) { // TODO: corner case - no events actually in schedule
                // show progress
                // start service
                Intent intent = new Intent(getActivity().getApplicationContext(),
                        AgendaUpdateService.class);
                getActivity().startService(intent);
                // show some no events event? or progress fragment?

                doingAsync = true;
                showProgress(true);


                ///
                RealmResults<ServiceLock> sl = realm.where(ServiceLock.class).equalTo("lockId", ServiceHelper.LOCK_AGENDA_UPDATE_SERVICE).findAll();
                ///

                RetainFragment retainFragment =
                        RetainFragment.findOrCreateRetainFragment(getActivity().getFragmentManager(), eventList, sl);
                RetainFragment.setFragment(this);
                retainFragment.waitAsync();
            } else {
                // Usual way
                populateView();
            }
        }
        return rootView;
    }

    public void populateView() {
        mAdapter = new AgendaAdapter(formDatabaseList(), getActivity());
        mAdapter.setRemoveOrphanHeaders(false)
                .setNotifyChangeOfUnfilteredItems(true)
                .setAnimationOnScrolling(true);
        mRecyclerView.setLayoutManager(new SmoothScrollLinearLayoutManager(getActivity()));
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mAdapter.setFastScroller(mFastScroller,
                getColorAccent(getActivity()), this);
        mAdapter.setLongPressDragEnabled(false)
                .setHandleDragEnabled(false)
                .setSwipeEnabled(false)
                .setUnlinkAllItemsOnRemoveHeaders(true)
                .setDisplayHeadersAtStartUp(true)
                .setStickyHeaders(true);;
        mAdapter.showLayoutInfo(true);
        mEmptyView.setAlpha(0);
        mFastScroller.setVisibility(View.VISIBLE);

        /*mRecyclerView.post(new Runnable() {
            @Override
            public void run() {
                mRecyclerView.smoothScrollToPosition(22);
            }
        });*/
    }

    @Override
    protected void onDestroyView(@NonNull View view) {
        super.onDestroyView(view);
        // TODO: fix detach?
        /* REWRITE
        if(rCalCandidate!=null) {
            rCalCandidate.removeChangeListeners();
        }*/
        realm.close();
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);
            mFastScroller.setVisibility(show ? View.GONE : View.VISIBLE);
            mEmptyView.setVisibility(show ? View.GONE : View.VISIBLE);
            mRecyclerView.setVisibility(show ? View.GONE : View.VISIBLE);
            /*
            mAgendaCalendarView.setVisibility(show ? View.GONE : View.VISIBLE);
            mAgendaCalendarView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mAgendaCalendarView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });
            */
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mFastScroller.setVisibility(show ? View.GONE : View.VISIBLE);
            mEmptyView.setVisibility(show ? View.GONE : View.VISIBLE);
            mRecyclerView.setVisibility(show ? View.GONE : View.VISIBLE);
            //mAgendaCalendarView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    @Override
    public void onFastScrollerStateChange(boolean scrolling) {

    }

    public void serviceIsDone(RealmResults<AgendaEvent> resultEvents) {
        eventList = resultEvents;
        doingAsync = false;
        populateView();
        showProgress(false);
    }

    public static class RetainFragment extends Fragment {
        private static final String TAG = "RetainFragmentAgenda";
        private static AgendaController mFragment;
        private static RealmResults<AgendaEvent> rAgendaEvents;
        private static RealmResults<ServiceLock> rServiceLock;


        public RetainFragment() {
        }
        public static void setFragment(AgendaController current) {
            mFragment = current;
        }
        public static RetainFragment findOrCreateRetainFragment(FragmentManager fm, RealmResults<AgendaEvent> agendaEvents, RealmResults<ServiceLock> sl) {
            RetainFragment fragment = (RetainFragment) fm.findFragmentByTag(TAG);
            if (fragment == null) {
                fragment = new RetainFragment();
                fm.beginTransaction().add(fragment, TAG).commit();
                rAgendaEvents = agendaEvents;
                rServiceLock = sl;
            }
            return fragment;
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setRetainInstance(true);
        }

        private void waitAsync() {
            rServiceLock.addChangeListener(new RealmChangeListener<RealmResults<ServiceLock>>() {
                @Override
                public void onChange(RealmResults<ServiceLock> results) {
                    if (rAgendaEvents.size() == 0) {
                        Log.d(LogConst.LOG, "SERVICE AGENDA DONE, BUT WOW NOTHING");
                        // wait more?
                    } else {
                        Log.d(LogConst.LOG, "SERVICE AGENDA DONE, GOGOGO");
                        rServiceLock.removeChangeListeners();
                        mFragment.serviceIsDone(rAgendaEvents);
                    }
                }
            });
        }
    }

}
