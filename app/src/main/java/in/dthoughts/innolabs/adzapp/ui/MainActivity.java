package in.dthoughts.innolabs.adzapp.ui;

import android.app.ActionBar;
import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.fragstack.controller.FragmentController;
import com.fragstack.controller.FragmentTransactionOptions;
import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetSequence;
import com.github.javiersantos.appupdater.AppUpdater;
import com.github.javiersantos.appupdater.enums.Display;
import com.github.javiersantos.appupdater.enums.Duration;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import in.dthoughts.innolabs.adzapp.BuildConfig;
import in.dthoughts.innolabs.adzapp.R;
import in.dthoughts.innolabs.adzapp.authentication.LoginActivity;
import in.dthoughts.innolabs.adzapp.authentication.SignupActivity;
import in.dthoughts.innolabs.adzapp.fragments.HomeFragment;
import in.dthoughts.innolabs.adzapp.fragments.MarketFragment;
import in.dthoughts.innolabs.adzapp.fragments.ProfileFragment;
import in.dthoughts.innolabs.adzapp.fragments.WalletFragment;
import in.dthoughts.innolabs.adzapp.helper.DetectDevice;
import in.dthoughts.innolabs.adzapp.helper.PrefManager;
import in.dthoughts.innolabs.adzapp.service.DownloadRt;




public class MainActivity extends AppCompatActivity {


    private FirebaseAuth.AuthStateListener authListener;
    private FirebaseAuth auth;
    FirebaseUser user;
    boolean emailVerified, activityFocused;
    private PrefManager prefManager;
    ConstraintLayout container;
    FragmentController mFragmentController;
    BottomNavigationView navigation;
    //for write settings dailogss
    private static final int CODE_WRITE_SETTINGS_PERMISSION = 111;
    private Button button, write_settings_granted_button, xiaomi_permission_button, xiaomi_permissions_granted_button;

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
            return displayFragment(fragment);//return loadFragment(fragment);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme); //for splash screen runs only app taking long time to initialize
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Action bar related stuff
       getSupportActionBar().setSubtitle("Let's make your ads fly!");

        container = findViewById(R.id.container);
        navigation = findViewById(R.id.navigation);//Don't delete this bruh!
        prefManager = new PrefManager(this);
//TODO: first check internet connection then proceed
        //fragment controller stuff
        mFragmentController = new FragmentController(getSupportFragmentManager(), R.id.fragment_container, savedInstanceState, null);
        //checking first run of app
        checkFirstRun();
        if (DetectDevice.isMiUi()) {
            Log.d("docc", "Xiaomi detected");
            if (prefManager.isPermissionGranted() == false) {
                showXiaomiPermissionDialog();
            }

        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                boolean settingsCanWrite = Settings.System.canWrite(getApplicationContext());
                if (!settingsCanWrite) {
                    showWriteSettingsDialog();

                }
            }
        }
        //end of obtaining permission to write settings. WE NEED THIS IN ORDER TO CHANGE RINGTONE.


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
        if (user != null) {
            user.reload();
            emailVerified = user.isEmailVerified();
            if (!emailVerified) {//TODO: IT SHOUD EMAIL NOT VERIFED
                emailVerifySnackBar(container, "Email not verified");
            }
        }

        //loading the default home fragment
        displayFragment(new HomeFragment());//fragstaack stuff// loadFragment(new HomeFragment());

        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
