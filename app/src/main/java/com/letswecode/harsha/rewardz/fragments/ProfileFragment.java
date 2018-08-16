package com.letswecode.harsha.rewardz.fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.letswecode.harsha.rewardz.R;
import com.letswecode.harsha.rewardz.authentication.LoginActivity;
import com.letswecode.harsha.rewardz.ui.AboutActivity;
import com.letswecode.harsha.rewardz.ui.ProfileUpdateActivity;
import com.squareup.picasso.Picasso;

public class ProfileFragment extends Fragment {

    Button updateProfile;
    TextView displayName, mobileNumber, city, rewardPoints ,emailVerified,ringtoneSwitchText;
    Switch adPublisher, ringtoneSwitch;
    ImageView displayPic;
    ProgressBar progressBar;
    Dialog myDialog;


    private String _displayName, _city, _mobileNumber, _adPublisher, _photoUri;

    FirebaseFirestore db;
    FirebaseUser user;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_profile, null);
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // do your variables initialisations here except Views!!!
        db = FirebaseFirestore.getInstance();

        //get current user
        user = FirebaseAuth.getInstance().getCurrentUser();

//
    }

    public void onViewCreated(View view, Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);
        // initialise your views

        updateProfile = view.findViewById(R.id.btn_edit);
        displayName = view.findViewById(R.id.displayname);
        mobileNumber = view.findViewById(R.id.mobilenumber);
        city = view.findViewById(R.id.city);
        adPublisher = view.findViewById(R.id.adpublisher);
        displayPic = view.findViewById(R.id.displaypic);
        progressBar = view.findViewById(R.id.progressBar);
        rewardPoints = view.findViewById(R.id.points);
        emailVerified = view.findViewById(R.id.emailVerified);
        ringtoneSwitch = view.findViewById(R.id.ringtone_switch);
        ringtoneSwitchText = view.findViewById(R.id.ringtoneSwitchText);
               ringtoneSwitchText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
             showRingtonePreferenceDialog();
            }
        });
        readUserProfile();
        user.reload();
        if(!user.isEmailVerified()){
            emailVerified.setVisibility(View.VISIBLE);
        }
        emailVerified.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendEmailVerification();
            }
        });


        updateProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), ProfileUpdateActivity.class);
                i.putExtra("UPDATE__PROFILE", true);
                i.putExtra("DISPLAY__NAME", _displayName);
                i.putExtra("MOBILE__NUMBER", _mobileNumber);
                i.putExtra("CITY__NAME", _city);
                i.putExtra("AD__PUBLISHER", _adPublisher);
                i.putExtra("PHOTO__URI", _photoUri);
                startActivity(i);
            }
        });
    }

    private void showRingtonePreferenceDialog() {

        myDialog = new Dialog(getContext());
        myDialog.setContentView(R.layout.ringtone_switch);
        myDialog.setTitle("Warning!");
        ringtoneSwitch = myDialog.findViewById(R.id.ringtone_switch);
        final SharedPreferences preferences = getActivity().getSharedPreferences("AdzAppRingtoneSwitchValue", Context.MODE_PRIVATE);
        boolean active = preferences.getBoolean("active", true);
        ringtoneSwitch.setChecked(active);
        ringtoneSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                preferences.edit().putBoolean("active", isChecked).commit();
            }
        });
        myDialog.show();
    }

    public  void sendEmailVerification() {

        user.sendEmailVerification()
                .addOnCompleteListener(getActivity(), new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(getActivity(), getString(R.string.email_sent_sucessfully)+ user.getEmail(), Toast.LENGTH_SHORT).show();
                            Log.d("doc", "Verification email sent to " + user.getEmail());
                        } else {
                            Log.d("doc", "sendEmailVerification failed!", task.getException());
                        }
                    }
                });
    }

    //TODO: Add transaction history of user


    private void readUserProfile() {
//TODO: changed onSuccessListener to snapshotChangedListener run it and check for errors
        //TODO: IF above works well remove below commented code.
        DocumentReference userdata = db.collection("usersProfile").document(user.getUid());
        userdata.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@javax.annotation.Nullable DocumentSnapshot documentSnapshot, @javax.annotation.Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.d("ERROR", e.getMessage());
                    return;
                }
                if (documentSnapshot != null && documentSnapshot.exists()) {
                    Log.d("doc",documentSnapshot.getData().toString());

                    displayName.setText(documentSnapshot.get("DisplayName").toString());
                    _displayName = documentSnapshot.get("DisplayName").toString();
                    mobileNumber.setText(documentSnapshot.get("Phone").toString());
                    _mobileNumber = documentSnapshot.get("Phone").toString();
                    city.setText(documentSnapshot.get("City").toString());
                    _city = documentSnapshot.get("City").toString();
                    adPublisher.setChecked(Boolean.parseBoolean(documentSnapshot.get("AdPublisher").toString()));
                    _adPublisher = documentSnapshot.get("AdPublisher").toString();
                    //_photoUri = documentSnapshot.get("PhotoUri").toString();
                    try{
                        Picasso.get()
                                .load(documentSnapshot.get("PhotoUri").toString())
                                .placeholder(R.drawable.ic_account_circle_black_24dp)
                                .into(displayPic);
                    }
                    catch (Exception error ){
                        Log.d("profile pic exception",error.getMessage());
                        Picasso.get().load(R.drawable.ic_account_circle_black_24dp).placeholder(R.drawable.ic_account_circle_black_24dp).into(displayPic);
                    }
                    try{
                        _photoUri = documentSnapshot.getString("PhotoUri").toString();
                    }
                    catch (Exception error){
                        _photoUri = null;
                    }



                }
            }
        });

        //to get reward points
        DocumentReference userRewards = db.collection("userRewards").document(user.getUid());
        userRewards.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@javax.annotation.Nullable DocumentSnapshot documentSnapshot, @javax.annotation.Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.d("ERROR", e.getMessage());
                    return;
                }
                if (documentSnapshot != null && documentSnapshot.exists()){
                    rewardPoints.setText(documentSnapshot.get("Rewards").toString());
                }

            }
        });

        //progressBar.setVisibility(View.VISIBLE); //TODO: ADD SHIMMER EFFECT
      /*  DocumentReference userdata = db.collection("usersProfile").document(user.getUid());
        userdata.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot doc = task.getResult();
                    displayName.setText(doc.get("DisplayName").toString());
                    _displayName = doc.get("DisplayName").toString();
                    mobileNumber.setText(doc.get("Phone").toString());
                    _mobileNumber = doc.get("Phone").toString();
                    city.setText(doc.get("City").toString());
                    _city = doc.get("City").toString();
                    adPublisher.setChecked(Boolean.parseBoolean(doc.get("AdPublisher").toString()));
                    _adPublisher = doc.get("AdPublisher").toString();
                    try{
                        Picasso.get()
                                .load(doc.get("PhotoUri").toString())
                                .into(displayPic);
                    }
                    catch (Exception e ){
                        Log.d("profile pic exception",e.getMessage());
                        Picasso.get().load(R.drawable.ic_account_circle_black_24dp).into(displayPic);
                    }
//                    if(doc.get("PhotoUri").toString() != null){
//                        Picasso.get()
//                                .load(doc.get("PhotoUri").toString())
//                                .into(displayPic);
//                    }else{
//                        Picasso.get().load(R.drawable.ic_account_circle_black_24dp).into(displayPic);
//                    }

                    _photoUri = doc.get("PhotoUri").toString();
                    progressBar.setVisibility(View.INVISIBLE);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d("error", e.getMessage());
                }
            }); */

    }
}