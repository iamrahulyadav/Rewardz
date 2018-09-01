package com.letswecode.harsha.rewardz.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.letswecode.harsha.rewardz.R;
import com.letswecode.harsha.rewardz.adapter.TransactionsListAdapter;
import com.letswecode.harsha.rewardz.modal.Transactions;

import java.util.ArrayList;
import java.util.List;

public class TransactionFragment extends Fragment {

    public static final String PAGE_TITLE = "Transactions";

    RecyclerView mainlist;
    LottieAnimationView loading_animation_view, empty_animation_view;
    FirebaseFirestore db;
    FirebaseUser user;

    private TransactionsListAdapter transactionsListAdapter;
    private List<Transactions> TransactionsList;

    public TransactionFragment() {
    }

    public static TransactionFragment newInstance() {
        TransactionFragment fragment = new TransactionFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        TransactionsList = new ArrayList<>();
        transactionsListAdapter = new TransactionsListAdapter(getContext() ,TransactionsList);

        db = FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();

        db.collection("Transactions").whereEqualTo("user_id", user.getUid()).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {
                try{
                    for (DocumentChange doc : queryDocumentSnapshots.getDocumentChanges()) {

                        if (doc.getType() == DocumentChange.Type.ADDED) { //DocumentChange.Type.ADDED
                            Transactions ads = doc.getDocument().toObject(Transactions.class);
                            TransactionsList.add(ads);
                            Log.d("doc", doc.getDocument().toString());
                            try{
                                transactionsListAdapter.notifyDataSetChanged();
                            }catch (Exception error){
                                Log.d("rewardz",error.getMessage());
                            }

                        }

                    }

                }catch (Exception errr){
                    Log.d("doc",errr.getMessage());
                }
            }
        });

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_transactions, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);

        mainlist = view.findViewById(R.id.recyclerView);
        mainlist.setHasFixedSize(true);
        mainlist.setLayoutManager(new LinearLayoutManager(getActivity()));
        mainlist.setAdapter(transactionsListAdapter);
        loading_animation_view = view.findViewById(R.id.loading_animation_view);
        empty_animation_view = view.findViewById(R.id.empty_animation_view);

        loading_animation_view.setVisibility(View.VISIBLE);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(transactionsListAdapter.getItemCount()== 0){
                    mainlist.setVisibility(View.GONE);
                    loading_animation_view.setVisibility(View.GONE);
                    empty_animation_view.setVisibility(View.VISIBLE);
                } else {
                    mainlist.setVisibility(View.VISIBLE);
                    loading_animation_view.setVisibility(View.GONE);
                    empty_animation_view.setVisibility(View.GONE);
                }
            }
        }, 3000);


    }
}
