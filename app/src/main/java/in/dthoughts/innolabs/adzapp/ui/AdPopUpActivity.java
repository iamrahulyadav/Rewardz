package in.dthoughts.innolabs.adzapp.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import in.dthoughts.innolabs.adzapp.R;
import in.dthoughts.innolabs.adzapp.helper.DeviceLocked;
import in.dthoughts.innolabs.adzapp.modal.Ads;
//import com.pierfrancescosoffritti.androidyoutubeplayer.player.YouTubePlayer;
//import com.pierfrancescosoffritti.androidyoutubeplayer.player.YouTubePlayerView;
//import com.pierfrancescosoffritti.androidyoutubeplayer.player.listeners.AbstractYouTubePlayerListener;
//import com.pierfrancescosoffritti.androidyoutubeplayer.player.listeners.YouTubePlayerInitListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import in.dthoughts.innolabs.adzapp.helper.DeviceLocked;
import in.dthoughts.innolabs.adzapp.receiver.PhoneStateReceiver;

import in.dthoughts.innolabs.adzapp.helper.DeviceLocked;
import in.dthoughts.innolabs.adzapp.receiver.PhoneStateReceiver;
import tcking.github.com.giraffeplayer2.VideoView;


import static in.dthoughts.innolabs.adzapp.receiver.PhoneStateReceiver.OLD_REWARDZ_TONE;
import static in.dthoughts.innolabs.adzapp.receiver.PhoneStateReceiver.REWARDZ_PREFS_TONE;
import static in.dthoughts.innolabs.adzapp.receiver.PhoneStateReceiver.REWARDZ_TONE;

public class AdPopUpActivity extends Activity {

    FirebaseFirestore db;
    FirebaseUser user;
    Double rewardpoints, pointsToAdd= Double.valueOf(5);
    //ArrayList<String> adsID;

    TextView publisher_name, city, expires_on, ad_description, ad_url;
    ImageView profile_pic, ad_banner;
    ImageButton ad_close;
    LinearLayout parentLayout;
    RelativeLayout rootLayout;
    //YouTubePlayerView Ad_video;
    VideoView videoView;

    DocumentSnapshot doc;

    private ShimmerFrameLayout mShimmerViewContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.setFinishOnTouchOutside(true);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ad_pop_up);
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        initializeView();


        db = FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();

        // adsID = new ArrayList<>();



        if(user!=null){
            DocumentReference userReawrdPoints = db.collection("userRewards").document(user.getUid());
            userReawrdPoints.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if(task.isSuccessful()){
                        DocumentSnapshot doc = task.getResult();
                        rewardpoints = Double.valueOf(doc.get("Rewards").toString());
                        Log.d("reward", String.valueOf(rewardpoints));

                        //checking ringtone toogle on/off
                        SharedPreferences preferences = getSharedPreferences("AdzAppRingtoneSwitchValue", Context.MODE_PRIVATE);
                        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                            boolean settingsCanWrite = Settings.System.canWrite(getApplicationContext());
                            if(!settingsCanWrite){
                                    pointsToAdd = 2.5;
                            }else{
                                if( preferences.getBoolean("active", true)){
                                    pointsToAdd = 5.0;
                                }else{
                                    pointsToAdd= 2.5;
                                }

                            }
                        }
                        else{
                            if(preferences.getBoolean("active", true) ){
                                pointsToAdd = 5.0;
                            }else{
                                pointsToAdd = 2.5;
                            }
                        }

                        updateUserRewards(rewardpoints, pointsToAdd);
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {


                }
            });

            //TODO: remove this block of code and repalce it with the code to read from the shared pefs
            // TODO: CONTINUE -- for ad_id and then call "callAD()" mehod with id as a string arg
            // callAd();//chnging this methid from oncreate to onResume for better staibilization TODO: un cmnt in goes wrng
