package com.letswecode.harsha.rewardz.ui.intro;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.airbnb.lottie.LottieAnimationView;
import com.letswecode.harsha.rewardz.R;

import agency.tango.materialintroscreen.SlideFragment;

public class CustomFinishSlide extends SlideFragment {

    LottieAnimationView animation_view;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_intro_custom_finish_slide, container, false);
        animation_view = view.findViewById(R.id.animation_view);
        return view;
    }

    @Override
    public int backgroundColor() {
        return R.color.white;
    }

    @Override
    public int buttonsColor() {
        return R.color.colorPrimary;
    }

    @Override
    public boolean canMoveFurther() {
        return true;
    }

    @Override
    public String cantMoveFurtherErrorMessage() {
        return getString(R.string.t_and_c_error_message);
    }
}
