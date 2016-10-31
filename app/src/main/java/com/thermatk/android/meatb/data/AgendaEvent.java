package com.thermatk.android.meatb.data;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.thermatk.android.meatb.R;
import com.thermatk.android.meatb.viewholders.AgendaEventViewHolder;

import java.util.Date;
import java.util.List;

import eu.davidea.flexibleadapter.FlexibleAdapter;
import eu.davidea.flexibleadapter.items.IFlexible;
import eu.davidea.flexibleadapter.items.ISectionable;
import io.realm.RealmObject;

public class AgendaEvent extends RealmObject implements IFlexible<AgendaEventViewHolder>,ISectionable<AgendaEventViewHolder, EventDay> {

    private Date date_end;
    private Date date_start;
    private long date_end_long;
    private long date_start_long;
    private String duration;
    private String description;
    private long id;
    private String supertitle;
    private String eventString;
    private String title;
    private long type;
    private long courseId;
    private long calendarId;

    private EventDay header;

    public EventDay getHeader() {
        return header;
    }
    public void setHeader(EventDay header) {
        this.header = header;
    }
    public long getCalendarId() {
        return calendarId;
    }
    public void setCalendarId(long calendarId) {
        this.calendarId = calendarId;
    }
    public String getDuration() {
        return duration;
    }
    public void setDuration(String duration) {
        this.duration = duration;
    }
    public String getEventString() {
        return eventString;
    }
    public long getDate_end_long() {
        return date_end_long;
    }
    public void setDate_end_long(long date_end_long) {
        this.date_end_long = date_end_long;
    }
    public long getDate_start_long() {
        return date_start_long;
    }
    public void setDate_start_long(long date_start_long) {
        this.date_start_long = date_start_long;
    }
    public void setEventString(String eventString) {
        this.eventString = eventString;
    }
    public long getCourseId() {
        return courseId;
    }
    public void setCourseId(long courseId) {
        this.courseId = courseId;
    }
    public void setDate_end(Date date_end){
        this.date_end = date_end;
    }
    public Date getDate_end(){
        return this.date_end;
    }
    public void setDate_start(Date date_start){
        this.date_start = date_start;
    }
    public Date getDate_start(){
        return this.date_start;
    }
    public void setDescription(String description){
        this.description = description;
    }
    public String getDescription(){
        return this.description;
    }
    public void setId(long id){
        this.id = id;
    }
    public long getId(){
        return this.id;
    }
    public void setSupertitle(String supertitle){
        this.supertitle = supertitle;
    }
    public String getSupertitle(){
        return this.supertitle;
    }
    public void setTitle(String title){
        this.title = title;
    }
    public String getTitle(){
        return this.title;
    }
    public void setType(long type){
        this.type = type;
    }
    public long getType(){
        return this.type;
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
    public AgendaEvent() {
    }

    public AgendaEvent(long id) {
        super();
        this.id = id;
    }

    public AgendaEvent(long id, EventDay header) {
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

        holder.mTitle.setText(getTitle());
        holder.mLocation.setText(getSupertitle());
        holder.mTime.setText(getDuration());
    }

    // override methods
    @Override
    public boolean equals(Object inObject) {
        if (inObject instanceof AgendaEvent) {
            AgendaEvent inItem = (AgendaEvent) inObject;
            return (this.id == inItem.id);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return (int) id;
    }
}


