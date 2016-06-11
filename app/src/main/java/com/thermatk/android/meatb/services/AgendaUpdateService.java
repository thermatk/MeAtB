package com.thermatk.android.meatb.services;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.thermatk.android.meatb.LogConst;
import com.thermatk.android.meatb.helpers.JobHelper;
import com.thermatk.android.meatb.helpers.ServiceHelper;

public class AgendaUpdateService extends IntentService {

    public AgendaUpdateService(String name) {
        super(name);
    }

    public AgendaUpdateService() {
        super("AgendaUpdateService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if(ServiceHelper.isAgendaServiceRunning(getApplicationContext())) {
            Log.d(LogConst.LOG, "AgendaUpdateService already running");
            return;
        }
        ServiceHelper.setAgendaServiceRunning(getApplicationContext(),true);
        Log.d(LogConst.LOG, "AgendaUpdateService started");
        JobHelper.runAgendaUpdate(getApplicationContext());
        Log.d(LogConst.LOG, "AgendaUpdateService ended");
        ServiceHelper.setAgendaServiceRunning(getApplicationContext(),false);
    }

}
