package com.abi.whatstrack.util;

import android.annotation.TargetApi;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;

public class Util {

    @TargetApi(23)
    public static void scheduleJobService(Context context,Class<?> targetClass) {
        ComponentName serviceComponent = new ComponentName(context,targetClass);
        JobInfo.Builder builder = new JobInfo.Builder(0, serviceComponent);
        builder.setMinimumLatency(1 * 1000); // wait at least
        builder.setOverrideDeadline(3 * 1000); // maximum delay
        builder.setPersisted(true);
        //builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_UNMETERED); // require unmetered network
        //builder.setRequiresDeviceIdle(true); // device should be idle
        //builder.setRequiresCharging(false); // we don't care if the device is charging or not
        JobScheduler jobScheduler = context.getSystemService(JobScheduler.class);
        jobScheduler.schedule(builder.build());
    }

    @TargetApi(23)
    public static void scheduleJobServiceCheck(Context context,Class<?> targetClass) {
        ComponentName serviceComponent = new ComponentName(context,targetClass);
        JobInfo.Builder builder = new JobInfo.Builder(0, serviceComponent);
        builder.setMinimumLatency(0 * 1000); // wait at least
        builder.setOverrideDeadline(0); // maximum delay
        //builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_UNMETERED); // require unmetered network
        //builder.setRequiresDeviceIdle(true); // device should be idle
        //builder.setRequiresCharging(false); // we don't care if the device is charging or not
        JobScheduler jobScheduler = context.getSystemService(JobScheduler.class);
        jobScheduler.schedule(builder.build());
        builder.build();
    }

    @TargetApi(23)
    public static void reScheduleJobService(Context context,Class<?> targetClass) {
        ComponentName serviceComponent = new ComponentName(context,targetClass);
        JobInfo.Builder builder = new JobInfo.Builder(0, serviceComponent);
        builder.setMinimumLatency(0 * 1000); // wait at least
        builder.setOverrideDeadline(0 * 1000); // maximum delay
        builder.setPeriodic(10000);
        builder.setPersisted(true);
        //builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_UNMETERED); // require unmetered network
        //builder.setRequiresDeviceIdle(true); // device should be idle
        //builder.setRequiresCharging(false); // we don't care if the device is charging or not
        JobScheduler jobScheduler = context.getSystemService(JobScheduler.class);
        jobScheduler.schedule(builder.build());
    }
}
