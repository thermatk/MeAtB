package com.thermatk.android.meatb.data;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.thermatk.android.meatb.R;
import com.thermatk.android.meatb.viewholders.AgendaHeaderViewHolder;

import java.util.Date;
import java.util.List;

import eu.davidea.flexibleadapter.FlexibleAdapter;
import eu.davidea.flexibleadapter.items.IHeader;
import eu.davidea.flexibleadapter.items.ISectionable;
import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class EventDay extends RealmObject implements IHeader<AgendaHeaderViewHolder> {

    @PrimaryKey
    private long id;
    private long dateLong;
    private Date date;
    private String dateString;
    private long lastUpdated;
    private String dayString;
    private String weekdayString;
    private RealmList<AgendaEvent> agendaEvents;

    private boolean hidden = true;

    public boolean isHidden() {
        return hidden;
    }

    public void setHidden(boolean hidden) {
        this.hidden = hidden;
    }

    public String getDateString() {
        return dateString;
    }

    public void setDateString(String dateString) {
        this.dateString = dateString;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getDayString() {
        return dayString;
    }

    public void setDayString(String dayString) {
        this.dayString = dayString;
    }

    public long getDateLong() {
        return dateLong;
    }

    public void setDateLong(long dateLong) {
        this.dateLong = dateLong;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public RealmList<AgendaEvent> getAgendaEvents() {
        return agendaEvents;
    }

    public void setAgendaEvents(RealmList<AgendaEvent> agendaEvents) {
        this.agendaEvents = agendaEvents;
    }

    public String getWeekdayString() {
        return weekdayString;
    }

    public void setWeekdayString(String weekdayString) {
        this.weekdayString = weekdayString;
    }

    public long getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(long lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    /* IFLEXIBLE INTERFACE DUMMY METHODS */
    public boolean isEnabled() { return true; }
    public void setEnabled(boolean enabled) { }
    public boolean isSelectable() { return false; }
    public void setSelectable(boolean selectable) { }
    public boolean isDraggable() { return false; }
    public void setDraggable(boolean draggable) { }
    public boolean isSwipeable() { return false; }
    public void setSwipeable(boolean swipeable) { }

    // constructors
    public EventDay() {
    }

    public EventDay(long id) {
        this.id = id;
    }

    // methods
    public int getLayoutRes() {
        return R.layout.recycler_agenda_header_item;
    }

    public AgendaHeaderViewHolder createViewHolder(FlexibleAdapter adapter, LayoutInflater inflater, ViewGroup parent) {
        return new AgendaHeaderViewHolder(inflater.inflate(getLayoutRes(), parent, false), adapter);
    }

    @SuppressWarnings("unchecked")
    public void bindViewHolder(FlexibleAdapter adapter, AgendaHeaderViewHolder holder, int position, List payloads) {
        if (payloads.size() > 0) {
            //Log.i(this.getClass().getSimpleName(), "HeaderItem " + id + " Payload " + payloads);
        } else {
            holder.mTitle.setText(getDateString());
        }
        List<ISectionable> sectionableList = adapter.getSectionItems(this);
        String subTitle = getWeekdayString() + ": " +(sectionableList.isEmpty() ? "no events" :
                sectionableList.size() + " events");
        holder.mSubtitle.setText(subTitle);
    }

    // override methods
    @Override
    public boolean equals(Object inObject) {
        if (inObject instanceof EventDay) {
            EventDay inItem = (EventDay) inObject;
            return (this.getId() == inItem.getId());
        }
        return false;
    }

    @Override
    public int hashCode() {
        // it shouldn't be that long
        return (int) id;
    }
}
