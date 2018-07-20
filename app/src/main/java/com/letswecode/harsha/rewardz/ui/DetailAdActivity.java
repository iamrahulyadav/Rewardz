package com.letswecode.harsha.rewardz.ui;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.letswecode.harsha.rewardz.R;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

public class DetailAdActivity extends AppCompatActivity {

    private static final String USER_ID_KEY ="user_id", POINTS_KEY = "points", COUPON_CODE_KEY = "coupon_code";

    ImageView Publisher_pic, Ad_banner;
    TextView Publisher_name, Expires_on, Ad_description, Ad_url;
    VideoView Ad_video;
    Button Redeem_button;
    String adPublisherPic, adPublisherName,adExpiresOn,adBanner,adDescription,adUrl,adType,adVideoUrl,adPoints, adCouponCode;
    double userTotalPoints, pointsAfterDeduction;

    FirebaseFirestore db;
    FirebaseUser user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_ad);

        final  Bundle  extras = getIntent().getExtras();

        adPublisherPic =  extras.get("adPublisherPic").toString();
        adPublisherName = extras.get("adPublisherName").toString();
        adExpiresOn = extras.get("adExpiresOn").toString();
        adBanner = extras.get("adBanner").toString();
        adDescription = extras.get("adDescription").toString();
        adUrl = extras.get("adUrl").toString();
        adType = extras.get("adType").toString();
        adVideoUrl = extras.get("adVideoUrl").toString();
        adPoints = extras.get("adPoints").toString();
        adCouponCode = extras.get("adCouponCode").toString();


        Publisher_pic =  findViewById(R.id.profilePic);
        Publisher_name = findViewById(R.id.name);
        Expires_on = findViewById(R.id.expireson);
        Ad_description =  findViewById(R.id.adDescription);
        Ad_url = findViewById(R.id.txtUrl);
        Ad_banner = findViewById(R.id.adBanner);
        Ad_video = findViewById(R.id.adVideo);
        Redeem_button =  findViewById(R.id.redeemButton);


        Picasso.get().load(adPublisherPic).into(Publisher_pic);
        Publisher_name.setText(adPublisherName);
        Expires_on.setText(adExpiresOn);
        Ad_description.setText(adDescription);
        Ad_url.setText(adUrl);
        Picasso.get().load(adBanner).into(Ad_banner);
        if(adType.equals("video")){
            Ad_video.setVideoURI(Uri.parse(adVideoUrl));
        }
        Redeem_button.setText("Redeem "+adPoints);

        db = FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
        Redeem_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO: deduct the points from user wallet in DB. and add this to transaction table
                //TODO: ->which contains AD-id, User-id, TimeSpan.
                //TODO: ADD coupon code attribute to published adds document and show the code here after deducting the points

                //code to show coupon code to user
                deductRewardPoints();



                //code to add transaction to db
//                Map< String, Object > newUserTransaction = new HashMap< >();
//                newUserTransaction.put(USER_ID_KEY, user.getUid());
//                newUserTransaction.put(POINTS_KEY,adPoints);
//                newUserTransaction.put(COUPON_CODE_KEY,adCouponCode);
//
//                db.collection("Transactions").add(newUserTransaction).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
//                    @Override
//                    public void onSuccess(DocumentReference documentReference) {
//                        Log.d("transaction", " success "+user.getUid());
//                    }
//                });


            }
        });




    }

    private void deductRewardPoints() {

        db.collection("userRewards").document(user.getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                userTotalPoints = documentSnapshot.getDouble("Rewards");
               //TODO:uncmnt unnecessary toasts and logs
                //AFter getting userTotalPoints check they r more than req. and take necessary steps
                if(userTotalPoints < Double.parseDouble(adPoints)){
                    Toast.makeText(getApplicationContext(), "No enough points in wallet", Toast.LENGTH_LONG).show();

                }
                else{
                    pointsAfterDeduction = userTotalPoints - Double.parseDouble(adPoints);
                    DocumentReference updatingRewards = db.collection("userRewards").document(user.getUid());
                    updatingRewards.update("Rewards", pointsAfterDeduction).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(getApplicationContext(), "Rewards updated to: " + pointsAfterDeduction + ".", Toast.LENGTH_SHORT).show();
                            showCouponCode();//code to display coupon code
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getApplicationContext(), "Unable to process request now, please try later.", Toast.LENGTH_SHORT).show();
                        }
                    });
                }



            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), "unable to process now. please try later", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void showCouponCode() {
        Log.d("coupon",adCouponCode);
        Toast.makeText(this, adCouponCode,Toast.LENGTH_LONG).show();

    }
}
