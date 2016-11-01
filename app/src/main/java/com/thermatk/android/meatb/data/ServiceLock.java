package com.thermatk.android.meatb.data;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class ServiceLock extends RealmObject {
    @PrimaryKey
    private String lockId;
    private long lastUpdated;
    private boolean lock;

    public String getLockId() {
        return lockId;
    }

    public void setLockId(String lockId) {
        this.lockId = lockId;
    }

    public long getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(long lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public boolean isLock() {
        return lock;
    }

    public void setLock(boolean lock) {
        this.lock = lock;
    }

}
