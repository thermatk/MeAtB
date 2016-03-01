package com.thermatk.android.meatb;

import io.realm.RealmObject;
import io.realm.annotations.Required;

public class InitData extends RealmObject {
    @Required
    private String firstname;
    private String lastname;


    private String photo_url;
    private String carreerTitle;
    private String carreerDescription;
    private String carreerNotes;
    private long carreerId;
    private String careerDateStart;

    private long lastUpdated;
    private String rawData;


    public String getCarreerNotes() {
        return carreerNotes;
    }

    public void setCarreerNotes(String carreerNotes) {
        this.carreerNotes = carreerNotes;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getPhoto_url() {
        return photo_url;
    }

    public void setPhoto_url(String photo_url) {
        this.photo_url = photo_url;
    }

    public String getCarreerTitle() {
        return carreerTitle;
    }

    public void setCarreerTitle(String carreerTitle) {
        this.carreerTitle = carreerTitle;
    }

    public String getCarreerDescription() {
        return carreerDescription;
    }

    public void setCarreerDescription(String carreerDescription) {
        this.carreerDescription = carreerDescription;
    }

    public long getCarreerId() {
        return carreerId;
    }

    public void setCarreerId(long carreerId) {
        this.carreerId = carreerId;
    }

    public String getCareerDateStart() {
        return careerDateStart;
    }

    public void setCareerDateStart(String careerDateStart) {
        this.careerDateStart = careerDateStart;
    }

    public long getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(long lastUpdate) {
        this.lastUpdated = lastUpdate;
    }

    public String getRawData() {
        return rawData;
    }

    public void setRawData(String rawData) {
        this.rawData = rawData;
    }
}
