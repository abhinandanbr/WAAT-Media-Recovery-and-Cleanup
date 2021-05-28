package com.abi.whatstrack;

import android.annotation.TargetApi;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Intent;

import static com.abi.whatstrack.Util.reScheduleJobService;


@TargetApi(21)
public class JobServiceJob extends JobService {
    public JobServiceJob() {
    }

    @TargetApi(26)
    @Override
    public boolean onStartJob(JobParameters params) {
//        Intent service = new Intent(getApplicationContext(), LowMem.class);
//        getApplicationContext().startService(service);
        startService(new Intent(getApplicationContext(),LowMem.class));
//        startForegroundService(new Intent(getApplicationContext(),Tracking_Service.class));
        reScheduleJobService(getApplicationContext(),JobServiceJob.class); // reschedule the job
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return true;
    }
}
