package com.thermatk.android.meatb.helpers;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.provider.CalendarContract;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.thermatk.android.meatb.LogConst;

import java.util.TimeZone;

public class CalendarHelper {
    public static final String ACCOUNT_NAME = "me@B";
    public static final String NAME = "me@B synced classes";

    /** Adds Events and Reminders in Calendar. */
    /*
    private void addReminderInCalendar(Context context) {
        Calendar cal = Calendar.getInstance();
        String calendarBaseUri = getCalendarUriBase(true);
        Uri EVENTS_URI = Uri.parse(calendarBaseUri + "events");
        ContentResolver cr = context.getContentResolver();
        TimeZone timeZone = TimeZone.getDefault();

        // Inserting an event in calendar.
        ContentValues values = new ContentValues();
        values.put(CalendarContract.Events.CALENDAR_ID, 1);
        values.put(CalendarContract.Events.TITLE, "Sanjeev Reminder 01");
        values.put(CalendarContract.Events.DESCRIPTION, "A test Reminder.");
        values.put(CalendarContract.Events.ALL_DAY, 0);
        // event starts at 11 minutes from now
        values.put(CalendarContract.Events.DTSTART, cal.getTimeInMillis() + 1 * 60 * 1000);
        // ends 60 minutes from now
        values.put(CalendarContract.Events.DTEND, cal.getTimeInMillis() + 2 * 60 * 1000);
        values.put(CalendarContract.Events.EVENT_TIMEZONE, timeZone.getID());
        values.put(CalendarContract.Events.HAS_ALARM, 1);
        Uri event = cr.insert(EVENTS_URI, values);
        String eventId = event.getLastPathSegment();
        // Display event id.
        Log.d(LogConst.LOG, "Event added :: ID :: " + eventId);

        // Adding reminder for event added.
        Uri REMINDERS_URI = Uri.parse(calendarBaseUri + "reminders");
        values = new ContentValues();
        values.put(CalendarContract.Reminders.EVENT_ID, Long.parseLong(eventId));
        values.put(CalendarContract.Reminders.METHOD, CalendarContract.Reminders.METHOD_ALERT);
        values.put(CalendarContract.Reminders.MINUTES, 10);
        cr.insert(REMINDERS_URI, values);
    }

    // Returns Calendar Base URI, supports both new and old OS.
    private String getCalendarUriBase(boolean eventUri) {
        Uri calendarURI = null;
        try {
            calendarURI = (eventUri) ? Uri.parse("content://com.android.calendar/") : Uri
                    .parse("content://com.android.calendar/calendars");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return calendarURI.toString();
    }
    */
    public static void setReminderState(SharedPreferences mSharedPreferences, boolean state, long calendarId) {
        SharedPreferences.Editor editSP = mSharedPreferences.edit();

        editSP.putBoolean("ReminderSet", state);
        editSP.putLong("calendarId", calendarId);
        editSP.apply();
    }

    public static boolean getReminderState(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean("ReminderSet", false);
    }

    public static long getCalendarId(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getLong("calendarId", -1);
    }

    public static long addEvent(Context context, long start, long end, String title, String description, String location) {

        try {
            ContentResolver cr = context.getContentResolver();
            ContentValues values = new ContentValues();
            values.put(CalendarContract.Events.DTSTART, start);
            values.put(CalendarContract.Events.DTEND, end);
            values.put(CalendarContract.Events.DESCRIPTION, description);
            values.put(CalendarContract.Events.EVENT_LOCATION, location);
            values.put(CalendarContract.Events.TITLE, title);
            values.put(CalendarContract.Events.ALL_DAY, 0);
            values.put(CalendarContract.Events.CALENDAR_ID, getCalendarId(context));
            values.put(CalendarContract.Events.EVENT_TIMEZONE, TimeZone.getDefault().getID());
            values.put(CalendarContract.Events.ACCESS_LEVEL, CalendarContract.Events.ACCESS_PUBLIC);

            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.WRITE_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return 0;
            } // TODO: fix this to somewhere
            Uri uri = cr.insert(CalendarContract.Events.CONTENT_URI, values);

            // Save the eventId into the Task object for possible future delete.
            Long eventId = Long.parseLong(uri.getLastPathSegment());
            return eventId;
            // Add a 5 minute, 1 hour (2 reminders)
            //setReminder(cr, eventId, 10);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    public static long addCalendar(Context context) {
        ContentValues values = new ContentValues();
        values.put(
                CalendarContract.Calendars.ACCOUNT_NAME, ACCOUNT_NAME
        );
        values.put(
                CalendarContract.Calendars.ACCOUNT_TYPE,
                CalendarContract.ACCOUNT_TYPE_LOCAL);
        values.put(
                CalendarContract.Calendars.NAME, NAME
        );
        values.put(
                CalendarContract.Calendars.CALENDAR_DISPLAY_NAME,
                NAME);
        values.put(
                CalendarContract.Calendars.CALENDAR_COLOR,
                0xffff0000);
        values.put(
                CalendarContract.Calendars.CALENDAR_ACCESS_LEVEL,
                CalendarContract.Calendars.CAL_ACCESS_OWNER);
        values.put(
                CalendarContract.Calendars.OWNER_ACCOUNT,
                ACCOUNT_NAME);
        values.put(
                CalendarContract.Calendars.CALENDAR_TIME_ZONE, TimeZone.getDefault().getID());
        values.put(
                CalendarContract.Calendars.SYNC_EVENTS,
                1);
        values.put(
                CalendarContract.Calendars.VISIBLE,
                1);
        Uri.Builder builder =
                CalendarContract.Calendars.CONTENT_URI.buildUpon();
        builder.appendQueryParameter(
                CalendarContract.Calendars.ACCOUNT_NAME,
                "com.thermatk.android.meatb");
        builder.appendQueryParameter(
                CalendarContract.Calendars.ACCOUNT_TYPE,
                CalendarContract.ACCOUNT_TYPE_LOCAL);
        builder.appendQueryParameter(
                CalendarContract.CALLER_IS_SYNCADAPTER,
                "true");
        Uri uri =
                context.getContentResolver().insert(builder.build(), values);
        long calendarId = Long.parseLong(uri.getLastPathSegment());
        Log.d(LogConst.LOG, "CalendarID: " + calendarId);
        return calendarId;
    }

