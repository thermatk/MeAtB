package com.thermatk.android.meatb.data;

import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by thermatk on 25.04.16.
 */
public class InboxMessage extends RealmObject {
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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

    public long getInternalId() {
        return internalId;
    }

    public void setInternalId(long internalId) {
        this.internalId = internalId;
    }

    public boolean isFavorite() {
        return isFavorite;
    }

    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
    }

    public boolean isFeatured() {
        return isFeatured;
    }

    public void setFeatured(boolean featured) {
        isFeatured = featured;
    }

    public boolean isUnread() {
        return isUnread;
    }

    public void setUnread(boolean unread) {
        isUnread = unread;
    }

    public String getSupertitle() {
        return supertitle;
    }

    public void setSupertitle(String supertitle) {
        this.supertitle = supertitle;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public long getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(long lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isDownloadedFull() {
        return downloadedFull;
    }

    public void setDownloadedFull(boolean downloadedFull) {
        this.downloadedFull = downloadedFull;
    }

    @PrimaryKey
    private long id;
    private long dateLong;
    private Date date;
    private long internalId;
    private boolean isFavorite;
    private boolean isFeatured;
    private boolean isUnread;
    private String supertitle;
    private String description;
    private String title;

    private long lastUpdated;
    private boolean downloadedFull;
}
