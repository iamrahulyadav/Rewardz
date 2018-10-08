package in.dthoughts.innolabs.adzapp.service;

import android.Manifest;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import in.dthoughts.innolabs.adzapp.helper.PrefManager;
import in.dthoughts.innolabs.adzapp.ui.MainActivity;
import in.dthoughts.innolabs.adzapp.R;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;

import static in.dthoughts.innolabs.adzapp.App.CHANNELID;

public class DownloadRt extends Service {

    FirebaseFirestore db;
    FirebaseUser user;
    FirebaseStorage storage;
    public String ringtoneDownloadUrl, videoDownloadUrl, fav_party_name;
    private FusedLocationProviderClient client;
    private PrefManager prefManager;
    Double Lat, Lon;
    String currentLocation, currentState;
    File RewardzRingtoneFolder, RewardzVideoFolder;
    Timestamp expiryDate_timestamp , createdDate_timestamp;
    Date todayDate, expiryDate, createdDate;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("docc12", "My foreground service onCreate().");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {



        Log.d("docc12", "Start foreground service.");


        Uri defaultSoundUri = /*Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.adzapp_notification);*/RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 123, notificationIntent, 0);
        Notification notification = new NotificationCompat.Builder(this, CHANNELID)
                .setContentTitle(getString(R.string.app_name))
                .setContentText(getString(R.string.service_notification_text))
                .setSound(defaultSoundUri)
                .setSmallIcon(R.drawable.ic_stat_notification_icon)
                .setContentIntent(pendingIntent)
                .build();
        startForeground(1, notification);


        storage = FirebaseStorage.getInstance();
        //getting date for querying
        todayDate = java.util.Calendar.getInstance().getTime();
//TODO:get ringtones from firebase storage and save in user device. --FINISHED
//TODO: dont download the songs ererytime take boolean in shared pref and check if they r downlaoded for today the stop the service

        //code to create a folder
        if (android.os.Environment.getExternalStorageState().equals(
                android.os.Environment.MEDIA_MOUNTED)) {
             RewardzRingtoneFolder = new File(Environment.getExternalStorageDirectory() + File.separator
                    + getString(R.string.app_name)+ File.separator + "AdTones");
            if (RewardzRingtoneFolder.isDirectory()) {
                try {
                    FileUtils.deleteDirectory(RewardzRingtoneFolder);
                    Log.d("docc12", "deleted file");
                } catch (IOException e) {
                    Log.d("docc12", e.getMessage());
                }
            }
            Log.d("docc12", RewardzRingtoneFolder.getAbsolutePath());
            RewardzRingtoneFolder.mkdirs();
            getUserLocation();
            //downloadRingtone(RewardzRingtoneFolder.getAbsoluteFile());
        } else {
            /* save the folder in internal memory of phone */

             RewardzRingtoneFolder = new File("/data/data/" + getPackageName()
                    + File.separator + getString(R.string.app_name)+ File.separator + "AdTones");

            if (RewardzRingtoneFolder.isDirectory()) {
                try {
                    FileUtils.deleteDirectory(RewardzRingtoneFolder);
                    Log.d("docc12", "deleted file2");
                } catch (IOException e) {
                    Log.d("docc12", e.getMessage());
                }
            }
            RewardzRingtoneFolder.mkdirs();
            getUserLocation();
            //downloadRingtone(RewardzRingtoneFolder.getAbsoluteFile());
            Log.d("docc12", RewardzRingtoneFolder.getAbsolutePath());

        }
//        stopSelf();

        return START_STICKY;
    }

    private void getUserLocation() {
        client = LocationServices.getFusedLocationProviderClient(getApplicationContext());
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        client.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    Lat = location.getLatitude();
                    Lon = location.getLongitude();

                    Log.d("docc12", String.valueOf(Lat) + " " + String.valueOf(Lon));
                    getCurrentCity(Lat, Lon);
                }
            }
        });
