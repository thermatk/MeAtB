package com.thermatk.android.meatb.data;

import java.util.Date;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class EventDay extends RealmObject {

    @PrimaryKey
    private long id;
    private long dateLong;
    private Date date;
    private String dateString;
    private long lastUpdated;
    private String dayString;
    private RealmList<AgendaEvent> agendaEvents;

    public String getDateString() {
        return dateString;
    }

    public void setDateString(String dateString) {
        this.dateString = dateString;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getDayString() {
        return dayString;
    }

    public void setDayString(String dayString) {
        this.dayString = dayString;
    }

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
