package com.thermatk.android.meatb.data.agenda;

import java.util.Date;

import io.realm.RealmObject;


public class REvent extends RealmObject {
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Date getInstanceDay() {
        return instanceDay;
    }

    public void setInstanceDay(Date instanceDay) {
        this.instanceDay = instanceDay;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public boolean isAllDay() {
        return allDay;
    }

    public void setAllDay(boolean allDay) {
        this.allDay = allDay;
    }

    public boolean isPlaceHolder() {
        return placeHolder;
    }

    public void setPlaceHolder(boolean placeHolder) {
        this.placeHolder = placeHolder;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public RDay getDayReference() {
        return dayReference;
    }

    public void setDayReference(RDay dayReference) {
        this.dayReference = dayReference;
    }

    public RWeek getWeekReference() {
        return weekReference;
    }

    public void setWeekReference(RWeek weekReference) {
        this.weekReference = weekReference;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    private long id;

    private int color;

    private String title;

    private String description;

    private String location;
    private Date instanceDay;
    private Date startTime;
    private Date endTime;
    private boolean allDay;
    private boolean placeHolder;
    private String duration;
    private RDay dayReference;
    private RWeek weekReference;

    private String date;

}
