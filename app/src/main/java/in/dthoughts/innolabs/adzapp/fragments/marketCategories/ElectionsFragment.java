package in.dthoughts.innolabs.adzapp.fragments.marketCategories;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetDialog;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import in.dthoughts.innolabs.adzapp.R;
import in.dthoughts.innolabs.adzapp.adapter.AdsListAdapter;
import in.dthoughts.innolabs.adzapp.modal.Ads;
import in.dthoughts.innolabs.adzapp.service.DownloadRt;
import in.dthoughts.innolabs.adzapp.ui.MainActivity;

public class ElectionsFragment extends Fragment {
    public static final String PAGE_TITLE = "Elections";

    RecyclerView mainlist;
    LottieAnimationView empty_animation_view;
    TextView fav_tune;
    BottomSheetDialog favTuneSheet;
    RadioGroup fav_tune_radio_grp;
    RadioButton trs_radio, congress_radio, mim_radio, ysrc_radio, cpi_radio, none_radio;
    FirebaseFirestore db;
    FirebaseUser user;
    Date todayDate, expiryDate, createdDate;
    int n = 0;
    FirebaseAnalytics firebaseAnalytics;
    Timestamp expiryDate_timestamp , createdDate_timestamp;
    private AdsListAdapter adsListAdapter;
    private List<Ads> AdsList;

    public ElectionsFragment() {
        // Required empty public constructor
    }

