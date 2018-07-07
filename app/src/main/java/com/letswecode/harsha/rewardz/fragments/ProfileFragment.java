package com.letswecode.harsha.rewardz.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.letswecode.harsha.rewardz.R;
import com.letswecode.harsha.rewardz.ui.ProfileUpdateActivity;
import com.squareup.picasso.Picasso;

public class ProfileFragment extends Fragment {

    Button updateProfile;
    TextView displayName, mobileNumber, city;
    Switch adPublisher;
    ImageView displayPic;
    ProgressBar progressBar;

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

        readUserProfile();


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

    private void readUserProfile() {
        progressBar.setVisibility(View.VISIBLE);
        DocumentReference userdata = db.collection("usersProfile").document(user.getUid());
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
                    Picasso.get()
                            .load(doc.get("PhotoUri").toString())
                            .into(displayPic);
                    _photoUri = doc.get("PhotoUri").toString();
                    progressBar.setVisibility(View.INVISIBLE);
                }
            }
        })
            .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d("error", e.getMessage());
                }
            });

    }
}