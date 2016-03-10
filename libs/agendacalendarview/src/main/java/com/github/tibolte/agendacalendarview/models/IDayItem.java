package com.github.tibolte.agendacalendarview.models;

import java.util.Date;

public interface IDayItem {

    // region Getters/Setters

    Date getDate();

    void setDate(Date date);

    public int getValue();

    public void setValue(int value);

    public boolean isToday();

    public void setToday(boolean today);

    public boolean isSelected();

    public void setSelected(boolean selected);

    public boolean isFirstDayOfTheMonth();

    public void setFirstDayOfTheMonth(boolean firstDayOfTheMonth);

    public String getMonth();

    void setMonth(String month);

    int getDayOftheWeek();

    void setDayOftheWeek(int mDayOftheWeek);

    // endregion

    String toString();

}
