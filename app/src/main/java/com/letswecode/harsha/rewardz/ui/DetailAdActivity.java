package com.letswecode.harsha.rewardz.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
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
import com.google.firebase.firestore.QuerySnapshot;
import com.letswecode.harsha.rewardz.R;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

public class DetailAdActivity extends AppCompatActivity {

    private static final String USER_ID_KEY ="user_id", POINTS_KEY = "points", COUPON_CODE_KEY = "coupon_code",
            TIMESTAMP_KEY = "time_stamp", PUBLISHER_NAME_KEY = "publisher_name", EXPIRES_ON_KEY = "expires_on", AD_ID_KEY = "ad_id";

    ImageView Publisher_pic, Ad_banner;
    TextView Publisher_name, Expires_on, Ad_description, Ad_url, couponCode;
    Dialog myDialog;
    VideoView Ad_video;
    Button Redeem_button;
    String adPublisherPic, adPublisherName,adExpiresOn,adBanner,adDescription,adUrl,adType,adVideoUrl,adPoints, adCouponCode, adID;
    double userTotalPoints, pointsAfterDeduction;

    FirebaseFirestore db;
    FirebaseUser user;
    boolean redeemed;
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
        adID = extras.get("adID").toString();
        Log.d("doc", adID);//TODO:remove this in release build

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
        Redeem_button.setText(getString(R.string.redeem)+adPoints);

        db = FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();




        //TODO: disable button after user pressing the redeem button while transaction is processing and add a progressbar as indication on button
        Redeem_button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //TODO: deduct the points from user wallet in DB. and add this to transaction table -- FNISHED
                //TODO: ->which contains AD-id, User-id, TimeSpan. --FINISHED
                //TODO: ADD coupon code attribute to published adds document and show the code here after deducting the points __FINISHED


                //code to show coupon code to user
               deductRewardPoints();

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
                    Toast.makeText(getApplicationContext(), getString(R.string.low_in_wallet), Toast.LENGTH_LONG).show();

                }
                else{
                    pointsAfterDeduction = userTotalPoints - Double.parseDouble(adPoints);
                    DocumentReference updatingRewards = db.collection("userRewards").document(user.getUid());
                    updatingRewards.update("Rewards", pointsAfterDeduction).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(getApplicationContext(), getString(R.string.rewards_updated) + pointsAfterDeduction + ".", Toast.LENGTH_SHORT).show();
                            showCouponCode();//code to display coupon code
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getApplicationContext(), getString(R.string.processing_failure), Toast.LENGTH_SHORT).show();
                        }
                    });
                }



            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), getString(R.string.processing_failure), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void showCouponCode() {
        addTransactionToDB();
        Log.d("coupon",adCouponCode);
        displayCouponCode();
//        myDialog = new Dialog(this);
//        myDialog.setContentView(R.layout.coupon_code_alert);
//        couponCode = myDialog.findViewById(R.id.copuon_code);
//        couponCode.setText(adCouponCode);
//        myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//        myDialog.show();

        //Toast.makeText(this, adCouponCode,Toast.LENGTH_LONG).show();

    }

    private void displayCouponCode(){
        myDialog = new Dialog(this);
        myDialog.setContentView(R.layout.coupon_code_alert);
        couponCode = myDialog.findViewById(R.id.copuon_code);
        couponCode.setText(adCouponCode);
        myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        myDialog.show();
    }

    private void addTransactionToDB() {
        //code to add transaction to db

        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String timestamp = sdf.format(c.getTime());


        Map< String, Object > newUserTransaction = new HashMap< >();
        newUserTransaction.put(USER_ID_KEY, user.getUid());
        newUserTransaction.put(POINTS_KEY,adPoints);
        newUserTransaction.put(PUBLISHER_NAME_KEY, adPublisherName);
        newUserTransaction.put(COUPON_CODE_KEY,adCouponCode);
        newUserTransaction.put(TIMESTAMP_KEY, timestamp);
        newUserTransaction.put(EXPIRES_ON_KEY, adExpiresOn);
        newUserTransaction.put(AD_ID_KEY, adID);

        db.collection("Transactions").add(newUserTransaction).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                Log.d("transaction", " success "+user.getUid());
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(),getString(R.string.processing_failure),Toast.LENGTH_LONG).show();
            }
        });
    }


}
