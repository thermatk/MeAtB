package com.thermatk.android.meatb.fragments;

import android.app.Fragment;
import android.content.Context;
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
import com.loopj.android.http.JsonHttpResponseHandler;

import com.thermatk.android.meatb.agenda.data.ACVDay;
import com.thermatk.android.meatb.agenda.data.ACVWeek;
import com.thermatk.android.meatb.agenda.data.BocconiCalendarEvent;
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
            mAgendaCalendarView.setVisibility(View.INVISIBLE);
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
        Calendar minDate = Calendar.getInstance();
        Calendar maxDate = Calendar.getInstance();

        minDate.add(Calendar.DAY_OF_MONTH, -1);
        //// TODO: optimize

        RealmResults<EventDay> days = realm.where(EventDay.class).findAllSorted("date");
        EventDay lastDay = days.last();


        ///
        maxDate.setTimeInMillis(lastDay.getDateLong());
        maxDate.add(Calendar.DAY_OF_WEEK,1);

        List<CalendarEvent> eventList = new ArrayList<>();
        // TODO: makeasync
        trueList(eventList);
        Log.d(LogConst.LOG, "Events: " + Integer.toString(eventList.size()));
        mAgendaCalendarView.init(eventList, minDate, maxDate, Locale.ENGLISH, this, new ACVWeek(), new ACVDay()); // TODO: LOCALE.getDefault()
        mAgendaCalendarView.addEventRenderer(new BocconiEventRenderer());

    }
    private void trueList(List<CalendarEvent> eventList) {
        /////

        RealmResults<EventDay> days = realm.where(EventDay.class).findAll();
        //TODO: only days in scope
        //Log.d(LogConst.LOG, Integer.toString(days.size()));
        for(EventDay day: days) {
            //Log.d(LogConst.LOG,day.getDate().toString());
            RealmList<AgendaEvent> events = day.getAgendaEvents();
            for(AgendaEvent event: events) {

                Calendar startTime = Calendar.getInstance();
                Calendar endTime = Calendar.getInstance();
                String dateString = event.getDuration();
                startTime.setTimeInMillis(event.getDate_start_long());
                endTime.setTimeInMillis(event.getDate_end_long());

                BocconiCalendarEvent eventBocconi = new BocconiCalendarEvent(event.getTitle(), event.getDescription(), event.getSupertitle(),
                    getColorWrapper(getActivity(), R.color.orange_dark), startTime, endTime, false, dateString);
                eventList.add(eventBocconi);
            }
        }

    }
    // support v4 workaround
    private static int getColorWrapper(Context context, int id) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return context.getColor(id);
        } else {
            return context.getResources().getColor(id);
        }
    }
    public void sendRequest() {

        Calendar minDate = Calendar.getInstance();
        Calendar maxDate = Calendar.getInstance();

        minDate.add(Calendar.DAY_OF_MONTH, -2);
        maxDate.add(Calendar.MONTH, 1);

        mAgendaCalendarView.init(new ArrayList<CalendarEvent>(), minDate, maxDate, Locale.ENGLISH, this, new ACVWeek(), new ACVDay());
        JsonHttpResponseHandler responseHandler = new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                DataWriter.writeAgendaData(response);
                mAgendaCalendarView.setVisibility(View.VISIBLE);
                fillView();
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

}
