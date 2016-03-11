package com.thermatk.android.meatb.data.agenda;

import com.github.tibolte.agendacalendarview.models.IDayItem;

import java.util.Date;
import java.util.List;

import io.realm.RealmList;
import io.realm.RealmObject;

/**
 * Created by thermatk on 11.03.16.
 */
public class RWeek extends RealmObject {
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

    public RealmList<RDay> getDayItems() {
        return dayItems;
    }

    public void setDayItems(RealmList<RDay> dayItems) {
        this.dayItems = dayItems;
    }

    private int weekInYear;
    private int year;
    private int month;
    private Date date;
    private String label;

    private RealmList<RDay> dayItems;
}
