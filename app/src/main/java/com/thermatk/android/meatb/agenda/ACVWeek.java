package com.thermatk.android.meatb.agenda;

import com.github.tibolte.agendacalendarview.models.IDayItem;
import com.github.tibolte.agendacalendarview.models.IWeekItem;
import com.thermatk.android.meatb.data.agenda.RWeek;

import java.util.Date;
import java.util.List;

/**
 * Created by thermatk on 11.03.16.
 */
public class ACVWeek implements IWeekItem {

    private int mWeekInYear;
    private int mYear;
    private int mMonth;
    private Date mDate;
    private String mLabel;
    private List<IDayItem> mDayItems;

    // region Constructor

    public ACVWeek(int weekInYear, int year, Date date, String label, int month) {
        this.mWeekInYear = weekInYear;
        this.mYear = year;
        this.mDate = date;
        this.mLabel = label;
        this.mMonth = month;
    }
    public ACVWeek(ACVWeek original) {
        this.mWeekInYear = original.getWeekInYear();
        this.mYear = original.getYear();
        this.mMonth = original.getMonth();
        this.mDate = original.getDate();
        this.mLabel = original.getLabel();
        this.mDayItems = original.getDayItems();
    }

    public ACVWeek(RWeek persistent) {
        this.mWeekInYear = persistent.getWeekInYear();
        this.mYear = persistent.getYear();
        this.mMonth = persistent.getMonth();
        this.mDate = persistent.getDate();
        this.mLabel = persistent.getLabel();
        /// except for days list
    }
    public ACVWeek(){

    }

    // endregion

    // region Getters/Setters

    public int getWeekInYear() {
        return mWeekInYear;
    }

    public void setWeekInYear(int weekInYear) {
        this.mWeekInYear = weekInYear;
    }

    public int getYear() {
        return mYear;
    }

    public void setYear(int year) {
        this.mYear = year;
    }

    public int getMonth() {
        return mMonth;
    }

    public void setMonth(int month) {
        this.mMonth = month;
    }

    public Date getDate() {
        return mDate;
    }

    public void setDate(Date date) {
        this.mDate = date;
    }

    public String getLabel() {
        return mLabel;
    }

    public void setLabel(String label) {
        this.mLabel = label;
    }

    public List<IDayItem> getDayItems() {
        return mDayItems;
    }

    public void setDayItems(List<IDayItem> dayItems) {
        this.mDayItems = dayItems;
    }

    @Override
    public IWeekItem copy() {
        return new ACVWeek(this);
    }

    // endregion

    @Override
    public String toString() {
        return "WeekItem{"
                + "label='"
                + mLabel
                + '\''
                + ", weekInYear="
                + mWeekInYear
                + ", year="
                + mYear
                + '}';
    }
}
