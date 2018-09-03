package com.letswecode.harsha.rewardz.ui;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SubscriptionManager;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.javiersantos.appupdater.AppUpdater;
import com.github.javiersantos.appupdater.enums.Display;
import com.github.javiersantos.appupdater.enums.Duration;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.protobuf.CodedOutputStream;
import com.letswecode.harsha.rewardz.BuildConfig;
import com.letswecode.harsha.rewardz.R;
import com.letswecode.harsha.rewardz.authentication.LoginActivity;
import com.letswecode.harsha.rewardz.helper.PrefManager;
import com.letswecode.harsha.rewardz.authentication.SignupActivity;
import com.letswecode.harsha.rewardz.fragments.HomeFragment;
import com.letswecode.harsha.rewardz.fragments.MarketFragment;
import com.letswecode.harsha.rewardz.fragments.ProfileFragment;
import com.letswecode.harsha.rewardz.fragments.WalletFragment;
import com.letswecode.harsha.rewardz.service.DownloadRt;


//import com.letswecode.harsha.rewardz.fragments.SupportFragment;

public class MainActivity extends AppCompatActivity {


    private FirebaseAuth.AuthStateListener authListener;
    private FirebaseAuth auth;
    FirebaseUser user;
    boolean emailVerified;
    private PrefManager prefManager;
    ConstraintLayout container;
    BottomNavigationView navigation;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment fragment = null;
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    fragment = new HomeFragment();
                    break;
                case R.id.navigation_market:
                    fragment = new MarketFragment();
                    break;
                case R.id.navigation_profile:
                    fragment = new ProfileFragment();
                    break;
                case R.id.navigation_wallet:
                  fragment = new WalletFragment();
                  break;
            }
            return loadFragment(fragment);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme); //for splash screen runs only app taking long time to initialize
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        container = findViewById(R.id.container);
        navigation = findViewById(R.id.navigation);//Dont delete this bruh!
//TODO: first check internet connection then proceed

        //checking first run of app
        checkFirstRun();

        boolean dualSim = Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1 && SubscriptionManager.from(this).getActiveSubscriptionInfoCount() >= 2;
        if(dualSim){
            Log.d("docc","dual sim detected");
        }else{
            Log.d("docc","dual sim not detected");
        }

        //get firebase auth instance
        auth = FirebaseAuth.getInstance();

        //get current user
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();


        if (user == null) {
            // user auth state is changed - user is null
            // launch login activity
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            finish();
        }

        authListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user == null) {
                    // user auth state is changed - user is null
                    // launch login activity
                    startActivity(new Intent(MainActivity.this, SignupActivity.class));
                    finish();
                }
            }
        };

        //for app updater dialog, notification, snack bar
        AppUpdater appUpdater = new AppUpdater(getApplicationContext())
                .setDisplay(Display.NOTIFICATION)
                .setDisplay(Display.SNACKBAR)
                .setDuration(Duration.INDEFINITE);
        appUpdater.start();

        //reloading user to check whether his/her email is verified or not(As firebase cache user data we have to reload)
        if(user != null){
            user.reload();
            emailVerified = user.isEmailVerified();
                 if(!emailVerified){//TODO: IT SHOUD EMAIL NOT VERIFED
                emailVerifySnackBar(container, "Email not verified");
            }
        }

        //loading the default home fragment
        loadFragment(new HomeFragment());

        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
//TODO:uncmnt this service inoreder  to run service -- FINISHED
        prefManager = new PrefManager(this);
        if (prefManager.isFirstTimeLaunchInDay()) {
            //starting service
            Intent intent = new Intent(MainActivity.this, DownloadRt.class);
            ContextCompat.startForegroundService(this,intent);

            //prefManager.setFirstTimeLaunchInDay(false);
        }

    }


    private void emailVerifySnackBar(final View coordinatorLayout, String snackTitle) {
        Log.d("docc","into email verify");
        Snackbar snackbar = Snackbar.make(coordinatorLayout, snackTitle, Snackbar.LENGTH_INDEFINITE)
                .setAction("Resend", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        sendEmailVerification();
                    }
                });
        int height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 56, getResources().getDisplayMetrics());
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) snackbar.getView().getLayoutParams();
        params.setMargins(0, 0, 0, height);
        snackbar.getView().setLayoutParams(params);
        snackbar.show(); Log.d("docc","snackbar shown");


    }

    public  void sendEmailVerification() {
        user = FirebaseAuth.getInstance().getCurrentUser();
        user.sendEmailVerification()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {

                            Toast.makeText(getApplicationContext(), getString(R.string.email_sent_sucessfully)+ user.getEmail(), Toast.LENGTH_SHORT).show();

                        } else {
                            Log.d("doc", "sendEmailVerification failed!", task.getException());
                            Toast.makeText(getApplicationContext(), "Unable to sent mail to "+ user.getEmail(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private boolean loadFragment(Fragment fragment) {
        //switching fragment
        if (fragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .commit();
            return true;
        }
        return false;
    }

    // Initiating Menu XML file (mainmenu.xml)
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.mainmenu, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
//            case R.id.menu_settings:
//                Toast.makeText(MainActivity.this, "Settings activity", Toast.LENGTH_LONG).show();
//              return true;
            case R.id.menu_about:
                startActivity(new Intent(MainActivity.this, AboutActivity.class));
                return true;
            case R.id.signOut:
                auth.signOut();
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
                finish();

            default:
                return super.onOptionsItemSelected(item);
        }

    }

    private void checkFirstRun() {

        final String PREFS_NAME = "MyPrefsFile";
        final String PREF_VERSION_CODE_KEY = "version_code";
        final int DOESNT_EXIST = -1;

        // Get current version code
        int currentVersionCode = BuildConfig.VERSION_CODE;

        // Get saved version code
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        int savedVersionCode = prefs.getInt(PREF_VERSION_CODE_KEY, DOESNT_EXIST);

        // Check for first run or upgrade
        if (currentVersionCode == savedVersionCode) {

            // This is just a normal run
            return;

        } else if (savedVersionCode == DOESNT_EXIST) {

            Toast.makeText(MainActivity.this, "First RUN",Toast.LENGTH_SHORT).show();


            // TODO This is a new install (or the user cleared the shared preferences)
        } else if (currentVersionCode > savedVersionCode) {

            // TODO This is an upgrade
        }

        // Update the shared preferences with the current version code
        prefs.edit().putInt(PREF_VERSION_CODE_KEY, currentVersionCode).apply();
    }


}
