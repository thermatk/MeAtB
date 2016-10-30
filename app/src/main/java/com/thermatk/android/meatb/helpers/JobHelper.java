package com.thermatk.android.meatb.helpers;

import android.content.Context;
import android.util.Log;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.thermatk.android.meatb.LogConst;
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

public class JobHelper {
    public static void runAgendaUpdate(final Context context) {
        JsonHttpResponseHandler responseHandler = new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                DataHelper.writeAgendaData(response,context);

                /* REWRITE
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
                DataHelper.getAgendaEventList(eventList, context);

                CalendarManager calendarManager = CalendarManager.getInstance(context);
                calendarManager.buildCal(minDate, maxDate, Locale.ENGLISH, new ACVDay(), new ACVWeek());
                calendarManager.loadEvents(eventList, new BocconiCalendarEvent());


                List<CalendarEvent> readyEvents = calendarManager.getEvents();
                List<IDayItem> readyDays = calendarManager.getDays();
                List<IWeekItem> readyWeeks = calendarManager.getWeeks();
                DataHelper.writeAgendaCalendarViewPersistence(readyEvents, readyDays, readyWeeks);
                realm.close();
                */
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
        yabAPIClient agendaClient = new yabAPIClient(context,false);
        agendaClient.getAgendaForAYear(responseHandler);
    }

    public static void runInboxUpdate(final Context context) {

        JsonHttpResponseHandler responseHandler = new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                DataHelper.writeInboxData(response);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable error, JSONObject response) {
                Log.i(LogConst.LOG, "InboxRequest failed " + response);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable error, JSONArray response) {
                Log.i(LogConst.LOG, "InboxRequest failed " + response);
            }
        };
        yabAPIClient inboxClient = new yabAPIClient(context,false);
        inboxClient.getInbox(responseHandler);
    }
}
