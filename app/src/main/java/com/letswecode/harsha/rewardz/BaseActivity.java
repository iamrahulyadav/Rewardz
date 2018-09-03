package com.letswecode.harsha.rewardz;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.letswecode.harsha.rewardz.authentication.LoginActivity;
import com.letswecode.harsha.rewardz.helper.PrefManager;
import com.letswecode.harsha.rewardz.ui.MainActivity;
import com.letswecode.harsha.rewardz.ui.intro.IntroActivity;

import agency.tango.materialintroscreen.MaterialIntroActivity;

public class BaseActivity extends AppCompatActivity {

    private PrefManager prefManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if(Build.VERSION.SDK_INT >=Build.VERSION_CODES.M){
            setTheme(R.style.SplashTheme);
        }else {
            setTheme(R.style.AppTheme);
        }
        super.onCreate(savedInstanceState);

        prefManager = new PrefManager(this);
        if (prefManager.isFirstTimeLaunch()) {
            Log.d("doc",String.valueOf(prefManager.isFirstTimeLaunch()));
            prefManager.setFirstTimeLaunch(false);
            startActivity(new Intent(BaseActivity.this, IntroActivity.class));
            finish();

        }
        else{
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
}
