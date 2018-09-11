package com.letswecode.harsha.rewardz.fragments.marketCategories;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.letswecode.harsha.rewardz.R;
import com.letswecode.harsha.rewardz.adapter.AdsListAdapter;
import com.letswecode.harsha.rewardz.modal.Ads;

import java.util.ArrayList;
import java.util.List;

public class ElectronicsFragment extends Fragment {
    public static final String PAGE_TITLE = "Electronics";

    RecyclerView mainlist;
    LottieAnimationView empty_animation_view;
    FirebaseFirestore db;
    FirebaseUser user;
    int n=0;
    boolean[] alreadyReddemed;

    private AdsListAdapter adsListAdapter;
    private List<Ads> AdsList;

    public ElectronicsFragment() {
        // Required empty public constructor
    }

    public static ElectronicsFragment newInstance() {
        ElectronicsFragment fragment = new ElectronicsFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        db = FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
        AdsList = new ArrayList<>();
        adsListAdapter = new AdsListAdapter(getContext(),AdsList);

        db.collection("Published Ads").whereEqualTo("category","electronics").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for(QueryDocumentSnapshot doc : task.getResult()){
                        n = n+1;
                        Log.d("docc","inside loop");
                        checkAlreadyRedeemed(doc);
                    }
                }
                else{

                }
                if(n==0){
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_categories, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);

        mainlist = view.findViewById(R.id.recyclerView);
        mainlist.setHasFixedSize(true);
        mainlist.setLayoutManager(new LinearLayoutManager(getActivity()));
        mainlist.setAdapter(adsListAdapter);
        empty_animation_view = view.findViewById(R.id.empty_animation_view);
        empty_animation_view.cancelAnimation();

    }

    public void checkAlreadyRedeemed(final /*DocumentChange*/ QueryDocumentSnapshot doc) {

        //alreadyReddemed = new boolean[1];


        db.collection("Transactions").whereEqualTo("user_id", user.getUid()).whereEqualTo("ad_id",doc/*getDocument()*/.getId()).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {

                try{

                    if(queryDocumentSnapshots.size()!= 0){
                        //                    alreadyReddemed[0] = true;
                        //                    Log.d("doc","inside if:"+ String.valueOf(alreadyReddemed[0]));
                    }else {
                        //alreadyReddemed[0] = false;
                        // Ads ads = doc.getDocument().toObject(Ads.class).withId(doc.getDocument().getId());
                        Ads ads = doc.toObject(Ads.class).withId(doc.getId());
                        AdsList.add(ads);
                        adsListAdapter.notifyDataSetChanged();
                        //                    Log.d("doc", doc/*.getDocument()*/.getId().toString());
                        //                    Log.d("doc","inside if:"+ String.valueOf(alreadyReddemed[0]));
                    }
                }catch (Exception err){
                    Log.d("doc",err.getMessage());
                }
            }
        });

    }
}