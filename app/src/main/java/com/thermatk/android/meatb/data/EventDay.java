package com.thermatk.android.meatb.data;

import java.util.Date;

import io.realm.RealmList;
import io.realm.RealmObject;

public class EventDay extends RealmObject {

    private long dateLong;
    private Date date;
    private long lastUpdated;
    private RealmList<AgendaEvent> agendaEvents;


    public long getDateLong() {
        return dateLong;
    }

    public void setDateLong(long dateLong) {
        this.dateLong = dateLong;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public RealmList<AgendaEvent> getAgendaEvents() {
        return agendaEvents;
    }

    public void setAgendaEvents(RealmList<AgendaEvent> agendaEvents) {
        this.agendaEvents = agendaEvents;
    }

    public long getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(long lastUpdated) {
        this.lastUpdated = lastUpdated;
    }
}
