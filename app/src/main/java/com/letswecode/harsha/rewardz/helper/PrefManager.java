package com.letswecode.harsha.rewardz.helper;

import android.content.Context;
import android.content.SharedPreferences;

public class PrefManager {

    SharedPreferences pref;
    SharedPreferences.Editor editor;
    Context _context;

    // shared pref mode
    int PRIVATE_MODE = 0;

    // Shared preferences file name
    private static final String PREF_NAME = "adzapp-welcome";

    private static final String IS_FIRST_TIME_LAUNCH = "IsFirstTimeLaunch";

    private static final String IS_FIRST_TIME_LAUNCH_IN_DAY = "IsFirstTimeLaunchInDay";

    private static final String IS_FIRST_TIME_RINGING = "IsFirstTimeRinging";

    private static final String IS_BOTTOM_NAV_TUT_FINISHED = "bottomNavTutorial";

    private static final String IS_DETAIL_AD_TUT_FINISHED = "detailAdTutorial";

    private static final String IS_PERMISSIONS_GRANTED = "permissionsGranted";

    private static final String IS_INTRO_FINISHED = "appIntroFinished";

    public PrefManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public void setFirstTimeLaunch(boolean isFirstTime) {
        editor.putBoolean(IS_FIRST_TIME_LAUNCH, isFirstTime);
        editor.commit();
    }

    public void setFirstTimeLaunchInDay(boolean isFirstTime) {
        editor.putBoolean(IS_FIRST_TIME_LAUNCH_IN_DAY, isFirstTime);
        editor.commit();
    }

    public void setIsFirstTimeRinging(boolean isFirstTime) {
        editor.putBoolean(IS_FIRST_TIME_RINGING, isFirstTime);
        editor.commit();
    }

    public void setIsBottomNavTutFinished(boolean bottomNavTut){
        editor.putBoolean(IS_BOTTOM_NAV_TUT_FINISHED, bottomNavTut);
        editor.commit();
    }

    public void setIsDetailAdTutFinished(boolean detailAdTut){
        editor.putBoolean(IS_DETAIL_AD_TUT_FINISHED, detailAdTut);
        editor.commit();
    }

    public void setPermissionDialog(boolean permissionGranted){
        editor.putBoolean(IS_PERMISSIONS_GRANTED, permissionGranted);
        editor.commit();
    }

    public void setIntroFinished(boolean introFinished){
        editor.putBoolean(IS_INTRO_FINISHED, introFinished);
        editor.commit();
    }

    public boolean isFirstTimeLaunch() {
        return pref.getBoolean(IS_FIRST_TIME_LAUNCH, true);
    }

    public boolean isFirstTimeLaunchInDay() { return pref.getBoolean(IS_FIRST_TIME_LAUNCH_IN_DAY, true);   }

    public boolean isFirstTimeRinging() {
        return pref.getBoolean(IS_FIRST_TIME_RINGING, true);
    }

    public boolean isBottomNavTutFinished() { return pref.getBoolean(IS_BOTTOM_NAV_TUT_FINISHED,false); }

    public boolean isDetailAdTutFinished() { return pref.getBoolean(IS_DETAIL_AD_TUT_FINISHED, false); }

    public boolean isPermissionGranted() { return  pref.getBoolean(IS_PERMISSIONS_GRANTED, false);}

    public boolean isIntroFinished(){ return pref.getBoolean(IS_INTRO_FINISHED, false); }
}
