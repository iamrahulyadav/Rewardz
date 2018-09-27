package in.dthoughts.innolabs.adzapp.ui;

import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.dynamiclinks.DynamicLink;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.ShortDynamicLink;
import com.marcoscg.licenser.Library;
import com.marcoscg.licenser.License;
import com.marcoscg.licenser.LicenserDialog;

import java.util.Calendar;

import in.dthoughts.innolabs.adzapp.BuildConfig;
import in.dthoughts.innolabs.adzapp.R;
import mehdi.sakout.aboutpage.AboutPage;
import mehdi.sakout.aboutpage.Element;

public class AboutActivity extends AppCompatActivity {

    Uri mInvitationUrl;

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
                .addGroup("Refer and Earn")
                .addItem(referAndEarn())
                .addGroup(getString(R.string.about_page_app_group_name))
                .addItem(new Element().setTitle("version code: " + String.valueOf(BuildConfig.VERSION_NAME)))
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

    private Element referAndEarn() {
        Element referAndEarn = new Element();
        referAndEarn.setTitle("Refer and earn");
        referAndEarn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("docc1", "clicked on refer and earn");
                startReferAndEarn();
            }
        });
        return referAndEarn;
    }

    private void startReferAndEarn() {
        Log.d("docc1", "enetred start refer and earn");
        final Dialog shareDialog = new Dialog(AboutActivity.this);
        shareDialog.setContentView(R.layout.share_app_dialog);
        final Button mail_share = shareDialog.findViewById(R.id.mail_share);
        final Button social_share = shareDialog.findViewById(R.id.social_share);
        final LottieAnimationView loading_animation_view = shareDialog.findViewById(R.id.loading_animation_view);
        final TextView tv3 = shareDialog.findViewById(R.id.tv3);
        shareDialog.show();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String uid = user.getUid();
        String link = "http://adzapp.in/?invitedby=" + uid;
        Log.d("docc1", "link:" + link);
        FirebaseDynamicLinks.getInstance().createDynamicLink()
                .setLink(Uri.parse(link))
                .setDynamicLinkDomain("adzapp.page.link")
                .setAndroidParameters(
                        new DynamicLink.AndroidParameters.Builder("in.dthoughtsinnolabs.adzapp")
                                .setMinimumVersion(1)
                                .build())
                .buildShortDynamicLink()
                .addOnSuccessListener(new OnSuccessListener<ShortDynamicLink>() {
                                          @Override
                                          public void onSuccess(ShortDynamicLink shortDynamicLink) {
                                              mInvitationUrl = shortDynamicLink.getShortLink();
                                              loading_animation_view.setVisibility(View.GONE);
                                              tv3.setText((String.valueOf(mInvitationUrl)));
                                              tv3.setVisibility(View.VISIBLE);
                                              mail_share.setEnabled(true);
                                              social_share.setEnabled(true);
                                              Log.d("docc1", "short url is" + String.valueOf(mInvitationUrl));


                                          }
                                      }
                )
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("docc1", "failed in line 104 " + e.getMessage());
                    }
                });
        tv3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final android.content.ClipboardManager clipboardManager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                ClipData clipData = ClipData.newPlainText("AdzApp referral invitation link", String.valueOf(mInvitationUrl));
                clipboardManager.setPrimaryClip(clipData);
            }
        });
        mail_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendInvitationCode(mInvitationUrl);
            }
        });
        social_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_TEXT, "Download AdzApp with my referral link " + String.valueOf(mInvitationUrl));
                startActivity(Intent.createChooser(shareIntent, "Share link using"));
            }
        });


    }

    private void sendInvitationCode(Uri mInvitationUrl) {
        Log.d("docc1", "came into send inivation");
        //String referrerName = FirebaseAuth.getInstance().getCurrentUser();
        String subject = "Download AdzApp and get reward points";//String.format("%s wants you to download ADZAPP and earn", referrerName);
        String invitationLink = mInvitationUrl.toString();
        String msg = "Let's install ADZAPP together! Use my referrer link: "
                + invitationLink;
        String msgHtml = String.format("<p>Let's install ADZAPP. Use my "
                + "<a href=\"%s\">referrer link</a>!</p>", invitationLink);
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:"));
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        intent.putExtra(Intent.EXTRA_TEXT, msg);
        intent.putExtra(Intent.EXTRA_HTML_TEXT, msgHtml);
        if (intent.resolveActivity(getPackageManager()) != null) {
            Log.d("docc1", "came into last if");
            startActivity(intent);
        }


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

    Element getThirdPartyLicenses() {
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
                .setLibrary(new Library("QRCodeReaderView",
                        "https://github.com/dlazaro66/QRCodeReaderView",
                        License.APACHE))
                .setLibrary(new Library("Diagonal Layout",
                        "https://github.com/florent37/DiagonalLayout",
                        License.APACHE))
                .setLibrary(new Library("Ken Burns View",
                        "https://github.com/flavioarfaria/KenBurnsView",
                        License.APACHE))
                .setLibrary(new Library("Submit Button",
                        "https://github.com/Someonewow/SubmitButton",
                        License.APACHE))
                .setLibrary(new Library("Custom Activity on crash",
                        "https://github.com/Ereza/CustomActivityOnCrash",
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
                .setLibrary(new Library("Happy Birthday!",
                        "https://www.lottiefiles.com/427-happy-birthday",
                        License.CREATIVE_COMMONS))
                .setLibrary(new Library("you're in!",
                        "https://www.lottiefiles.com/2628-youre-in",
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
        copyRightsElement.setTitle(getString(R.string.about_page_company_name) + " " + copyrights);
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
