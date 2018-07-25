package com.letswecode.harsha.rewardz.helper;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;

import java.util.Random;

public class ColorChooser {

    public static GradientDrawable ColorChooser(){
        int[] colors = new int[2];//TODO: can make this code and method at last into a class and call it no repetition of code
        colors[0] = getRandomColor();//method defined at last
        colors[1] = getRandomColor();
        GradientDrawable gd = new GradientDrawable(
                GradientDrawable.Orientation.TOP_BOTTOM, colors);

        gd.setGradientType(GradientDrawable.RADIAL_GRADIENT);
        gd.setGradientRadius(300f);
        gd.setCornerRadius(0f);


        return gd;
    }

    private static int getRandomColor(){
        Random rnd = new Random();
        return Color.argb(255, rnd.nextInt(256), rnd.nextInt(56), rnd.nextInt(256));
    }

}
