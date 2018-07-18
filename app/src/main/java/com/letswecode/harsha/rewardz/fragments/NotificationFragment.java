package com.letswecode.harsha.rewardz.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.letswecode.harsha.rewardz.R;
import com.letswecode.harsha.rewardz.adapter.AdsListAdapter;
//import com.letswecode.harsha.rewardz.adapter.NotifListAdapter;
import com.letswecode.harsha.rewardz.modal.Ads;

import java.util.ArrayList;
import java.util.List;

public class NotificationFragment extends Fragment {

    FirebaseFirestore db;
    FirebaseUser user;

    private ShimmerFrameLayout mShimmerViewContainer;

    RecyclerView mainlist;
   // private NotifListAdapter notifListAdapter;
    private List<Ads> AdsList;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //just change the fragment_dashboard
        //with the fragment you want to inflate
        //like if the class is HomeFragment it should have R.layout.home_fragment
        //if it is DashboardFragment it should have R.layout.fragment_dashboard
        return inflater.inflate(R.layout.fragment_notification, null);
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        db = FirebaseFirestore.getInstance();

        AdsList = new ArrayList<>();
       // notifListAdapter = new NotifListAdapter(getContext(),AdsList);
    }

    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mShimmerViewContainer =  view.findViewById(R.id.shimmer_view_container);
        mShimmerViewContainer.startShimmerAnimation();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mShimmerViewContainer.stopShimmerAnimation();
                mShimmerViewContainer.setVisibility(View.INVISIBLE);
            }
        }, 5000);

        mainlist = view.findViewById(R.id.recyclerView);
        mainlist.setHasFixedSize(true);
        mainlist.setLayoutManager(new LinearLayoutManager(getActivity()));
        //mainlist.setAdapter(notifListAdapter);
    }
}
