package com.letswecode.harsha.rewardz.receiver;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.ContextCompat;

import com.letswecode.harsha.rewardz.service.DownloadRt;


public class BootStateReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(final Context context, final Intent intent) {
        Intent serviceIntent = new Intent(context, DownloadRt.class);
        //context.startService(serviceIntent);
        ContextCompat.startForegroundService(context,serviceIntent);
    }

}
