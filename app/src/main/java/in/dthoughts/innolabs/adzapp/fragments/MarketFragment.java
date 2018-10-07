package in.dthoughts.innolabs.adzapp.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.fragstack.contracts.StackableFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import in.dthoughts.innolabs.adzapp.R;
import in.dthoughts.innolabs.adzapp.fragments.marketCategories.AutoMobileFragment;
import in.dthoughts.innolabs.adzapp.fragments.marketCategories.ElectionsFragment;
import in.dthoughts.innolabs.adzapp.fragments.marketCategories.ElectronicsFragment;
import in.dthoughts.innolabs.adzapp.fragments.marketCategories.EntertainmentFragment;
import in.dthoughts.innolabs.adzapp.fragments.marketCategories.FashionFragment;
import in.dthoughts.innolabs.adzapp.fragments.marketCategories.FoodFragment;
import in.dthoughts.innolabs.adzapp.ui.AddNewAd;

public class MarketFragment extends Fragment implements StackableFragment {

    ViewPager viewPager;
    ViewPagerAdapter viewPagerAdapter;
    FirebaseFirestore db;
    FirebaseUser user;

    private TabLayout mTabLayout;

    public MarketFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_market, container, false);

        viewPager = view.findViewById(R.id.viewpager);
        viewPagerAdapter = new MarketFragment.ViewPagerAdapter(getChildFragmentManager());
        viewPager.setAdapter(viewPagerAdapter);
        // link the tabLayout and the viewpager together
        mTabLayout =  view.findViewById(R.id.tab_layout);
        mTabLayout.setupWithViewPager(viewPager);
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

    }

    @Override
    public String getFragmentStackName() {
        return "MarketFragment";
    }

    @Override
    public void onFragmentScroll() {

    }

    public static class ViewPagerAdapter extends FragmentStatePagerAdapter {
        private static final int NUM_ITEMS = 5;

        public ViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {//TODO: use switch case for categories --FINISHED
            switch (position) {
                case 0:
                    return FoodFragment.newInstance();

                case 1:
                    return EntertainmentFragment.newInstance();

                case 2:
                    return FashionFragment.newInstance();

                case 3:
                    return AutoMobileFragment.newInstance();

                case 4:
                    return ElectronicsFragment.newInstance();

//                case 5:
//                    return ElectionsFragment.newInstance();

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
            switch (position) {
                case 0:
                    return FoodFragment.PAGE_TITLE;

                case 1:
                    return EntertainmentFragment.PAGE_TITLE;

                case 2:
                    return FashionFragment.PAGE_TITLE;

                case 3:
                    return AutoMobileFragment.PAGE_TITLE;

                case 4:
                    return ElectronicsFragment.PAGE_TITLE;

//                case 5:
//                    return ElectionsFragment.PAGE_TITLE;

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