    public static ElectionsFragment newInstance() {
        ElectionsFragment fragment = new ElectionsFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        firebaseAnalytics = FirebaseAnalytics.getInstance(getContext());
        //getting date for querying
        todayDate = java.util.Calendar.getInstance().getTime();
        db = FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
        AdsList = new ArrayList<>();
        adsListAdapter = new AdsListAdapter(getContext(), AdsList);

        db.collection("Published Ads").whereEqualTo("category", "elections").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot doc : task.getResult()) {
                        //n = n + 1;
                        Log.d("docc", "inside loop");

                        try {
                            expiryDate_timestamp = doc.getTimestamp("expires_on");
                            createdDate_timestamp = doc.getTimestamp("created_on");
                            Log.d("docc7","timestamp "+ expiryDate_timestamp+" , "+createdDate_timestamp);
                            expiryDate = expiryDate_timestamp.toDate();
                            createdDate = createdDate_timestamp.toDate();
//                            expiryDate = sdf.parse(String.valueOf(doc.get("expires_on")));
//                            createdDate = sdf.parse(String.valueOf(doc.get("created_on")));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if ((todayDate.equals(createdDate) || todayDate.after(createdDate)) && (todayDate.before(expiryDate) || todayDate.equals(expiryDate))) {
                            Log.d("docc6", "ads havent expired");
                            n = n + 1;
                            //checkAlreadyRedeemed(doc);
                            Ads ads = doc.toObject(Ads.class).withId(doc.getId());
                            AdsList.add(ads);
                            adsListAdapter.notifyDataSetChanged();
                        } else {
                            Log.d("docc6", "ads expired");
                        }
                    }
                } else {

                }
                if (n == 0) {
                    mainlist.setVisibility(View.GONE);
//                    loading_animation_view.pauseAnimation();
//                    loading_animation_view.setVisibility(View.GONE);
                    empty_animation_view.playAnimation();
                    empty_animation_view.setVisibility(View.VISIBLE);

                } else {
                    mainlist.setVisibility(View.VISIBLE);
//                    loading_animation_view.pauseAnimation();
//                    loading_animation_view.setVisibility(View.GONE);
                    empty_animation_view.setVisibility(View.GONE);
                }
            }
        });


    }

    private void showFavTuneSheet() {
        final SharedPreferences preferences = getActivity().getSharedPreferences("AdzAppRingtoneFavParty", Context.MODE_PRIVATE);
        int fav_party = preferences.getInt("favParty", 5);

        favTuneSheet = new BottomSheetDialog(getContext());
        favTuneSheet.setContentView(R.layout.fav_tune_sheet);
        fav_tune_radio_grp = favTuneSheet.findViewById(R.id.fav_tune_radio_grp);
        fav_tune_radio_grp.check(fav_tune_radio_grp.getChildAt(fav_party).getId());
        fav_tune_radio_grp.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
               switch (checkedId){
                   case R.id.trs_radio:
                       Toast.makeText(getActivity(),"cliked TRS", Toast.LENGTH_LONG).show();
                       preferences.edit().putInt("favParty", 0).commit();
                       preferences.edit().putString("favPartyName", "trs").commit();

                       break;
                   case R.id.congress_radio:
                       Toast.makeText(getActivity(),"cliked congres", Toast.LENGTH_LONG).show();
                       preferences.edit().putInt("favParty", 1).commit();
                       preferences.edit().putString("favPartyName", "congress").commit();

                       break;
                   case R.id.mim_radio:
                       Toast.makeText(getActivity(),"cliked mim", Toast.LENGTH_LONG).show();
                       preferences.edit().putInt("favParty", 2).commit();
                       preferences.edit().putString("favPartyName", "mim").commit();

                       break;
                   case R.id.ysrc_radio:
                       Toast.makeText(getActivity(),"cliked ysrc", Toast.LENGTH_LONG).show();
                       preferences.edit().putInt("favParty", 3).commit();
                       preferences.edit().putString("favPartyName", "ysrcp").commit();

                       break;
                   case R.id.cpi_radio:
                       Toast.makeText(getActivity(),"cliked cpi", Toast.LENGTH_LONG).show();
                       preferences.edit().putInt("favParty", 4).commit();
                       preferences.edit().putString("favPartyName", "cpi").commit();

                       break;
                   case R.id.none_radio:
                       Toast.makeText(getActivity(),"cliked none", Toast.LENGTH_LONG).show();
                       preferences.edit().putInt("favParty", 5).commit();
                       preferences.edit().putString("favPartyName", "none").commit();
                       break;
               }

            }
        });
        favTuneSheet.show();
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_categories, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mainlist = view.findViewById(R.id.recyclerView);
        mainlist.setHasFixedSize(true);
        mainlist.setLayoutManager(new LinearLayoutManager(getActivity()));
        mainlist.setAdapter(adsListAdapter);
        fav_tune = view.findViewById(R.id.fav_tune);
        //fav_tune.setVisibility(View.VISIBLE);
        empty_animation_view = view.findViewById(R.id.empty_animation_view);
        empty_animation_view.cancelAnimation();

        fav_tune.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("docc9","click on text view");
                showFavTuneSheet();
            }
        });

    }

    public void checkAlreadyRedeemed(final /*DocumentChange*/ QueryDocumentSnapshot doc) {

        //alreadyReddemed = new boolean[1];


        db.collection("Transactions").whereEqualTo("user_id", user.getUid()).whereEqualTo("ad_id", doc/*getDocument()*/.getId()).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {

                try {

                    if (queryDocumentSnapshots.size() != 0) {
                        //                    alreadyReddemed[0] = true;
                        //                    Log.d("doc","inside if:"+ String.valueOf(alreadyReddemed[0]));
                    } else {
                        //alreadyReddemed[0] = false;
                        // Ads ads = doc.getDocument().toObject(Ads.class).withId(doc.getDocument().getId());
                        Ads ads = doc.toObject(Ads.class).withId(doc.getId());
                        AdsList.add(ads);
                        adsListAdapter.notifyDataSetChanged();
                        //                    Log.d("doc", doc/*.getDocument()*/.getId().toString());
                        //                    Log.d("doc","inside if:"+ String.valueOf(alreadyReddemed[0]));
                    }
                } catch (Exception err) {
                    Log.d("doc", err.getMessage());
                }
            }
        });

    }
}
