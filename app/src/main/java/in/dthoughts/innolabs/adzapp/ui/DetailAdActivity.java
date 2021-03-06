package in.dthoughts.innolabs.adzapp.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.text.util.Linkify;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.codemybrainsout.ratingdialog.RatingDialog;
import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetSequence;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import in.dthoughts.innolabs.adzapp.R;
import in.dthoughts.innolabs.adzapp.helper.PrefManager;
import tcking.github.com.giraffeplayer2.VideoInfo;
import tcking.github.com.giraffeplayer2.VideoView;


public class DetailAdActivity extends AppCompatActivity {

    private static final String USER_ID_KEY = "user_id", POINTS_KEY = "points", COUPON_CODE_KEY = "coupon_code",
            TIMESTAMP_KEY = "time_stamp", PUBLISHER_NAME_KEY = "publisher_name", EXPIRES_ON_KEY = "expires_on",
            AD_ID_KEY = "ad_id", FEEDBACK_KEY = "feedback";

    ImageView Publisher_pic, Ad_banner;
    TextView Publisher_name, Expires_on, Ad_description, Ad_url, couponCode;
    Dialog myDialog;
    VideoView videoView;
    Button Redeem_button;
    String adPublisherPic, adPublisherName, adExpiresOn, adBanner, adDescription, adUrl, adType, adVideoUrl, adPoints, adCouponCode, adRewardThrough, adID;
    double userTotalPoints, pointsAfterDeduction;
    Long AnalyticsDetailAdWatchers, AnalyticsRedeemButtonClicked;
    PrefManager prefManager;
    FirebaseFirestore db;
    FirebaseUser user;
    FirebaseAnalytics firebaseAnalytics;
    private File file;
    String Video_path;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(R.layout.activity_detail_ad);
        Toolbar mToolbar = findViewById(R.id.toolbar);
        mToolbar.setTitle(getString(R.string.title_DetailAd));
        mToolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);

        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();
            }
        });

        firebaseAnalytics = FirebaseAnalytics.getInstance(this);

        prefManager = new PrefManager(this);
        if (prefManager.isDetailAdTutFinished() == false) {
            new TapTargetSequence(this)
                    .targets(TapTarget.forView(findViewById(R.id.name), "Publisher name", "you can see same of publisher"),
                            TapTarget.forView(findViewById(R.id.expireson), "Expires on", "This is the expiration date of ad"),
                            TapTarget.forView(findViewById(R.id.adDescription), "Description", "AD's description"),
                            TapTarget.forView(findViewById(R.id.redeemButton), "Redeem buton", "Button to redeem")
                                    .cancelable(false))
                    .listener(new TapTargetSequence.Listener() {
                        @Override
                        public void onSequenceFinish() {
                            prefManager.setIsDetailAdTutFinished(true);
                        }

                        @Override
                        public void onSequenceStep(TapTarget lastTarget, boolean targetClicked) {

                        }

                        @Override
                        public void onSequenceCanceled(TapTarget lastTarget) {

                        }
                    }).start();


        }



        final Bundle extras = getIntent().getExtras();

        adPublisherPic = extras.get("adPublisherPic").toString();
        adPublisherName = extras.get("adPublisherName").toString();
        adExpiresOn = extras.get("adExpiresOn").toString();
        adBanner = extras.get("adBanner").toString();
        adDescription = extras.get("adDescription").toString();
        adUrl = extras.get("adUrl").toString();
        adType = extras.get("adType").toString();
        adVideoUrl = extras.get("adVideoUrl").toString();
        adPoints = extras.get("adPoints").toString();
        adCouponCode = extras.get("adCouponCode").toString();
        adRewardThrough = extras.get("rewardThrough").toString();
        Log.d("docc9", adRewardThrough);
        adID = extras.get("adID").toString();

        String folder_path = Environment.getExternalStorageDirectory() + File.separator
                + getString(R.string.app_name)+ File.separator + "Advideos";
        file = new File( folder_path ) ;
        File list[] = file.listFiles();
        for( int i=0; i< list.length; i++)
        {
            if(list[i].getName().contains(adID)){
                //video_path = list[i].getAbsolutePath();
                Log.d("docc13", list[i].getAbsolutePath());
                Log.d("docc13","path: "+ list[i].getPath());
                Video_path = list[i].getPath();
            }
            //myList.add( list[i].getName() );
        }

        Publisher_pic = findViewById(R.id.profilePic);
        Publisher_name = findViewById(R.id.name);
        Expires_on = findViewById(R.id.expireson);
        Ad_description = findViewById(R.id.adDescription);
        Ad_url = findViewById(R.id.txtUrl);
        Ad_banner = findViewById(R.id.adBanner);
        Redeem_button = findViewById(R.id.redeemButton);
        videoView = findViewById(R.id.video_view);



        Picasso.with(getApplicationContext()).load(adPublisherPic).into(Publisher_pic);
        Publisher_name.setText(adPublisherName);
        Expires_on.setText(adExpiresOn);
        Ad_description.setText(adDescription);
        Ad_url.setText(adUrl);
        Linkify.addLinks(Ad_url, Linkify.WEB_URLS);//To make links(URL) highlighted and clickable

        if (adType.equals("standard") || adType.equals("basic")) {
            Picasso.with(getApplicationContext()).load(adBanner).into(Ad_banner);
        }

        if (adType.equals("premium")) {
            Log.d("doc", "video file detected");
            videoView.setVisibility(View.VISIBLE);
            Ad_banner.setVisibility(View.INVISIBLE);
            if(!TextUtils.isEmpty(Video_path)){
                adVideoUrl = Video_path;
                Log.d("docc13","detail ad ofline url");
            }

            videoView.setVideoPath(adVideoUrl).getPlayer().start();
            videoView.getPlayer().aspectRatio(VideoInfo.AR_ASPECT_FILL_PARENT);


        }
        if(adRewardThrough.contains("link")){
            Redeem_button.setVisibility(View.GONE);
        }
        Redeem_button.setText(getString(R.string.redeem) + " " + adPoints);


        db = FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();


        //TODO: disable button after user pressing the redeem button while transaction is processing and add a progressbar as indication on button
        Redeem_button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //TODO: deduct the points from user wallet in DB. and add this to transaction table -- FNISHED
                //TODO: ->which contains AD-id, User-id, TimeSpan. --FINISHED
                //TODO: ADD coupon code attribute to published adds document and show the code here after deducting the points __FINISHED
                Bundle bundle = new Bundle();
                bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "REDEEM");
                bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "REDEEM_CLICKED");
                bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "Button");
                firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);

                //code to show coupon code to user
                user.reload();
                if (user.isEmailVerified()) {
                    deductRewardPoints();
                } else {
                    emailVerifyAlert();
                }

            }
        });
        addWatcherCountToDetailAd();
        openAppRater();

    }

    private void addWatcherCountToDetailAd() {
        db.collection("Published Ads").document(adID).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                AnalyticsDetailAdWatchers = Long.parseLong(String.valueOf(documentSnapshot.get("AnalyticsDetailAdWatchers")));
                if(AnalyticsDetailAdWatchers >= 0){
                    db.collection("Published Ads").document(adID).update("AnalyticsDetailAdWatchers",AnalyticsDetailAdWatchers+1).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d("adsAnalytics","updated count to: "+ AnalyticsDetailAdWatchers);
                        }
                    });
                }
            }
        });

    }

    private void openAppRater() {

        final RatingDialog ratingDialog = new RatingDialog.Builder(this)
                .threshold(3)
                .session(5)
                .onRatingBarFormSumbit(new RatingDialog.Builder.RatingDialogFormListener() {
                    @Override
                    public void onFormSubmitted(String feedback) {

                        sendFeedBackToDb(feedback);

                    }
                }).build();

        ratingDialog.show();
    }

    private void sendFeedBackToDb(String feedback) {

        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String timestamp = sdf.format(c.getTime());

        Map<String, Object> newUserFeedback = new HashMap<>();
        newUserFeedback.put(USER_ID_KEY, user.getUid());
        newUserFeedback.put(TIMESTAMP_KEY, timestamp);
        newUserFeedback.put(FEEDBACK_KEY, feedback);

        db.collection("Users Feedback").add(newUserFeedback).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                Toast.makeText(getApplicationContext(), getString(R.string.Feedback_success), Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), getString(R.string.Feedback_failure), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void sendEmailVerification() {
        user = FirebaseAuth.getInstance().getCurrentUser();
        user.sendEmailVerification()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(getApplicationContext(), getString(R.string.email_sent_sucessfully) + user.getEmail(), Toast.LENGTH_SHORT).show();
                            Log.d("doc", "Verification email sent to " + user.getEmail());
                        } else {
                            Log.d("doc", "sendEmailVerification failed!", task.getException());
                        }
                    }
                });
    }


    private void deductRewardPoints() {

        db.collection("userRewards").document(user.getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                userTotalPoints = documentSnapshot.getDouble("Rewards");
                //TODO:uncmnt unnecessary toasts and logs , use try catch for null in above line
                //AFter getting userTotalPoints check they r more than req. and take necessary steps
                if (userTotalPoints < Double.parseDouble(adPoints)) {
                    Toast.makeText(getApplicationContext(), getString(R.string.low_in_wallet), Toast.LENGTH_LONG).show();

                } else {
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
        Log.d("coupon", adCouponCode);
        displayCouponCode();
//        myDialog = new Dialog(this);
//        myDialog.setContentView(R.layout.coupon_code_alert);
//        couponCode = myDialog.findViewById(R.id.copuon_code);
//        couponCode.setText(adCouponCode);
//        myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//        myDialog.show();

        //Toast.makeText(this, adCouponCode,Toast.LENGTH_LONG).show();

    }

    private void displayCouponCode() {
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


        Map<String, Object> newUserTransaction = new HashMap<>();
        newUserTransaction.put(USER_ID_KEY, user.getUid());
        newUserTransaction.put(POINTS_KEY, adPoints);
        newUserTransaction.put(PUBLISHER_NAME_KEY, adPublisherName);
        newUserTransaction.put(COUPON_CODE_KEY, adCouponCode);
        newUserTransaction.put(TIMESTAMP_KEY, timestamp);
        newUserTransaction.put(EXPIRES_ON_KEY, adExpiresOn);
        newUserTransaction.put(AD_ID_KEY, adID);

        db.collection("Transactions").add(newUserTransaction).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                Log.d("transaction", " success " + user.getUid());
                db.collection("Published Ads").document(adID).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        AnalyticsRedeemButtonClicked = documentSnapshot.getLong("AnalyticsRedeemButtonClicked");
                        if(AnalyticsRedeemButtonClicked >= 0){
                            db.collection("Published Ads").document(adID).update("AnalyticsRedeemButtonClicked",AnalyticsRedeemButtonClicked+1).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d("adsAnalytics","updated AnalyticsRedeemButtonClicked count to: "+ AnalyticsRedeemButtonClicked);
                                }
                            });
                        }
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), getString(R.string.processing_failure), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void emailVerifyAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(DetailAdActivity.this);
        // Setting Dialog Title
        alertDialog.setTitle(getString(R.string.emailVerification_dailog_title));
        // Setting Dialog Message
        alertDialog.setMessage(getString(R.string.emailVerification_dailog_message));
        // Setting Icon to Dialog
        alertDialog.setIcon(R.drawable.ic_warning_black_24dp);
        //make dailog stays on screen even clicks some where on screen
        alertDialog.setCancelable(false);
        // Setting Positive "Yes" Button
        alertDialog.setPositiveButton(getString(R.string.resend_email), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // User pressed YES button. Write Logic Here
                Bundle bundle = new Bundle();
                bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "EMAIL_VERIFICATION");
                bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "SEND_EMAIL_VERIFY_CLICKED");
                bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "Button");
                firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
                sendEmailVerification();
                Toast.makeText(getApplicationContext(), getString(R.string.email_sent_sucessfully), Toast.LENGTH_SHORT).show();
            }
        });
        // Setting Negative "NO" Button
        alertDialog.setNegativeButton(getString(R.string.dont_send_email), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // User pressed No button. Write Logic Here
                Bundle bundle = new Bundle();
                bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "EMAIL_VERIFICATION");
                bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "DONT_EMAIL_VERIFY_CLICKED");
                bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "Button");
                firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
                Toast.makeText(getApplicationContext(), getString(R.string.dont_send_email_toast), Toast.LENGTH_SHORT).show();
            }
        });
        // Setting Netural "Cancel" Button
        alertDialog.setNeutralButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // User pressed Cancel button. Write Logic Here
                Bundle bundle = new Bundle();
                bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "EMAIL_VERIFICATION");
                bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "CANCEL_EMAIL_VERIFY_CLICKED");
                bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "Button");
                firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
                Toast.makeText(getApplicationContext(), "You clicked on Cancel",
                        Toast.LENGTH_SHORT).show();
            }
        });

        // Showing Alert Message
        alertDialog.show();
    }

    @Override
    protected void onPause() {
        if (videoView.getVisibility() == View.VISIBLE) {
            videoView.getPlayer().pause();
        }
        super.onPause();
    }


    @Override
    public void onUserLeaveHint () {

    }

}
