package com.letswecode.harsha.rewardz;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

import com.letswecode.harsha.rewardz.receiver.ConnectivityReceiver;


public class App extends Application {
    public static final String CHANNELID = "AdzAppServiceChannel";
    private static App mInstance;
    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        createNotificationChannel();
    }

    public static synchronized App getInstance() {
        return mInstance;
    }

    public void setConnectivityListener(ConnectivityReceiver.ConnectivityReceiverListener listener) {
        ConnectivityReceiver.connectivityReceiverListener = listener;
    }

    private void createNotificationChannel() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNELID,
                    "Rewardz service channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
        }
    }
}
