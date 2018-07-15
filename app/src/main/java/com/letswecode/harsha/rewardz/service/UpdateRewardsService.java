package com.letswecode.harsha.rewardz.service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class UpdateRewardsService extends IntentService {
    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */

    FirebaseFirestore db;
    FirebaseUser user;
    Integer rewardpoints, pointsToAdd=5;

//    public UpdateRewardsService(String name) {
//        super(name);
//    }

    public UpdateRewardsService() {
        super("UpdateRewardsService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

        Toast.makeText(getApplicationContext(),"Intent service ",Toast.LENGTH_SHORT).show();

        //updates user reward points in DB, whenever user receives a call(ringtone)
        db = FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
               DocumentReference userReawrdPoints = db.collection("userRewards").document(user.getUid());
               userReawrdPoints.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                   @Override
                   public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                       if(task.isSuccessful()){
                           DocumentSnapshot doc = task.getResult();
                          rewardpoints = (Integer) doc.get("Rewards");
                          updateUserRewards(rewardpoints, pointsToAdd);
                       }
                   }
               }).addOnFailureListener(new OnFailureListener() {
                   @Override
                   public void onFailure(@NonNull Exception e) {


                   }
               });
    }

        public void updateUserRewards(Integer rewardpoints, Integer pointsToAdd){
        final Integer update_points = rewardpoints + pointsToAdd;
        DocumentReference updatingRewards = db.collection("userRewards").document(user.getUid());
        updatingRewards.update("Rewards",update_points).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(getApplicationContext(),"Rewards updated to: "+ update_points + ".",Toast.LENGTH_SHORT).show();
            }
        });
    }
}
