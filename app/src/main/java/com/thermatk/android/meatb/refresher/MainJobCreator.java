package com.thermatk.android.meatb.refresher;

import com.evernote.android.job.Job;
import com.evernote.android.job.JobCreator;

public class MainJobCreator implements JobCreator {

    @Override
    public Job create(String tag) {
        switch (tag) {
            case RefreshAllJob.TAG:
                return new RefreshAllJob();
            default:
                return null;
        }
    }
}
