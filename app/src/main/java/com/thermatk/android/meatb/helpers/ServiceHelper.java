package com.thermatk.android.meatb.helpers;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class ServiceHelper {
    public final static String LOCK_AGENDA_UPDATE_SERVICE = "AgendaUpdateServiceLock";
    public static boolean isAgendaServiceRunning(Context mContext) {
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(mContext);
        return sharedPrefs.getBoolean(LOCK_AGENDA_UPDATE_SERVICE, false);
    }
    public static void setAgendaServiceRunning(Context mContext, boolean state) {
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(mContext).edit();
        editor.putBoolean(LOCK_AGENDA_UPDATE_SERVICE, state);
        editor.apply();
    }
}
