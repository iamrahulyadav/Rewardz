package com.letswecode.harsha.rewardz.service;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.letswecode.harsha.rewardz.MainActivity;
import com.letswecode.harsha.rewardz.R;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

import static com.letswecode.harsha.rewardz.App.CHANNELID;

public class DownloadRt extends Service {

    FirebaseFirestore db;
    FirebaseUser user;
    FirebaseStorage storage;
    public String ringtoneDownloadUrl;
    public boolean downlaoded_for_today = true;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("FGservice", "My foreground service onCreate().");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if(downlaoded_for_today){
            stopSelf();
        }

        Log.d("FGservice", "Start foreground service.");

        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,123, notificationIntent, 0);
       Notification notification = new NotificationCompat.Builder(this, CHANNELID)
               .setContentTitle("Rewardz")
               .setContentText("Looking for new Ring tones")
               .setSmallIcon(R.mipmap.ic_launcher_round)
               .setContentIntent(pendingIntent)
               .build();
       startForeground(1, notification);



         storage = FirebaseStorage.getInstance();

//TODO:get ringtones from firebase storage and save in user device. --FINISHED
//TODO: dont download the songs ererytime take boolean in shared pref and check if they r downlaoded for today the stop the service

        //code to create a folder
        if (android.os.Environment.getExternalStorageState().equals(
                android.os.Environment.MEDIA_MOUNTED)) {

                File RewardzRingtoneFolder = new File(Environment.getExternalStorageDirectory() + File.separator
                        + getString(R.string.app_name));
            if(RewardzRingtoneFolder.isDirectory()){
                try {
                    FileUtils.deleteDirectory(RewardzRingtoneFolder);
                    Log.d("file", "deleted file");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            Log.d("file", RewardzRingtoneFolder.getAbsolutePath());
            RewardzRingtoneFolder.mkdir();
            downloadRingtone(RewardzRingtoneFolder.getAbsoluteFile());
        } else {
            /* save the folder in internal memory of phone */

            File RewardzRingtoneFolder = new File("/data/data/" + getPackageName()
                    + File.separator + getString(R.string.app_name));

            if(RewardzRingtoneFolder.isDirectory()){
                try {
                    FileUtils.deleteDirectory(RewardzRingtoneFolder);
                    Log.d("file", "deleted file2");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            RewardzRingtoneFolder.mkdir();
            downloadRingtone(RewardzRingtoneFolder.getAbsoluteFile());
            Log.d("file", RewardzRingtoneFolder.getAbsolutePath());

        }
//        stopSelf();

        return START_STICKY;
    }

    private void downloadRingtone(final File directory) {

        db = FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();

        db.collection("Published Ads")//.whereEqualTo("ringtone_available", "yes")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d("ringtone", document.getId());
                                ringtoneDownloadUrl = document.get("ringtone_url").toString();
                                //ringtoneDownloadUrl = document.get("ringtone_url").toString();//TODO:get this uncmnt and remove next line
                                //ringtoneDownloadUrl = "https://firebasestorage.googleapis.com/v0/b/startup-demos.appspot.com/o/profilePic%2FBwAA7E3BJlRIevhl55h4YXDAZPg2.jpg?alt=media&token=c3432851-b6ca-4398-83dc-5c5c309a2dde";
                                try {
                                    File ringtone = File.createTempFile(document.getId(),".mp3",directory);//TODO: change suufix to mp3
                                    Log.d("ringtone", document.getId());
                                    StorageReference httpsReference = storage.getReferenceFromUrl(ringtoneDownloadUrl);
                                    httpsReference.getFile(ringtone).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                                        @Override
                                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                            Log.d("ringtone","downloaded");
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.d("ringtone","not-downloaded");
                                        }
                                    });
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }

                            }

                        } else {
                            Log.d("doc", "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        // I want to restart this service again in one hour
        AlarmManager alarm = (AlarmManager)getSystemService(ALARM_SERVICE);
        alarm.set(
                alarm.RTC_WAKEUP,
                System.currentTimeMillis() + (1000 * 60 * 60 * 24 ),//runs for every 24hrs from time of app install(from time at which first time service run)
                PendingIntent.getService(this, 0, new Intent(this, DownloadRt.class), 0)
        );
    }
}
