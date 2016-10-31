package com.thermatk.android.meatb.fragments;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.thermatk.android.meatb.R;
import com.thermatk.android.meatb.adapters.AgendaAdapter;
import com.thermatk.android.meatb.data.AgendaEvent;
import com.thermatk.android.meatb.data.EventDay;
import com.thermatk.android.meatb.services.AgendaUpdateService;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import eu.davidea.fastscroller.FastScroller;
import eu.davidea.flexibleadapter.common.SmoothScrollLinearLayoutManager;
import eu.davidea.flexibleadapter.items.IFlexible;
import eu.davidea.flexibleadapter.items.IHeader;
import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;


public class AgendaFragment extends Fragment implements FastScroller.OnScrollStateChangeListener {

    private boolean doingAsync = false;
    protected Realm realm;
    private View mProgressView;

    private RecyclerView mRecyclerView;
    private FastScroller mFastScroller;
    private View mEmptyView;
    private AgendaAdapter mAdapter;


    public AgendaFragment() {
        // Required empty public constructor
    }


    @Override
    public void onSaveInstanceState (Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("doingAsync", doingAsync);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().setTitle("Agenda");
        if(savedInstanceState != null) {
            if(savedInstanceState.getBoolean("doingAsync", false)) {
                /* REWRITE
                RetainFragment.setFragment(this);
                                *  */
                doingAsync = true;
            }
        }
    }

    public List<IFlexible> getDatabaseList() {
        int size = 400;
        int headers =100;
        List<IFlexible> mItems = new ArrayList<>();
        EventDay header = null;
        mItems.clear();
        int lastHeaderId = 0;
        for (int i = 0; i < size; i++) {
            header = i % Math.round(size / headers) == 0 ? newHeader(++lastHeaderId) : header;
            mItems.add(newSimpleItem(i + 1, header));
        }
        //Return a copy of the DB: we will perform some tricky code on this list.
        return mItems;
    }

    public List<IFlexible> getDatabaseList2() {
        List<IFlexible> mItems;

        Calendar c = new GregorianCalendar();
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        Date today = c.getTime();

        RealmResults<AgendaEvent> eventList = realm.where(AgendaEvent.class).greaterThan("date_start",today).findAllSorted("date_start", Sort.DESCENDING);
        mItems = new ArrayList(eventList);
        Log.d("TESTM",mItems.size() + " size " + ((AgendaEvent) mItems.get(2)).getDuration());


        return getDatabaseList();
    }

    public static EventDay newHeader(int i) {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, (i-1));
        DateFormat dateFormatY = new SimpleDateFormat("dd.MM.yyyy");
        DateFormat dateFormatW = new SimpleDateFormat("EEEE");
        String dateY = dateFormatY.format(cal.getTime());
        String dateW = dateFormatW.format(cal.getTime());

