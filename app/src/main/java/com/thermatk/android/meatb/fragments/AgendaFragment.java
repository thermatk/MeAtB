package com.thermatk.android.meatb.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.tibolte.agendacalendarview.AgendaCalendarView;
import com.github.tibolte.agendacalendarview.CalendarManager;
import com.github.tibolte.agendacalendarview.CalendarPickerController;
import com.github.tibolte.agendacalendarview.models.CalendarEvent;
import com.github.tibolte.agendacalendarview.models.IDayItem;
import com.github.tibolte.agendacalendarview.models.IWeekItem;
import com.loopj.android.http.JsonHttpResponseHandler;

import com.thermatk.android.meatb.agenda.ACVDay;
import com.thermatk.android.meatb.agenda.ACVWeek;
import com.thermatk.android.meatb.agenda.BocconiCalendarEvent;
import com.thermatk.android.meatb.LogConst;
import com.thermatk.android.meatb.R;
import com.thermatk.android.meatb.agenda.BocconiEventRenderer;
import com.thermatk.android.meatb.data.DataUtilities;
import com.thermatk.android.meatb.data.EventDay;
import com.thermatk.android.meatb.data.agenda.RDay;
import com.thermatk.android.meatb.data.agenda.REvent;
import com.thermatk.android.meatb.data.agenda.RWeek;
import com.thermatk.android.meatb.yabAPIClient;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import cz.msebera.android.httpclient.Header;
import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;


public class AgendaFragment extends Fragment implements CalendarPickerController {

    List<CalendarEvent> mLoadEvents = new ArrayList<>();
    List<IDayItem> mLoadDays = new ArrayList<>();
    List<IWeekItem> mLoadWeeks = new ArrayList<>();


    RealmResults<RDay> rDays;
    RealmResults<RWeek> rWeeks;
    RealmResults<REvent> rEvents;


    Realm realm;
    AgendaCalendarView mAgendaCalendarView;

    public AgendaFragment() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().setTitle("Agenda");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_agenda, container, false);
        mAgendaCalendarView = (AgendaCalendarView) rootView.findViewById(R.id.agenda_calendar_view);



        realm = Realm.getDefaultInstance();


        setRetainInstance(true);

        if(mLoadEvents.size()>0) {
            populateView();
        } else {
            // TODO: RealmChangeListener
            // TODO: call populate database
            //// check if first time in agenda

            RealmResults<EventDay> days = realm.allObjects(EventDay.class);
            if (days.size() == 0) {
                mAgendaCalendarView.setVisibility(View.INVISIBLE);
                // show progress
                sendRequest();
                /// TODO: callback, with RealmChangeListener?
            } else {
                /////
                getData();
            }
        }

        ////

        return rootView;
    }

    public void sendRequest() {

        Calendar minDate = Calendar.getInstance();
        Calendar maxDate = Calendar.getInstance();

        minDate.add(Calendar.DAY_OF_MONTH, -2);
        maxDate.add(Calendar.MONTH, 1);

        mAgendaCalendarView.init(new ArrayList<CalendarEvent>(), minDate, maxDate, Locale.ENGLISH, this, new ACVWeek(), new ACVDay(),new BocconiCalendarEvent());
        JsonHttpResponseHandler responseHandler = new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                DataUtilities.writeAgendaData(response);



                // minimum and maximum date of our calendar
                Calendar minDate = Calendar.getInstance();
                Calendar maxDate = Calendar.getInstance();

                minDate.add(Calendar.DAY_OF_MONTH, -1);
                //// TODO: optimize

                RealmResults<EventDay> days = realm.where(EventDay.class).findAllSorted("date");
                EventDay lastDay = days.last();


                ///
                maxDate.setTimeInMillis(lastDay.getDateLong());
                maxDate.add(Calendar.DAY_OF_WEEK, 1);

                List<CalendarEvent> eventList = new ArrayList<>();
                DataUtilities.getAgendaEventList(eventList, getActivity());

                CalendarManager calendarManager = CalendarManager.getInstance(getActivity());
                calendarManager.buildCal(minDate, maxDate, Locale.ENGLISH, new ACVDay(), new ACVWeek());
                calendarManager.loadEvents(eventList, new BocconiCalendarEvent());


                List<CalendarEvent> readyEvents = calendarManager.getEvents();
                List<IDayItem> readyDays = calendarManager.getDays();
                List<IWeekItem> readyWeeks = calendarManager.getWeeks();
                DataUtilities.writeAgendaCalendarViewPersistence(readyEvents, readyDays, readyWeeks);
                mAgendaCalendarView.setVisibility(View.VISIBLE);
                getData();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable error, JSONObject response) {
                Log.i(LogConst.LOG, "AgendaRequest failed " + response);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable error, JSONArray response) {
                Log.i(LogConst.LOG, "AgendaRequest failed " + response);
            }
        };
        yabAPIClient agendaClient = new yabAPIClient(getActivity());
        agendaClient.getAgendaForAYear(responseHandler);

    }

    @Override
    public void onDaySelected(IDayItem dayItem) {

    }

    @Override
    public void onEventSelected(CalendarEvent event) {

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
        rDays.removeChangeListeners();
        rWeeks.removeChangeListeners();
        rEvents.removeChangeListeners();
    }

    public void getData() {
        // TODO: kill orientation workaround and switch to AsyncTask when realm releases freeze
        rDays = realm.where(RDay.class).findAllAsync();
        rDays.addChangeListener(new RealmChangeListener() {
            @Override
            public void onChange() {
                mLoadDays.addAll(rDays);
                rDays.removeChangeListeners();
                rWeeks = realm.where(RWeek.class).findAllAsync();
                rWeeks.addChangeListener(new RealmChangeListener() {
                    @Override
                    public void onChange() {
                        mLoadWeeks.addAll(rWeeks);
                        rWeeks.removeChangeListeners();
                        rEvents = realm.where(REvent.class).findAllAsync();
                        rEvents.addChangeListener(new RealmChangeListener() {
                            @Override
                            public void onChange() {
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

    public void getDataSync() {
        mLoadDays.addAll(realm.allObjects(RDay.class));
        mLoadWeeks.addAll(realm.allObjects(RWeek.class));
        mLoadEvents.addAll(realm.allObjects(REvent.class));
        populateView();
    }

}
