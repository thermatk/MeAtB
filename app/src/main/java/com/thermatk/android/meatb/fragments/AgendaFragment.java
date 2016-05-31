package com.thermatk.android.meatb.fragments;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.tibolte.agendacalendarview.AgendaCalendarView;
import com.github.tibolte.agendacalendarview.CalendarPickerController;
import com.github.tibolte.agendacalendarview.models.CalendarEvent;
import com.github.tibolte.agendacalendarview.models.IDayItem;
import com.github.tibolte.agendacalendarview.models.IWeekItem;
import com.thermatk.android.meatb.LogConst;
import com.thermatk.android.meatb.R;
import com.thermatk.android.meatb.agenda.BocconiEventRenderer;
import com.thermatk.android.meatb.data.agenda.RCal;
import com.thermatk.android.meatb.data.agenda.RDay;
import com.thermatk.android.meatb.data.agenda.REvent;
import com.thermatk.android.meatb.data.agenda.RWeek;
import com.thermatk.android.meatb.services.AgendaUpdateService;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;


public class AgendaFragment extends Fragment implements CalendarPickerController {

    List<CalendarEvent> mLoadEvents = new ArrayList<>();
    List<IDayItem> mLoadDays = new ArrayList<>();
    List<IWeekItem> mLoadWeeks = new ArrayList<>();


    RealmResults<RCal> rCalCandidate;
    RealmResults<RDay> rDays;
    RealmResults<RWeek> rWeeks;
    RealmResults<REvent> rEvents;

    private boolean doingAsync = false;
    Realm realm;
    private AgendaCalendarView mAgendaCalendarView;
    private View mProgressView;

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
                doingAsync = true;
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_agenda, container, false);
        mAgendaCalendarView = (AgendaCalendarView) rootView.findViewById(R.id.agenda_calendar_view);
        mProgressView = rootView.findViewById(R.id.agenda_progress);



        realm = Realm.getDefaultInstance();

        if(doingAsync) {
            showProgress(true);
        } else {

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
            } else {
                // Usual way
                getData();
            }
        }
        return rootView;
    }

    @Override
    public void onDaySelected(IDayItem dayItem) {

    }

    @Override
    public void onEventSelected(CalendarEvent event) {
        Log.d(LogConst.LOG,event.getTitle());
        // TODO: show extended event info
    }

    @Override
    public void onScrollToDate(Calendar calendar) {

    }

    public void populateView() {
        mAgendaCalendarView.init(Locale.ENGLISH, mLoadWeeks, mLoadDays, mLoadEvents, this); // TODO: LOCALE.getDefault()
        mAgendaCalendarView.addEventRenderer(new BocconiEventRenderer());

    }

    @Override
    public void onDetach() {
        super.onDetach();
        if(rEvents!=null) {
            rDays.removeChangeListeners();
            rWeeks.removeChangeListeners();
            rEvents.removeChangeListeners();
        }
        realm.close();
    }

    public void getData() {
        // TODO: kill orientation workaround and switch to AsyncTask when realm releases freeze
        rDays = realm.where(RDay.class).findAllAsync();
        rDays.addChangeListener(new RealmChangeListener() {
            @Override
            public void onChange(Object object) {
                mLoadDays.addAll(rDays);
                rDays.removeChangeListeners();
                rWeeks = realm.where(RWeek.class).findAllAsync();
                rWeeks.addChangeListener(new RealmChangeListener() {
                    @Override
                    public void onChange(Object object) {
                        mLoadWeeks.addAll(rWeeks);
                        rWeeks.removeChangeListeners();
                        rEvents = realm.where(REvent.class).findAllAsync();
                        rEvents.addChangeListener(new RealmChangeListener() {
                            @Override
                            public void onChange(Object object) {
                                mLoadEvents.addAll(rEvents);
                                rEvents.removeChangeListeners();
                                //////
                                populateView();
                            }
                        });
                    }
                });
            }
        });
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mAgendaCalendarView.setVisibility(show ? View.GONE : View.VISIBLE);
            mAgendaCalendarView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mAgendaCalendarView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

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
            mAgendaCalendarView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }
    public void getDataSync() {
        //RCal cal = realm.where(RCal.class).findAll();
        mLoadDays.addAll(realm.allObjects(RDay.class));
        mLoadWeeks.addAll(realm.allObjects(RWeek.class));
        mLoadEvents.addAll(realm.allObjects(REvent.class));
        populateView();
    }

    public void getDataFromRCal() {
        RCal candidate = rCalCandidate.first();

        mLoadDays.addAll(candidate.getrDays());
        mLoadWeeks.addAll(candidate.getrWeeks());
        mLoadEvents.addAll(candidate.getrEvents());
    }

    public void serviceIsDone() {
        doingAsync = false;
        getData();
        showProgress(false);
    }

    public static class RetainFragment extends Fragment {
        private static final String TAG = "RetainFragmentAgenda";


        public RetainFragment() {
        }
        public static RetainFragment findOrCreateRetainFragment(FragmentManager fm) {
            RetainFragment fragment = (RetainFragment) fm.findFragmentByTag(TAG);
            if (fragment == null) {
                fragment = new RetainFragment();
                fm.beginTransaction().add(fragment, TAG).commit();
            }
            return fragment;
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setRetainInstance(true);
        }

        private void loadAsync() {

        }
    }

}
