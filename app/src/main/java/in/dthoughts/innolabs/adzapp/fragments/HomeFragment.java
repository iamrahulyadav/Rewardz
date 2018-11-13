package in.dthoughts.innolabs.adzapp.fragments;


import android.Manifest;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.fragstack.contracts.StackableFragment;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.DexterError;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.PermissionRequestErrorListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import in.dthoughts.innolabs.adzapp.R;
import in.dthoughts.innolabs.adzapp.adapter.AdsListAdapter;
import in.dthoughts.innolabs.adzapp.helper.PrefManager;
import in.dthoughts.innolabs.adzapp.modal.Ads;


public class HomeFragment extends Fragment implements StackableFragment {

    FirebaseFirestore db;
    FirebaseUser user;
    FirebaseAnalytics firebaseAnalytics;
    private FusedLocationProviderClient client;
    private Button button, write_settings_granted_button, xiaomi_permission_button, xiaomi_permissions_granted_button;
    RecyclerView mainlist;
    LottieAnimationView loading_animation_view, empty_animation_view;
    TextView no_location_Tv;
    public AdsListAdapter adsListAdapter;
    public List<Ads> AdsList;
    public String currentLocation, currentState;
    double Lat, Lon;
    int n = 0;
    int delayInMillis = 7000;
    boolean[] alreadyReddemed;
    PrefManager prefManager;
    Timestamp expiryDate_timestamp , createdDate_timestamp;
    Date todayDate, expiryDate, createdDate;

    //public static List<String> AdsIDs;

    private static final int CODE_WRITE_SETTINGS_PERMISSION = 111;

