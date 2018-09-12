package in.dthoughts.innolabs.adzapp.fragments;


import android.Manifest;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ComponentName;
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
import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetView;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.DexterError;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.PermissionRequestErrorListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import in.dthoughts.innolabs.adzapp.R;
import in.dthoughts.innolabs.adzapp.adapter.AdsListAdapter;
import in.dthoughts.innolabs.adzapp.helper.DetectDevice;
import in.dthoughts.innolabs.adzapp.helper.PrefManager;
import in.dthoughts.innolabs.adzapp.modal.Ads;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;



public class HomeFragment extends Fragment implements StackableFragment{

    FirebaseFirestore db;
    FirebaseUser user;

    private FusedLocationProviderClient client;
    private Button button, write_settings_granted_button, xiaomi_permission_button,xiaomi_permissions_granted_button;
    RecyclerView mainlist;
    LottieAnimationView loading_animation_view, empty_animation_view;
    TextView no_location_Tv;
    public AdsListAdapter adsListAdapter;
    public List<Ads> AdsList;
    public String currentLocation;
    double Lat, Lon;
    int n=0;
    int delayInMillis = 7000;
    boolean[] alreadyReddemed;
    PrefManager prefManager;

    //public static List<String> AdsIDs;

    private static final int CODE_WRITE_SETTINGS_PERMISSION = 111;

    public HomeFragment(){}
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_home, null);
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // do your variables initialisations here except Views!!!

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
                    .withPermissions(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.READ_PHONE_STATE
                        ,Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.CAMERA)
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
                    Toast.makeText(getActivity(), getString(R.string.error_occured)+ error.toString(), Toast.LENGTH_SHORT).show();
                }
            }).onSameThread()
                    .check();


            return;
        }
//TODO:TEST BELOW BLOCK OF CODE ON DEVICES RUNNING OS > MARSHMALLOW --FINISHED
       //code block to get the write_settings permission

        if(DetectDevice.isMiUi()){
            Log.d("docc","Xiaomi detected");
            if(prefManager.isPermissionGranted() == false){
                showXiaomiPermissionDialog();
            }

        }else{
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                boolean settingsCanWrite = Settings.System.canWrite(getContext());
                if(!settingsCanWrite){
                    showWriteSettingsDialog();

                }
            }
        }
        //end of obtaining permission to write settings. WE NEED THIS IN ORDER TO CHANGE RINGTONE.


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

    private void showXiaomiPermissionDialog() {
        final Dialog xiaomiDialog =  new Dialog(getContext());
        xiaomiDialog.setContentView(R.layout.xiaomi_permission_dialog);
        xiaomiDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        xiaomi_permission_button = xiaomiDialog.findViewById(R.id.xiaomi_permissions_button);
        xiaomi_permissions_granted_button = xiaomiDialog.findViewById(R.id.xiaomi_permissions_granted_button);
        xiaomi_permission_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                xiaomiDialog.dismiss();
                Intent intent = new Intent();
                intent.setComponent(new ComponentName("com.miui.securitycenter", "com.miui.permcenter.autostart.AutoStartManagementActivity"));
                startActivity(intent);
            }
        });
        xiaomi_permissions_granted_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                prefManager.setPermissionDialog(true);
                xiaomiDialog.dismiss();
            }
        });
        xiaomiDialog.show();

        //for tap target view TODO:HAve a look
//          TapTargetView.showFor(xiaomiDialog,
//                  TapTarget.forView(xiaomiDialog.findViewById(R.id.xiaomi_permissions_button),"Please grant permissions","In order to get most points grant permission"));

    }

    private void showWriteSettingsDialog(){
        Log.d("docc","came into this dialog");
        final Dialog dialog = new Dialog(getContext());
        LottieAnimationView animation_view = dialog.findViewById(R.id.animation_view);
        dialog.setContentView(R.layout.write_settings_dialog);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        button = dialog.findViewById(R.id.write_settings_button);
        write_settings_granted_button = dialog.findViewById(R.id.write_settings_granted_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
                intent.setData(Uri.parse("package:" + getContext().getPackageName()));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivityForResult(intent,HomeFragment.CODE_WRITE_SETTINGS_PERMISSION);
            }
        });
        write_settings_granted_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                prefManager.setPermissionDialog(true);
                dialog.dismiss();//TODO: save in shared prerferencs and dont show user again
            }
        });
        dialog.show();

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

        try{
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
                    Toast.makeText(getActivity(), currentLocation,
                            Toast.LENGTH_LONG).show();
                    Log.d("city", currentLocation);
                }catch (Exception e){
                    Log.d("error in location", e.getMessage());
                }




                if(user!= null){
                    db.collection("Published Ads").whereEqualTo("city", currentLocation).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if(task.isSuccessful()){
                                for(QueryDocumentSnapshot doc : task.getResult()){
                                    n = n+1;
                                    Log.d("docc","inside loop");
                                    checkAlreadyRedeemed(doc);
                                }
                            }else{

                            }
                            if(n==0){
                                mainlist.setVisibility(View.GONE);
                                loading_animation_view.pauseAnimation();
                                loading_animation_view.setVisibility(View.GONE);
                                empty_animation_view.playAnimation();
                                empty_animation_view.setVisibility(View.VISIBLE);

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
        }catch (Exception err){
            Log.d("doc", err.getMessage());
        }

    }


    public void checkAlreadyRedeemed(final /*DocumentChange*/ QueryDocumentSnapshot doc) {

        alreadyReddemed = new boolean[1];


        db.collection("Transactions").whereEqualTo("user_id", user.getUid()).whereEqualTo("ad_id",doc/*getDocument()*/.getId()).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {

                try{

                    if(queryDocumentSnapshots.size()!= 0){
                        alreadyReddemed[0] = true;
                        Log.d("doc","inside if:"+ String.valueOf(alreadyReddemed[0]));
                    }else {
                        alreadyReddemed[0] = false;
                       // Ads ads = doc.getDocument().toObject(Ads.class).withId(doc.getDocument().getId());
                        Ads ads = doc.toObject(Ads.class).withId(doc.getId());
                        AdsList.add(ads);
                        adsListAdapter.notifyDataSetChanged();
                        Log.d("doc", doc/*.getDocument()*/.getId().toString());
                        Log.d("doc","inside if:"+ String.valueOf(alreadyReddemed[0]));
                    }
                }catch (Exception err){
                    Log.d("doc",err.getMessage());
                }
            }
        });

    }

    @TargetApi(23)
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == HomeFragment.CODE_WRITE_SETTINGS_PERMISSION && Settings.System.canWrite(getContext())){
            Log.d("doc1", requestCode+" "+resultCode+" "+"CODE_WRITE_SETTINGS_PERMISSION success");

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