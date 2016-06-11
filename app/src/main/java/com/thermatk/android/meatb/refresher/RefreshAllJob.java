package com.thermatk.android.meatb.refresher;

import android.support.annotation.NonNull;

import com.evernote.android.job.Job;
import com.evernote.android.job.JobRequest;

public class RefreshAllJob extends Job {

    public static final String TAG = "MeAtBRefreshJob";

    @Override
    @NonNull
    protected Result onRunJob(Params params) {
        // run your job
        return Result.SUCCESS;
    }

    public static void scheduleJob() {
        new JobRequest.Builder(RefreshAllJob.TAG)
                .setExecutionWindow(30_000L, 40_000L)
                .build()
                .schedule();
    }


}
