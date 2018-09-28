package in.dthoughts.innolabs.adzapp.fragments;


import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.fragstack.contracts.StackableFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.HashMap;
import java.util.Map;

import in.dthoughts.innolabs.adzapp.R;
import in.dthoughts.innolabs.adzapp.helper.DetectDevice;

import static android.content.Context.MODE_PRIVATE;

public class WalletFragment extends Fragment implements StackableFragment {

    ViewPager viewPager;
    ViewPagerAdapter viewPagerAdapter;
    Button gift_dismiss;
    TextView rewardPoints, write_settings_text;
    Snackbar snackbar;
    FirebaseFirestore db;
    FirebaseUser user;
    private TabLayout mTabLayout;

    double INVITATION_POINTS = 10.0, rewardpoints, frndRewardpoints;
    private static final String USER_ID_KEY = "UserID", REF_USER_ID_KEY = "ReferredBy";
    private static final int CODE_WRITE_SETTINGS_PERMISSION = 111;

    public WalletFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_wallet, container, false);

        viewPager = view.findViewById(R.id.viewpager);
        viewPagerAdapter = new ViewPagerAdapter(getChildFragmentManager());
        viewPager.setAdapter(viewPagerAdapter);
        // link the tabLayout and the viewpager together
        mTabLayout =  view.findViewById(R.id.tab_layout);
        mTabLayout.setupWithViewPager(viewPager);
        return view;

        //   return inflater.inflate(R.layout.fragment_wallet, null);
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // do your variables initialisations here except Views!!!
        db = FirebaseFirestore.getInstance();

        //get current user
        user = FirebaseAuth.getInstance().getCurrentUser();

//
    }

    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rewardPoints = view.findViewById(R.id.rewardPoints);

        // write_settings_text = view.findViewById(R.id.write_settings_text);

        final SharedPreferences prefs = getActivity().getSharedPreferences("AdzApp_INVITATION_REF", MODE_PRIVATE);
        final String restoredText = prefs.getString("REF_ID", null);
        //checking if user already referred or not
        final boolean[] alreadyReferred = new boolean[1];
        final DocumentReference userReferred = db.collection("invitations").document(user.getUid());
        userReferred.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot doc = task.getResult();
                    if (doc.exists()) {
                        Log.d("docc1", "line 109 inivation doc exists");

                    } else {
                        Log.d("docc1", "line 112 inivation doc not exists");
                        final String refUserId = prefs.getString("REF_ID", "No name defined");//"No name defined" is the default value.
                        final String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                        if (restoredText != null && !refUserId.equals(userId)) {

                            //TODO:check user already used this reference
                            Log.d("docc1", "ref id line 122 " + refUserId);
                            final DocumentReference refFrndReawrdPoints = db.collection("userRewards").document(refUserId);
                            refFrndReawrdPoints.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful()) {
                                        DocumentSnapshot doc = task.getResult();
                                        frndRewardpoints = Double.valueOf(doc.get("Rewards").toString());
                                        updateRefFrndRewards(frndRewardpoints, INVITATION_POINTS, refUserId);
                                        updateRefInvitation(refUserId, userId);
                                    }
                                }
                            });

                            DocumentReference userReawrdPoints = db.collection("userRewards").document(user.getUid());
                            userReawrdPoints.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful()) {
                                        DocumentSnapshot doc = task.getResult();
                                        rewardpoints = Double.valueOf(doc.get("Rewards").toString());
                                        updateUserRewards(rewardpoints, INVITATION_POINTS);

                                    }
                                }
                            });


                        }
                    }
                }
            }
        });


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            boolean settingsCanWrite = Settings.System.canWrite(getContext());
            if (!settingsCanWrite) {
                if (DetectDevice.isMiUi()) {

                    showSnack(true);

                } else {
                    showSnack(false);
                }


            }
        }
        db.collection("userRewards").document(user.getUid()).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@javax.annotation.Nullable DocumentSnapshot documentSnapshot, @javax.annotation.Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.d("ERROR", e.getMessage());
                    return;
                }
                if (documentSnapshot != null && documentSnapshot.exists()) {
                    try {
                        rewardPoints.setText(documentSnapshot.get("Rewards").toString());
                    } catch (Exception error) {
                        Log.d("rewards", "null-error" + error.getMessage());
                    }
                }
            }
        });

    }

    private void showSnack(final boolean isMIUI) {
        snackbar = Snackbar.make(getActivity().findViewById(R.id.container), "Permission not granted", Snackbar.LENGTH_INDEFINITE)
                .setAction("Show me how", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (isMIUI) {
                            showWriteSettingsWay();
                        } else {
                            try {
                                Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
                                intent.setData(Uri.parse("package:" + getContext().getPackageName()));
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivityForResult(intent, CODE_WRITE_SETTINGS_PERMISSION);
                            } catch (Exception errrr) {

                            }

                        }

                    }
                });
        int height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 56, getResources().getDisplayMetrics());
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) snackbar.getView().getLayoutParams();
        params.setMargins(0, 0, 0, height);
        snackbar.getView().setLayoutParams(params);
        snackbar.show();

    }

    private void updateRefInvitation(String refUserId, String userId) {
        Map<String, Object> newInvitationRef = new HashMap<>();
        newInvitationRef.put(USER_ID_KEY, userId);
        newInvitationRef.put(REF_USER_ID_KEY, refUserId);
        db.collection("invitations").document(userId).set(newInvitationRef)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("docc1", " line 203 added into invitations");
                    }
                });

    }

    private void updateRefFrndRewards(double frndRewardpoints, double invitation_points, String refUserId) {
        double TOTAL_POINTS = frndRewardpoints + invitation_points;
        DocumentReference updatingRefFrndRewards = db.collection("userRewards").document(refUserId);
        updatingRefFrndRewards.update("Rewards", TOTAL_POINTS).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Log.d("docc1", "line 215 updated frnds points");
            }
        });
    }

    private void updateUserRewards(double rewardpoints, double invitation_points) {
        double TOTAL_POINTS = rewardpoints + invitation_points;
        DocumentReference updatingRewards = db.collection("userRewards").document(user.getUid());
        updatingRewards.update("Rewards", TOTAL_POINTS).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
//                    Toast.makeText(getApplicationContext(),  getString(R.string.rewards_updated)+ update_points + ".", Toast.LENGTH_SHORT).show();
                Log.d("docc1", " line 227 came in before dialog");
                final Dialog giftDialog = new Dialog(getContext());
                giftDialog.setContentView(R.layout.referred_gift_dailog);
                giftDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                gift_dismiss = giftDialog.findViewById(R.id.gift_dismiss);
                gift_dismiss.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        giftDialog.dismiss();
                    }
                });
                giftDialog.show();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