// .addOnCompleteListener(new OnCompleteListener<Location>() {
//            @Override
//            public void onComplete(@NonNull Task<Location> task) {
//                if (task.isSuccessful()) {//TODO: make this block into try anc catch for null pointer exception
//                    try{
//                        Lat = task.getResult().getLatitude();
//                        Lon = task.getResult().getLongitude();
//                    }catch(NullPointerException exp){
//                        Log.d("docc12","null pointer exception while obtaining lat and lon");
//                    }
//
//                    getCurrentCity(Lat, Lon);
//                }
//                else{
//                    lo
//                }
//            }
//          });
//
    }

    private void getCurrentCity(double lat, double lon) {
        try {
            Geocoder gc = new Geocoder(getApplicationContext());
            if (gc.isPresent()) {
                List<Address> list = null;
                try {
                    list = gc.getFromLocation(Lat, Lon, 1);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    Address address = list.get(0);
                    StringBuilder str = new StringBuilder();
                    str.append("Name:" + address.getLocality() + "\n");
                    str.append("Sub-Admin Ares: " + address.getSubAdminArea() + "\n");
                    str.append("Admin Area: " + address.getAdminArea() + "\n");
                    str.append("Country: " + address.getCountryName() + "\n");
                    str.append("Country Code: " + address.getCountryCode() + "\n");
                    String strAddress = str.toString();
                    Log.d("address", strAddress);
                    currentLocation = address.getLocality().trim().toLowerCase().toString();
                    currentState = address.getAdminArea().trim().toLowerCase().toString();
//                    Toast.makeText(getActivity(), currentLocation,
//                            Toast.LENGTH_LONG).show();
                    Log.d("docc12", currentLocation + " , " + currentState + "in service");
                    downloadRingtone(RewardzRingtoneFolder.getAbsoluteFile());
                } catch (Exception e) {
                    Log.d("docc12", e.getMessage());
                }
            }

        } catch (Exception err) {
            Log.d("docc12", "got location exception in download rt service"+ err.getMessage());
        }
    }



    private void downloadRingtone(final File directory) {
        Log.d("docc12", "download staretd");
        db = FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
        try{
            Log.d("docc12","current Lcation is:"+ currentLocation);
        }catch (NullPointerException e){
            Log.d("docc12", "null location");
        }

        //getting from shared preferences
        final SharedPreferences preferences = getSharedPreferences("AdzAppRingtoneFavParty", Context.MODE_PRIVATE);
         fav_party_name =  preferences.getString("favPartyName","none");
        db.collection("Published Ads").whereEqualTo("ringtone_available","true").whereEqualTo("city","india")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if(task.isSuccessful()){
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    Log.d("docc12", document.getId());
                                    ringtoneDownloadUrl = document.get("ringtone_url").toString();
                                    expiryDate_timestamp = document.getTimestamp("expires_on");
                                    createdDate_timestamp = document.getTimestamp("created_on");
                                    Log.d("docc12","timestamp "+ expiryDate_timestamp+" , "+createdDate_timestamp);
                                    expiryDate = expiryDate_timestamp.toDate();
                                    createdDate = createdDate_timestamp.toDate();

                                    if ((todayDate.equals(createdDate) || todayDate.after(createdDate)) && (todayDate.before(expiryDate) || todayDate.equals(expiryDate))) {
                                        if(document.get("category").toString().equals("elections")){
                                            Log.d("docc12","election ad: "+ document.get("publisher_name"));
                                                if(document.get("publisher_name").toString().toLowerCase().trim().contains(fav_party_name)){
                                                    try {
                                                        File ringtone = File.createTempFile(document.getId(),".mp3",directory);//TODO: change suufix to mp3
                                                        Log.d("ringtone", document.getId());
                                                        StorageReference httpsReference = storage.getReferenceFromUrl(ringtoneDownloadUrl);
                                                        httpsReference.getFile(ringtone).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                                                            @Override
                                                            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                                                Log.d("docc12","downloaded fav party tune");
                                                            }
                                                        }).addOnFailureListener(new OnFailureListener() {
                                                            @Override
                                                            public void onFailure(@NonNull Exception e) {
                                                                Log.d("docc12","not-downloaded fav party tune");
                                                            }
                                                        });
                                                    } catch (IOException e) {
                                                        Log.d("docc12", e.getMessage());
                                                    }
                                                }else{
                                                    Log.d("docc12","not fav party");

                                                }
                                        }else{
                                            try {
                                                File ringtone = File.createTempFile(document.getId(),".mp3",directory);//TODO: change suufix to mp3
                                                Log.d("ringtone", document.getId());
                                                StorageReference httpsReference = storage.getReferenceFromUrl(ringtoneDownloadUrl);
                                                httpsReference.getFile(ringtone).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                                                    @Override
                                                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                                        Log.d("docc12","downloaded inside india");
                                                    }
                                                }).addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Log.d("docc12","not-downloaded inside india");
                                                    }
                                                });
                                            } catch (IOException e) {
                                                Log.d("docc12", e.getMessage());
                                            }
                                        }

                                    }else {Log.d("docc12","a skipped due to date");}


                                }
                            }
                    }
                });

        db.collection("Published Ads").whereEqualTo("ringtone_available","true").whereEqualTo("city",currentState)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()){

                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d("docc12", document.getId());
                                ringtoneDownloadUrl = document.get("ringtone_url").toString();
                                expiryDate_timestamp = document.getTimestamp("expires_on");
                                createdDate_timestamp = document.getTimestamp("created_on");
                                Log.d("docc12","timestamp "+ expiryDate_timestamp+" , "+createdDate_timestamp);
                                expiryDate = expiryDate_timestamp.toDate();
                                createdDate = createdDate_timestamp.toDate();

                                if ((todayDate.equals(createdDate) || todayDate.after(createdDate)) && (todayDate.before(expiryDate) || todayDate.equals(expiryDate))) {

                                    if(document.get("category").toString().equals("elections")){
                                        Log.d("docc12","election ad: "+ document.get("publisher_name"));
                                        if(document.get("publisher_name").toString().toLowerCase().trim().contains(fav_party_name)){
                                            try {
                                                File ringtone = File.createTempFile(document.getId(),".mp3",directory);//TODO: change suufix to mp3
                                                Log.d("ringtone", document.getId());
                                                StorageReference httpsReference = storage.getReferenceFromUrl(ringtoneDownloadUrl);
                                                httpsReference.getFile(ringtone).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                                                    @Override
                                                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                                        Log.d("docc12","downloaded fav party tune");
                                                    }
                                                }).addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Log.d("docc12","not-downloaded fav party tune");
                                                    }
                                                });
                                            } catch (IOException e) {
                                                Log.d("docc12", e.getMessage());
                                            }
                                        }else{
                                            Log.d("docc12","not fav party");

                                        }
                                    }else{
                                        try {
                                            File ringtone = File.createTempFile(document.getId(),".mp3",directory);//TODO: change suufix to mp3
                                            Log.d("ringtone", document.getId());
                                            StorageReference httpsReference = storage.getReferenceFromUrl(ringtoneDownloadUrl);
                                            httpsReference.getFile(ringtone).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                                                @Override
                                                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                                    Log.d("docc12","downloaded inside state: "+currentState);
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Log.d("docc12","not-downloaded inside sate: "+currentState);
                                                }
                                            });
                                        } catch (IOException e) {
                                            Log.d("docc12", e.getMessage());
                                        }
                                    }
                                }else {Log.d("docc12","a skipped due to date");}


                            }
                        }
                    }
                });

        db.collection("Published Ads").whereEqualTo("ringtone_available","true").whereEqualTo("city",currentLocation)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d("docc12", document.getId());
                                ringtoneDownloadUrl = document.get("ringtone_url").toString();
                                expiryDate_timestamp = document.getTimestamp("expires_on");
                                createdDate_timestamp = document.getTimestamp("created_on");
                                Log.d("docc12","timestamp "+ expiryDate_timestamp+" , "+createdDate_timestamp);
                                expiryDate = expiryDate_timestamp.toDate();
                                createdDate = createdDate_timestamp.toDate();

                                if ((todayDate.equals(createdDate) || todayDate.after(createdDate)) && (todayDate.before(expiryDate) || todayDate.equals(expiryDate))) {

                                    if(document.get("category").toString().equals("elections")){
                                        Log.d("docc12","election ad: "+ document.get("publisher_name"));
                                        if(document.get("publisher_name").toString().toLowerCase().trim().contains(fav_party_name)){
                                            try {
                                                File ringtone = File.createTempFile(document.getId(),".mp3",directory);//TODO: change suufix to mp3
                                                Log.d("ringtone", document.getId());
                                                StorageReference httpsReference = storage.getReferenceFromUrl(ringtoneDownloadUrl);
                                                httpsReference.getFile(ringtone).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                                                    @Override
                                                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                                        Log.d("docc12","downloaded fav party tune");
                                                    }
                                                }).addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Log.d("docc12","not-downloaded fav party tune");
                                                    }
                                                });
                                            } catch (IOException e) {
                                                Log.d("docc12", e.getMessage());
                                            }
                                        }else{
                                            Log.d("docc12","not fav party");

                                        }
                                    }else{
                                        try {
                                            File ringtone = File.createTempFile(document.getId(),".mp3",directory);//TODO: change suufix to mp3
                                            Log.d("ringtone", document.getId());
                                            StorageReference httpsReference = storage.getReferenceFromUrl(ringtoneDownloadUrl);
                                            httpsReference.getFile(ringtone).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                                                @Override
                                                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                                    Log.d("docc12","downloaded inside city: "+currentLocation);
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Log.d("docc12","not-downloaded inside city: "+currentLocation);
                                                }
                                            });
                                        } catch (IOException e) {
                                            Log.d("docc12", e.getMessage());
                                        }
                                    }

                                }else {Log.d("docc12","a skipped due to date");}


                            }
                            prefManager = new PrefManager(getApplicationContext());
                            prefManager.setFirstTimeLaunchInDay(false);
                            downloadForYouVideos();
                            stopSelf();

                        } else {
                            Log.d("docc12", "Error getting documents: "+ task.getException());
                        }
                    }
                });
    }

    private void downloadForYouVideos() {
        Log.d("docc12","came here");
        if (android.os.Environment.getExternalStorageState().equals(
                android.os.Environment.MEDIA_MOUNTED)) {
            RewardzVideoFolder = new File(Environment.getExternalStorageDirectory() + File.separator
                    + getString(R.string.app_name)+ File.separator + "Advideos");
            if (RewardzVideoFolder.isDirectory()) {
                try {
                    FileUtils.deleteDirectory(RewardzVideoFolder);
                    Log.d("docc12", "deleted file");
                } catch (IOException e) {
                    Log.d("docc12", e.getMessage());
                }

            }
            RewardzVideoFolder.mkdirs();
            Log.d("docc12","adbsolute path of video folder"+ RewardzVideoFolder.getAbsolutePath());

        }else {
            /* save the folder in internal memory of phone */

            RewardzVideoFolder = new File("/data/data/" + getPackageName()
                    + File.separator + getString(R.string.app_name) + File.separator + ".Advideos");

            if (RewardzVideoFolder.isDirectory()) {
                try {
                    FileUtils.deleteDirectory(RewardzVideoFolder);
                    Log.d("docc12", "deleted file2");
                } catch (IOException e) {
                    Log.d("docc12", e.getMessage());
                }
            }
            RewardzVideoFolder.mkdirs();


        }

        db.collection("Published Ads").whereEqualTo("video_available","true").whereEqualTo("ringtone_available","true").whereEqualTo("city",currentLocation)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d("docc12", document.getId());
                                videoDownloadUrl = document.get("video_url").toString();
                                expiryDate_timestamp = document.getTimestamp("expires_on");
                                createdDate_timestamp = document.getTimestamp("created_on");
                                Log.d("docc12","timestamp "+ expiryDate_timestamp+" , "+createdDate_timestamp);
                                expiryDate = expiryDate_timestamp.toDate();
                                createdDate = createdDate_timestamp.toDate();

                                if ((todayDate.equals(createdDate) || todayDate.after(createdDate)) && (todayDate.before(expiryDate) || todayDate.equals(expiryDate))) {
                                    if(document.get("category").toString().equals("elections")){
                                        Log.d("docc12","election ad: "+ document.get("publisher_name"));
                                        if(document.get("publisher_name").toString().toLowerCase().trim().contains(fav_party_name)){
                                            try {
                                                File video = File.createTempFile(document.getId(),".mp4",RewardzVideoFolder);//TODO: change suufix to mp3
                                                Log.d("ringtone", document.getId());
                                                StorageReference httpsReference = storage.getReferenceFromUrl(ringtoneDownloadUrl);
                                                httpsReference.getFile(video).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                                                    @Override
                                                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                                        Log.d("docc12","downloaded fav party video");
                                                    }
                                                }).addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Log.d("docc12","not-downloaded fav party video");
                                                    }
                                                });
                                            } catch (IOException e) {
                                                Log.d("docc12", e.getMessage());
                                            }
                                        }else{
                                            Log.d("docc12","not fav party");

                                        }
                                    }else{
                                        try {
                                            File video = File.createTempFile(document.getId(),".mp4",RewardzVideoFolder);//TODO: change suufix to mp3
                                            Log.d("ringtone", document.getId());
                                            StorageReference httpsReference = storage.getReferenceFromUrl(videoDownloadUrl);
                                            httpsReference.getFile(video).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                                                @Override
                                                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                                    Log.d("docc12","downloaded video inside city");
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Log.d("docc12","not-downloaded video inside city");
                                                }
                                            });
                                        } catch (IOException e) {
                                            Log.d("docc12", e.getMessage());
                                        }
                                    }

                                }else {Log.d("docc12","a skipped due to date");}


                            }
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
        final SharedPreferences preferences = getSharedPreferences("AdzAppRingtoneIntervalValue", Context.MODE_PRIVATE);
        int interval_time = preferences.getInt("interval_time", 24);
        AlarmManager alarm = (AlarmManager)getSystemService(ALARM_SERVICE);
        alarm.set(
                alarm.RTC_WAKEUP,
                System.currentTimeMillis() + (1000 * 60 * 60 * interval_time ),// TODO:chnage time ADD EXTRA 60, runs for every 24hrs from time of app install(from time at which first time service run)
                PendingIntent.getService(this, 0, new Intent(this, DownloadRt.class), 0)
        );
    }
}









