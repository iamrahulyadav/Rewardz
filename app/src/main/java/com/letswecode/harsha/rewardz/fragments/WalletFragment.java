package com.letswecode.harsha.rewardz.fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.letswecode.harsha.rewardz.R;

public class WalletFragment extends Fragment {

    ViewPager viewPager;
    ViewPagerAdapter viewPagerAdapter;
    TextView rewardPoints;

    FirebaseFirestore db;
    FirebaseUser user;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_wallet, container, false);

        viewPager =  view.findViewById(R.id.viewpager);
        viewPagerAdapter = new ViewPagerAdapter(getChildFragmentManager());
        viewPager.setAdapter(viewPagerAdapter);
        return view;

     //   return inflater.inflate(R.layout.fragment_wallet, null);
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // do your variables initialisations here except Views!!!
        db = FirebaseFirestore.getInstance();

        //get current user
        user = FirebaseAuth.getInstance().getCurrentUser();

//
    }

    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rewardPoints =  view.findViewById(R.id.rewardPoints);

        db.collection("userRewards").document(user.getUid()).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@javax.annotation.Nullable DocumentSnapshot documentSnapshot, @javax.annotation.Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.d("ERROR", e.getMessage());
                    return;
                }
                if (documentSnapshot != null && documentSnapshot.exists()){
                    try{
                        rewardPoints.setText(documentSnapshot.get("Rewards").toString());
                    }
                    catch (Exception error){
                        Log.d("rewards", "null-error"+ error.getMessage());
                    }
                }
            }
        });

    }

    public static class ViewPagerAdapter extends FragmentStatePagerAdapter {
        private static final int NUM_ITEMS = 2;

        public ViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            if(position == 0){
                return CouponsFragment.newInstance();
            }
            else {
                return TransactionFragment.newInstance();
            }
        }

        @Override
        public int getCount() {
            return NUM_ITEMS;
        }

        @Override
        public CharSequence getPageTitle(int position) {

            if(position == 0){
                return CouponsFragment.PAGE_TITLE;
            }
            else {
                return TransactionFragment.PAGE_TITLE;
            }
        }
    }
}
