package com.letswecode.harsha.rewardz.fragments;


import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;


import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.DexterError;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.PermissionRequestErrorListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.letswecode.harsha.rewardz.MainActivity;
import com.letswecode.harsha.rewardz.R;
import com.letswecode.harsha.rewardz.adapter.AdsListAdapter;
import com.letswecode.harsha.rewardz.modal.Ads;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    FirebaseFirestore db;
    FirebaseUser user;

    private FusedLocationProviderClient client;

    RecyclerView mainlist;
    private AdsListAdapter adsListAdapter;
    private List<Ads> AdsList;
    public String currentLocation;
    double Lat, Lon;
    boolean[] alreadyReddemed;

    //public static List<String> AdsIDs;

    private ShimmerFrameLayout mShimmerViewContainer;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_home, null);
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // do your variables initialisations here except Views!!!


        client = LocationServices.getFusedLocationProviderClient(getActivity());
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.

            Dexter.withActivity(getActivity())//TODO:ADD more permissions to user on first launch
                    .withPermissions(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.READ_PHONE_STATE
                        ,Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    .withListener(new MultiplePermissionsListener() {
                        @Override
                        public void onPermissionsChecked(MultiplePermissionsReport report) {
                            // check if all permissions are granted
                            if (report.areAllPermissionsGranted()) {
                              //  Toast.makeText(getContext(), "All permissions are granted!", Toast.LENGTH_SHORT).show();
                                Log.d("rewardz","all permissions granted");
                            }
                            if (report.isAnyPermissionPermanentlyDenied()) {
                                // show alert dialog navigating to Settings
                                showSettingsDialog();
                            }
                        }

                        @Override
                        public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                            token.continuePermissionRequest();
                        }
                    }).withErrorListener(new PermissionRequestErrorListener() {
                @Override
                public void onError(DexterError error) {
                    Toast.makeText(getActivity(), getString(R.string.error_occured)+ error.toString(), Toast.LENGTH_SHORT).show();
                }
            }).onSameThread()
                    .check();


            return;
        }
//TODO:TEST BELOW BLOCK OF CODE ON DEVICES RUNNING OS > MARSHMALLOW --FINISHED
        //code block to get the write_settings permission
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            boolean settingsCanWrite = Settings.System.canWrite(getContext());
            if(!settingsCanWrite){
                Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
                intent.setData(Uri.parse("package:" + getContext().getPackageName()));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        }//end of obtaining permission to write settings. WE NEED THIS IN ORDER TO CHANGE RINGTONE.


        client.getLastLocation().addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {

                if (location != null) {
                    Lat = location.getLatitude();
                    Lon = location.getLongitude();

                    Log.d("latlon",String.valueOf(Lat)+ " "+ String.valueOf(Lon));
                    getCurrentCity(Lat, Lon);
                }

            }
        });


        db = FirebaseFirestore.getInstance();

        AdsList = new ArrayList<>();
        adsListAdapter = new AdsListAdapter(getContext() ,AdsList);
        //get current user
        user = FirebaseAuth.getInstance().getCurrentUser();



        if(user != null){
            String userCurrentLocation = currentLocation;
            //Here I had the code of getting ads from db, but i moved it down

        }

    }

    public void onViewCreated(View view, Bundle savedInstanceState) {
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

    private void showSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(getString(R.string.permissions_title));
        builder.setMessage(getString(R.string.permissions_message));
        builder.setPositiveButton(getString(R.string.permissions_goto_settings), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                openSettings();
            }
        });
        builder.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();

    }

    // navigating user to app settings
    private void openSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getActivity().getPackageName(), null);
        intent.setData(uri);
        startActivityForResult(intent, 101);
    }


    public void getCurrentCity(double Lat, double Lon) {

        Geocoder gc = new Geocoder(getContext());
        if(gc.isPresent()){
            List<Address> list = null;
            try {
                list = gc.getFromLocation(Lat, Lon,1);
            } catch (IOException e) {
                e.printStackTrace();
            }
            try{
                Address address = list.get(0);
                StringBuilder str = new StringBuilder();
                str.append("Name:" + address.getLocality() + "\n");
                str.append("Sub-Admin Ares: " + address.getSubAdminArea() +"\n");
                str.append("Admin Area: " + address.getAdminArea() + "\n");
                str.append("Country: " + address.getCountryName() + "\n");
                str.append("Country Code: " + address.getCountryCode() + "\n");
                String strAddress = str.toString();
                Log.d("address", strAddress);
                currentLocation = address.getLocality().trim().toLowerCase().toString();
                Toast.makeText(getActivity(), "one "+currentLocation,
                        Toast.LENGTH_LONG).show();
                Log.d("city", currentLocation);
            }catch (Exception e){
                Log.d("error in location", e.getMessage());
            }




           if(user!= null){
               db.collection("Published Ads").whereEqualTo("city", currentLocation).addSnapshotListener(new EventListener<QuerySnapshot>() {
                   @Override
                   public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {

                       for (final DocumentChange doc : queryDocumentSnapshots.getDocumentChanges()) {

                           if (doc.getType() == DocumentChange.Type.ADDED) { //DocumentChange.Type.MODIFIED
                               checkAlreadyRedeemed(doc);
                                    Log.d("doc", "inside");
//                                    Ads ads = doc.getDocument().toObject(Ads.class).withId(doc.getDocument().getId());
//                                    AdsList.add(ads);
//                                    Log.d("doc", doc.getDocument().getId().toString());
                                   // adsListAdapter.notifyDataSetChanged();


                           }

                       }
                   }
               });
           }
        }
    }


    public void checkAlreadyRedeemed(final DocumentChange doc) {

        alreadyReddemed = new boolean[1];

        //Log.d("doc","user: "+user.getUid()+" ad_id "+adID);
        db.collection("Transactions").whereEqualTo("user_id", user.getUid()).whereEqualTo("ad_id",doc.getDocument().getId()).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {
                Log.d("doc",String.valueOf(queryDocumentSnapshots.size()));
                if(queryDocumentSnapshots.size()!= 0){
                    alreadyReddemed[0] = true;
                    Log.d("doc","inside if:"+ String.valueOf(alreadyReddemed[0]));
                }else {
                    alreadyReddemed[0] = false;
                    Ads ads = doc.getDocument().toObject(Ads.class).withId(doc.getDocument().getId());
                    AdsList.add(ads);
                    adsListAdapter.notifyDataSetChanged();
                    Log.d("doc", doc.getDocument().getId().toString());
                    Log.d("doc","inside if:"+ String.valueOf(alreadyReddemed[0]));
                }
            }
        });


        //return alreadyReddemed[0];
    }

}