//                    Toast.makeText(getApplicationContext(), getString(R.string.processing_failure), Toast.LENGTH_SHORT).show();
            }
        });

    }

    protected void showWriteSettingsWay() {
        final BottomSheetDialog xiaomiDialog = new BottomSheetDialog(getContext());
        xiaomiDialog.setContentView(R.layout.xiaomi_permission_dialog);

        TextView tv = xiaomiDialog.findViewById(R.id.tv);
        tv.setText("Grant Modify system settings permission by opening SETTINGS > INSTALLED APPS > ADZAPP > OTHER PERMISSION");
        Button xiaomi_permission_button = xiaomiDialog.findViewById(R.id.xiaomi_permissions_button);
        Button xiaomi_permissions_granted_button = xiaomiDialog.findViewById(R.id.xiaomi_permissions_granted_button);
        xiaomi_permissions_granted_button.setVisibility(View.GONE);
        xiaomi_permission_button.setText("dismiss");
        xiaomi_permission_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                xiaomiDialog.dismiss();

            }
        });

        xiaomiDialog.show();
    }

    @Override
    public String getFragmentStackName() {
        return "WalletFragment";
    }

    @Override
    public void onFragmentScroll() {

    }

    public static class ViewPagerAdapter extends FragmentStatePagerAdapter {
        private static final int NUM_ITEMS = 2;

        public ViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            if (position == 0) {
                return CouponsFragment.newInstance();
            } else {
                return TransactionFragment.newInstance();
            }
        }

        @Override
        public int getCount() {
            return NUM_ITEMS;
        }

        @Override
        public CharSequence getPageTitle(int position) {

            if (position == 0) {
                return CouponsFragment.PAGE_TITLE;
            } else {
                return TransactionFragment.PAGE_TITLE;
            }
        }
    }

    @Override
    public void onDestroy() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            boolean settingsCanWrite = Settings.System.canWrite(getContext());
            if (!settingsCanWrite) {
                if (DetectDevice.isMiUi()) {
                    snackbar.dismiss();
                } else {
                    snackbar.dismiss();
                }
            }

        }
        super.onDestroy();
    }
}