/*package in.dthoughts.innolabs.adzapp.service;

import android.Manifest;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Environment;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import in.dthoughts.innolabs.adzapp.helper.PrefManager;
import in.dthoughts.innolabs.adzapp.ui.MainActivity;
import in.dthoughts.innolabs.adzapp.R;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;

import static in.dthoughts.innolabs.adzapp.App.CHANNELID;

public class DownloadRt extends Service {

    FirebaseFirestore db;
    FirebaseUser user;
    FirebaseStorage storage;
    public String ringtoneDownloadUrl;
    private PrefManager prefManager;
    public String currentLocation, currentState;
    private FusedLocationProviderClient client;
    double Lat, Lon;
    private FirebaseAuth auth;
    Timestamp expiryDate_timestamp , createdDate_timestamp;
    Date todayDate, expiryDate, createdDate;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("docc12", "My foreground service onCreate().");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        String FROM = intent.getStringExtra("CAME_FROM");
        Log.d("docc12", "came from: " + FROM);
        Log.d("docc12", "Start foreground service.");

        Uri defaultSoundUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.adzapp_notification);//RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 123, notificationIntent, 0);
        Notification notification = new NotificationCompat.Builder(this, CHANNELID)
                .setContentTitle(getString(R.string.app_name))
                .setContentText(getString(R.string.service_notification_text))
                .setSound(defaultSoundUri)
                .setSmallIcon(R.drawable.ic_stat_notification_icon)
                .setContentIntent(pendingIntent)
                .build();
        startForeground(1, notification);

        auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() == null) {
            Log.d("docc12", "current user is null in service");

        }else {
            Log.d("docc12", "current user is not null in service");
            Log.d("docc12","uid of user is "+ auth.getUid());
        }
        //for obtaining location

        getLocation();

        createFolder();

        //code to create a folder
        //stopSelf();

        return START_STICKY;
    }

    private void getLocation() {
        client = LocationServices.getFusedLocationProviderClient(getApplicationContext());

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        client.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    Lat = location.getLatitude();
                    Lon = location.getLongitude();

                    Log.d("docc12", String.valueOf(Lat) + " " + String.valueOf(Lon));
                    getCurrentCity(Lat, Lon);
                }
            }
        });

    }

    private void getCurrentCity(double lat, double lon) {
        try {
            Geocoder gc = new Geocoder(getApplicationContext());
            if (gc.isPresent()) {
                List<Address> list = null;
                try {
                    list = gc.getFromLocation(Lat, Lon, 1);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    Address address = list.get(0);
                    StringBuilder str = new StringBuilder();
                    str.append("Name:" + address.getLocality() + "\n");
                    str.append("Sub-Admin Ares: " + address.getSubAdminArea() + "\n");
                    str.append("Admin Area: " + address.getAdminArea() + "\n");
                    str.append("Country: " + address.getCountryName() + "\n");
                    str.append("Country Code: " + address.getCountryCode() + "\n");
                    String strAddress = str.toString();
                    Log.d("address", strAddress);
                    currentLocation = address.getLocality().trim().toLowerCase().toString();
                    currentState = address.getAdminArea().trim().toLowerCase().toString();
//                    Toast.makeText(getActivity(), currentLocation,
//                            Toast.LENGTH_LONG).show();
                    Log.d("docc12", currentLocation + " , " + currentState + "in service");
                } catch (Exception e) {
                    Log.d("docc12", e.getMessage());
                }
            }

        } catch (Exception err) {
            Log.d("docc12", "got location exception in download rt service"+ err.getMessage());
        }
    }


    private void createFolder() {

        if (android.os.Environment.getExternalStorageState().equals(
                android.os.Environment.MEDIA_MOUNTED)) {

            File RewardzRingtoneFolder = new File(Environment.getExternalStorageDirectory(), "AdzApp");
//            File RewardzRingtoneFolder = new File(Environment.getExternalStorageDirectory() + File.separator
//                    + getString(R.string.app_name)); //TODO: re check this cmnt
            if (RewardzRingtoneFolder.isDirectory()) {
                try {
                    FileUtils.deleteDirectory(RewardzRingtoneFolder);
                    Log.d("docc12", "deleted file");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            Log.d("docc12", RewardzRingtoneFolder.getAbsolutePath());
            if (!RewardzRingtoneFolder.exists()) {
                boolean file_created = RewardzRingtoneFolder.mkdir();
                Log.i("docc12", "file created" + file_created);
            }

            downloadRingtone(RewardzRingtoneFolder.getAbsoluteFile()); //TODO: unmcnt when req.
        } else {
            // save the folder in internal memory of phone //


            File RewardzRingtoneFolder = new File(Environment.getExternalStorageDirectory(), "AdzApp");
            if (RewardzRingtoneFolder.isDirectory()) {
                try {
                    FileUtils.deleteDirectory(RewardzRingtoneFolder);
                    Log.d("docc12", "deleted file2");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            boolean file_created = RewardzRingtoneFolder.mkdir();
            Log.d("docc12", "file created" + file_created);
            downloadRingtone(RewardzRingtoneFolder.getAbsoluteFile()); //TODO: unmcnt when req.
            Log.d("docc12", RewardzRingtoneFolder.getAbsolutePath());

        }

    }

    private void downloadRingtone(final File directory) {

        todayDate = java.util.Calendar.getInstance().getTime();

        storage = FirebaseStorage.getInstance();
        db = FirebaseFirestore.getInstance();
        FirebaseFirestore.setLoggingEnabled(true);
        user = FirebaseAuth.getInstance().getCurrentUser();

        Log.d("docc12", "dowload started");

        db.collection("Published Ads").whereEqualTo("ringtone_available", "true").whereEqualTo("city", "india")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {

                            for (QueryDocumentSnapshot document : task.getResult()) {
                               // Log.d("docc12", document.getId());
                                expiryDate_timestamp = document.getTimestamp("expires_on");
                                createdDate_timestamp = document.getTimestamp("created_on");
                                expiryDate = expiryDate_timestamp.toDate();
                                createdDate = createdDate_timestamp.toDate();
                                ringtoneDownloadUrl = document.get("ringtone_url").toString();
                                if ((todayDate.equals(createdDate) || todayDate.after(createdDate)) && (todayDate.before(expiryDate) || todayDate.equals(expiryDate))) {

                                    try {
                                        File ringtone = File.createTempFile(document.getId(), ".mp3", directory);//TODO: change suufix to mp3 -- FINISHED
                                        Log.d("docc12", document.getId());
                                        StorageReference httpsReference = storage.getReferenceFromUrl(ringtoneDownloadUrl);
                                        httpsReference.getFile(ringtone).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                                            @Override
                                            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                                Log.d("docc12", "downloaded");
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.d("docc12", "not-downloaded");
                                            }
                                        });
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }


                            }
                        }
                    }
                });


        db.collection("Published Ads").whereEqualTo("ringtone_available", "true").whereEqualTo("city", currentState)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {

                            for (QueryDocumentSnapshot document : task.getResult()) {
                                //Log.d("docc12", document.getId());
                                expiryDate_timestamp = document.getTimestamp("expires_on");
                                createdDate_timestamp = document.getTimestamp("created_on");
                                expiryDate = expiryDate_timestamp.toDate();
                                createdDate = createdDate_timestamp.toDate();
                                ringtoneDownloadUrl = document.get("ringtone_url").toString();
                                if ((todayDate.equals(createdDate) || todayDate.after(createdDate)) && (todayDate.before(expiryDate) || todayDate.equals(expiryDate))) {

                                    try {
                                        File ringtone = File.createTempFile(document.getId(), ".mp3", directory);//TODO: change suufix to mp3 -- FINISHED
                                        Log.d("docc12", document.getId());
                                        StorageReference httpsReference = storage.getReferenceFromUrl(ringtoneDownloadUrl);
                                        httpsReference.getFile(ringtone).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                                            @Override
                                            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                                Log.d("docc12", "downloaded");
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.d("docc12", "not-downloaded");
                                            }
                                        });
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }


                            }
                        }
                    }
                });



        db.collection("Published Ads").whereEqualTo("ringtone_available", "true").whereEqualTo("city", currentLocation)//.whereEqualTo("city","india")//.whereEqualTo("ad_type", "premium")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {

                            for (QueryDocumentSnapshot document : task.getResult()) {
                               // Log.d("docc12", document.getId());
                                expiryDate_timestamp = document.getTimestamp("expires_on");
                                createdDate_timestamp = document.getTimestamp("created_on");
                                expiryDate = expiryDate_timestamp.toDate();
                                createdDate = createdDate_timestamp.toDate();
                                ringtoneDownloadUrl = document.get("ringtone_url").toString();
                                if ((todayDate.equals(createdDate) || todayDate.after(createdDate)) && (todayDate.before(expiryDate) || todayDate.equals(expiryDate))) {

                                    try {
                                        File ringtone = File.createTempFile(document.getId(), ".mp3", directory);//TODO: change suufix to mp3 -- FINISHED
                                        Log.d("docc12", document.getId());
                                        StorageReference httpsReference = storage.getReferenceFromUrl(ringtoneDownloadUrl);
                                        httpsReference.getFile(ringtone).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                                            @Override
                                            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                                Log.d("docc12", "downloaded");
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.d("docc12", "not-downloaded");
                                            }
                                        });
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }


                            }
                            prefManager = new PrefManager(getApplicationContext());
                            prefManager.setFirstTimeLaunchInDay(true);
                            //stopSelf();
                            Log.d("docc12", "stopped foreground service");
                        } else {
                            Log.d("docc12", "Error getting documents: ", task.getException());
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
        // In order to restart this service again in one day
        Intent intent = new Intent(this, DownloadRt.class);
        intent.putExtra("CAME_FROM","SERVICE");
        AlarmManager alarm = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarm.set(
                alarm.RTC_WAKEUP, //TODO: CHANGE IT TO 1000*60*60*24
                System.currentTimeMillis() + (1000 * 60 * 2),//runs for every 24hrs from time of app install(from time at which first time service run)
                PendingIntent.getService(this, 0, intent, 0)
        );
        Log.d("docc", "alarmtriggred");
    }
}*/



