package com.thermatk.android.meatb.data.agenda;

import com.github.tibolte.agendacalendarview.models.IDayItem;

import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.Ignore;

public class RDay extends RealmObject implements IDayItem {


    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public int getDayOfTheWeek() {
        return dayOfTheWeek;
    }

    public void setDayOfTheWeek(int dayOfTheWeek) {
        this.dayOfTheWeek = dayOfTheWeek;
    }

    @Override
    public IDayItem copy() {
        return new RDay(this);
    }

    public boolean isToday() {
        return today;
    }

    public void setToday(boolean today) {
        this.today = today;
    }

    public boolean isFirstDayOfTheMonth() {
        return firstDayOfTheMonth;
    }

    public void setFirstDayOfTheMonth(boolean firstDayOfTheMonth) {
        this.firstDayOfTheMonth = firstDayOfTheMonth;
    }

    public boolean isSelected() {
        return selectedv;
    }

    public void setSelected(boolean selected) {
        this.selectedv = selected;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    private Date date;
    private int value;
    private int dayOfTheWeek;
    private boolean today;
    private boolean firstDayOfTheMonth;
    private String month;

    @Ignore
    private boolean selectedv;

    public RDay () {

    }
    public RDay(RDay original) {
        this.date = original.getDate();
        this.value = original.getValue();
        this.dayOfTheWeek = original.getDayOfTheWeek();
        this.today = original.isToday();
        this.firstDayOfTheMonth = original.isFirstDayOfTheMonth();
        this.selectedv = original.isSelected();
        this.month = original.getMonth();
    }
}
