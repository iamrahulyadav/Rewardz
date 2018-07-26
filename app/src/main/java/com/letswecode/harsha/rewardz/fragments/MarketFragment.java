package com.letswecode.harsha.rewardz.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.letswecode.harsha.rewardz.R;
import com.letswecode.harsha.rewardz.fragments.marketCategories.AutoMobileFragment;
import com.letswecode.harsha.rewardz.fragments.marketCategories.EntertainmentFragment;
import com.letswecode.harsha.rewardz.fragments.marketCategories.FashionFragment;
import com.letswecode.harsha.rewardz.fragments.marketCategories.FoodFragment;
import com.letswecode.harsha.rewardz.ui.AddNewAd;

public class MarketFragment extends Fragment {

    ViewPager viewPager;
    ViewPagerAdapter viewPagerAdapter;
    FloatingActionButton fab;
    FirebaseFirestore db;
    FirebaseUser user;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_market, container, false);

        viewPager =  view.findViewById(R.id.viewpager);
        viewPagerAdapter = new MarketFragment.ViewPagerAdapter(getChildFragmentManager());
        viewPager.setAdapter(viewPagerAdapter);
        return view;

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

        fab = view.findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), AddNewAd.class);
                startActivity(i);

            }
        });
    }

    public static class ViewPagerAdapter extends FragmentStatePagerAdapter {
        private static final int NUM_ITEMS = 4;

        public ViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {//TODO: use switch case for categories
            switch (position){
                case 0 :
                    return FoodFragment.newInstance();

                case 1 :
                    return EntertainmentFragment.newInstance();

                case 2 :
                    return FashionFragment.newInstance();

                case 3 :
                    return AutoMobileFragment.newInstance();

                    default:
                     return FoodFragment.newInstance();
            }


        }

        @Override
        public int getCount() {
            return NUM_ITEMS;
        }

        @Override
        public CharSequence getPageTitle(int position) {

           //TODO: use switch case to get page titles for categories name.
            switch (position){
                case 0 :
                    return FoodFragment.PAGE_TITLE;

                case 1 :
                    return EntertainmentFragment.PAGE_TITLE;

                case 2 :
                    return FashionFragment.PAGE_TITLE;

                case 3 :
                    return AutoMobileFragment.PAGE_TITLE;

                default:
                    return FoodFragment.PAGE_TITLE;
            }
        }
    }
}
/*public class MarketFragment extends Fragment {

    FirebaseFirestore db;
    FirebaseUser user;

    private ShimmerFrameLayout mShimmerViewContainer;

    RecyclerView mainlist;
    private AdsListAdapter adsListAdapter;
    private List<Ads> AdsList;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //just change the fragment_dashboard
        //with the fragment you want to inflate
        //like if the class is HomeFragment it should have R.layout.home_fragment
        //if it is DashboardFragment it should have R.layout.fragment_dashboard
        Log.d("life","onCreateView");
        return inflater.inflate(R.layout.fragment_market, null);
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        db = FirebaseFirestore.getInstance();

        AdsList = new ArrayList<>();
        adsListAdapter = new AdsListAdapter(getContext(),AdsList);

        db.collection("Published Ads").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {

                for(DocumentChange doc: queryDocumentSnapshots.getDocumentChanges()){

                    if(doc.getType() == DocumentChange.Type.ADDED){ //DocumentChange.Type.ADDED
                        Ads ads = doc.getDocument().toObject(Ads.class);
                        AdsList.add(ads);
                        Log.d("doc", doc.getDocument().toString());
                        adsListAdapter.notifyDataSetChanged();
                    }

                }
            }
        });
    }

    public void onViewCreated(View view, Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);
        // initialise your views

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
        mainlist.setAdapter(adsListAdapter);

    }
}*/