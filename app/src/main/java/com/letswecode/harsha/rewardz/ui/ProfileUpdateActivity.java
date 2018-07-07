package com.letswecode.harsha.rewardz.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.letswecode.harsha.rewardz.MainActivity;
import com.letswecode.harsha.rewardz.R;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.net.URI;
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

    private String displayName, city, mobileNumber, userId, photoUri, adPublisher, email, rewards, uploadedImage= "hh";
    private Boolean updateProfile = false;
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


        //filling the details into rersp. fields when user came from profile page to update GET ALL DATA THROUGH INTENT EXTRAS
        //TODO:DOnt forget to get intent extras from profile page while updating a user data.
        if(updateProfile == true){
            displayName = extras.getString("DISPLAY__NAME");
            mobileNumber = extras.getString("MOBILE__NUMBER");
            city = extras.getString("CITY__NAME");
            adPublisher = extras.getString("AD__PUBLISHER");;
            photoUri = extras.getString("PHOTO__URL");

            Log.d("extras", extras.toString());

            displaynameTV.setText(displayName);
            mobilenumberTV.setText(mobileNumber);
            cityTV.setText(city);
            adpublisherSW.setChecked(Boolean.parseBoolean(adPublisher));
            //imageViewIV.setImageURI(Uri.parse("https://firebasestorage.googleapis.com/v0/b/startup-demos.appspot.com/o/profilePic%2Fi6zk0sOlrvPuSufDLndHTgdZdYq2.jpg?alt=media&token=918471f6-2c1b-4200-95a7-38aedd8a7131"));//(Uri.parse(photoUri));
            //TODO:apply glide properly and check it out later
            Picasso.get()
                    .load(photoUri)//"https://firebasestorage.googleapis.com/v0/b/startup-demos.appspot.com/o/profilePic%2Fi6zk0sOlrvPuSufDLndHTgdZdYq2.jpg?alt=media&token=918471f6-2c1b-4200-95a7-38aedd8a7131")
                    .into(imageViewIV);
            //GlideApp.with(this).load(photoUri).into(imageViewIV);
        }


        savedetialsBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(updateProfile == false){
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
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
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
                    uploadedImage = task.getResult().toString();
                    progressBar.setVisibility(View.INVISIBLE);
                    Log.i("url",downloadUri.toString());

                } else {
                    // Handle failures
                    // ...
                }
            }
        });
    }

    private void addNewUser() {

        progressBar2.setVisibility(View.VISIBLE);

        displayName = displaynameTV.getText().toString();
        email = user.getEmail();
        userId = user.getUid();
        mobileNumber = mobilenumberTV.getText().toString();
        city = cityTV.getText().toString();
        adPublisher = Boolean.toString(adpublisherSW.isChecked());
        rewards = "0";
        photoUri = downloadUri.toString();
        Log.i("photouri", photoUri);//TODO: change with uploaded image URL (guess finished have to check)

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
                        Toast.makeText(ProfileUpdateActivity.this, "User Registered",
                                Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(ProfileUpdateActivity.this, MainActivity.class));
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressBar2.setVisibility(View.INVISIBLE);
                        Toast.makeText(ProfileUpdateActivity.this, "ERROR" + e.toString(),
                                Toast.LENGTH_SHORT).show();
                        Log.d("DATABASE ERROR", e.toString());
                    }
                });

    }

    //TODO: create a UpdateUser method if existing user came to this activity from profile page.
    private void updateUser() {

        progressBar2.setVisibility(View.VISIBLE);
        DocumentReference updatingProfile = db.collection("usersProfile").document(user.getUid());
        updatingProfile.update(NAME_KEY, displayName);
        updatingProfile.update(PHONE_KEY, mobileNumber);
        updatingProfile.update(CITY_KEY, city);
        updatingProfile.update(ADPUBLISHER_KEY, adPublisher);
        updatingProfile.update(PHOTOURI_KEY, photoUri).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                progressBar2.setVisibility(View.INVISIBLE);
                Toast.makeText(ProfileUpdateActivity.this, "Updated Successfully",
                        Toast.LENGTH_SHORT).show();
                finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressBar2.setVisibility(View.INVISIBLE);
                Toast.makeText(ProfileUpdateActivity.this, "Updated Failed, please try again",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }



}