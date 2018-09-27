package in.dthoughts.innolabs.adzapp.ui;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.ArrayList;
import java.util.List;

import in.dthoughts.innolabs.adzapp.R;
import in.dthoughts.innolabs.adzapp.adapter.AdsListAdapter;
import in.dthoughts.innolabs.adzapp.modal.Ads;

public class PublisherDetailActivity extends AppCompatActivity {

    LinearLayout linearLayout;
    ImageView Image_Publisher;
    TextView Text_Publisher;
    RecyclerView mainlist;
    String Name_Publisher, Pic_Publisher;
    public AdsListAdapter adsListAdapter;
    public List<Ads> AdsList;
    int n = 0;
    FirebaseFirestore db;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publisher_view);
        Toolbar mToolbar = findViewById(R.id.toolbar);
        mToolbar.setTitle("Publisher Details");
        mToolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        final Bundle extras = getIntent().getExtras();
        try {
            Name_Publisher = extras.getString("Publisher_Name");
            Pic_Publisher = extras.getString("Publisher_Image");
        } catch (Exception e) {
            Log.d("exception", e.getMessage());
        }

        linearLayout = findViewById(R.id.linearLayout3);
        Image_Publisher = findViewById(R.id.Image_Publisher);
        Text_Publisher = findViewById(R.id.Text_Publisher);


        //Picasso.with(getApplicationContext()).load(Pic_Publisher).placeholder(R.drawable.ic_sort_black_24dp).error(R.drawable.ic_error_black_24dp).into((Target) relativeLayout);
        Text_Publisher.setText(Name_Publisher);
        Picasso.with(getApplicationContext()).load(Pic_Publisher).placeholder(R.drawable.ic_sort_black_24dp).error(R.drawable.ic_error_black_24dp).into(Image_Publisher);
        Picasso.with(this).load(Pic_Publisher).into(new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                linearLayout.setBackground(new BitmapDrawable(bitmap));
                linearLayout.getBackground().setAlpha(50);
            }

            @Override
            public void onBitmapFailed(Drawable errorDrawable) {

            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {

            }
        });
        //linearLayout.getBackground().setAlpha(50);

        mainlist = findViewById(R.id.mainlist);

        AdsList = new ArrayList<>();
        adsListAdapter = new AdsListAdapter(getApplicationContext(), AdsList);

        mainlist.setHasFixedSize(true);
        mainlist.setLayoutManager(new LinearLayoutManager(this));
        mainlist.setAdapter(adsListAdapter);


        db = FirebaseFirestore.getInstance();
        db.collection("Published Ads").whereEqualTo("publisher_name", Name_Publisher).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot doc : task.getResult()) {

                        Ads ads = doc.toObject(Ads.class).withId(doc.getId());
                        AdsList.add(ads);
                        adsListAdapter.notifyDataSetChanged();
                    }
                } else {

                }

            }
        });

    }
}
