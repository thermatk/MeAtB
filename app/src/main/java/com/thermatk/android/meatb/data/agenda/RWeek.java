package com.thermatk.android.meatb.data.agenda;

import com.github.tibolte.agendacalendarview.models.IDayItem;
import com.github.tibolte.agendacalendarview.models.IWeekItem;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import io.realm.RealmList;
import io.realm.RealmObject;

public class RWeek extends RealmObject implements IWeekItem {
    public int getWeekInYear() {
        return weekInYear;
    }

    public void setWeekInYear(int weekInYear) {
        this.weekInYear = weekInYear;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }


    public List<IDayItem> getDayItems() {
        return new ArrayList<IDayItem>(dayItemsR);
    }

    public RealmList<RDay> getDayItemsR() {
        return dayItemsR;
    }
    @Override
    public void setDayItems(List<IDayItem> dayItems) {
        this.dayItemsR = new RealmList<>((RDay[])dayItems.toArray());

    }

    @Override
    public IWeekItem copy() {
        return new RWeek(this);
    }

    public void setDayItemsR(RealmList<RDay> dayItemsR) {
        this.dayItemsR = dayItemsR;
    }

    private int weekInYear;
    private int year;
    private int month;
    private Date date;
    private String label;

    private RealmList<RDay> dayItemsR;

    public RWeek (RWeek original) {

        this.weekInYear = original.getWeekInYear();
        this.year = original.getYear();
        this.month = original.getMonth();
        this.date = original.getDate();
        this.label = original.getLabel();
        this.dayItemsR = original.getDayItemsR();
    }
    public RWeek () {

    }
}
