package com.thermatk.android.meatb.services;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.thermatk.android.meatb.LogConst;
import com.thermatk.android.meatb.helpers.JobHelper;
import com.thermatk.android.meatb.helpers.ServiceHelper;

public class InboxUpdateService extends IntentService {

    public InboxUpdateService() {
        super("InboxUpdateService");
    }

    public InboxUpdateService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        if(ServiceHelper.isInboxServiceRunning()) {
            Log.d(LogConst.LOG, "InboxUpdateService already running");
            return;
        }
        ServiceHelper.setInboxServiceRunning(true);
        Log.d(LogConst.LOG, "InboxUpdateService started");
        JobHelper.runInboxUpdate(getApplicationContext());
        Log.d(LogConst.LOG, "InboxUpdateService ended");
        ServiceHelper.setInboxServiceRunning(false);
    }
}
