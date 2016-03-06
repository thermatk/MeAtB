package com.thermatk.android.meatb.agenda;

import com.github.tibolte.agendacalendarview.models.BaseCalendarEvent;
import com.github.tibolte.agendacalendarview.models.CalendarEvent;

import java.util.Calendar;

public class BocconiCalendarEvent extends BaseCalendarEvent {
    private String mDate;

    // region Constructors

    public BocconiCalendarEvent(long id, int color, String title, String description, String location, long dateStart, long dateEnd, int allDay, String duration, String dateText) {
        super(id, color, title, description, location, dateStart, dateEnd, allDay, duration);
        this.mDate = dateText;
    }

    public BocconiCalendarEvent(String title, String description, String location, int color, Calendar startTime, Calendar endTime, boolean allDay, String dateText) {
        super(title, description, location, color, startTime, endTime, allDay);
        this.mDate = dateText;
    }

    public BocconiCalendarEvent(BocconiCalendarEvent calendarEvent) {
        super(calendarEvent);
        this.mDate = calendarEvent.getDate();
    }

    // endregion

    // region Public methods
    public String getDate() {
        return mDate;
    }

    public void setDate(String dateText) {
        this.mDate = dateText;
    }

    // endregion

    // region Class - BaseCalendarEvent

    @Override
    public CalendarEvent copy() {
        return new BocconiCalendarEvent(this);
    }

    // endregion
}