//              db.collection("Published Ads")
//                    .get()
//                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                        @Override
//                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                            if (task.isSuccessful()) {
//                                for (QueryDocumentSnapshot document : task.getResult()) {
//                                    adsID.add(document.getId());
//                                    Log.d("doc", document.getId() + " => " + document.getData());
//
//                                }
//                                callAd();
//                            } else {
//                                Log.d("doc", "Error getting documents: ", task.getException());
//                            }
//                        }
//                    });
        }

    }

    @Override
    public void onResume() { //this was called coz of abnormal behaviour of ad pop up nly sound no activity. TODO: if goes wrng remove whole method unmcnt 112
        super.onResume();
        callAd();
    }

    private void initializeView() {

        rootLayout = findViewById(R.id.rootLayout);
        parentLayout = findViewById(R.id.parentLayout);
        profile_pic = findViewById(R.id.profilePic);
        publisher_name = findViewById(R.id.name);
        expires_on = findViewById(R.id.expireson);
        ad_description = findViewById(R.id.adDescription);
        ad_url = findViewById(R.id.txtUrl);
        ad_banner = findViewById(R.id.adBanner);
        ad_close =  findViewById(R.id.closeAd);
        mShimmerViewContainer =  findViewById(R.id.shimmer_view_container);
        mShimmerViewContainer.startShimmerAnimation();
        //Ad_video = findViewById(R.id.adVideo);
        videoView =  findViewById(R.id.video_view);


        parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("docc","got here: onClick of parentLayout");
                callDetailAdActivity();
            }
        });


        ad_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeAdActivity();
            }
        });
    }



    private void closeAdActivity() {

        AdPopUpActivity.this.finish();
        System.exit(0);
    }

    private void callAd() {

        SharedPreferences sharedPreferences = getSharedPreferences(PhoneStateReceiver.REWARDZ_PREFS_TONE, Context.MODE_PRIVATE);
        String adID = sharedPreferences.getString(PhoneStateReceiver.OLD_REWARDZ_TONE,"1rPyNckD3E3Vn4eWcX6t");
        Log.d("ringtone","adID from pref "+adID);
        DocumentReference randomAd = db.collection("Published Ads").document(adID);
        randomAd.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    //DocumentSnapshot doc = task.getResult();
                    doc = task.getResult();
                    mShimmerViewContainer.stopShimmerAnimation();
                    parentLayout.setVisibility(View.VISIBLE);
                    mShimmerViewContainer.setVisibility(View.INVISIBLE);//TODO: check how shimmer layout working IT has to overlap on Below layout, IF needed change root layout to relative -- FINISHED

                    try{
                        Picasso.with(getApplicationContext()).load(doc.get("publisher_image").toString()).into(profile_pic);
//                        Picasso.get().load(doc.get("publisher_image").toString()).into(profile_pic);
                        if(doc.get("ad_type").toString().equals("standard") || doc.get("ad_type").toString().equals("basic")){
                            //Picasso.get().load(doc.get("ad_banner").toString()).into(ad_banner);
                            Picasso.with(getApplicationContext()).load(doc.get("ad_banner").toString()).into(ad_banner);
                        }
                        if(doc.get("ad_type").toString().equals("premium")){
                            ad_banner.setVisibility(View.GONE);
                            videoView.setVisibility(View.VISIBLE);
                            if(DeviceLocked.isDeviceLocked(getApplicationContext())){
                                videoView.setVideoPath(doc.get("video_url").toString()).getPlayer();
                            }
                            else{
                                videoView.setVideoPath(doc.get("video_url").toString()).getPlayer().start();
                            }

//                            Ad_video.setVisibility(View.VISIBLE);
//                            //initializing the youtube player and sending youtube video ID to stream.
//                            Ad_video.initialize(new YouTubePlayerInitListener() {
//                                @Override
//                                public void onInitSuccess(@NonNull final YouTubePlayer initializedYouTubePlayer) {
//                                    initializedYouTubePlayer.addListener(new AbstractYouTubePlayerListener() {
//                                        @Override
//                                        public void onReady() {
//                                            String videoId = getYoutubeID(doc.get("video_url").toString());
//
//                                            initializedYouTubePlayer.loadVideo(videoId,0);
//                                        }
//                                    });
//                                }
//                            }, true);
//                            //end of youtube player
                        }

                        publisher_name.setText(doc.get("publisher_name").toString());
                        expires_on.setText(doc.get("expires_on").toString());
                        ad_description.setText(doc.get("ad_description").toString());
                        ad_url.setText(doc.get("ad_url").toString());
                    }catch (Exception errr){
                        Log.d("doc", errr.getMessage());
                    }


                    //PUSHING THIS AD TO USER NOTIFICATIONS THROUGH THIS METHOD
                    //sendAdtoNotif(adsID.get(index));
                }
            }
        });

    }
