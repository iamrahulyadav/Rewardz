package com.letswecode.harsha.rewardz.receiver;

import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import com.letswecode.harsha.rewardz.helper.PrefManager;
import com.letswecode.harsha.rewardz.ui.AdPopUpActivity;

import java.io.File;
import java.util.Random;

public class PhoneStateReceiver extends BroadcastReceiver {

    public static final String REWARDZ_PREFS_TONE = "com.letswecode.harsha.rewardz.receiver";
    public static final String REWARDZ_TONE= "AD_ID";
    public static final String OLD_REWARDZ_TONE = "OLD_REWARDZ_TONE";
    private static boolean isIncoming, callReceived;
    static long start_time, end_time;
    private PrefManager prefManager;

    File[] ringtones;
    File RewardzRingtoneFolder, ringtoneFile, selectedRingtone;
    @Override
    public void onReceive(final Context context, final Intent intent) {
        String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);


        try{
            if(state.equals(TelephonyManager.EXTRA_STATE_RINGING)){
                isIncoming = true;
                SharedPreferences preferences = context.getSharedPreferences("AdzAppRingtoneSwitchValue", Context.MODE_PRIVATE);

                RewardzRingtoneFolder = new File(Environment.getExternalStorageDirectory(), "AdzApp");
                Log.d("ringtone", "path is :"+RewardzRingtoneFolder.toString());
                ringtones = RewardzRingtoneFolder.listFiles();
                //Log.d("ringtone", ringtones.clone().toString());

                Random rand = new Random();
                ringtoneFile = ringtones[rand.nextInt(ringtones.length)];

                selectedRingtone = new File(String.valueOf(ringtoneFile));
                ContentValues content = new ContentValues();
                content.put(MediaStore.MediaColumns.DATA,ringtoneFile.getAbsolutePath());
                content.put(MediaStore.MediaColumns.TITLE, selectedRingtone.getName());
                content.put(MediaStore.MediaColumns.SIZE, 215454);
                content.put(MediaStore.MediaColumns.MIME_TYPE, "audio/*");
                content.put(MediaStore.Audio.Media.DURATION, 230);
                content.put(MediaStore.Audio.Media.IS_RINGTONE, true);
                content.put(MediaStore.Audio.Media.IS_NOTIFICATION, false);
                content.put(MediaStore.Audio.Media.IS_ALARM, false);
                content.put(MediaStore.Audio.Media.IS_MUSIC, false);

                Log.i("ringtone", "the absolute path of the file is :"+
                        selectedRingtone.getAbsolutePath()+" name is "+ selectedRingtone.getName());
                Uri uri = MediaStore.Audio.Media.getContentUriForPath(
                        selectedRingtone.getAbsolutePath());
                context.getContentResolver().delete(uri, MediaStore.MediaColumns.DATA + "=\"" + selectedRingtone.getAbsolutePath() + "\"",
                        null);
                Uri newUri = context.getContentResolver().insert(uri, content);
                Log.i("ringtone","the ringtone uri is :"+newUri);

                //checks user preference and changes ringtone by checking if switch ON/OFF status
                if(preferences.getBoolean("active", true)){

                    RingtoneManager.setActualDefaultRingtoneUri(
                            context.getApplicationContext(), RingtoneManager.TYPE_RINGTONE,
                            newUri);


                }
//               RingtoneManager.setActualDefaultRingtoneUri(
//                       context.getApplicationContext(), RingtoneManager.TYPE_RINGTONE,
//                       newUri);

                SharedPreferences sharedPreferences = context.getSharedPreferences(REWARDZ_PREFS_TONE, Context.MODE_PRIVATE);
                String OLD_TONE = sharedPreferences.getString(REWARDZ_TONE,"1rPyNckD3E3Vn4eWcX6t");//TODO: change this df. value to ADZAPP a in database (dont delete adzapp ad in db)
                sharedPreferences.edit().putString(OLD_REWARDZ_TONE,OLD_TONE).apply();
                sharedPreferences.edit().putString(REWARDZ_TONE, selectedRingtone.getName().substring(0,20)).apply();
                Log.d("ringtone","subString "+selectedRingtone.getName().substring(0,20));

                //TODO: removes toasts here in this file
                //TODO:properly match ringtone with ad, tweak code here


            }
            if ((state.equals(TelephonyManager.EXTRA_STATE_OFFHOOK))){
                Toast.makeText(context,"Received State",Toast.LENGTH_SHORT).show();
                Log.d("phoneCall","Received state");
                start_time = System.currentTimeMillis();
                callReceived = true;
            }
            if (state.equals(TelephonyManager.EXTRA_STATE_IDLE)){

                prefManager = new PrefManager(context);
                if (!prefManager.isFirstTimeRinging()) {

                    end_time = System.currentTimeMillis();
                    long total_time = end_time - start_time;
                    Log.d("doc",String.valueOf(total_time));
                    //Intent to call dialog activity to show ad and update reward points to user after call ends
                    if(isIncoming && callReceived ) {  //  if(total_time >= 30000 && isIncoming ){
                        //final Intent i = new Intent(context, AdPopUpActivity.class);TODO:testing different layout
                        final Intent i = new Intent(context, AdPopUpActivity.class);
                        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        i.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        i.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                context.startActivity(i);
                            }
                        }, 500);

                    } // }
                    isIncoming = false;
                    callReceived = false;
                }
                else {
                    prefManager.setIsFirstTimeRinging(false);
                }


            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }


    }


}
