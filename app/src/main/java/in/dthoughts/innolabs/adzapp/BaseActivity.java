package in.dthoughts.innolabs.adzapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.PendingDynamicLinkData;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import in.dthoughts.innolabs.adzapp.helper.PrefManager;
import in.dthoughts.innolabs.adzapp.ui.DetailAdActivity;
import in.dthoughts.innolabs.adzapp.ui.MainActivity;
import in.dthoughts.innolabs.adzapp.ui.intro.IntroActivity;

public class BaseActivity extends AppCompatActivity {

    private PrefManager prefManager;
    DocumentSnapshot doc;
    FirebaseFirestore db;

    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            setTheme(R.style.SplashTheme);
        } else {
            setTheme(R.style.AppTheme);
        }
        super.onCreate(savedInstanceState);


        FirebaseDynamicLinks.getInstance().getDynamicLink(getIntent())
                .addOnSuccessListener(new OnSuccessListener<PendingDynamicLinkData>() {
                    @Override
                    public void onSuccess(PendingDynamicLinkData pendingDynamicLinkData) {
                        Uri deepLink = null;
                        if (pendingDynamicLinkData != null) {
                            deepLink = pendingDynamicLinkData.getLink();
                            Log.d("docc5", deepLink.toString());
                            String path = deepLink.getPath();
                            String[] parts = path.split("/");
                            for (int i = 0; i < parts.length; i++) {
                                Log.d("docc5", "part of " + i + ":" + parts[i].toString());
                            }

                            if (parts[1].contains("detailad") /*== "detailad"*/) {
                                Log.d("docc5", "came into first deatil ad dynamic link");
                                String AdId = parts[2];
                                getAdDetails(AdId);
                            }
                        }

                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        if (/*user != null &&*/ deepLink != null
                                && deepLink.getBooleanQueryParameter("invitedby", false)) {
                            String referrerUid = deepLink.getQueryParameter("invitedby");
                            Log.d("docc5", "base acitivty got reference id" + referrerUid);
                            SharedPreferences.Editor editor = getSharedPreferences("AdzApp_INVITATION_REF", MODE_PRIVATE).edit();
                            editor.putString("REF_ID", referrerUid);
                            editor.commit();

                        }

                    }
                });

        prefManager = new PrefManager(this);
        if (prefManager.isFirstTimeLaunch() || prefManager.isIntroFinished() == false) {
            Log.d("doc", String.valueOf(prefManager.isFirstTimeLaunch()));
            prefManager.setFirstTimeLaunch(false);
            startActivity(new Intent(BaseActivity.this, IntroActivity.class));

            finish();

        } else {
            startActivity(new Intent(BaseActivity.this, MainActivity.class));
            finish();
        }
        setContentView(R.layout.activity_login);
        onNewIntent(getIntent());
    }


    protected void onNewIntent(Intent intent) {
        String action = intent.getAction();
        String data = intent.getDataString();
        if (Intent.ACTION_VIEW.equals(action) && data != null) {
            //String recipeId = data.substring(data.lastIndexOf("/") + 1);

        }
    }

    private void getAdDetails(String AdId) {
        //TODO:SHOWING LODAING STUFF
        db = FirebaseFirestore.getInstance();
        DocumentReference scannedAd = db.collection("Published Ads").document(AdId);
        scannedAd.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    doc = task.getResult();
                    if (!doc.exists()) {
                        Log.d("docc2", "no ad found");
                        //callErrorDialog();
                    } else {
                        Intent intent = new Intent(BaseActivity.this, DetailAdActivity.class);
                        try {
                            intent.putExtra("adPublisherPic", doc.get("publisher_image").toString());
                            intent.putExtra("adPublisherName", doc.get("publisher_name").toString());
                            intent.putExtra("adExpiresOn", doc.get("expires_on").toString());
                            intent.putExtra("adBanner", doc.get("ad_banner").toString());
                            intent.putExtra("adDescription", doc.get("ad_description").toString());
                            intent.putExtra("adUrl", doc.get("ad_url").toString());
                            intent.putExtra("adType", doc.get("ad_type").toString());
                            intent.putExtra("adVideoUrl", doc.get("video_url").toString());
                            intent.putExtra("adPoints", doc.get("points").toString());
                            intent.putExtra("adCouponCode", doc.get("coupon_code").toString());
                            intent.putExtra("adID", doc.getId());
                        } catch (Exception err) {
                            Log.d("errr", err.getMessage());
                        }
                        startActivity(intent);
                        finish();
                    }

                } else {
                    Toast.makeText(BaseActivity.this, "Invalid AD to fetch or please try again", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


}