//TODO: if req. use tis code to send the ad to user as a notif in-app
//    private void sendAdtoNotif(String AdID) {
//      String  userId = user.getUid();
//
//        Map< String, Object > newNotif = new HashMap< >();
//        newNotif.put("user_id",userId);
//        newNotif.put("ad_id",AdID);
//
//        db.collection("Notifications").add(newNotif).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
//            @Override
//            public void onSuccess(DocumentReference documentReference) {
//                Log.d("notification","send to db");
//            }
//        }).addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception e) {
//                Log.d("notification", "cannot send to db");
//            }
//        });
//
//
//    }

    public void updateUserRewards(Double rewardpoints, Double pointsToAdd) {
        final Double update_points = rewardpoints + pointsToAdd;
        DocumentReference updatingRewards = db.collection("userRewards").document(user.getUid());
        updatingRewards.update("Rewards", update_points).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(getApplicationContext(),  getString(R.string.rewards_updated)+ update_points + ".", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), getString(R.string.processing_failure), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void callDetailAdActivity() {

        Intent intent = new Intent(AdPopUpActivity.this, DetailAdActivity.class);
        try{
            intent.putExtra("adPublisherPic",doc.get("publisher_image").toString() );
            intent.putExtra("adPublisherName", doc.get("publisher_name").toString());
            intent.putExtra("adExpiresOn", doc.get("expires_on").toString());
            intent.putExtra("adBanner", doc.get("ad_banner").toString());
            intent.putExtra("adDescription", doc.get("ad_description").toString());
            intent.putExtra("adUrl", doc.get("ad_url").toString());
            intent.putExtra("adType", doc.get("ad_type").toString());
            intent.putExtra("adVideoUrl", doc.get("video_url").toString());
            intent.putExtra("adPoints",doc.get("points").toString());
            intent.putExtra("adCouponCode", doc.get("coupon_code").toString());
            intent.putExtra("adID", doc.getId());
        }catch (Exception err){
            Log.d("errr", err.getMessage());
        }

       /* //for shared transition
        final Pair<View, String> p1 = Pair.create((View)profile_pic, "profilePic");
        final Pair<View, String> p2 = Pair.create((View)publisher_name, "publisherName");
        final Pair<View, String> p3 = Pair.create((View)expires_on, "expiryDate");
        final Pair<View, String> p4 = Pair.create((View)ad_description, "description");
        final Pair<View, String> p5 = Pair.create((View)ad_banner, "banner");
        final Pair<View, String> p6 = Pair.create((View)videoView, "videoPlayer");
        ActivityOptionsCompat options = ActivityOptionsCompat.
                makeSceneTransitionAnimation(this, p1, p2, p3, p4, p5, p6); */
        startActivity(intent/*, options.toBundle()*/);
//removing the ad dailog activity from the stack.
        closeAdActivity();
    }

    @Override
    protected void onPause(){
        if(videoView.getVisibility() == View.VISIBLE){
            videoView.getPlayer().pause();
        }
        super.onPause();


    }
    @Override
    protected void onDestroy(){
        if(videoView.getVisibility() == View.VISIBLE){
            videoView.getPlayer().pause();
        }
        super.onDestroy();


    }
}



