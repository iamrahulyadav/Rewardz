package com.letswecode.harsha.rewardz.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
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
import com.letswecode.harsha.rewardz.adapter.CouponsListAdapter;
import com.letswecode.harsha.rewardz.modal.Transactions;

import java.util.ArrayList;
import java.util.List;

public class CouponsFragment extends Fragment {

    public static final String PAGE_TITLE = "Redeemed Coupons";

    RecyclerView mainlist;
    LottieAnimationView loading_animation_view, empty_animation_view;
    FirebaseFirestore db;
    FirebaseUser user;
    int n =0;

    private CouponsListAdapter couponsListAdapter;
    private List<Transactions> TransactionsList;


    public CouponsFragment() {
        // Required empty public constructor
    }

    public static CouponsFragment newInstance() {
        CouponsFragment fragment = new CouponsFragment();
        return fragment;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        TransactionsList = new ArrayList<>();
        couponsListAdapter = new CouponsListAdapter(getContext() ,TransactionsList);

        db = FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();

//        db.collection("Transactions").whereEqualTo("user_id", user.getUid()).addSnapshotListener(new EventListener<QuerySnapshot>() {
//            @Override
//            public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {
//
//               try{
//                   for (DocumentChange doc : queryDocumentSnapshots.getDocumentChanges()) {
//
//                       if (doc.getType() == DocumentChange.Type.ADDED) { //DocumentChange.Type.ADDED
//                           Transactions transactions = doc.getDocument().toObject(Transactions.class);
//                           TransactionsList.add(transactions);
//                           Log.d("doc", doc.getDocument().toString());
//                           try{
//                               couponsListAdapter.notifyDataSetChanged();
//                           }catch (Exception error){
//                               Log.d("rewardz", error.getMessage());
//                           }
//
//                       }
//
//                   }
//
//               }catch (Exception err){
//                   Log.d("doc", "onErr: "+err.getMessage());
//               }
//            }
//        });
        db.collection("Transactions").whereEqualTo("user_id", user.getUid()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for(QueryDocumentSnapshot doc : task.getResult()){
                        n = n+1;
                        Log.d("docc","inside loop");
                        Transactions transactions = doc.toObject(Transactions.class);
                        TransactionsList.add(transactions);
                    }

                }
                else{
                    Log.d("docc","error");
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_coupons, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);

        mainlist = view.findViewById(R.id.recyclerView);
        mainlist.setHasFixedSize(true);
        mainlist.setLayoutManager(new LinearLayoutManager(getActivity()));
        mainlist.setAdapter(couponsListAdapter);
        loading_animation_view = view.findViewById(R.id.loading_animation_view);
        empty_animation_view = view.findViewById(R.id.empty_animation_view);

        loading_animation_view.setVisibility(View.VISIBLE);

//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                if(couponsListAdapter.getItemCount()== 0){
//                    mainlist.setVisibility(View.GONE);
//                    loading_animation_view.setVisibility(View.GONE);
//                    empty_animation_view.setVisibility(View.VISIBLE);
//
//                } else {
//                    mainlist.setVisibility(View.VISIBLE);
//                    loading_animation_view.setVisibility(View.GONE);
//                    empty_animation_view.setVisibility(View.GONE);
//
//                }
//            }
//        }, 3000);
    }
}
