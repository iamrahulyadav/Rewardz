package in.dthoughts.innolabs.adzapp.service;

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

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("FGservice", "My foreground service onCreate().");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.d("doc1", "Start foreground service.");

        Uri defaultSoundUri= Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.adzapp_notification);//RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
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

                    Log.d("latlon",String.valueOf(Lat)+ " "+ String.valueOf(Lon));
                    getCurrentCity(Lat, Lon);
                }
            }
        });

    }

    private void getCurrentCity(double lat, double lon) {
        try{
            Geocoder gc = new Geocoder(getApplicationContext());
            if(gc.isPresent()){
                List<Address> list = null;
                try {
                    list = gc.getFromLocation(Lat, Lon,1);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try{
                    Address address = list.get(0);
                    StringBuilder str = new StringBuilder();
                    str.append("Name:" + address.getLocality() + "\n");
                    str.append("Sub-Admin Ares: " + address.getSubAdminArea() +"\n");
                    str.append("Admin Area: " + address.getAdminArea() + "\n");
                    str.append("Country: " + address.getCountryName() + "\n");
                    str.append("Country Code: " + address.getCountryCode() + "\n");
                    String strAddress = str.toString();
                    Log.d("address", strAddress);
                    currentLocation = address.getLocality().trim().toLowerCase().toString();
                    currentState = address.getAdminArea().trim().toLowerCase().toString();
//                    Toast.makeText(getActivity(), currentLocation,
//                            Toast.LENGTH_LONG).show();
                    Log.d("city", currentLocation+" , "+currentState+"in service");
                }catch (Exception e){
                    Log.d("error in location", e.getMessage());
                }
            }

        }catch(Exception err){
            Log.d("exception","got location exception in download rt service");
        }
    }


    private void createFolder() {

        if (android.os.Environment.getExternalStorageState().equals(
                android.os.Environment.MEDIA_MOUNTED)) {

            File RewardzRingtoneFolder = new File(Environment.getExternalStorageDirectory(),"AdzApp");
//            File RewardzRingtoneFolder = new File(Environment.getExternalStorageDirectory() + File.separator
//                    + getString(R.string.app_name)); //TODO: re check this cmnt
            if(RewardzRingtoneFolder.isDirectory()){
                try {
                    FileUtils.deleteDirectory(RewardzRingtoneFolder);
                    Log.d("file", "deleted file");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            Log.d("file", RewardzRingtoneFolder.getAbsolutePath());
            if(!RewardzRingtoneFolder.exists()){
                boolean  file_created = RewardzRingtoneFolder.mkdir();
                Log.i("rewardz","file created"+file_created);
            }

            downloadRingtone(RewardzRingtoneFolder.getAbsoluteFile()); //TODO: unmcnt when req.
        }
        else {
            /* save the folder in internal memory of phone */

           /* File RewardzRingtoneFolder = new File("/data/data/" + getPackageName()
                    + File.separator + getString(R.string.app_name));*/ //TODO:Re check and uncmnt this line
            File RewardzRingtoneFolder = new File(Environment.getExternalStorageDirectory(),"AdzApp");
            if(RewardzRingtoneFolder.isDirectory()){
                try {
                    FileUtils.deleteDirectory(RewardzRingtoneFolder);
                    Log.d("file", "deleted file2");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            boolean  file_created = RewardzRingtoneFolder.mkdir();
            Log.d("rewardz","file created"+file_created);
            downloadRingtone(RewardzRingtoneFolder.getAbsoluteFile()); //TODO: unmcnt when req.
            Log.d("file", RewardzRingtoneFolder.getAbsolutePath());

        }

    }

    private void downloadRingtone(final File directory) {

        storage = FirebaseStorage.getInstance();
        db = FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();

        Log.d("city","dowload started");

        db.collection("Published Ads").whereEqualTo("ringtone_available", "true").whereEqualTo("city", "india" )
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()){
                            for (QueryDocumentSnapshot document : task.getResult()){
                                Log.d("ringtone", document.getId());
                                ringtoneDownloadUrl = document.get("ringtone_url").toString();

                                try {
                                    File ringtone = File.createTempFile(document.getId(),".mp3",directory);//TODO: change suufix to mp3 -- FINISHED
                                    Log.d("ringtone", document.getId());
                                    StorageReference httpsReference = storage.getReferenceFromUrl(ringtoneDownloadUrl);
                                    httpsReference.getFile(ringtone).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                                        @Override
                                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                            Log.d("doc1","downloaded");
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
                        }
                    }
                });

        db.collection("Published Ads").whereEqualTo("ringtone_available", "true").whereEqualTo("city", currentLocation )//.whereEqualTo("city","india")//.whereEqualTo("ad_type", "premium")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d("ringtone", document.getId());
                                ringtoneDownloadUrl = document.get("ringtone_url").toString();

                                try {
                                    File ringtone = File.createTempFile(document.getId(),".mp3",directory);//TODO: change suufix to mp3 -- FINISHED
                                    Log.d("ringtone", document.getId());
                                    StorageReference httpsReference = storage.getReferenceFromUrl(ringtoneDownloadUrl);
                                    httpsReference.getFile(ringtone).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                                        @Override
                                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                            Log.d("doc1","downloaded");
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
                            prefManager = new PrefManager(getApplicationContext());
                            prefManager.setFirstTimeLaunchInDay(false);
                            stopSelf();
                            Log.d("doc1","stopped foreground service");
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
        // In order to restart this service again in one day
        AlarmManager alarm = (AlarmManager)getSystemService(ALARM_SERVICE);
        alarm.set(
                alarm.RTC_WAKEUP, //TODO: CHANGE IT TO 1000*60*60*24
                System.currentTimeMillis() + (1000 * 60 * 60 * 12 ),//runs for every 24hrs from time of app install(from time at which first time service run)
                PendingIntent.getService(this, 0, new Intent(this, DownloadRt.class), 0)
        );
        Log.d("docc","alarmtriggred");
    }
}



/*public class DownloadRt extends Service {

    FirebaseFirestore db;
    FirebaseUser user;
    FirebaseStorage storage;
    public String ringtoneDownloadUrl;
    private PrefManager prefManager;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("FGservice", "My foreground service onCreate().");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.d("doc1", "Start foreground service.");

        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,123, notificationIntent, 0);
       Notification notification = new NotificationCompat.Builder(this, CHANNELID)
               .setContentTitle(getString(R.string.app_name))
               .setContentText(getString(R.string.service_notification_text))
               .setSmallIcon(R.drawable.ic_stat_notification_icon)
               .setContentIntent(pendingIntent)
               .build();
       startForeground(1, notification);

        createFolder();

        //code to create a folder
               //stopSelf();

        return START_STICKY;
    }

    private void createFolder() {

        if (android.os.Environment.getExternalStorageState().equals(
                android.os.Environment.MEDIA_MOUNTED)) {

            File RewardzRingtoneFolder = new File(Environment.getExternalStorageDirectory(),"AdzApp");
//            File RewardzRingtoneFolder = new File(Environment.getExternalStorageDirectory() + File.separator
//                    + getString(R.string.app_name)); //TODO: re check this cmnt
            if(RewardzRingtoneFolder.isDirectory()){
                try {
                    FileUtils.deleteDirectory(RewardzRingtoneFolder);
                    Log.d("file", "deleted file");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            Log.d("file", RewardzRingtoneFolder.getAbsolutePath());
            if(!RewardzRingtoneFolder.exists()){
                boolean  file_created = RewardzRingtoneFolder.mkdir();
                Log.i("adzapp","file created"+file_created);
            }

           downloadRingtone(RewardzRingtoneFolder.getAbsoluteFile()); //TODO: unmcnt when req.
        }
        else {
            //save the folder in internal memory of phone

//            File RewardzRingtoneFolder = new File("/data/data/" + getPackageName()
//                    + File.separator + getString(R.string.app_name)); //TODO:Re check and uncmnt this line
            File RewardzRingtoneFolder = new File(Environment.getExternalStorageDirectory(),"AdzApp");
            if(RewardzRingtoneFolder.isDirectory()){
                try {
                    FileUtils.deleteDirectory(RewardzRingtoneFolder);
                    Log.d("file", "deleted file2");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            boolean  file_created = RewardzRingtoneFolder.mkdir();
            Log.d("adzapp","file created"+file_created);
           downloadRingtone(RewardzRingtoneFolder.getAbsoluteFile()); //TODO: unmcnt when req.
            Log.d("file", RewardzRingtoneFolder.getAbsolutePath());

        }

    }

    private void downloadRingtone(final File directory) {

        storage = FirebaseStorage.getInstance();
        db = FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();

        db.collection("Published Ads").whereEqualTo("ringtone_available", "true")//.whereEqualTo("ad_type", "premium")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d("ringtone", document.getId());
                                ringtoneDownloadUrl = document.get("ringtone_url").toString();

                                try {
                                    File ringtone = File.createTempFile(document.getId(),".mp3",directory);//TODO: change suufix to mp3 -- FINISHED
                                    Log.d("ringtone", document.getId());
                                    StorageReference httpsReference = storage.getReferenceFromUrl(ringtoneDownloadUrl);
                                    httpsReference.getFile(ringtone).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                                        @Override
                                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                            Log.d("doc1","downloaded");
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.d("ringtone","not-downloaded");
                                        }
                                    });
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                            }
                            prefManager = new PrefManager(getApplicationContext());
                            prefManager.setFirstTimeLaunchInDay(false);
                            stopSelf();
                            Log.d("doc1","stopped foreground service");
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
        // In order to restart this service again in one day
        AlarmManager alarm = (AlarmManager)getSystemService(ALARM_SERVICE);
        alarm.set(
                alarm.RTC_WAKEUP, //TODO: CHANGE IT TO 1000*60*60*24
                System.currentTimeMillis() + (1000 * 60 * 60 * 24 ),//runs for every 24hrs from time of app install(from time at which first time service run)
                PendingIntent.getService(this, 0, new Intent(this, DownloadRt.class), 0)
        );
    }
} */