        EventDay header = new EventDay(i);
        header.setDateString(dateY);
        header.setWeekdayString(dateW);
        //header is hidden and un-selectable by default!
        return header;
    }

    /*
     * Creates a normal item with a Header linked.
     */
    public static AgendaEvent newSimpleItem(int i, IHeader header) {
        AgendaEvent item = new AgendaEvent(i, (EventDay) header);
        item.setTitle("Simplest Item " + i);
        item.setDuration("8:44-"+i);
        item.setSupertitle("Room 555");
        return item;
    }
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static int getColorAccent(Context context) {

        int colorAccent = -1;
        if (colorAccent < 0) {
            int accentAttr = eu.davidea.flexibleadapter.utils.Utils.hasLollipop() ? android.R.attr.colorAccent : R.attr.colorAccent;
            TypedArray androidAttr = context.getTheme().obtainStyledAttributes(new int[] { accentAttr });
            colorAccent = androidAttr.getColor(0, 0xFF009688); //Default: material_deep_teal_500
            androidAttr.recycle();
        }
        return colorAccent;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_agenda, container, false);
        realm = Realm.getDefaultInstance();


        mProgressView = rootView.findViewById(R.id.agenda_progress);

        mFastScroller = (FastScroller) rootView.findViewById(R.id.fast_scroller);
        mEmptyView = rootView.findViewById(R.id.empty_view);
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);
        mAdapter = new AgendaAdapter(getDatabaseList2(), getActivity());

        mAdapter.setRemoveOrphanHeaders(false)
                .setNotifyChangeOfUnfilteredItems(true)//We have highlighted text while filtering, so let's enable this feature to be consistent with the active filter
                .setAnimationOnScrolling(true);
        mRecyclerView.setLayoutManager(new SmoothScrollLinearLayoutManager(getActivity()));
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setHasFixedSize(true); //Size of RV will not change
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        //Add FastScroll to the RecyclerView, after the Adapter has been attached the RecyclerView!!!
        mAdapter.setFastScroller((FastScroller) rootView.findViewById(R.id.fast_scroller),
                getColorAccent(getActivity()), this);

        mAdapter.setLongPressDragEnabled(false)
                .setHandleDragEnabled(false)
                .setSwipeEnabled(false)
                .setUnlinkAllItemsOnRemoveHeaders(true)
                //Show Headers at startUp, 1st call, correctly executed, no warning log message!
                .setDisplayHeadersAtStartUp(true)
                .enableStickyHeaders();
        mAdapter.showLayoutInfo(savedInstanceState == null);

        // if size>0
        mEmptyView.setAlpha(0);
        mFastScroller.setVisibility(View.VISIBLE);
        // else
        /*
            mEmptyView.setAlpha(0);
            //mRefreshHandler.sendEmptyMessage(2);
            mFastScroller.setVisibility(View.GONE);
         */

        mRecyclerView.post(new Runnable() {
            @Override
            public void run() {
                mRecyclerView.smoothScrollToPosition(22);
            }
        });


        if(doingAsync) {
            showProgress(true);
        } else {
            /* REWRITE
            rCalCandidate = realm.where(RCal.class).findAll();
            if (rCalCandidate.size() == 0 ||rCalCandidate.first().getrEvents().size() == 0) {
                // show progress
                // start service
                Intent intent = new Intent(getActivity().getApplicationContext(),
                        AgendaUpdateService.class);
                getActivity().startService(intent);
                // show some no events event? or progress fragment?

                doingAsync = true;
                showProgress(true);

                RetainFragment retainFragment =
                        RetainFragment.findOrCreateRetainFragment(getFragmentManager(), rCalCandidate);
                RetainFragment.setFragment(this);
                retainFragment.waitAsync();
            } else {
                // Usual way
                getDataFromRCal();
                populateView();
            }
            */
        }
        return rootView;
    }

    public void populateView() {
        /* REWRITE
        mAgendaCalendarView.init(Locale.ENGLISH, mLoadWeeks, mLoadDays, mLoadEvents, this); // TODO: LOCALE.getDefault()
        mAgendaCalendarView.addEventRenderer(new BocconiEventRenderer());
        */
    }

    @Override
    public void onDetach() {
        super.onDetach();
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
            //mAgendaCalendarView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    public void getDataFromRCal() {
        /* REWRITE
        RCal candidate = rCalCandidate.first();

        mLoadDays.addAll(candidate.getrDays());
        mLoadWeeks.addAll(candidate.getrWeeks());
        mLoadEvents.addAll(candidate.getrEvents());
        */
    }

    @Override
    public void onFastScrollerStateChange(boolean scrolling) {

    }

    /* REWRITE
    public void serviceIsDone(RealmResults<RCal> resultRCal) {
        rCalCandidate = resultRCal;
        doingAsync = false;
        getDataFromRCal();
        populateView();
        showProgress(false);
    }

    public static class RetainFragment extends Fragment {
        private static final String TAG = "RetainFragmentAgenda";
        private static AgendaFragment mFragment;
        private static RealmResults<> rCalCandidateRetain;


        public RetainFragment() {
        }
        public static void setFragment(AgendaFragment current) {
            mFragment = current;
        }
        public static RetainFragment findOrCreateRetainFragment(FragmentManager fm, RealmResults<> rCal) {
            RetainFragment fragment = (RetainFragment) fm.findFragmentByTag(TAG);
            if (fragment == null) {
                fragment = new RetainFragment();
                fm.beginTransaction().add(fragment, TAG).commit();
                rCalCandidateRetain = rCal;
            }
            return fragment;
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setRetainInstance(true);
        }

        private void waitAsync() {
            rCalCandidateRetain.addChangeListener(new RealmChangeListener<RealmResults<RCal>>() {
                @Override
                public void onChange(RealmResults<RCal> results) {

                    if (results.size() == 0 ||results.first().getrEvents().size() == 0) {
                        // wait more
                    } else {
                        rCalCandidateRetain.removeChangeListeners();
                        mFragment.serviceIsDone(rCalCandidateRetain);
                    }
                }
            });
        }
    }
    */

}
