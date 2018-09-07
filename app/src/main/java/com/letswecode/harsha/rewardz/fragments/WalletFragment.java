package com.letswecode.harsha.rewardz.fragments;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.fragstack.contracts.StackableFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.letswecode.harsha.rewardz.R;
import com.letswecode.harsha.rewardz.helper.DetectDevice;

public class WalletFragment extends Fragment implements StackableFragment{

    ViewPager viewPager;
    ViewPagerAdapter viewPagerAdapter;
    TextView rewardPoints, write_settings_text;
    Snackbar snackbar;
    FirebaseFirestore db;
    FirebaseUser user;

    public WalletFragment(){}

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
       // write_settings_text = view.findViewById(R.id.write_settings_text);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            boolean settingsCanWrite = Settings.System.canWrite(getContext());
            if(!settingsCanWrite){
                if(DetectDevice.isMiUi()){

                     snackbar = Snackbar.make(getActivity().findViewById(R.id.container), "Permission not granted", Snackbar.LENGTH_INDEFINITE)
                            .setAction("Show me how", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    showWriteSettingsWay();
                                }
                            });
                    int height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 56, getResources().getDisplayMetrics());
                    FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) snackbar.getView().getLayoutParams();
                    params.setMargins(0, 0, 0, height);
                    snackbar.getView().setLayoutParams(params);
                    snackbar.show();
                }


            }
        }
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

    protected void showWriteSettingsWay() {
        final Dialog xiaomiDialog =  new Dialog(getContext());
        xiaomiDialog.setContentView(R.layout.xiaomi_permission_dialog);
        xiaomiDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        TextView tv = xiaomiDialog.findViewById(R.id.tv);
        tv.setText("Grant Modify system settings permission by opening SETTINGS > INSTALLED APPS > ADZAPP > OTHER PERMISSION");
       Button xiaomi_permission_button = xiaomiDialog.findViewById(R.id.xiaomi_permissions_button);
       Button  xiaomi_permissions_granted_button = xiaomiDialog.findViewById(R.id.xiaomi_permissions_granted_button);
      xiaomi_permissions_granted_button.setVisibility(View.GONE);
        xiaomi_permission_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                xiaomiDialog.dismiss();

            }
        });

        xiaomiDialog.show();
    }

    @Override
    public String getFragmentStackName() {
        return "WalletFragment";
    }

    @Override
    public void onFragmentScroll() {

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

    @Override
    public void onDestroy(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            boolean settingsCanWrite = Settings.System.canWrite(getContext());
            if (!settingsCanWrite) {
                if(DetectDevice.isMiUi()) {
                    snackbar.dismiss();
                }
            }

        }
        super.onDestroy();
    }
}
