package com.thermatk.android.meatb.data;

import java.util.Date;

import io.realm.RealmObject;

public class AgendaEvent extends RealmObject {

    private Date date_end;
    private Date date_start;
    private String description;
    private long id;
    private String supertitle;
    private String eventString;
    private String title;
    private long type;
    private long courseId;


    public String getEventString() {
        return eventString;
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
}


