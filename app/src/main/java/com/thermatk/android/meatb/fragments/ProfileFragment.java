package com.thermatk.android.meatb.fragments;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.app.Fragment;
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
import com.thermatk.android.meatb.LogConst;
import com.thermatk.android.meatb.R;
import com.thermatk.android.meatb.agenda.ACVDay;
import com.thermatk.android.meatb.agenda.ACVWeek;
import com.thermatk.android.meatb.agenda.BocconiCalendarEvent;
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


public class ProfileFragment extends Fragment implements CalendarPickerController{

    List<CalendarEvent> mLoadEvents = new ArrayList<>();
    Calendar minDate;
    Calendar maxDate;

    LoadDataTask lt;

    Realm realm;
    AgendaCalendarView mAgendaCalendarView;

    public ProfileFragment() {
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

        RealmResults<EventDay> days = realm.allObjects(EventDay.class);
        if (days.size() == 0) {
            mAgendaCalendarView.setVisibility(View.INVISIBLE);
            // show progress
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
        ///////////

        lt = new LoadDataTask(this);
        lt.execute();

    }
    private void trueList() {
        /////
        Realm realm = Realm.getDefaultInstance();
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
                mLoadEvents.add(eventBocconi);
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

        mAgendaCalendarView.init(new ArrayList<CalendarEvent>(), minDate, maxDate, Locale.ENGLISH, this, new ACVWeek(), new ACVDay(), new BocconiCalendarEvent());
        JsonHttpResponseHandler responseHandler = new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                DataWriter.writeAgendaData(response);



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
                trueList();

                CalendarManager calendarManager = CalendarManager.getInstance(getActivity());
                calendarManager.buildCal(minDate, maxDate, Locale.ENGLISH, new ACVDay(), new ACVWeek());
                calendarManager.loadEvents(eventList, new BocconiCalendarEvent());


                List<CalendarEvent> readyEvents = calendarManager.getEvents();
                List<IDayItem> readyDays = calendarManager.getDays();
                List<IWeekItem> readyWeeks = calendarManager.getWeeks();
                DataWriter.writeAgendaCalendarViewPersistence(readyEvents, readyDays, readyWeeks);
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

    private void constructFromRealm() {

        Realm realm = Realm.getDefaultInstance();
        // minimum and maximum date of our calendar
        minDate = Calendar.getInstance();
        maxDate = Calendar.getInstance();

        minDate.add(Calendar.DAY_OF_MONTH, -1);
        //// TODO: optimize

        RealmResults<EventDay> days = realm.where(EventDay.class).findAllSorted("date");
        EventDay lastDay = days.last();


        ///
        maxDate.setTimeInMillis(lastDay.getDateLong());
        maxDate.add(Calendar.DAY_OF_WEEK, 1);
        trueList();
    }


    class LoadDataTask extends AsyncTask<Void, Void, Void> {
        private CalendarPickerController pickerCallback;

        public LoadDataTask(CalendarPickerController picker) {
            pickerCallback = picker;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //tvInfo.setText("Begin"); possibly show progress
        }

        @Override
        protected Void doInBackground(Void... params) {
            constructFromRealm();
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            //tvInfo.setText("End");
            mAgendaCalendarView.init(mLoadEvents,minDate,maxDate,Locale.ENGLISH,pickerCallback, new ACVWeek(), new ACVDay(), new BocconiCalendarEvent());
            mAgendaCalendarView.addEventRenderer(new BocconiEventRenderer());

        }
    }
}
