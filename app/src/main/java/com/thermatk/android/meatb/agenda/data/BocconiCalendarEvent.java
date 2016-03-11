package com.thermatk.android.meatb.agenda.data;

import com.github.tibolte.agendacalendarview.models.CalendarEvent;
import com.github.tibolte.agendacalendarview.models.IDayItem;
import com.github.tibolte.agendacalendarview.models.IWeekItem;

import java.util.Calendar;

import io.realm.RealmObject;

public class BocconiCalendarEvent implements CalendarEvent {


    /**
     * Id of the event.
     */
    private long mId;
    /**
     * Color to be displayed in the agenda view.
     */
    private int mColor;
    /**
     * Title of the event.
     */
    private String mTitle;
    /**
     * Description of the event.
     */
    private String mDescription;
    /**
     * Where the event takes place.
     */
    private String mLocation;
    /**
     * Calendar instance helping sorting the events per section in the agenda view.
     */
    private Calendar mInstanceDay;
    /**
     * Start time of the event.
     */
    private Calendar mStartTime;
    /**
     * End time of the event.
     */
    private Calendar mEndTime;
    /**
     * Indicates if the event lasts all day.
     */
    private boolean mAllDay;
    /**
     * Tells if this BaseCalendarEvent instance is used as a placeholder in the agenda view, if there's
     * no event for that day.
     */
    private boolean mPlaceHolder;
    /**
     * Duration of the event.
     */
    private String mDuration;
    /**
     * References to a DayItem instance for that event, used to link interaction between the
     * calendar view and the agenda view.
     */
    private ACVDay mDayReference;
    /**
     * References to a WeekItem instance for that event, used to link interaction between the
     * calendar view and the agenda view.
     */
    private ACVWeek mWeekReference;

    private String mDate;

    public long getmId() {
        return mId;
    }

    public void setmId(long mId) {
        this.mId = mId;
    }

    public int getmColor() {
        return mColor;
    }

    public void setmColor(int mColor) {
        this.mColor = mColor;
    }

    public String getmTitle() {
        return mTitle;
    }

    public void setmTitle(String mTitle) {
        this.mTitle = mTitle;
    }

    public String getmDescription() {
        return mDescription;
    }

    public void setmDescription(String mDescription) {
        this.mDescription = mDescription;
    }

    public String getmLocation() {
        return mLocation;
    }

    public void setmLocation(String mLocation) {
        this.mLocation = mLocation;
    }

    public Calendar getmInstanceDay() {
        return mInstanceDay;
    }

    public void setmInstanceDay(Calendar mInstanceDay) {
        this.mInstanceDay = mInstanceDay;
    }

    public Calendar getmStartTime() {
        return mStartTime;
    }

    public void setmStartTime(Calendar mStartTime) {
        this.mStartTime = mStartTime;
    }

    public Calendar getmEndTime() {
        return mEndTime;
    }

    public void setmEndTime(Calendar mEndTime) {
        this.mEndTime = mEndTime;
    }

    public boolean ismAllDay() {
        return mAllDay;
    }

    public void setmAllDay(boolean mAllDay) {
        this.mAllDay = mAllDay;
    }

    public boolean ismPlaceHolder() {
        return mPlaceHolder;
    }

    public void setmPlaceHolder(boolean mPlaceHolder) {
        this.mPlaceHolder = mPlaceHolder;
    }

    public String getmDuration() {
        return mDuration;
    }

    public void setmDuration(String mDuration) {
        this.mDuration = mDuration;
    }

    public IDayItem getmDayReference() {
        return mDayReference;
    }

    public void setmDayReference(IDayItem mDayReference) {
        this.mDayReference = (ACVDay) mDayReference;
    }

    public IWeekItem getmWeekReference() {
        return mWeekReference;
    }

    public void setmWeekReference(IWeekItem mWeekReference) {
        this.mWeekReference = (ACVWeek) mWeekReference;
    }

    public String getmDate() {
        return mDate;
    }

    public void setmDate(String mDate) {
        this.mDate = mDate;
    }
    // region Constructors

