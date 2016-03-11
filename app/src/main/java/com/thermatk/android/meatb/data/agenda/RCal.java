package com.thermatk.android.meatb.data.agenda;

import io.realm.RealmList;
import io.realm.RealmObject;

/**
 * Created by thermatk on 11.03.16.
 */
public class RCal extends RealmObject {
    public RealmList<RDay> getrDays() {
        return rDays;
    }

    public void setrDays(RealmList<RDay> rDays) {
        this.rDays = rDays;
    }

    public RealmList<RWeek> getrWeeks() {
        return rWeeks;
    }

    public void setrWeeks(RealmList<RWeek> rWeeks) {
        this.rWeeks = rWeeks;
    }

    public RealmList<REvent> getrEvents() {
        return rEvents;
    }

    public void setrEvents(RealmList<REvent> rEvents) {
        this.rEvents = rEvents;
    }

    public long getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(long lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    private long lastUpdated;
    private RealmList<RDay> rDays;
    private RealmList<RWeek> rWeeks;
    private RealmList<REvent> rEvents;
}
