package com.thermatk.android.meatb.services;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.github.tibolte.agendacalendarview.CalendarManager;
import com.github.tibolte.agendacalendarview.models.CalendarEvent;
import com.github.tibolte.agendacalendarview.models.IDayItem;
import com.github.tibolte.agendacalendarview.models.IWeekItem;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.thermatk.android.meatb.LogConst;
import com.thermatk.android.meatb.agenda.ACVDay;
import com.thermatk.android.meatb.agenda.ACVWeek;
import com.thermatk.android.meatb.agenda.BocconiCalendarEvent;
import com.thermatk.android.meatb.helpers.DataHelper;
import com.thermatk.android.meatb.data.EventDay;
import com.thermatk.android.meatb.helpers.ServiceHelper;
import com.thermatk.android.meatb.yabAPIClient;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import cz.msebera.android.httpclient.Header;
import io.realm.Realm;
import io.realm.RealmResults;


public class AgendaUpdateService extends IntentService {
    public static final String ACTION_AgendaUpdateService_DONE = "com.thermatk.android.meatb.services.AgendaUpdateService.DONE";

    public AgendaUpdateService(String name) {
        super(name);
    }

    public AgendaUpdateService() {
        super("AgendaUpdateService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if(ServiceHelper.isAgendaServiceRunning(getApplicationContext())) {
            Log.d(LogConst.LOG, "AgendaUpdateService already running");
            return;
        }
        ServiceHelper.setAgendaServiceRunning(getApplicationContext(),true);
        Log.d(LogConst.LOG, "AgendaUpdateService started");
        JsonHttpResponseHandler responseHandler = new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                DataHelper.writeAgendaData(response,getApplicationContext());


                Realm realm = Realm.getDefaultInstance();

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
                DataHelper.getAgendaEventList(eventList, getApplicationContext());

                CalendarManager calendarManager = CalendarManager.getInstance(getApplicationContext());
                calendarManager.buildCal(minDate, maxDate, Locale.ENGLISH, new ACVDay(), new ACVWeek());
                calendarManager.loadEvents(eventList, new BocconiCalendarEvent());


                List<CalendarEvent> readyEvents = calendarManager.getEvents();
                List<IDayItem> readyDays = calendarManager.getDays();
                List<IWeekItem> readyWeeks = calendarManager.getWeeks();
                DataHelper.writeAgendaCalendarViewPersistence(readyEvents, readyDays, readyWeeks);
                realm.close();
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
        yabAPIClient agendaClient = new yabAPIClient(getApplicationContext(),false);
        agendaClient.getAgendaForAYear(responseHandler);
        Log.d(LogConst.LOG, "AgendaUpdateService ended");
        ServiceHelper.setAgendaServiceRunning(getApplicationContext(),false);
    }

}
