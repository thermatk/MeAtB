package com.thermatk.android.meatb.helpers;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class ServiceHelper {
    public final static String LOCK_AGENDA_UPDATE_SERVICE = "AgendaUpdateServiceLock";
    public final static String LOCK_INBOX_UPDATE_SERVICE = "InboxUpdateServiceLock";

    public static boolean isAgendaServiceRunning(Context mContext) {
        return isLocked(mContext,LOCK_AGENDA_UPDATE_SERVICE);
    }
    public static void setAgendaServiceRunning(Context mContext, boolean state) {
        setLocked(mContext,state,LOCK_AGENDA_UPDATE_SERVICE);
    }
    public static boolean isInboxServiceRunning(Context mContext) {
        return isLocked(mContext,LOCK_INBOX_UPDATE_SERVICE);
    }
    public static void setInboxServiceRunning(Context mContext, boolean state) {
        setLocked(mContext,state,LOCK_INBOX_UPDATE_SERVICE);
    }

    private static boolean isLocked(Context mContext, String lock) {
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(mContext);
        return sharedPrefs.getBoolean(lock, false);
    }
    private static void setLocked(Context mContext, boolean state, String lock) {
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(mContext).edit();
        editor.putBoolean(lock, state);
        editor.apply();
    }
}
