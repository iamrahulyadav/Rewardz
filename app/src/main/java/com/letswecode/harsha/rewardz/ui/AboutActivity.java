package com.letswecode.harsha.rewardz.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.widget.Toast;

import com.letswecode.harsha.rewardz.BuildConfig;
import com.letswecode.harsha.rewardz.R;

import java.util.Calendar;

import mehdi.sakout.aboutpage.AboutPage;
import mehdi.sakout.aboutpage.Element;

public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        View aboutPage = new AboutPage(this)
                .isRTL(false)
                .setImage(R.drawable.ic_home_black_24dp)//TODO:chnage logo path here
                .setDescription(getString(R.string.about_page_company_description))
                .addItem(new Element().setTitle("version code: "+String.valueOf(BuildConfig.VERSION_CODE)))
                .addGroup(getString(R.string.about_page_group_name))
                .addEmail(getString(R.string.about_paage_developer_email_address))//TODO: chnage these values in strings.xml while release
                .addWebsite(getString(R.string.about_paage_developer_website))
                .addFacebook(getString(R.string.about_paage_developer_facebook_address))
                .addTwitter(getString(R.string.about_paage_developer_twitter_address))
                .addPlayStore(getString(R.string.about_paage_developer_playStore_address))
                .addItem(getCopyRightsElement())
                .create();

        setContentView(aboutPage);

    }

    Element getCopyRightsElement() {
        Element copyRightsElement = new Element();
        final String copyrights = String.format(getString(R.string.copy_right), Calendar.getInstance().get(Calendar.YEAR));
        copyRightsElement.setTitle(getString(R.string.about_page_company_name)+" "+copyrights);
        copyRightsElement.setIconDrawable(R.drawable.ic_copyright_black_24dp);
        copyRightsElement.setIconTint(mehdi.sakout.aboutpage.R.color.about_item_icon_color);
        copyRightsElement.setIconNightTint(android.R.color.white);
        copyRightsElement.setGravity(Gravity.CENTER);
        copyRightsElement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(AboutActivity.this, copyrights, Toast.LENGTH_SHORT).show();
            }
        });
        return copyRightsElement;
    }
}
