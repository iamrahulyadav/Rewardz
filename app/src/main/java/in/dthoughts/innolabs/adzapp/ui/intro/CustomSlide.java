package in.dthoughts.innolabs.adzapp.ui.intro;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import in.dthoughts.innolabs.adzapp.R;

import agency.tango.materialintroscreen.SlideFragment;

public class CustomSlide extends SlideFragment {

    private CheckBox checkBox;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_intro_custom_slide, container, false);
        checkBox = view.findViewById(R.id.checkBox);
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
        return checkBox.isChecked();
    }

    @Override
    public String cantMoveFurtherErrorMessage() {
        return getString(R.string.t_and_c_error_message);
    }
}
