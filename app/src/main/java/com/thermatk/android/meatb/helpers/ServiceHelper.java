package com.thermatk.android.meatb.helpers;

import com.thermatk.android.meatb.data.ServiceLock;

import io.realm.Realm;
import io.realm.RealmResults;

public class ServiceHelper {
    public final static String LOCK_AGENDA_UPDATE_SERVICE = "AgendaUpdateServiceLock";
    public final static String LOCK_INBOX_UPDATE_SERVICE = "InboxUpdateServiceLock";

    public static boolean isAgendaServiceRunning()  {
        return isLocked(LOCK_AGENDA_UPDATE_SERVICE);
    }
    public static void setAgendaServiceRunning(boolean state) {
        setLocked(state,LOCK_AGENDA_UPDATE_SERVICE);
    }
    public static boolean isInboxServiceRunning() {
        return isLocked(LOCK_INBOX_UPDATE_SERVICE);
    }
    public static void setInboxServiceRunning(boolean state) {
        setLocked(state,LOCK_INBOX_UPDATE_SERVICE);
    }

    private static boolean isLocked(String lock) {
        boolean result = false; // default
        Realm realm = Realm.getDefaultInstance();
        RealmResults<ServiceLock> rr = realm.where(ServiceLock.class).equalTo("lockId", lock).findAll();
        if (rr.size() == 1) {
            result = rr.get(0).isLock();
        }
        realm.close();
        return result;
    }
    private static void setLocked(boolean state, String lock) {

        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        ServiceLock sl;
        RealmResults<ServiceLock> rr = realm.where(ServiceLock.class).equalTo("lockId", lock).findAll();
        if (rr.size() == 1) {
            sl = rr.get(0);
            sl.setLock(state);
            sl.setLastUpdated(System.currentTimeMillis());
        } else {
            sl = realm.createObject(ServiceLock.class,lock);
        }
        sl.setLastUpdated(System.currentTimeMillis());
        sl.setLock(state);
        realm.commitTransaction();
    }
}
