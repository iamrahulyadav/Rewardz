package com.letswecode.harsha.rewardz.ui;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.letswecode.harsha.rewardz.R;
import com.letswecode.harsha.rewardz.adapter.SearchAdapter;

import java.util.ArrayList;

import javax.annotation.Nullable;

public class SearchActivity extends AppCompatActivity {

    EditText search_editText;
    RecyclerView recyclerView;
    FirebaseFirestore db;
    FirebaseUser user;
    ArrayList<String> publisherNameList;
    ArrayList<String> profilePicList;
    SearchAdapter searchAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        Toolbar mToolbar = findViewById(R.id.toolbar_search);
        mToolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();
            }
        });

        search_editText = mToolbar.findViewById(R.id.search_editText);
        recyclerView = findViewById(R.id.recyclerView);


        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));

        db = FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();

        publisherNameList = new ArrayList<>();
        profilePicList = new ArrayList<>();

        search_editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(!s.toString().isEmpty()){
                        setAdapter(s.toString());
                }
                else{
                    publisherNameList.clear();
                    profilePicList.clear();
                    recyclerView.removeAllViews();
                }
            }
        });
    }

    private void setAdapter(final String searchedString) {
Log.d("docc3","came into set adapter");

        db.collection("Published Ads").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                publisherNameList.clear();
                profilePicList.clear();
                recyclerView.removeAllViews();

                if(task.isSuccessful()){
                    int counter = 0;
                    for(QueryDocumentSnapshot doc : task.getResult()){
                        String Publisher_Name = doc.get("publisher_name").toString();
                        String Publisher_Image = doc.get("publisher_image").toString();

                        if(Publisher_Name.toLowerCase().contains(searchedString.toLowerCase())){
                            publisherNameList.add(Publisher_Name);
                            profilePicList.add(Publisher_Image);
                            counter++;
                        }
                        if(counter == 10){
                            break;
                        }

                    }
                    searchAdapter = new SearchAdapter(SearchActivity.this, publisherNameList, profilePicList);
                    recyclerView.setAdapter(searchAdapter);
                }else{
                        Log.d("docc3","rror");
                }

            }
        });


    }
}
