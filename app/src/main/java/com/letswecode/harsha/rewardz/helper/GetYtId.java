package com.letswecode.harsha.rewardz.helper;

import android.text.TextUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GetYtId {

    public static String getYoutubeID(String youtubeUrl) {

        if (TextUtils.isEmpty(youtubeUrl)) {
            return "";
        }
        String video_id = "";

        String expression = "^.*((youtu.be" + "\\/)" + "|(v\\/)|(\\/u\\/w\\/)|(embed\\/)|(watch\\?))\\??v?=?([^#\\&\\?]*).*"; // var regExp = /^.*((youtu.be\/)|(v\/)|(\/u\/\w\/)|(embed\/)|(watch\?))\??v?=?([^#\&\?]*).*/;
        CharSequence input = youtubeUrl;
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(input);
        if (matcher.matches()) {
            String groupIndex1 = matcher.group(7);
            if (groupIndex1 != null && groupIndex1.length() == 11)
                video_id = groupIndex1;
        }
        if (TextUtils.isEmpty(video_id)) {
            if (youtubeUrl.contains("youtu.be/")  ) {
                String spl = youtubeUrl.split("youtu.be/")[1];
                if (spl.contains("\\?")) {
                    video_id = spl.split("\\?")[0];
                }else {
                    video_id =spl;
                }

            }
        }

        return video_id;
    }

}
