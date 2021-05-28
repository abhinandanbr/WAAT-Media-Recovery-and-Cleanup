package com.abi.whatstrack;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class Boot extends BroadcastReceiver {

    public void onReceive(Context context, Intent intent) {

        context.startService(new Intent(context,LowMem.class));
    }
}
