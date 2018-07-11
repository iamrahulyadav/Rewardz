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
import android.os.Bundle;
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


import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
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
    String currentLocation;
    double Lat, Lon;

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

            Dexter.withActivity(getActivity())
                    .withPermissions(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.READ_EXTERNAL_STORAGE)
                    .withListener(new MultiplePermissionsListener() {
                        @Override
                        public void onPermissionsChecked(MultiplePermissionsReport report) {
                            // check if all permissions are granted
                            if (report.areAllPermissionsGranted()) {
                                Toast.makeText(getActivity(), "All permissions are granted!", Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(getActivity(), "Error occurred! ", Toast.LENGTH_SHORT).show();
                }
            }).onSameThread()
                    .check();


            return;
        }

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
        adsListAdapter = new AdsListAdapter(AdsList);
        //get current user
        user = FirebaseAuth.getInstance().getCurrentUser();



        if(user != null){
            String userCurrentLocation = currentLocation;
            Toast.makeText(getActivity(), "one "+userCurrentLocation+" two "+currentLocation,
                    Toast.LENGTH_SHORT).show();

//            db.collection("Published Ads").whereEqualTo("city", userCurrentLocation).addSnapshotListener(new EventListener<QuerySnapshot>() {
//                @Override
//                public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {
//
//                    for (DocumentChange doc : queryDocumentSnapshots.getDocumentChanges()) {
//
//                        if (doc.getType() == DocumentChange.Type.ADDED) { //DocumentChange.Type.ADDED
//                            Ads ads = doc.getDocument().toObject(Ads.class);
//                            AdsList.add(ads);
//                            Log.d("doc", doc.getDocument().toString());
//                            adsListAdapter.notifyDataSetChanged();
//                        }
//
//                    }
//                }
//            });
        }




//
    }

    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // initialise your views


        mainlist = view.findViewById(R.id.recyclerView);
        mainlist.setHasFixedSize(true);
        mainlist.setLayoutManager(new LinearLayoutManager(getActivity()));
        mainlist.setAdapter(adsListAdapter);


    }

    private void showSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Need Permissions");
        builder.setMessage("This app needs permission to use this feature. You can grant them in app settings.");
        builder.setPositiveButton("GOTO SETTINGS", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                openSettings();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
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



            db.collection("Published Ads").whereEqualTo("city", currentLocation).addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {

                    for (DocumentChange doc : queryDocumentSnapshots.getDocumentChanges()) {

                        if (doc.getType() == DocumentChange.Type.ADDED) { //DocumentChange.Type.ADDED
                            Ads ads = doc.getDocument().toObject(Ads.class);
                            AdsList.add(ads);
                            Log.d("doc", doc.getDocument().toString());
                            adsListAdapter.notifyDataSetChanged();
                        }

                    }
                }
            });
        }
    }

}