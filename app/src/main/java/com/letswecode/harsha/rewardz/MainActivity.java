package com.letswecode.harsha.rewardz;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.letswecode.harsha.rewardz.authentication.LoginActivity;
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
        setTheme(R.style.AppTheme); //for slpash screen runs only app taking long time to initialize
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//TODO: first check internet connection then proceed
        //checking first run of app
        checkFirstRun();

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

        //loading the default home fragment
        loadFragment(new HomeFragment());

        BottomNavigationView navigation =  findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
//TODO:uncmnt this service inoreder  to run service -- FINISHED
        //starting service
        Intent intent = new Intent(MainActivity.this, DownloadRt.class);
        ContextCompat.startForegroundService(this,intent);
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

    // Initiating Menu XML file (menu.xml)
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
            case R.id.menu_settings:
                //startActivity(new Intent(MainActivity.this, SettingActivity.class));
                Toast.makeText(MainActivity.this, "Settings activity", Toast.LENGTH_LONG).show();
                return true;

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
            Log.d("savedVersionCode", String.valueOf(savedVersionCode));
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
