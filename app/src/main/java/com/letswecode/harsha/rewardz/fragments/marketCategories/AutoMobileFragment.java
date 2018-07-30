package com.letswecode.harsha.rewardz.fragments.marketCategories;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.letswecode.harsha.rewardz.R;
import com.letswecode.harsha.rewardz.adapter.AdsListAdapter;
import com.letswecode.harsha.rewardz.modal.Ads;

import java.util.ArrayList;
import java.util.List;

public class AutoMobileFragment extends Fragment {
    public static final String PAGE_TITLE = "Auto Mobile"; //TODO:change this strings.xml

    RecyclerView mainlist;

    FirebaseFirestore db;
    FirebaseUser user;

    private AdsListAdapter adsListAdapter;
    private List<Ads> AdsList;

    public AutoMobileFragment() {
        // Required empty public constructor
    }

    public static AutoMobileFragment newInstance() {
        AutoMobileFragment fragment = new AutoMobileFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        db = FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
        AdsList = new ArrayList<>();
        adsListAdapter = new AdsListAdapter(getContext(),AdsList);

        db.collection("Published Ads").whereEqualTo("category","automobile").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {

                try{
                    for(DocumentChange doc: queryDocumentSnapshots.getDocumentChanges()){

                        if(doc.getType() == DocumentChange.Type.ADDED){ //DocumentChange.Type.ADDED
                            checkAlreadyRedeemed(doc);
//                        Ads ads = doc.getDocument().toObject(Ads.class).withId(doc.getDocument().getId());
//                        AdsList.add(ads);
//                        Log.d("doc", doc.getDocument().toString());
//                        adsListAdapter.notifyDataSetChanged();
                        }

                    }
                }catch (Exception err){
                    Log.d("doc","err "+err.getMessage());
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


    }

    public void checkAlreadyRedeemed(final DocumentChange doc) {

        //alreadyReddemed = new boolean[1];

        //Log.d("doc","user: "+user.getUid()+" ad_id "+adID);
        db.collection("Transactions").whereEqualTo("user_id", user.getUid()).whereEqualTo("ad_id",doc.getDocument().getId()).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {
               // Log.d("doc",String.valueOf(queryDocumentSnapshots.size()));
                if(queryDocumentSnapshots.size()!= 0){
                    //alreadyReddemed[0] = true;
                   // Log.d("doc","inside if:"+ String.valueOf(alreadyReddemed[0]));
                }else {
                    //alreadyReddemed[0] = false;
                    Ads ads = doc.getDocument().toObject(Ads.class).withId(doc.getDocument().getId());
                    AdsList.add(ads);
                    adsListAdapter.notifyDataSetChanged();
                    Log.d("doc", doc.getDocument().getId().toString());
                    //Log.d("doc","inside if:"+ String.valueOf(alreadyReddemed[0]));
                }
            }
        });


        //return alreadyReddemed[0];
    }

}
