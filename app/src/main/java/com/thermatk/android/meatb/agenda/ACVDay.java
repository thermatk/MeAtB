package com.thermatk.android.meatb.agenda;

import com.github.tibolte.agendacalendarview.models.IDayItem;
import com.thermatk.android.meatb.data.agenda.RDay;

import java.util.Date;

/**
 * Created by thermatk on 11.03.16.
 */
public class ACVDay implements IDayItem {

    private Date mDate;
    private int mValue;
    private int mDayOfTheWeek;
    private boolean mFirstDayOfTheMonth;
    private boolean mSelected;
    private String mMonth;

    // region Constructor

    public ACVDay(Date date, int value, boolean today, String month) {
        this.mDate = date;
        this.mValue = value;
        this.mMonth = month;
    }
    // only for cleanDay
    public ACVDay() {

    }
    public ACVDay(ACVDay original) {

        this.mDate = original.getDate();
        this.mValue = original.getValue();
        this.mDayOfTheWeek = original.getDayOfTheWeek();
        this.mFirstDayOfTheMonth = original.isFirstDayOfTheMonth();
        this.mSelected = original.isSelected();
        this.mMonth = original.getMonth();
    }
    public ACVDay(RDay persistent) {
        this.mDate = persistent.getDate();
        this.mValue = persistent.getValue();
        this.mDayOfTheWeek = persistent.getDayOfTheWeek();
        this.mFirstDayOfTheMonth = persistent.isFirstDayOfTheMonth();
        this.mSelected = persistent.isSelected();
        this.mMonth = persistent.getMonth();
    }
    // endregion

    // region Getters/Setters

    public Date getDate() {
        return mDate;
    }

    public void setDate(Date date) {
        this.mDate = date;
    }

    public int getValue() {
        return mValue;
    }

    public void setValue(int value) {
        this.mValue = value;
    }

    public boolean isSelected() {
        return mSelected;
    }

    public void setSelected(boolean selected) {
        this.mSelected = selected;
    }

    public boolean isFirstDayOfTheMonth() {
        return mFirstDayOfTheMonth;
    }

    public void setFirstDayOfTheMonth(boolean firstDayOfTheMonth) {
        this.mFirstDayOfTheMonth = firstDayOfTheMonth;
    }

    public String getMonth() {
        return mMonth;
    }

    public void setMonth(String month) {
        this.mMonth = month;
    }

    public int getDayOfTheWeek() {
        return mDayOfTheWeek;
    }

    public void setDayOfTheWeek(int mDayOftheWeek) {
        this.mDayOfTheWeek = mDayOftheWeek;
    }


    @Override
    public IDayItem copy() {
        return new ACVDay(this);
    }
}
