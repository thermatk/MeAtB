package com.thermatk.android.meatb.refresher;

import android.support.annotation.NonNull;
import android.util.Log;

import com.evernote.android.job.Job;
import com.evernote.android.job.JobManager;
import com.evernote.android.job.JobRequest;
import com.thermatk.android.meatb.LogConst;
import com.thermatk.android.meatb.helpers.JobHelper;
import com.thermatk.android.meatb.helpers.NotificationsHelper;

import java.util.concurrent.TimeUnit;

import static com.thermatk.android.meatb.helpers.NotificationsHelper.doNotify;

public class RefreshAllJob extends Job {

    public static final String TAG = "MeAtBRefreshJob";

    @Override
    @NonNull
    protected Result onRunJob(Params params) {
        // run your job
        JobHelper.runInboxUpdate(getContext());
        JobHelper.runAgendaUpdate(getContext()); // untested and TODO: add notifications for agenda updates
        doNotify(getContext(), "me@B updated data!", NotificationsHelper.createNotificationAfterJob());
        return Result.SUCCESS;
    }

    public static void scheduleJob() {
        if(JobManager.instance().getAllJobRequestsForTag(TAG).size() > 0) {
            Log.d(LogConst.LOG,"JOB: ALREADY IN SCHEDULE");
        } else {
            new JobRequest.Builder(RefreshAllJob.TAG)
                    .setPeriodic(TimeUnit.HOURS.toMillis(8))
                    .setRequiredNetworkType(JobRequest.NetworkType.CONNECTED)
                    .setPersisted(true)
                    .build()
                    .schedule();
        }
    }


}
