package com.thermatk.android.meatb.agenda;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.thermatk.android.meatb.R;
import com.thermatk.android.meatb.viewholders.AgendaHeaderViewHolder;

import java.util.List;

import eu.davidea.flexibleadapter.FlexibleAdapter;
import eu.davidea.flexibleadapter.items.IHeader;
import eu.davidea.flexibleadapter.items.ISectionable;

public class HeaderItem implements IHeader<AgendaHeaderViewHolder> {
    //fields
    private boolean hidden = false;
    private String id;
    private String title;
    private String subtitle;

    // std getters/setters
    public boolean isHidden() {
        return hidden;
    }
    public void setHidden(boolean hidden) {
        this.hidden = hidden;
    }
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
    public HeaderItem() {
    }

    public HeaderItem(String id) {
        setHidden(true);
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
            holder.mTitle.setText(getTitle());
        }
        List<ISectionable> sectionableList = adapter.getSectionItems(this);
        String subTitle = getSubtitle() + ": " +(sectionableList.isEmpty() ? "no events" :
                sectionableList.size() + " events");
        holder.mSubtitle.setText(subTitle);
    }

    // override methods
    @Override
    public boolean equals(Object inObject) {
        if (inObject instanceof HeaderItem) {
            HeaderItem inItem = (HeaderItem) inObject;
            return this.getId().equals(inItem.getId());
        }
        return false;
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
