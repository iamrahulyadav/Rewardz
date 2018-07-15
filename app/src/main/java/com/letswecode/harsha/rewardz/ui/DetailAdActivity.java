package com.letswecode.harsha.rewardz.ui;

import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.letswecode.harsha.rewardz.R;
import com.squareup.picasso.Picasso;

public class DetailAdActivity extends AppCompatActivity {

    ImageView Publisher_pic, Ad_banner;
    TextView Publisher_name, Expires_on, Ad_description, Ad_url;
    VideoView Ad_video;
    Button Redeem_button;
    String adPublisherPic, adPublisherName,adExpiresOn,adBanner,adDescription,adUrl,adType,adVideoUrl,adPoints;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_ad);

        final  Bundle  extras = getIntent().getExtras();

        adPublisherPic =  extras.get("adPublisherPic").toString();
        adPublisherName = extras.get("adPublisherName").toString();
        adExpiresOn = extras.get("adExpiresOn").toString();
        adBanner = extras.get("adBanner").toString();
        adDescription = extras.get("adDescription").toString();
        adUrl = extras.get("adUrl").toString();
        adType = extras.get("adType").toString();
        adVideoUrl = extras.get("adVideoUrl").toString();
        adPoints = extras.get("adPoints").toString();


        Publisher_pic =  findViewById(R.id.profilePic);
        Publisher_name = findViewById(R.id.name);
        Expires_on = findViewById(R.id.expireson);
        Ad_description =  findViewById(R.id.adDescription);
        Ad_url = findViewById(R.id.txtUrl);
        Ad_banner = findViewById(R.id.adBanner);
        Ad_video = findViewById(R.id.adVideo);
        Redeem_button =  findViewById(R.id.redeemButton);


        Picasso.get().load(adPublisherPic).into(Publisher_pic);
        Publisher_name.setText(adPublisherName);
        Expires_on.setText(adExpiresOn);
        Ad_description.setText(adDescription);
        Ad_url.setText(adUrl);
        Picasso.get().load(adBanner).into(Ad_banner);
        if(adType.equals("video")){
            Ad_video.setVideoURI(Uri.parse(adVideoUrl));
        }
        Redeem_button.setText("Redeem "+adPoints);


        Redeem_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(DetailAdActivity.this, "Redeemed "+adPoints+ " not connected to DB",Toast.LENGTH_LONG).show();
            }
        });




    }
}
