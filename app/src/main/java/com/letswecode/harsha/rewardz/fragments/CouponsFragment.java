package com.letswecode.harsha.rewardz.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.letswecode.harsha.rewardz.R;
import com.letswecode.harsha.rewardz.adapter.CouponsListAdapter;
import com.letswecode.harsha.rewardz.modal.Transactions;

import java.util.ArrayList;
import java.util.List;

public class CouponsFragment extends Fragment {

    public static final String PAGE_TITLE = "Redeemed Coupons";

    RecyclerView mainlist;

    FirebaseFirestore db;
    FirebaseUser user;

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

        db.collection("Transactions").whereEqualTo("user_id", user.getUid()).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {

               try{
                   for (DocumentChange doc : queryDocumentSnapshots.getDocumentChanges()) {

                       if (doc.getType() == DocumentChange.Type.ADDED) { //DocumentChange.Type.ADDED
                           Transactions transactions = doc.getDocument().toObject(Transactions.class);
                           TransactionsList.add(transactions);
                           Log.d("doc", doc.getDocument().toString());
                           try{
                               couponsListAdapter.notifyDataSetChanged();
                           }catch (Exception error){
                               Log.d("rewardz", error.getMessage());
                           }

                       }

                   }
               }catch (Exception err){
                   Log.d("doc", "onErr: "+err.getMessage());
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


    }
}
