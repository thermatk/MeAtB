package com.thermatk.android.meatb.agenda;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.thermatk.android.meatb.R;
import com.thermatk.android.meatb.viewholders.AgendaEventViewHolder;

import java.util.List;

import eu.davidea.flexibleadapter.FlexibleAdapter;
import eu.davidea.flexibleadapter.items.IFlexible;
import eu.davidea.flexibleadapter.items.ISectionable;

public class EventItem implements IFlexible<AgendaEventViewHolder>,ISectionable<AgendaEventViewHolder, HeaderItem> {
    // fields
    private HeaderItem header;
    private String id;
    private String title;
    private String subtitle;

    // std getters/setters
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public String getSubtitle() {
        return subtitle;
    }
    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }
    public HeaderItem getHeader() {
        return header;
    }
    public void setHeader(HeaderItem header) {
        this.header = header;
    }

	/* IFLEXIBLE INTERFACE DUMMY METHODS */
    public boolean isEnabled() { return true; }
    public void setEnabled(boolean enabled) {}
    public boolean isHidden() { return false; }
    public void setHidden(boolean hidden) {}
    public boolean isSelectable() { return false; }
    public void setSelectable(boolean selectable) {}
    public boolean isDraggable() { return false; }
    public void setDraggable(boolean draggable) {}
    public boolean isSwipeable() { return false; }
    public void setSwipeable(boolean swipeable) {}

    // constructors
    public EventItem() {
    }

    public EventItem(String id) {
        super();
        this.id = id;
    }

    public EventItem(String id, HeaderItem header) {
        this(id);
        this.header = header;
    }

    // methods
    public int getLayoutRes() {
        return R.layout.recycler_agenda_event_item;
    }

    public AgendaEventViewHolder createViewHolder(FlexibleAdapter adapter, LayoutInflater inflater, ViewGroup parent) {
        return new AgendaEventViewHolder(inflater.inflate(getLayoutRes(), parent, false), adapter);
    }

    public void bindViewHolder(final FlexibleAdapter adapter, AgendaEventViewHolder holder, int position, List payloads) {
        setSubtitle(getId());
        setSubtitle(getSubtitle() + (getHeader() != null ? " - " + getHeader().getId() : ""));

        holder.mTitle.setText(getTitle());
        holder.mSubtitle.setText(getSubtitle());
    }

    // override methods
    @Override
    public boolean equals(Object inObject) {
        if (inObject instanceof EventItem) {
            EventItem inItem = (EventItem) inObject;
            return this.id.equals(inItem.id);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
