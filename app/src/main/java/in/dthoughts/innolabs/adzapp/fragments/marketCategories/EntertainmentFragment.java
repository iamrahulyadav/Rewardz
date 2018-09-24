package in.dthoughts.innolabs.adzapp.fragments.marketCategories;

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
import in.dthoughts.innolabs.adzapp.R;
import in.dthoughts.innolabs.adzapp.adapter.AdsListAdapter;
import in.dthoughts.innolabs.adzapp.modal.Ads;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class EntertainmentFragment extends Fragment {
    public static final String PAGE_TITLE = "Entertainment"; //TODO:change this strings.xml

    RecyclerView mainlist;
    LottieAnimationView  empty_animation_view;
    FirebaseFirestore db;
    FirebaseUser user;
    Date todayDate,expiryDate,createdDate;
    int n=0;
    private AdsListAdapter adsListAdapter;
    private List<Ads> AdsList;

    public EntertainmentFragment() {
        // Required empty public constructor
    }

    public static EntertainmentFragment newInstance() {
        EntertainmentFragment fragment = new EntertainmentFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //getting date for querying
        todayDate=java.util.Calendar.getInstance().getTime();
        db = FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
        AdsList = new ArrayList<>();
        adsListAdapter = new AdsListAdapter(getContext(),AdsList);

//        db.collection("Published Ads").whereEqualTo("category","entertainment").addSnapshotListener(new EventListener<QuerySnapshot>() {
//            @Override
//            public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {
//
//                try{
//                    for(DocumentChange doc: queryDocumentSnapshots.getDocumentChanges()){
//
//                        if(doc.getType() == DocumentChange.Type.ADDED){ //DocumentChange.Type.ADDED
//                            checkAlreadyRedeemed(doc);
////                        Ads ads = doc.getDocument().toObject(Ads.class).withId(doc.getDocument().getId());
////                        AdsList.add(ads);
////                        Log.d("doc", doc.getDocument().toString());
////                        adsListAdapter.notifyDataSetChanged();
//                        }
//
//                    }
//                }catch (Exception err){
//                    Log.d("doc","err "+err.getMessage());
//                }
//            }
//        });
        db.collection("Published Ads").whereEqualTo("category","entertainment").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for(QueryDocumentSnapshot doc : task.getResult()){
                        n = n+1;
                        Log.d("docc","inside loop");
                        SimpleDateFormat sdf = new SimpleDateFormat("E MMM dd HH:mm:ss z yyyy");
                        try {
                            expiryDate = sdf.parse(String.valueOf(doc.get("expires_on")));
                            createdDate = sdf.parse(String.valueOf(doc.get("created_on")));
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        if((todayDate.equals(createdDate) || todayDate.after(createdDate)) && (todayDate.before(expiryDate) || todayDate.equals(expiryDate))){
                            Log.d("docc6","ads havent expired");
                            checkAlreadyRedeemed(doc);
                        }
                        else{
                            Log.d("docc6","ads expired");
                        }
                    }
                }
                else{

                }
                if(n==0){
                    mainlist.setVisibility(View.GONE);
//                    loading_animation_view.pauseAnimation();
//                    loading_animation_view.setVisibility(View.GONE);
                    empty_animation_view.playAnimation();
                    empty_animation_view.setVisibility(View.VISIBLE);

                } else {
                    mainlist.setVisibility(View.VISIBLE);
//                    loading_animation_view.pauseAnimation();
//                    loading_animation_view.setVisibility(View.GONE);
                    empty_animation_view.setVisibility(View.GONE);
                }
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_categories, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);

        mainlist = view.findViewById(R.id.recyclerView);
        mainlist.setHasFixedSize(true);
        mainlist.setLayoutManager(new LinearLayoutManager(getActivity()));
        mainlist.setAdapter(adsListAdapter);
        empty_animation_view = view.findViewById(R.id.empty_animation_view);
        empty_animation_view.cancelAnimation();

//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                if(adsListAdapter.getItemCount()== 0){
//                    mainlist.setVisibility(View.GONE);
//
//                    empty_animation_view.playAnimation();
//                    empty_animation_view.setVisibility(View.VISIBLE);
//                } else {
//                    mainlist.setVisibility(View.VISIBLE);
//                    empty_animation_view.setVisibility(View.GONE);
//                }
//            }
//        }, 3000);


    }

//    public void checkAlreadyRedeemed(final DocumentChange doc) {
//
//        //alreadyReddemed = new boolean[1];
//
//        //Log.d("doc","user: "+user.getUid()+" ad_id "+adID);
//        db.collection("Transactions").whereEqualTo("user_id", user.getUid()).whereEqualTo("ad_id",doc.getDocument().getId()).addSnapshotListener(new EventListener<QuerySnapshot>() {
//            @Override
//            public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {
//                //Log.d("doc",String.valueOf(queryDocumentSnapshots.size()));
//                try{
//                    if(queryDocumentSnapshots.size()!= 0){
//                        //alreadyReddemed[0] = true;
//                        // Log.d("doc","inside if:"+ String.valueOf(alreadyReddemed[0]));
//                    }else {
//                        //alreadyReddemed[0] = false;
//                        Ads ads = doc.getDocument().toObject(Ads.class).withId(doc.getDocument().getId());
//                        AdsList.add(ads);
//                        adsListAdapter.notifyDataSetChanged();
//                        Log.d("doc", doc.getDocument().getId().toString());
//                        //Log.d("doc","inside if:"+ String.valueOf(alreadyReddemed[0]));
//                    }
//                }catch (Exception errrr){
//
//                }
//
//            }
//        });
//
//
//        //return alreadyReddemed[0];
//    }
public void checkAlreadyRedeemed(final /*DocumentChange*/ QueryDocumentSnapshot doc) {

    //alreadyReddemed = new boolean[1];


    db.collection("Transactions").whereEqualTo("user_id", user.getUid()).whereEqualTo("ad_id",doc/*getDocument()*/.getId()).addSnapshotListener(new EventListener<QuerySnapshot>() {
        @Override
        public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {

            try{

                if(queryDocumentSnapshots.size()!= 0){
                    //                    alreadyReddemed[0] = true;
                    //                    Log.d("doc","inside if:"+ String.valueOf(alreadyReddemed[0]));
                }else {
                    //alreadyReddemed[0] = false;
                    // Ads ads = doc.getDocument().toObject(Ads.class).withId(doc.getDocument().getId());
                    Ads ads = doc.toObject(Ads.class).withId(doc.getId());
                    AdsList.add(ads);
                    adsListAdapter.notifyDataSetChanged();
                    //                    Log.d("doc", doc/*.getDocument()*/.getId().toString());
                    //                    Log.d("doc","inside if:"+ String.valueOf(alreadyReddemed[0]));
                }
            }catch (Exception err){
                Log.d("doc",err.getMessage());
            }
        }
    });

}

}
