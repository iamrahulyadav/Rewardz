package in.dthoughts.innolabs.adzapp.ui;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.PointF;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.dlazaro66.qrcodereaderview.QRCodeReaderView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import in.dthoughts.innolabs.adzapp.R;



public class ScanQrActivity extends AppCompatActivity implements QRCodeReaderView.OnQRCodeReadListener {

    private QRCodeReaderView qrCodeReaderView;
    DocumentSnapshot doc;
    FirebaseFirestore db;
    private static final int MY_PERMISSION_REQUEST_CAMERA = 0;

    private ViewGroup mainLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_rq_code);
        Toolbar mToolbar = findViewById(R.id.toolbar);
        mToolbar.setTitle(getString(R.string.title_ScanQR));
        mToolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);

        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();
            }
        });
        mainLayout =  findViewById(R.id.mainLayout);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {
            Log.d("docc2","came into if");
            initQRCodeReaderView();



        } else {
            Log.d("docc2","came into else");
            requestCameraPermission();
        }



    }

    private void initQRCodeReaderView() {
        qrCodeReaderView = findViewById(R.id.qr_decoder_view);
        qrCodeReaderView.setOnQRCodeReadListener(this);
        qrCodeReaderView.setAutofocusInterval(2000L);
        qrCodeReaderView.setQRDecodingEnabled(true);
        qrCodeReaderView.setBackCamera();
        qrCodeReaderView.startCamera();

    }

    private void requestCameraPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
            Snackbar.make(mainLayout, "Camera access is required to display the camera preview.",
                    Snackbar.LENGTH_INDEFINITE).setAction("OK", new View.OnClickListener() {
                @Override public void onClick(View view) {
                    ActivityCompat.requestPermissions(ScanQrActivity.this, new String[] {
                            Manifest.permission.CAMERA
                    }, MY_PERMISSION_REQUEST_CAMERA);
                }
            }).show();
        } else {
            Snackbar.make(mainLayout, "Permission is not available. Requesting camera permission.",
                    Snackbar.LENGTH_INDEFINITE).show();
            ActivityCompat.requestPermissions(this, new String[] {
                    Manifest.permission.CAMERA
            }, MY_PERMISSION_REQUEST_CAMERA);
        }
    }

    @Override
    public void onQRCodeRead(String text, PointF[] points) {

        Log.d("docc2",text);
        getAdDetails(text);
        //pointsOverlayView.setPoints(points);
        if (qrCodeReaderView != null) {
            Log.d("docc2","scanner stopped");
            qrCodeReaderView.stopCamera();
        }
    }

    private void getAdDetails(String AdId) {
        //TODO:SHOWING LODAING STUFF
        db = FirebaseFirestore.getInstance();
        DocumentReference scannedAd = db.collection("Published Ads").document(AdId);
        scannedAd.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    doc = task.getResult();
                    if(!doc.exists()){
                        Log.d("docc2","no ad found");
                        callErrorDialog();
                    }else{
                        Intent intent = new Intent(ScanQrActivity.this, DetailAdActivity.class);
                        try{
                            intent.putExtra("adPublisherPic",doc.get("publisher_image").toString() );
                            intent.putExtra("adPublisherName", doc.get("publisher_name").toString());
                            intent.putExtra("adExpiresOn", doc.get("expires_on").toString());
                            intent.putExtra("adBanner", doc.get("ad_banner").toString());
                            intent.putExtra("adDescription", doc.get("ad_description").toString());
                            intent.putExtra("adUrl", doc.get("ad_url").toString());
                            intent.putExtra("adType", doc.get("ad_type").toString());
                            intent.putExtra("adVideoUrl", doc.get("video_url").toString());
                            intent.putExtra("adPoints",doc.get("points").toString());
                            intent.putExtra("adCouponCode", doc.get("coupon_code").toString());
                            intent.putExtra("adID", doc.getId());
                        }catch (Exception err){
                            Log.d("errr", err.getMessage());
                        }
                        startActivity(intent);
                        finish();
                    }

                }
                else {
                    Toast.makeText(ScanQrActivity.this, "Invalid QR Code or please try again", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void callErrorDialog() {
        Log.d("docc2","cam in dailog");
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("Oops!");
        alertDialogBuilder.setMessage("Invalid QR Code or please try again");
                alertDialogBuilder.setPositiveButton("Okay",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {
                                qrCodeReaderView.startCamera();
                               finish();
                            }
                        });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    @Override protected void onResume() {
        super.onResume();

        if (qrCodeReaderView != null) {
            qrCodeReaderView.startCamera();
        }
    }

    @Override protected void onPause() {
        super.onPause();

        if (qrCodeReaderView != null) {
            qrCodeReaderView.stopCamera();
        }
    }
    @Override public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                                     @NonNull int[] grantResults) {
        if (requestCode != MY_PERMISSION_REQUEST_CAMERA) {
            return;
        }

        if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Snackbar.make(mainLayout, "Camera permission was granted.", Snackbar.LENGTH_SHORT).show();
            //initQRCodeReaderView();
            startActivity(new Intent(ScanQrActivity.this, ScanQrActivity.class));
            finish();
        } else {
            Snackbar.make(mainLayout, "Camera permission request was denied.", Snackbar.LENGTH_SHORT)
                    .show();
        }
    }
}
