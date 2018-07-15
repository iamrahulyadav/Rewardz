package com.letswecode.harsha.rewardz.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.telephony.TelephonyManager;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.letswecode.harsha.rewardz.service.UpdateRewardsService;

public class PhoneStateReceiver extends BroadcastReceiver {

    FirebaseFirestore db;
    FirebaseUser user;
    Integer rewardpoints, pointsToAdd=5;

    @Override
    public void onReceive(final Context context, Intent intent) {
        String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);

       try{
           if(state.equals(TelephonyManager.EXTRA_STATE_RINGING)){

               Toast.makeText(context,"Ringing State ",Toast.LENGTH_LONG).show();
               //intent service to update the DB
//               Intent updateRewardsIntent = new Intent(context, UpdateRewardsService.class);
//               context.startService(updateRewardsIntent);



               //updates user reward points in DB, whenever user receives a call(ringtone)
//               DocumentReference userReawrdPoints = db.collection("userRewards").document(user.getUid());
//               userReawrdPoints.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//                   @Override
//                   public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                       if(task.isSuccessful()){
//                           DocumentSnapshot doc = task.getResult();
//                          rewardpoints = (Integer) doc.get("Rewards");
//                          updateUserRewards(rewardpoints, pointsToAdd, context);
//                       }
//                   }
//               }).addOnFailureListener(new OnFailureListener() {
//                   @Override
//                   public void onFailure(@NonNull Exception e) {
//
//
//                   }
//               });
           }
           if ((state.equals(TelephonyManager.EXTRA_STATE_OFFHOOK))){
               Toast.makeText(context,"Received State",Toast.LENGTH_SHORT).show();
           }
           if (state.equals(TelephonyManager.EXTRA_STATE_IDLE)){
               Toast.makeText(context,"Idle State",Toast.LENGTH_SHORT).show();
           }
       }
       catch (Exception e) {
           e.printStackTrace();
       }


    }

//    public void updateUserRewards(Integer rewardpoints, Integer pointsToAdd, final Context context){
//        final Integer update_points = rewardpoints + pointsToAdd;
//        DocumentReference updatingRewards = db.collection("userRewards").document(user.getUid());
//        updatingRewards.update("Rewards",update_points).addOnSuccessListener(new OnSuccessListener<Void>() {
//            @Override
//            public void onSuccess(Void aVoid) {
//                Toast.makeText(context,"Rewards updated to: "+ update_points + ".",Toast.LENGTH_SHORT).show();
//            }
//        });
//    }
}
