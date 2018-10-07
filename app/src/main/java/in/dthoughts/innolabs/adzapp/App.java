package in.dthoughts.innolabs.adzapp;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;
import android.util.Log;

import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;

import cat.ereza.customactivityoncrash.config.CaocConfig;

public class App extends Application {
    public static final String CHANNELID = "AdzAppServiceChannel";
//    FirebaseFirestore db;
    @Override
    public void onCreate() {
        super.onCreate();
//        FirebaseApp.initializeApp(this);
//        db = FirebaseFirestore.getInstance();
//        try{
//            FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
//                    .setTimestampsInSnapshotsEnabled(true)
//                    .build();
//            db.setFirestoreSettings(settings);
//        }catch (IllegalStateException exp){
//            Log.d("docc12","illegal state exp in fireStore settings");
//        }

        createNotificationChannel();

        //for custom app crashing
        CaocConfig.Builder.create()
                .backgroundMode(CaocConfig.BACKGROUND_MODE_SILENT)
                .enabled(true) //default: true
                .showErrorDetails(true) //default: true
                .showRestartButton(true) //default: true
                .logErrorOnRestart(false) //default: true
                .trackActivities(true) //default: false
                .minTimeBetweenCrashesMs(2000) //default: 3000
                .apply();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNELID,
                    "AdzApp service channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
        }
    }
}
