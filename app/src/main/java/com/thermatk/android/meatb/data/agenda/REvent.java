package com.thermatk.android.meatb.data.agenda;

import com.github.tibolte.agendacalendarview.models.CalendarEvent;
import com.github.tibolte.agendacalendarview.models.IDayItem;
import com.github.tibolte.agendacalendarview.models.IWeekItem;

import java.util.Calendar;
import java.util.Date;

import io.realm.RealmObject;


public class REvent extends RealmObject implements CalendarEvent {
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

    public Date getInstanceDayD() {
        return instanceDayD;
    }

    public void setInstanceDayD(Date instanceDay) {
        this.instanceDayD = instanceDay;
    }

    public Date getStartTimeD() {
        return startTimeD;
    }

    public void setStartTimeD(Date startTime) {
        this.startTimeD = startTime;
    }

    public Date getEndTimeD() {
        return endTimeD;
    }

    public void setEndTimeD(Date endTime) {
        this.endTimeD = endTime;
    }

    public boolean isAllDay() {
        return allDay;
    }

    public void setAllDay(boolean allDay) {
        this.allDay = allDay;
    }

    public boolean isPlaceholder() {
        return placeholder;
    }

    public void setPlaceholder(boolean placeHolder) {
        this.placeholder = placeHolder;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public RDay getDayReferenceR() {
        return dayReferenceR;
    }

    public void setDayReferenceR(RDay dayReference) {
        this.dayReferenceR = dayReference;
    }

    public RWeek getWeekReferenceR() {
        return weekReferenceR;
    }

    public void setWeekReferenceR(RWeek weekReference) {
        this.weekReferenceR = weekReference;
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
    private Date instanceDayD;
    private Date startTimeD;
    private Date endTimeD;
    private boolean allDay;
    private boolean placeholder;
    private String duration;
    private RDay dayReferenceR;
    private RWeek weekReferenceR;

    private String date;


    public IDayItem getDayReference() {
        return dayReferenceR;
    }

    public void setDayReference(IDayItem mDayReference) {
        this.dayReferenceR = (RDay) mDayReference;
    }

    public IWeekItem getWeekReference() {
        return weekReferenceR;
    }

    public void setWeekReference(IWeekItem mWeekReference) {
        this.weekReferenceR = (RWeek) mWeekReference;
    }

    // endregion
    @Override
    public CalendarEvent copy() {
        return new REvent(this);
    }

    public REvent() {

    }
    public REvent(REvent original) {

        this.id = original.getId();
        this.color = original.getColor();
        this.allDay = original.isAllDay();
        this.duration = original.getDuration();
        this.title = original.getTitle();
        this.description = original.getDescription();
        this.location = original.getLocation();
        this.startTimeD = original.getStartTimeD();
        this.endTimeD = original.getEndTimeD();
        this.date = original.getDate();
        this.instanceDayD = original.getInstanceDayD();
        this.placeholder = original.isPlaceholder();
        this.dayReferenceR = original.getDayReferenceR();
        this.weekReferenceR = original.getWeekReferenceR();
    }



    public Calendar getStartTime() {
        Calendar cStartTime = Calendar.getInstance();

        cStartTime.setTime(startTimeD);
        return cStartTime;
    }

    public Calendar getEndTime() {
        Calendar cEndTime = Calendar.getInstance();

        cEndTime.setTime(endTimeD);
        return cEndTime;
    }

    public void setStartTime(Calendar mStartTime) {
        this.startTimeD = mStartTime.getTime();
    }


    public void setEndTime(Calendar mEndTime) {
        this.endTimeD = mEndTime.getTime();

    }

    public Calendar getInstanceDay() {
        Calendar cInstanceDay = Calendar.getInstance();

        cInstanceDay.setTime(instanceDayD);
        return cInstanceDay;
    }

    public void setInstanceDay(Calendar mInstanceDay) {
        this.instanceDayD = mInstanceDay.getTime();
    }


}
