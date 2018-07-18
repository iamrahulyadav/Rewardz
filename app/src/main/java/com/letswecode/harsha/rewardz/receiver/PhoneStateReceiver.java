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
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.ShareActionProvider;
import android.widget.Toast;

import com.letswecode.harsha.rewardz.R;
import com.letswecode.harsha.rewardz.ui.AdPopUpActivity;

import java.io.File;
import java.util.Random;

public class PhoneStateReceiver extends BroadcastReceiver {

    public static final String REWARDZ_PREFS_TONE = "com.letswecode.harsha.rewardz.receiver";
    public static final String REWARDZ_TONE= "AD_ID";
    public static final String OLD_REWARDZ_TONE = "OLD_REWARDZ_TONE";

    File[] ringtones;
    File RewardzRingtoneFolder, ringtoneFile, selectedRingtone;
    @Override
    public void onReceive(final Context context, final Intent intent) {
        String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);

       try{
           if(state.equals(TelephonyManager.EXTRA_STATE_RINGING)){

                RewardzRingtoneFolder = new File(Environment.getExternalStorageDirectory() + File.separator
                       + "Rewardz");
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
               RingtoneManager.setActualDefaultRingtoneUri(
                       context.getApplicationContext(), RingtoneManager.TYPE_RINGTONE,
                       newUri);

               SharedPreferences sharedPreferences = context.getSharedPreferences(REWARDZ_PREFS_TONE, Context.MODE_PRIVATE);
               sharedPreferences.edit().putString("REWARDZ_TONE", selectedRingtone.getName().substring(0,20));
               Log.d("ringtone","subString "+selectedRingtone.getName().substring(0,20));

               Toast.makeText(context,"Ringing State ",Toast.LENGTH_SHORT).show();//TODO: removes toasts here in this file


           }
           if ((state.equals(TelephonyManager.EXTRA_STATE_OFFHOOK))){
               Toast.makeText(context,"Received State",Toast.LENGTH_SHORT).show();
           }
           if (state.equals(TelephonyManager.EXTRA_STATE_IDLE)){
               Toast.makeText(context,"Idle State",Toast.LENGTH_SHORT).show();

                //Intent to call dailog activity to show ad and update reward points to user after call ends
               final Intent i = new Intent(context, AdPopUpActivity.class);
               i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
               i.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
               i.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);


               new Handler().postDelayed(new Runnable()
               {
                   @Override
                   public void run()
                   {
                       context.startActivity(i);
                   }
               },500);


           }
       }
       catch (Exception e) {
           e.printStackTrace();
       }


    }


}
