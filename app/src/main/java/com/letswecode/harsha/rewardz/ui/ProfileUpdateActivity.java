package com.letswecode.harsha.rewardz.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.letswecode.harsha.rewardz.R;
import com.squareup.picasso.Picasso;


import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ProfileUpdateActivity extends AppCompatActivity {

    private static final String NAME_KEY = "DisplayName",
            EMAIL_KEY = "Email",
            PHONE_KEY = "Phone",
            CITY_KEY = "City",
            REWARDS_KEY = "Rewards",
            PHOTOURI_KEY = "PhotoUri",
            ADPUBLISHER_KEY = "AdPublisher",
            USERID_KEY = "UserID"
                    ;

    FirebaseFirestore db;
    FirebaseAuth mAuth;
    FirebaseUser user;
    FirebaseStorage firebaseStorage;

    Task<Uri> urlTask;


    TextView displaynameTV, cityTV, mobilenumberTV;
    Switch adpublisherSW;
    Button savedetialsBT;
    ImageButton imageButtonIB;
    ImageView imageViewIV;
    ProgressBar progressBar, progressBar2;

    private String displayName, city, mobileNumber, userId, photoUri, adPublisher, email, rewards, updatedprofilepic;
    private Boolean updateProfile = false, profilePicChanged = false;
    private static final int PICK_IMAGE_REQUEST = 234;
    //a Uri object to store file path
    private Uri filePath, downloadUri;


    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_update);

        final Bundle extras = getIntent().getExtras();
        if(extras != null){
            updateProfile = extras.getBoolean("UPDATE__PROFILE", false);
        }


        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();



        //get current user
        user = FirebaseAuth.getInstance().getCurrentUser();

        displaynameTV = findViewById(R.id.displayname);
        cityTV = findViewById(R.id.city);
        mobilenumberTV = findViewById(R.id.mobilenumber);
        adpublisherSW = findViewById(R.id.adpublisher);
        imageViewIV =  findViewById(R.id.displaypic);
        imageButtonIB = findViewById(R.id.changePic);
        savedetialsBT = findViewById(R.id.btn_save);
        progressBar = findViewById(R.id.progressBar);
        progressBar2 =  findViewById(R.id.progressBar2);


        //filling the details into resp. fields when user came from profile page to update GET ALL DATA THROUGH INTENT EXTRAS
        //TODO:DOnt forget to get intent extras from profile page while updating a user data. -- FINISHED
        if(updateProfile){
            displayName = extras.getString("DISPLAY__NAME");
            mobileNumber = extras.getString("MOBILE__NUMBER");
            city = extras.getString("CITY__NAME");
            adPublisher = extras.getString("AD__PUBLISHER");
            photoUri = extras.getString("PHOTO__URI");



            displaynameTV.setText(displayName);
            mobilenumberTV.setText(mobileNumber);
            cityTV.setText(city);
            adpublisherSW.setChecked(Boolean.parseBoolean(adPublisher));
            //imageViewIV.setImageURI(Uri.parse("https://firebasestorage.googleapis.com/v0/b/startup-demos.appspot.com/o/profilePic%2Fi6zk0sOlrvPuSufDLndHTgdZdYq2.jpg?alt=media&token=918471f6-2c1b-4200-95a7-38aedd8a7131"));//(Uri.parse(photoUri));
            //TODO:apply glide properly and check it out later -- FINISHED(instead of glide used PICASSO)


            try{
                //Picasso.get()
                Picasso.with(getApplicationContext())
                        .load(photoUri)
                        .placeholder(R.drawable.ic_account_circle_black_24dp)//"https://firebasestorage.googleapis.com/v0/b/startup-demos.appspot.com/o/profilePic%2Fi6zk0sOlrvPuSufDLndHTgdZdYq2.jpg?alt=media&token=918471f6-2c1b-4200-95a7-38aedd8a7131")
                        .into(imageViewIV);
            }
            catch (Exception error){
                Log.d("profile pic exception",error.getMessage());
                //Picasso.get().load(R.drawable.ic_account_circle_black_24dp).placeholder(R.drawable.ic_account_circle_black_24dp).into(imageViewIV);
                Picasso.with(getApplicationContext()).load(R.drawable.ic_account_circle_black_24dp).placeholder(R.drawable.ic_account_circle_black_24dp).into(imageViewIV);
            }

        }


        savedetialsBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!updateProfile){
                    addNewUser();
                }
                else {
                    updateUser();
                }

            }
        });

        imageButtonIB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, getString(R.string.intent_choose_image)), PICK_IMAGE_REQUEST);
            }
        });
    }

    //handling the image chooser activity result
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            filePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                imageViewIV.setImageBitmap(bitmap);
                uploadImageFile();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void uploadImageFile() {
        progressBar.setVisibility(View.VISIBLE);

        final   StorageReference storageRef = firebaseStorage.getReference();
        final StorageReference uploadeRef = storageRef.child("profilePic/"+user.getUid()+".jpg");

        UploadTask uploadTask = uploadeRef.putFile(filePath);

        urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {

                    throw task.getException();

                }

                return uploadeRef.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    downloadUri = task.getResult();
                    updatedprofilepic = String.valueOf(task.getResult());
                    //uploadedImage = task.getResult().toString();
                    progressBar.setVisibility(View.INVISIBLE);

                    Log.i("url",downloadUri.toString());
                    profilePicChanged = true;

                } else {
                    // Handle failures
                    // ...
                }
            }
        });
    }

    private void addNewUser() {
//TODO:MAKE sure user dont leave any field empty which in turn crashes app when we try to retrive data from FIREBASE
        //TODO: if user wont upload a photo app crashes while retriving make it empty if not uploaded (code written test it once)
        progressBar2.setVisibility(View.VISIBLE);

        if(!displaynameTV.getText().toString().isEmpty()){
            displayName = displaynameTV.getText().toString();
        }else {displayName = " ";}

        email = user.getEmail();
        userId = user.getUid();

        if(!mobilenumberTV.getText().toString().isEmpty()){
            mobileNumber = mobilenumberTV.getText().toString();
        }else { mobileNumber = " ";}

        if(!cityTV.getText().toString().isEmpty()){
            city = cityTV.getText().toString().toLowerCase();
            subscribeUserToCityNotif(city);
        }else { city = " ";}

        adPublisher = Boolean.toString(adpublisherSW.isChecked());
        rewards = "0";
        try{
            if(downloadUri.toString()!=null){
                photoUri = downloadUri.toString();
            }
            else {
                photoUri = " ";
            }
        }catch (Exception errr){
            Log.d("doc",errr.getMessage());
            photoUri = " ";
        }

        Log.i("photouri", photoUri);//TODO: change with uploaded image URL - FINISHED

        Map< String, Object > newUser = new HashMap< >();
        newUser.put(NAME_KEY, displayName);
        newUser.put(EMAIL_KEY, email);
        newUser.put(USERID_KEY, userId);
        newUser.put(PHONE_KEY, mobileNumber);
        newUser.put(CITY_KEY, city);
        newUser.put(ADPUBLISHER_KEY, adPublisher);
        newUser.put(PHOTOURI_KEY, photoUri);
        newUser.put(REWARDS_KEY, rewards);

        db.collection("usersProfile").document(userId).set(newUser)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        progressBar2.setVisibility(View.INVISIBLE);
                        Toast.makeText(ProfileUpdateActivity.this, getString(R.string.user_registred),
                                Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(ProfileUpdateActivity.this, MainActivity.class));
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        progressBar2.setVisibility(View.INVISIBLE);
                        Toast.makeText(ProfileUpdateActivity.this, getString(R.string.error_occured) + e.toString(),
                                Toast.LENGTH_SHORT).show();
                        Log.d("DATABASE ERROR", e.toString());
                    }
                });

    }

    private void subscribeUserToCityNotif(String cityName) {

        FirebaseMessaging.getInstance().subscribeToTopic(cityName)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        String msg = getString(R.string.msg_subscribed);
                        if (!task.isSuccessful()) {
                            msg = getString(R.string.msg_subscribe_failed);
                        }
                        Toast.makeText(ProfileUpdateActivity.this, msg, Toast.LENGTH_SHORT).show();//TODO: remove this toast in release build
                    }
                });
    }

    //TODO: create a UpdateUser method if existing user came to this activity from profile page.-- FINISHED
    private void updateUser() {

        progressBar2.setVisibility(View.VISIBLE);

        if(!cityTV.getText().toString().isEmpty()){
            subscribeUserToCityNotif(cityTV.getText().toString().toLowerCase());
        }
        DocumentReference updatingProfile = db.collection("usersProfile").document(user.getUid());
        updatingProfile.update(NAME_KEY, displaynameTV.getText().toString());
        updatingProfile.update(PHONE_KEY, mobilenumberTV.getText().toString());
        updatingProfile.update(CITY_KEY, cityTV.getText().toString().toLowerCase());
        updatingProfile.update(ADPUBLISHER_KEY, Boolean.toString(adpublisherSW.isChecked()));
        updatingProfile.update(PHOTOURI_KEY, profilePicChanged ? updatedprofilepic: photoUri ).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                progressBar2.setVisibility(View.INVISIBLE);
                Toast.makeText(ProfileUpdateActivity.this, getString(R.string.updated_successfully),
                        Toast.LENGTH_SHORT).show();

                finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressBar2.setVisibility(View.INVISIBLE);
                Toast.makeText(ProfileUpdateActivity.this, getString(R.string.update_failed)+e.getMessage(),
                        Toast.LENGTH_SHORT).show();

            }
        });
    }



}