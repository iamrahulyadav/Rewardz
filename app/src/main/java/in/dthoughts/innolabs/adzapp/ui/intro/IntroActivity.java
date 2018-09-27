package in.dthoughts.innolabs.adzapp.ui.intro;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.FloatRange;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.View;

import agency.tango.materialintroscreen.MaterialIntroActivity;
import agency.tango.materialintroscreen.MessageButtonBehaviour;
import agency.tango.materialintroscreen.SlideFragmentBuilder;
import agency.tango.materialintroscreen.animations.IViewTranslation;
import in.dthoughts.innolabs.adzapp.R;
import in.dthoughts.innolabs.adzapp.helper.PrefManager;
import in.dthoughts.innolabs.adzapp.ui.MainActivity;

public class IntroActivity extends MaterialIntroActivity {

    PrefManager prefManager;
    boolean downloadedFromPlayStore, isRooted;
    Context context;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        enableLastSlideAlphaExitTransition(true);

        getBackButtonTranslationWrapper()
                .setEnterTranslation(new IViewTranslation() {
                    @Override
                    public void translate(View view, @FloatRange(from = 0, to = 1.0) float percentage) {
                        view.setAlpha(percentage);
                    }
                });

        //checking whether app downloaded from playstore or not

        downloadedFromPlayStore = isStoreVersion(context);
//        //isRooted = CommonUtils.isRooted(context);
        //TODO:UNCMNT WHOLE BLOCK
//        if(!downloadedFromPlayStore /*|| isRooted*/){
//            //As app is side loaded we will cause a crash such that user cant use the app until they downloaded from store.
//            final AlertDialog.Builder builder = new AlertDialog.Builder(IntroActivity.this);
//            builder.setTitle("App is side loaded");
//            builder.setMessage("Seems Like AdzApp is not downloaded from play store. TO enjoy our services please download app from play store");
//
//            builder.setPositiveButton("Go to Play store", new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialog, int which) {
//                    //perform any action
//                    final String appPackageName = getPackageName();
//                    try {
//                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
//                        finish();
//                        System.exit(0);
//                    } catch (android.content.ActivityNotFoundException anfe) {
//                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
//                        finish();
//                        System.exit(0);
//                    }
//                }
//            });
//
//            builder.setNegativeButton("Close App", new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialog, int which) {
//                    finish();
//                    System.exit(0);
//                }
//            });
//
//            //creating alert dialog
//            AlertDialog alertDialog = builder.create();
//            alertDialog.show();
//
//        }


        addSlide(new SlideFragmentBuilder()
                .backgroundColor(R.color.first_slide_background)
                .buttonsColor(R.color.first_slide_buttons)
                .image(R.mipmap.ic_launcher)
                .title(getString(R.string.intro_slide1_title))
                .description(getString(R.string.intro_slide1_description))
                .build());


        addSlide(new CustomSlide());

        addSlide(new SlideFragmentBuilder()
                        .backgroundColor(R.color.third_slide_background)
                        .buttonsColor(R.color.third_slide_buttons)
                        .neededPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.READ_PHONE_STATE
                                , Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA})
                        .image(R.drawable.ic_lock_open_black_24dp)
                        .title(getString(R.string.permissions_title))
                        .description(getString(R.string.intro_slide3_permission_description))
                        .build(),
                new MessageButtonBehaviour(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showMessage(getString(R.string.intro_slide3_mesasge));
                    }
                }, getString(R.string.intro_slide3_button_text)));

        addSlide(new CustomFinishSlide());
//        addSlide(new SlideFragmentBuilder()
//                .backgroundColor(R.color.fourth_slide_background)
//                .buttonsColor(R.color.fourth_slide_buttons)
//                .image(R.drawable.ic_mood_black_24dp)
//                .title(getString(R.string.intro_slide4_finish_title))
//                .description(getString(R.string.intro_slide4_finish_description))
//                .build());

    }


    public static boolean isStoreVersion(Context context) {
        boolean result = false;

        try {
            String installer = context.getPackageManager()
                    .getInstallerPackageName(context.getPackageName());
            result = !TextUtils.isEmpty(installer);
        } catch (Throwable e) {
        }

        return result;
    }

    @Override
    public void onFinish() {
        super.onFinish();
        prefManager = new PrefManager(this);
        prefManager.setIntroFinished(true);
        startActivity(new Intent(IntroActivity.this, MainActivity.class));
        finish();
    }


}