//TODO:uncmnt this service inoreder  to run service -- FINISHED
        prefManager = new PrefManager(this);
        if (prefManager.isFirstTimeLaunchInDay()) {
            //starting service
            Intent intent = new Intent(MainActivity.this, DownloadRt.class);
            intent.putExtra("CAME_FROM", "MAIN_ACTIVITY");
            ContextCompat.startForegroundService(this, intent);

        }

    }


    private void emailVerifySnackBar(final View coordinatorLayout, String snackTitle) {
        Log.d("docc", "into email verify");
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
        snackbar.show();
        Log.d("docc", "snackbar shown");


    }

    public void sendEmailVerification() {
        user = FirebaseAuth.getInstance().getCurrentUser();
        user.sendEmailVerification()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {

                            Toast.makeText(getApplicationContext(), getString(R.string.email_sent_sucessfully) + user.getEmail(), Toast.LENGTH_SHORT).show();

                        } else {
                            Log.d("doc", "sendEmailVerification failed!", task.getException());
                            Toast.makeText(getApplicationContext(), "Unable to sent mail to " + user.getEmail(), Toast.LENGTH_SHORT).show();
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
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.mainmenu, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.search:
                startActivity(new Intent(MainActivity.this, SearchActivity.class));
                return true;
            case R.id.scan_qr:
                startActivity(new Intent(MainActivity.this, ScanQrActivity.class));
                return true;
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

            Toast.makeText(MainActivity.this, "First RUN", Toast.LENGTH_SHORT).show();


            // TODO This is a new install (or the user cleared the shared preferences)
        } else if (currentVersionCode > savedVersionCode) {

            // TODO This is an upgrade
        }

        // Update the shared preferences with the current version code
        prefs.edit().putInt(PREF_VERSION_CODE_KEY, currentVersionCode).apply();
    }

    //fragstack stuff
    public boolean displayFragment(Fragment fragment) {
        FragmentTransactionOptions fragmentTransactionOptions = new FragmentTransactionOptions.Builder()
                .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out).build();
        mFragmentController.displayFragment(fragment, fragmentTransactionOptions);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (!mFragmentController.popBackStackImmediate()) {
            Log.d("docc", "inside if:" + String.valueOf(mFragmentController.getCurrentFragment().getId()));
            super.onBackPressed();
        }
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.d("docc1", "inside run" + String.valueOf(mFragmentController.getCurrentFragment()));
                if (String.valueOf(mFragmentController.getCurrentFragment()).contains("HomeFragment")) {
                    navigation.setSelectedItemId(R.id.navigation_home);

                }
                if (String.valueOf(mFragmentController.getCurrentFragment()).contains("MarketFragment")) {
                    navigation.setSelectedItemId(R.id.navigation_market);
                }
                if (String.valueOf(mFragmentController.getCurrentFragment()).contains("ProfileFragment")) {
                    navigation.setSelectedItemId(R.id.navigation_profile);
                }
                if (String.valueOf(mFragmentController.getCurrentFragment()).contains("WalletFragment")) {
                    navigation.setSelectedItemId(R.id.navigation_wallet);
                }

            }
        }, 100);
        Log.d("docc1", String.valueOf(mFragmentController.getCurrentFragment()));
        Log.d("docc1", "ID IS" + String.valueOf(navigation.getMenu().findItem(navigation.getSelectedItemId())));


    }

    private void showXiaomiPermissionDialog() {
        final BottomSheetDialog xiaomiSheet = new BottomSheetDialog(this);
        xiaomiSheet.setContentView(R.layout.xiaomi_permission_dialog);
        xiaomi_permission_button = xiaomiSheet.findViewById(R.id.xiaomi_permissions_button);
        xiaomi_permissions_granted_button = xiaomiSheet.findViewById(R.id.xiaomi_permissions_granted_button);
        xiaomi_permission_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                xiaomiSheet.dismiss();
                Intent intent = new Intent();
                intent.setComponent(new ComponentName("com.miui.securitycenter", "com.miui.permcenter.autostart.AutoStartManagementActivity"));
                startActivity(intent);
                showIntro();
            }
        });
        xiaomi_permissions_granted_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                prefManager.setPermissionDialog(true);
                xiaomiSheet.dismiss();
                showIntro();
            }
        });
        xiaomiSheet.show();

    }

    private void showIntro() {

        prefManager = new PrefManager(this);
        if (prefManager.isBottomNavTutFinished() == false) {
            new TapTargetSequence(this)
                    .targets(TapTarget.forView(navigation.findViewById(R.id.navigation_home), "For you", "Tailored ads based on your location"),
                            TapTarget.forView(navigation.findViewById(R.id.navigation_market), "Ads Market", "Here you can find all ads"),
                            TapTarget.forView(navigation.findViewById(R.id.navigation_wallet), "Wallet", "Here you can see your rewards and redeemed codes"),
                            TapTarget.forView(navigation.findViewById(R.id.navigation_profile), "Profile", "You can update your profile from here")

                                    .cancelable(false)
                    ).listener(new TapTargetSequence.Listener() {
                @Override
                public void onSequenceFinish() {
                    Log.d("docc", "sequence finished");
                    prefManager.setIsBottomNavTutFinished(true);
                }

                @Override
                public void onSequenceStep(TapTarget lastTarget, boolean targetClicked) {
                    Log.d("docc", "sequence dequnce step" + lastTarget.id() + " " + targetClicked);
                }

                @Override
                public void onSequenceCanceled(TapTarget lastTarget) {
                    Log.d("docc", "sequence canclled");
                }
            }).start();

        }

    }

    private void showWriteSettingsDialog() {
        Log.d("docc", "came into this dialog");
        final BottomSheetDialog dialog = new BottomSheetDialog(this);
        LottieAnimationView animation_view = dialog.findViewById(R.id.animation_view);
        dialog.setContentView(R.layout.write_settings_dialog);
        button = dialog.findViewById(R.id.write_settings_button);
        write_settings_granted_button = dialog.findViewById(R.id.write_settings_granted_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
                intent.setData(Uri.parse("package:" + MainActivity.this.getPackageName()));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivityForResult(intent, CODE_WRITE_SETTINGS_PERMISSION);
                showIntro();
            }
        });
        write_settings_granted_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                prefManager.setPermissionDialog(true);
                dialog.dismiss();//TODO: save in shared prerferencs and dont show user again -- FINISHED
                showIntro();
            }
        });
        dialog.show();

    }


}