    public BocconiCalendarEvent(long id, int color, String title, String description, String location, long dateStart, long dateEnd, int allDay, String duration, String dateText) {

        this.mId = id;
        this.mColor = color;
        this.mAllDay = (allDay == 1);
        this.mDuration = duration;
        this.mTitle = title;
        this.mDescription = description;
        this.mLocation = location;

        this.mStartTime = Calendar.getInstance();
        this.mStartTime.setTimeInMillis(dateStart);
        this.mEndTime = Calendar.getInstance();
        this.mEndTime.setTimeInMillis(dateEnd);
        this.mDate = dateText;
    }

    public BocconiCalendarEvent(String title, String description, String location, int color, Calendar startTime, Calendar endTime, boolean allDay, String dateText) {

        this.mTitle = title;
        this.mDescription = description;
        this.mLocation = location;
        this.mColor = color;
        this.mStartTime = startTime;
        this.mEndTime = endTime;
        this.mAllDay = allDay;
        this.mDate = dateText;
    }

    public BocconiCalendarEvent(BocconiCalendarEvent calendarEvent) {

        this.mId = calendarEvent.getId();
        this.mColor = calendarEvent.getColor();
        this.mAllDay = calendarEvent.isAllDay();
        this.mDuration = calendarEvent.getDuration();
        this.mTitle = calendarEvent.getTitle();
        this.mDescription = calendarEvent.getDescription();
        this.mLocation = calendarEvent.getLocation();
        this.mStartTime = calendarEvent.getStartTime();
        this.mEndTime = calendarEvent.getEndTime();
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

    public int getColor() {
        return mColor;
    }

    public void setColor(int mColor) {
        this.mColor = mColor;
    }

    public String getDescription() {
        return mDescription;
    }

    public boolean isAllDay() {
        return mAllDay;
    }

    public void setAllDay(boolean allDay) {
        this.mAllDay = allDay;
    }

    public void setDescription(String mDescription) {
        this.mDescription = mDescription;
    }

    public Calendar getInstanceDay() {
        return mInstanceDay;
    }

    public void setInstanceDay(Calendar mInstanceDay) {
        this.mInstanceDay = mInstanceDay;
        this.mInstanceDay.set(Calendar.HOUR, 0);
        this.mInstanceDay.set(Calendar.MINUTE, 0);
        this.mInstanceDay.set(Calendar.SECOND, 0);
        this.mInstanceDay.set(Calendar.MILLISECOND, 0);
        this.mInstanceDay.set(Calendar.AM_PM, 0);
    }

    public Calendar getEndTime() {
        return mEndTime;
    }

    public void setEndTime(Calendar mEndTime) {
        this.mEndTime = mEndTime;
    }
    public void setPlaceholder(boolean placeholder) {
        mPlaceHolder = placeholder;
    }
    public boolean isPlaceholder() {
        return mPlaceHolder;
    }

    public long getId() {
        return mId;
    }

    public void setId(long mId) {
        this.mId = mId;
    }

    public String getLocation() {
        return mLocation;
    }

    public void setLocation(String mLocation) {
        this.mLocation = mLocation;
    }

    public Calendar getStartTime() {
        return mStartTime;
    }

    public void setStartTime(Calendar mStartTime) {
        this.mStartTime = mStartTime;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String mTitle) {
        this.mTitle = mTitle;
    }

    public String getDuration() {
        return mDuration;
    }

    public void setDuration(String duration) {
        this.mDuration = duration;
    }

    public boolean isPlaceHolder() {
        return mPlaceHolder;
    }

    public void setPlaceHolder(boolean mPlaceHolder) {
        this.mPlaceHolder = mPlaceHolder;
    }

    public IDayItem getDayReference() {
        return mDayReference;
    }

    public void setDayReference(IDayItem mDayReference) {
        this.mDayReference = (ACVDay) mDayReference;
    }

    public IWeekItem getWeekReference() {
        return mWeekReference;
    }

    public void setWeekReference(IWeekItem mWeekReference) {
        this.mWeekReference = (ACVWeek) mWeekReference;
    }

    // endregion

    @Override
    public String toString() {
        return "BaseCalendarEvent{"
                + "title='"
                + mTitle
                + ", instanceDay= "
                + mInstanceDay.getTime()
                + "}";
    }

    @Override
    public CalendarEvent copy() {
        return new BocconiCalendarEvent(this);
    }

    // endregion
}