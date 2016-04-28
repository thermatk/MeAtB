package com.thermatk.android.meatb.services;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.util.Log;
import android.view.View;

import com.github.tibolte.agendacalendarview.CalendarManager;
import com.github.tibolte.agendacalendarview.models.CalendarEvent;
import com.github.tibolte.agendacalendarview.models.IDayItem;
import com.github.tibolte.agendacalendarview.models.IWeekItem;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.thermatk.android.meatb.LogConst;
import com.thermatk.android.meatb.agenda.ACVDay;
import com.thermatk.android.meatb.agenda.ACVWeek;
import com.thermatk.android.meatb.agenda.BocconiCalendarEvent;
import com.thermatk.android.meatb.data.DataUtilities;
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
import io.realm.RealmResults;


public class AgendaUpdateService extends Service {
    public AgendaUpdateService() {
    }

    @Override
    public void onDestroy () {
        Log.d(LogConst.LOG,"Service AGENDAUPDATESERVICE finished");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        new UpdateTask().execute();
        return Service.START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private class UpdateTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {JsonHttpResponseHandler responseHandler = new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                DataUtilities.writeAgendaData(response,getApplicationContext());


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
                DataUtilities.getAgendaEventList(eventList, getApplicationContext());

                CalendarManager calendarManager = CalendarManager.getInstance(getApplicationContext());
                calendarManager.buildCal(minDate, maxDate, Locale.ENGLISH, new ACVDay(), new ACVWeek());
                calendarManager.loadEvents(eventList, new BocconiCalendarEvent());


                List<CalendarEvent> readyEvents = calendarManager.getEvents();
                List<IDayItem> readyDays = calendarManager.getDays();
                List<IWeekItem> readyWeeks = calendarManager.getWeeks();
                DataUtilities.writeAgendaCalendarViewPersistence(readyEvents, readyDays, readyWeeks);
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
            return null;
        }

        @Override
        protected void onPostExecute(Void v) {
            stopSelf();
        }
    }
}
