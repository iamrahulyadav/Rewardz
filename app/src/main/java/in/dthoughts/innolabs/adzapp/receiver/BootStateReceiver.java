package in.dthoughts.innolabs.adzapp.receiver;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import in.dthoughts.innolabs.adzapp.service.DownloadRt;


public class BootStateReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(final Context context, final Intent intent) {
        Log.d("docc11","bootstate receiver called");
        Intent serviceIntent = new Intent(context, DownloadRt.class);
        //context.startService(serviceIntent);
        ContextCompat.startForegroundService(context, serviceIntent);
    }

}