    public static void deleteCalendar(Context context, long calID) {
        Uri evuri = CalendarContract.Calendars.CONTENT_URI;
        Uri deleteUri = ContentUris.withAppendedId(evuri, calID);
        context.getContentResolver().delete(deleteUri, null, null);
    }

    public static void findAndDeleteCalendars(Context context) {
        Uri evuri = CalendarContract.Calendars.CONTENT_URI;
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
            // TODO: wipe but no permission?
            return;
        }
        Cursor result = context.getContentResolver().query(evuri, new String[]{CalendarContract.Calendars._ID, CalendarContract.Calendars.ACCOUNT_NAME, CalendarContract.Calendars.CALENDAR_DISPLAY_NAME}, null, null, null);
        while (result.moveToNext())
        {
            if(result.getString(2).equals(NAME))
            {
                long calid = result.getLong(0);
                deleteCalendar(context, calid);
            }
        }
    }

    // routine to add reminders with the event
    public static void setReminder(Context context, long eventID, int timeBefore) {
        try {
            ContentResolver cr = context.getContentResolver();
            ContentValues values = new ContentValues();
            values.put(CalendarContract.Reminders.MINUTES, timeBefore);
            values.put(CalendarContract.Reminders.EVENT_ID, eventID);
            values.put(CalendarContract.Reminders.METHOD, CalendarContract.Reminders.METHOD_ALERT);
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.WRITE_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }// TODO: fix this to somewhere
            Uri uri = cr.insert(CalendarContract.Reminders.CONTENT_URI, values);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //use following method to remove an event from the calendar using the eventId stored within the Task object.
    public static void removeEvent(Context context, long eventId) {
        ContentResolver cr = context.getContentResolver();

        int iNumRowsDeleted = 0;
        Uri eventUri = ContentUris.withAppendedId(CalendarContract.Events.CONTENT_URI, eventId);
        iNumRowsDeleted = cr.delete(eventUri, null, null);

        Log.d(LogConst.LOG, "Deleted " + iNumRowsDeleted + " calendar entry.");
    }

    /*
    public int updateEvent(Context context) {

        int iNumRowsUpdated = 0;
        GregorianCalendar calDate = new GregorianCalendar(this._year, this._month, this._day, this._hour, this._minute);

        ContentValues event = new ContentValues();

        event.put(CalendarContract.Events.TITLE, this._title);
        event.put("hasAlarm", 1); // 0 for false, 1 for true
        event.put(CalendarContract.Events.DTSTART, calDate.getTimeInMillis());
        event.put(CalendarContract.Events.DTEND, calDate.getTimeInMillis()+60*60*1000);

        Uri eventsUri = Uri.parse(CALENDAR_URI_BASE+"events");
        Uri eventUri = ContentUris.withAppendedId(eventsUri, this._eventId);

        iNumRowsUpdated = context.getContentResolver().update(eventUri, event, null,
                null);

        // TODO put text into strings.xml
        Log.i(DEBUG_TAG, "Updated " + iNumRowsUpdated + " calendar entry.");
        return iNumRowsUpdated;
    }
        */
}