    public HomeFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_home, null);
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // do your variables initialisations here except Views!!!
        firebaseAnalytics = FirebaseAnalytics.getInstance(getContext());
        prefManager = new PrefManager(getContext());
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
                    .withPermissions(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.READ_PHONE_STATE
                            , Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA)
                    .withListener(new MultiplePermissionsListener() {
                        @Override
                        public void onPermissionsChecked(MultiplePermissionsReport report) {
                            // check if all permissions are granted
                            if (report.areAllPermissionsGranted()) {
                                //  Toast.makeText(getContext(), "All permissions are granted!", Toast.LENGTH_SHORT).show();

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
                    Toast.makeText(getActivity(), getString(R.string.error_occured) + error.toString(), Toast.LENGTH_SHORT).show();
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


                    getCurrentCity(Lat, Lon);
                }

            }
        });

        //getting date for querying
        todayDate = java.util.Calendar.getInstance().getTime();

        Log.d("docc6", String.valueOf(todayDate));
        db = FirebaseFirestore.getInstance();


        AdsList = new ArrayList<>();
        adsListAdapter = new AdsListAdapter(getContext(), AdsList);
        //get current user
        user = FirebaseAuth.getInstance().getCurrentUser();


        if (user != null) {
            String userCurrentLocation = currentLocation;
            //Here I had the code of getting ads from db, but i moved it down

        }

    }

    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // initialise your views

        mainlist = view.findViewById(R.id.recyclerView);
        mainlist.setHasFixedSize(true);
        mainlist.setLayoutManager(new LinearLayoutManager(getActivity()));
        mainlist.setAdapter(adsListAdapter);
        loading_animation_view = view.findViewById(R.id.loading_animation_view);
        empty_animation_view = view.findViewById(R.id.empty_animation_view);
        empty_animation_view.cancelAnimation();
        loading_animation_view.setVisibility(View.VISIBLE);
        no_location_Tv = view.findViewById(R.id.no_location_Tv);
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

        try {
            Geocoder gc = new Geocoder(getContext());

            if (gc.isPresent()) {
                List<Address> list = null;
                try {
                    list = gc.getFromLocation(Lat, Lon, 1);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    Address address = list.get(0);
                    StringBuilder str = new StringBuilder();
                    str.append("Name:" + address.getLocality() + "\n");
                    str.append("Sub-Admin Ares: " + address.getSubAdminArea() + "\n");
                    str.append("Admin Area: " + address.getAdminArea() + "\n");
                    str.append("Country: " + address.getCountryName() + "\n");
                    str.append("Country Code: " + address.getCountryCode() + "\n");
                    String strAddress = str.toString();
                    Log.d("address", strAddress);
                    currentLocation = address.getLocality().trim().toLowerCase();
                    currentState = address.getAdminArea().trim().toLowerCase();
                    Toast.makeText(getActivity(), currentLocation,
                            Toast.LENGTH_LONG).show();
                    Log.d("city", currentLocation+" "+address.getAdminArea().trim().toLowerCase().toString());
                } catch (Exception e) {
                    Log.d("error in location", e.getMessage());
                }


                if (user != null) {
                    Log.d("docc6", "statrting quierying");
                    db.collection("Published Ads").whereEqualTo("city", currentLocation).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot doc : task.getResult()) {
                                    n = n + 1;
                                    Log.d("docc6", "inside loop: " + String.valueOf(doc.get("expires_on")));

                                    try {
                                         expiryDate_timestamp = doc.getTimestamp("expires_on");
                                        createdDate_timestamp = doc.getTimestamp("created_on");
                                        Log.d("docc7","timestamp "+ expiryDate_timestamp+" , "+createdDate_timestamp);
                                        expiryDate = expiryDate_timestamp.toDate();
                                        createdDate = createdDate_timestamp.toDate();
                                        Log.d("docc7","timestamp=>date "+ expiryDate+" , "+createdDate);
//                                        expiryDate = sdf.parse(String.valueOf(doc.get("expires_on")));
//                                        createdDate = sdf.parse(String.valueOf(doc.get("created_on")));
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                    if ((todayDate.equals(createdDate) || todayDate.after(createdDate)) && (todayDate.before(expiryDate) || todayDate.equals(expiryDate))) {
                                        Log.d("docc6", "ads havent expired");
                                        checkAlreadyRedeemed(doc);
                                    } else {
                                        Log.d("docc6", "ads expired");
                                    }

                                }
                            } else {

                            }
                            if (n == 0) {
                                getStateAds();
//                                mainlist.setVisibility(View.GONE);
//                                loading_animation_view.pauseAnimation();
//                                loading_animation_view.setVisibility(View.GONE);
//                                empty_animation_view.playAnimation();
//                                empty_animation_view.setVisibility(View.VISIBLE);

                            } else {
                                mainlist.setVisibility(View.VISIBLE);
                                loading_animation_view.pauseAnimation();
                                loading_animation_view.setVisibility(View.GONE);
                                empty_animation_view.setVisibility(View.GONE);
                            }
                        }
                    });
                }
            }
        } catch (Exception err) {
            Log.d("doc", err.getMessage());
        }

    }

    private void getStateAds() {

        db.collection("Published Ads").whereEqualTo("city", currentState).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot doc : task.getResult()) {
                        n = n + 1;
                        Log.d("docc6", "inside loop: " + String.valueOf(doc.get("expires_on")));

                        try {
                            expiryDate_timestamp = doc.getTimestamp("expires_on");
                            createdDate_timestamp = doc.getTimestamp("created_on");
                            Log.d("docc7","timestamp "+ expiryDate_timestamp+" , "+createdDate_timestamp);
                            expiryDate = expiryDate_timestamp.toDate();
                            createdDate = createdDate_timestamp.toDate();
                            Log.d("docc7","timestamp=>date "+ expiryDate+" , "+createdDate);
//                                        expiryDate = sdf.parse(String.valueOf(doc.get("expires_on")));
//                                        createdDate = sdf.parse(String.valueOf(doc.get("created_on")));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if ((todayDate.equals(createdDate) || todayDate.after(createdDate)) && (todayDate.before(expiryDate) || todayDate.equals(expiryDate))) {
                            Log.d("docc6", "ads havent expired");
                            checkAlreadyRedeemed(doc);
                        } else {
                            Log.d("docc6", "ads expired");
                        }

                    }
                } else {

                }
                if (n == 0) {
                    getIndiaAds();

                } else {
                    Toast.makeText(getActivity(), "showing state ads", Toast.LENGTH_SHORT).show();
                    mainlist.setVisibility(View.VISIBLE);
                    loading_animation_view.pauseAnimation();
                    loading_animation_view.setVisibility(View.GONE);
                    empty_animation_view.setVisibility(View.GONE);
                }
            }
        });


    }

    private void getIndiaAds() {
        db.collection("Published Ads").whereEqualTo("city", "india").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot doc : task.getResult()) {
                        n = n + 1;
                        Log.d("docc6", "inside loop: " + String.valueOf(doc.get("expires_on")));

                        try {
                            expiryDate_timestamp = doc.getTimestamp("expires_on");
                            createdDate_timestamp = doc.getTimestamp("created_on");
                            Log.d("docc7","timestamp "+ expiryDate_timestamp+" , "+createdDate_timestamp);
                            expiryDate = expiryDate_timestamp.toDate();
                            createdDate = createdDate_timestamp.toDate();
                            Log.d("docc7","timestamp=>date "+ expiryDate+" , "+createdDate);
//                                        expiryDate = sdf.parse(String.valueOf(doc.get("expires_on")));
//                                        createdDate = sdf.parse(String.valueOf(doc.get("created_on")));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if ((todayDate.equals(createdDate) || todayDate.after(createdDate)) && (todayDate.before(expiryDate) || todayDate.equals(expiryDate))) {
                            Log.d("docc6", "ads havent expired");
                            checkAlreadyRedeemed(doc);
                        } else {
                            Log.d("docc6", "ads expired");
                        }

                    }
                } else {

                }
                if (n == 0) {

                    mainlist.setVisibility(View.GONE);
                    loading_animation_view.pauseAnimation();
                    loading_animation_view.setVisibility(View.GONE);
                    empty_animation_view.playAnimation();
                    empty_animation_view.setVisibility(View.VISIBLE);

                } else {
                    Toast.makeText(getActivity(), "showing India ads", Toast.LENGTH_SHORT).show();
                    mainlist.setVisibility(View.VISIBLE);
                    loading_animation_view.pauseAnimation();
                    loading_animation_view.setVisibility(View.GONE);
                    empty_animation_view.setVisibility(View.GONE);
                }
            }
        });
    }


    public void checkAlreadyRedeemed(final /*DocumentChange*/ QueryDocumentSnapshot doc) {

        alreadyReddemed = new boolean[1];


        db.collection("Transactions").whereEqualTo("user_id", user.getUid()).whereEqualTo("ad_id", doc/*getDocument()*/.getId()).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {

                try {

                    if (queryDocumentSnapshots.size() != 0) {
                        alreadyReddemed[0] = true;
                        Log.d("doc", "inside if:" + String.valueOf(alreadyReddemed[0]));
                    } else {
                        alreadyReddemed[0] = false;
                        // Ads ads = doc.getDocument().toObject(Ads.class).withId(doc.getDocument().getId());
                        Ads ads = doc.toObject(Ads.class).withId(doc.getId());
                        AdsList.add(ads);
                        adsListAdapter.notifyDataSetChanged();
                        Log.d("doc", doc/*.getDocument()*/.getId().toString());
                        Log.d("doc", "inside if:" + String.valueOf(alreadyReddemed[0]));
                    }
                } catch (Exception err) {
                    Log.d("doc", err.getMessage());
                }
            }
        });

    }

    @TargetApi(23)
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == HomeFragment.CODE_WRITE_SETTINGS_PERMISSION && Settings.System.canWrite(getContext())) {
            Log.d("doc1", requestCode + " " + resultCode + " " + "CODE_WRITE_SETTINGS_PERMISSION success");

        }
    }


    @Override
    public String getFragmentStackName() {
        return "HomeFragment";
    }

    @Override
    public void onFragmentScroll() {

    }
}