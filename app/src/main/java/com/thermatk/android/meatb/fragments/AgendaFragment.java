package com.thermatk.android.meatb.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.format.DateFormat;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.tibolte.agendacalendarview.AgendaCalendarView;
import com.github.tibolte.agendacalendarview.CalendarPickerController;
import com.github.tibolte.agendacalendarview.models.CalendarEvent;
import com.github.tibolte.agendacalendarview.models.DayItem;
import com.loopj.android.http.JsonHttpResponseHandler;

import com.thermatk.android.meatb.agenda.BocconiCalendarEvent;
import com.thermatk.android.meatb.LogConst;
import com.thermatk.android.meatb.R;
import com.thermatk.android.meatb.agenda.BocconiEventRenderer;
import com.thermatk.android.meatb.data.AgendaEvent;
import com.thermatk.android.meatb.data.DataWriter;
import com.thermatk.android.meatb.data.EventDay;
import com.thermatk.android.meatb.yabAPIClient;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import cz.msebera.android.httpclient.Header;
import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;


public class AgendaFragment extends Fragment implements CalendarPickerController {

    Realm realm;
    AgendaCalendarView mAgendaCalendarView;

    public AgendaFragment() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_agenda, container, false);
        mAgendaCalendarView = (AgendaCalendarView) rootView.findViewById(R.id.agenda_calendar_view);
        /*Button mSignInButton = (Button) rootView.findViewById(R.id.button);
        mSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendRequest();
            }
        });*/
        // TODO: RealmChangeListener
        // TODO: call populate database


        realm = Realm.getDefaultInstance();
        //// check if first time in agenda

        RealmResults<EventDay> days = realm.where(EventDay.class).findAll();
        if (days.size() == 0) {
            sendRequest();
            /// TODO: callback, with RealmChangeListener?
        } else {
            /////
            fillView();
        }

        ////

        return rootView;
    }

    private void fillView(){

        // minimum and maximum date of our calendar
        // 2 month behind, one year ahead, example: March 2015 <-> May 2015 <-> May 2016
        Calendar minDate = Calendar.getInstance();
        Calendar maxDate = Calendar.getInstance();

        minDate.add(Calendar.MONTH, -2);
        minDate.set(Calendar.DAY_OF_MONTH, 1);
        maxDate.add(Calendar.YEAR, 1);

        List<CalendarEvent> eventList = new ArrayList<>();
        // TODO: makeasync
        trueList(eventList);
        Log.d(LogConst.LOG, Integer.toString(eventList.size()));
        mAgendaCalendarView.init(eventList, minDate, maxDate, Locale.getDefault(), this);
        mAgendaCalendarView.addEventRenderer(new BocconiEventRenderer());

    }
    private void trueList(List<CalendarEvent> eventList) {
        /////

        RealmResults<EventDay> days = realm.where(EventDay.class).findAll();
        Log.d(LogConst.LOG,Integer.toString(days.size()));
        for(EventDay day: days) {
            Log.d(LogConst.LOG,day.getDate().toString());
            RealmList<AgendaEvent> events = day.getAgendaEvents();
            for(AgendaEvent event: events) {

                Calendar startTime = Calendar.getInstance();
                Calendar endTime = Calendar.getInstance();
                Date start = event.getDate_start();
                Date end = event.getDate_end();
                String dateString;
                if (end == null){
                    end =  new Date(start.getTime() + DateUtils.HOUR_IN_MILLIS);
                    dateString = DateFormat.format("kk:mm", event.getDate_start()) + " - ∞";
                } else {
                    dateString  = DateFormat.format("kk:mm", event.getDate_start()) + " - " +DateFormat.format("kk:mm", event.getDate_end());
                }
                startTime.setTimeInMillis(start.getTime());
                endTime.setTimeInMillis(end.getTime());

                BocconiCalendarEvent eventBocconi = new BocconiCalendarEvent(event.getTitle(), event.getDescription(), event.getSupertitle(),
                        ContextCompat.getColor(getActivity(), R.color.orange_dark), startTime, endTime, false, dateString);
                        //new SampleItem().withName(event.getTitle() + " Starts " + DateFormat.format("yyyyMMdd kk:mm:ss", event.getDate_start()))
                eventList.add(eventBocconi);
            }
        }

    }

    public void sendRequest() {
        JsonHttpResponseHandler responseHandler = new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                DataWriter.writeAgendaData(response);
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
    public void onDaySelected(DayItem dayItem) {

    }

    @Override
    public void onEventSelected(CalendarEvent event) {

    }

    @Override
    public void onScrollToDate(Calendar calendar) {

    }
}
