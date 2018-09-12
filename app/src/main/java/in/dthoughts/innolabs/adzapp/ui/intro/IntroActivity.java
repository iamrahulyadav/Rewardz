package in.dthoughts.innolabs.adzapp.ui.intro;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.FloatRange;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Toast;

import in.dthoughts.innolabs.adzapp.helper.PrefManager;
import in.dthoughts.innolabs.adzapp.service.DownloadRt;
import in.dthoughts.innolabs.adzapp.ui.MainActivity;
import in.dthoughts.innolabs.adzapp.R;

import agency.tango.materialintroscreen.MaterialIntroActivity;
import agency.tango.materialintroscreen.MessageButtonBehaviour;
import agency.tango.materialintroscreen.SlideFragmentBuilder;
import agency.tango.materialintroscreen.animations.IViewTranslation;
import in.dthoughts.innolabs.adzapp.helper.PrefManager;

import in.dthoughts.innolabs.adzapp.helper.PrefManager;

public class IntroActivity extends MaterialIntroActivity {

    PrefManager prefManager;
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
                        .neededPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.READ_PHONE_STATE
                                ,Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA})
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

    @Override
    public void onFinish() {
        super.onFinish();
        prefManager = new PrefManager(this);
        prefManager.setIntroFinished(true);
        startActivity(new Intent(IntroActivity.this, MainActivity.class));
        finish();
    }
}
