package com.letswecode.harsha.rewardz.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.widget.Toast;

import com.letswecode.harsha.rewardz.BuildConfig;
import com.letswecode.harsha.rewardz.R;
import com.marcoscg.licenser.Library;
import com.marcoscg.licenser.License;
import com.marcoscg.licenser.LicenserDialog;

import java.util.Calendar;

import mehdi.sakout.aboutpage.AboutPage;
import mehdi.sakout.aboutpage.Element;

public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        View aboutPage = new AboutPage(this)
                .isRTL(false)
                .setImage(R.mipmap.ic_launcher_round)//TODO:chnage logo path here --FINISHED
                .setDescription(getString(R.string.about_page_company_description))
                .addGroup(getString(R.string.about_page_app_preference_group_name))
                //.addItem(openRingtonePreference())
                .addItem(openAdPublisherAndAgent())
                .addGroup(getString(R.string.about_page_app_group_name))
                .addItem(new Element().setTitle("version code: "+String.valueOf(BuildConfig.VERSION_NAME)))
                .addItem(getThirdPartyLicenses())
                .addItem(getPrivacyPolicy())
                .addItem(getTermsAndConditions())
                .addGroup(getString(R.string.about_page_contact_group_name))
                .addEmail(getString(R.string.about_page_developer_email_address))//TODO: chnage these values in strings.xml while release
                .addWebsite(getString(R.string.about_page_developer_website))
                .addFacebook(getString(R.string.about_page_developer_facebook_address))
                .addTwitter(getString(R.string.about_page_developer_twitter_address))
                .addPlayStore(getString(R.string.about_page_developer_playStore_address))
                .addItem(getCopyRightsElement())
                .create();
        setContentView(aboutPage);

    }

    private Element openAdPublisherAndAgent() {
        Element adPublisher_Agent = new Element();
        adPublisher_Agent.setTitle("Publisher and Agent login");
        adPublisher_Agent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AboutActivity.this, PublisherAgentActivity.class);
                startActivity(intent);
            }
        });
        return adPublisher_Agent;
    }


    private Element getTermsAndConditions() {
        Element termsAndCondictions = new Element();
        termsAndCondictions.setTitle(getString(R.string.terms_and_conditions));
        termsAndCondictions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.terms_and_conditions_url)));
                startActivity(browserIntent);
            }
        });
        return termsAndCondictions;
    }

    private Element getPrivacyPolicy() {
        Element privacyPolicy = new Element();
        privacyPolicy.setTitle(getString(R.string.privacy_policy));
        privacyPolicy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.privacy_policy_url)));
                startActivity(browserIntent);
            }
        });
        return privacyPolicy;
    }

    Element getThirdPartyLicenses(){
        Element ThirdPartyLicenses = new Element();
        ThirdPartyLicenses.setTitle(getString(R.string.Third_party_licenses));
        ThirdPartyLicenses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openLicenseDailog();
            }
        });


        return ThirdPartyLicenses;
    }

    private void openLicenseDailog() {

        new LicenserDialog(this)
                .setTitle("Licenses")
                .setCustomNoticeTitle("Notices for files:")
                .setLibrary(new Library("Android Support Libraries",
                        "https://developer.android.com/topic/libraries/support-library/index.html",
                        License.APACHE))
                .setLibrary(new Library("Dexter Library",
                        "https://github.com/Karumi/Dexter",
                        License.APACHE))
                .setLibrary(new Library("Picasso Library",
                        "https://github.com/square/picasso",
                        License.APACHE))
                .setLibrary(new Library("Shimmer Android Library",
                        "https://github.com/facebook/shimmer-android",
                        License.APACHE))
                .setLibrary(new Library("Commons-io Library",
                        "https://github.com/apache/commons-io",
                        License.APACHE))
                .setLibrary(new Library("TicketView Library",
                        "https://github.com/vipulasri/TicketView",
                        License.APACHE))
                .setLibrary(new Library("Giraffe player2",
                        "https://github.com/tcking/GiraffePlayer2",
                        License.APACHE))
                .setLibrary(new Library("Smary-App-Rate",
                        "https://github.com/codemybrainsout/smart-app-rate",
                        License.APACHE))
                .setLibrary(new Library("AppUpdater",
                        "https://github.com/javiersantos/AppUpdater",
                        License.APACHE))
                .setLibrary(new Library("FragStack",
                        "https://github.com/abhishesh-srivastava/fragstack",
                        License.APACHE))
                .setLibrary(new Library("Material Intro screen",
                        "https://github.com/TangoAgency/material-intro-screen",
                        License.MIT))
                .setLibrary(new Library("Android About page",
                        "https://github.com/medyo/android-about-page",
                        License.MIT))
                .setLibrary(new Library("Licenser",
                        "https://github.com/marcoscgdev/Licenser",
                        License.MIT))
                .setLibrary(new Library("LottieFile-checked_done",
                        "https://www.lottiefiles.com/433-checked-done",
                        License.CREATIVE_COMMONS))
                .setLibrary(new Library("LottieFile-Empty_box",
                        "https://www.lottiefiles.com/629-empty-box",
                        License.CREATIVE_COMMONS))
                .setLibrary(new Library("LottieFile-Phone",
                        "https://www.lottiefiles.com/1286-phone",
                        License.CREATIVE_COMMONS))
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .show();
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