/*package com.dthoughts.harsha.adzapp.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;

import com.dthoughts.harsha.adzapp.R;

import com.dthoughts.harsha.adzapp.helper.DeviceLocked;
import com.squareup.picasso.Picasso;


import tcking.github.com.giraffeplayer2.VideoView;


import static com.dthoughts.harsha.adzapp.receiver.PhoneStateReceiver.OLD_REWARDZ_TONE;
import static com.dthoughts.harsha.adzapp.receiver.PhoneStateReceiver.REWARDZ_PREFS_TONE;


public class AdPopUpActivity extends Activity {

    FirebaseFirestore db;
    FirebaseUser user;
    Double rewardpoints, pointsToAdd= Double.valueOf(5);
    //ArrayList<String> adsID;

     TextView publisher_name, city, expires_on, ad_description, ad_url;
     ImageView profile_pic, ad_banner;
     ImageButton ad_close;
     LinearLayout parentLayout;
     RelativeLayout rootLayout;
    VideoView videoView;

    DocumentSnapshot doc;

    private ShimmerFrameLayout mShimmerViewContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.setFinishOnTouchOutside(true);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ad_pop_up);
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        initializeView();


        db = FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();

       // adsID = new ArrayList<>();



        if(user!=null){
            DocumentReference userReawrdPoints = db.collection("userRewards").document(user.getUid());
            userReawrdPoints.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if(task.isSuccessful()){
                        DocumentSnapshot doc = task.getResult();
                        rewardpoints = Double.valueOf(doc.get("Rewards").toString());
                        Log.d("reward", String.valueOf(rewardpoints));
                        updateUserRewards(rewardpoints, pointsToAdd);
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {


                }
            });

            //TODO: remove this block of code and repalce it with the code to read from the shared pefs
            // TODO: CONTINUE -- for ad_id and then call "callAD()" mehod with id as a string arg
              callAd();
//              db.collection("Published Ads")
//                    .get()
//                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                        @Override
//                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                            if (task.isSuccessful()) {
//                                for (QueryDocumentSnapshot document : task.getResult()) {
//                                    adsID.add(document.getId());
//                                    Log.d("doc", document.getId() + " => " + document.getData());
//
//                                }
//                                callAd();
//                            } else {
//                                Log.d("doc", "Error getting documents: ", task.getException());
//                            }
//                        }
//                    });
        }

    }

    private void initializeView() {

        rootLayout = findViewById(R.id.rootLayout);
        parentLayout = findViewById(R.id.parentLayout);
        profile_pic = findViewById(R.id.profilePic);
        publisher_name = findViewById(R.id.name);
        expires_on = findViewById(R.id.expireson);
        ad_description = findViewById(R.id.adDescription);
        ad_url = findViewById(R.id.txtUrl);
        ad_banner = findViewById(R.id.adBanner);
        ad_close =  findViewById(R.id.closeAd);
        mShimmerViewContainer =  findViewById(R.id.shimmer_view_container);
        mShimmerViewContainer.startShimmerAnimation();
        videoView =  findViewById(R.id.video_view);


        parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               callDetailAdActivity();
            }
        });


        ad_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeAdActivity();
            }
        });
    }



    private void closeAdActivity() {

        AdPopUpActivity.this.finish();
        System.exit(0);
    }

    private void callAd() {

        SharedPreferences sharedPreferences = getSharedPreferences(REWARDZ_PREFS_TONE, Context.MODE_PRIVATE);
        String adID = sharedPreferences.getString(OLD_REWARDZ_TONE,"1rPyNckD3E3Vn4eWcX6t");
        Log.d("ringtone","adID from pref "+adID);
        DocumentReference randomAd = db.collection("Published Ads").document(adID);
        randomAd.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    //DocumentSnapshot doc = task.getResult();
                    doc = task.getResult();
                    mShimmerViewContainer.stopShimmerAnimation();
                    parentLayout.setVisibility(View.VISIBLE);
                    mShimmerViewContainer.setVisibility(View.INVISIBLE);//TODO: check how shimmer layout working IT has to overlap on Below layout, IF needed change root layout to relative -- FINISHED

                    try{
                        Picasso.get().load(doc.get("publisher_image").toString()).into(profile_pic);
                        if(doc.get("ad_type").toString().equals("standard") || doc.get("ad_type").toString().equals("basic")){
                            Picasso.get().load(doc.get("ad_banner").toString()).into(ad_banner);
                        }
                        if(doc.get("ad_type").toString().equals("premium")){
                            ad_banner.setVisibility(View.INVISIBLE);
                            videoView.setVisibility(View.VISIBLE);
                            if(DeviceLocked.isDeviceLocked(getApplicationContext())){
                                videoView.setVideoPath(doc.get("video_url").toString()).getPlayer();
                            }
                            else{
                                videoView.setVideoPath(doc.get("video_url").toString()).getPlayer().start();
                            }

                        }

                        publisher_name.setText(doc.get("publisher_name").toString());
                        expires_on.setText(doc.get("expires_on").toString());
                        ad_description.setText(doc.get("ad_description").toString());
                        ad_url.setText(doc.get("ad_url").toString());
                    }catch (Exception errr){
                        Log.d("doc", errr.getMessage());
                    }


                    //PUSHING THIS AD TO USER NOTIFICATIONS THROUGH THIS METHOD
                    //sendAdtoNotif(adsID.get(index));
                }
            }
        });

    }
//TODO: if req. use tis code to send the ad to user as a notif in-app
//    private void sendAdtoNotif(String AdID) {
//      String  userId = user.getUid();
//
//        Map< String, Object > newNotif = new HashMap< >();
//        newNotif.put("user_id",userId);
//        newNotif.put("ad_id",AdID);
//
//        db.collection("Notifications").add(newNotif).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
//            @Override
//            public void onSuccess(DocumentReference documentReference) {
//                Log.d("notification","send to db");
//            }
//        }).addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception e) {
//                Log.d("notification", "cannot send to db");
//            }
//        });
//
//
//    }

    public void updateUserRewards(Double rewardpoints, Double pointsToAdd) {
        final Double update_points = rewardpoints + pointsToAdd;
        DocumentReference updatingRewards = db.collection("userRewards").document(user.getUid());
        updatingRewards.update("Rewards", update_points).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(getApplicationContext(),  getString(R.string.rewards_updated)+ update_points + ".", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), getString(R.string.processing_failure), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void callDetailAdActivity() {

        Intent intent = new Intent(AdPopUpActivity.this, DetailAdActivity.class);
        intent.putExtra("adPublisherPic",doc.get("publisher_image").toString() );
        intent.putExtra("adPublisherName", doc.get("publisher_name").toString());
        intent.putExtra("adExpiresOn", doc.get("expires_on").toString());
        intent.putExtra("adBanner", doc.get("ad_banner").toString());
        intent.putExtra("adDescription", doc.get("ad_description").toString());
        intent.putExtra("adUrl", doc.get("ad_url").toString());
        intent.putExtra("adType", doc.get("ad_type").toString());
        intent.putExtra("adVideoUrl", doc.get("video_url").toString());
        intent.putExtra("adPoints",doc.get("points").toString());
        intent.putExtra("adCouponCode", doc.get("coupon_code").toString());
        intent.putExtra("adID", doc.getId());

        startActivity(intent);
//removing the ad dailog activity from the stack.
        closeAdActivity();
    }
} */
