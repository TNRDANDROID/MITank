package com.nic.MITank.activity;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import com.android.volley.VolleyError;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.nic.MITank.R;
import com.nic.MITank.adapter.CommonAdapter;
import com.nic.MITank.api.Api;
import com.nic.MITank.api.ServerResponse;
import com.nic.MITank.constant.AppConstant;
import com.nic.MITank.dataBase.DBHelper;
import com.nic.MITank.dataBase.dbData;
import com.nic.MITank.databinding.CameraScreenBinding;
import com.nic.MITank.model.MITank;
import com.nic.MITank.session.PrefManager;
import com.nic.MITank.support.MyLocationListener;
import com.nic.MITank.utils.CameraUtils;
import com.nic.MITank.utils.Utils;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import es.dmoral.toasty.Toasty;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.CAMERA;

public class CameraScreen extends AppCompatActivity implements View.OnClickListener, Api.ServerResponseListener {

    public static final int MEDIA_TYPE_IMAGE = 1;

    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 2500;
    private static final int CAMERA_CAPTURE_VIDEO_REQUEST_CODE = 200;
    private static final int PERMISSION_REQUEST_CODE = 200;
    private static String imageStoragePath;
    public static final int BITMAP_SAMPLE_SIZE = 8;
    LocationManager mlocManager = null;
    LocationListener mlocListener;
    Double offlatTextValue, offlongTextValue;
    private PrefManager prefManager;
    private CameraScreenBinding cameraScreenBinding;


    private List<View> viewArrayList = new ArrayList<>();


    public static DBHelper dbHelper;
    public static SQLiteDatabase db;
    private com.nic.MITank.dataBase.dbData dbData = new dbData(this);
    private List<MITank> ConditionList = new ArrayList<>();



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        cameraScreenBinding = DataBindingUtil.setContentView(this, R.layout.camera_screen);
        cameraScreenBinding.setActivity(this);
        try {
            dbHelper = new DBHelper(this);
            db = dbHelper.getWritableDatabase();
        } catch (Exception e) {
            e.printStackTrace();
        }


        intializeUI();
        loadConditionSpinnervalue();
    }

    public void intializeUI() {
        prefManager = new PrefManager(this);

        mlocManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        mlocListener = new MyLocationListener();
    }

    public void loadConditionSpinnervalue() {
        Cursor conditionCursor = null;
        conditionCursor = db.rawQuery("SELECT * FROM " + DBHelper.MI_TANK_CONDITION, null);

        ConditionList.clear();
        MITank conditionListValue = new MITank();
        conditionListValue.setMiTankConditionName("Select Condition");
        ConditionList.add(conditionListValue);
        if (conditionCursor.getCount() > 0) {
            if (conditionCursor.moveToFirst()) {
                do {
                    MITank conditionList = new MITank();
                    String miTankConditionId = conditionCursor.getString(conditionCursor.getColumnIndexOrThrow(AppConstant.MI_TANK_CONDITION_ID));
                    String miTankConditionName = conditionCursor.getString(conditionCursor.getColumnIndexOrThrow(AppConstant.MI_TANK_CONDITION_NAME));

                    conditionList.setMiTankConditionId(miTankConditionId);
                    conditionList.setMiTankConditionName(miTankConditionName);

                    ConditionList.add(conditionList);
                    Log.d("conditonsize", "" + ConditionList.size());
                } while (conditionCursor.moveToNext());
            }
        }
        cameraScreenBinding.condition.setAdapter(new CommonAdapter(this, ConditionList, "ConditionList"));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

        }
    }



    private void captureImage() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        File file = CameraUtils.getOutputMediaFile(MEDIA_TYPE_IMAGE);
        if (file != null) {
            imageStoragePath = file.getAbsolutePath();
        }

        Uri fileUri = CameraUtils.getOutputMediaFileUri(this, file);

        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);

        // start the image capture Intent
        startActivityForResult(intent, CAMERA_CAPTURE_IMAGE_REQUEST_CODE);
        if (MyLocationListener.latitude > 0) {
            offlatTextValue = MyLocationListener.latitude;
            offlongTextValue = MyLocationListener.longitude;
        }
    }

    public void getLatLong() {
        mlocManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        mlocListener = new MyLocationListener();


        // permission was granted, yay! Do the
        // location-related task you need to do.
        if (ContextCompat.checkSelfPermission(CameraScreen.this,
                ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {

            //Request location updates:
            mlocManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, mlocListener);

        }

        if (mlocManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (ActivityCompat.checkSelfPermission(CameraScreen.this, ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(CameraScreen.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
                    requestPermissions(new String[]{CAMERA, ACCESS_FINE_LOCATION}, PERMISSION_REQUEST_CODE);
            } else {
                if (ActivityCompat.checkSelfPermission(CameraScreen.this, ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(CameraScreen.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(CameraScreen.this, new String[]{ACCESS_FINE_LOCATION}, 1);

                }
            }
            if (MyLocationListener.latitude > 0) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (CameraUtils.checkPermissions(CameraScreen.this)) {
                        captureImage();
                    } else {
                        requestCameraPermission(MEDIA_TYPE_IMAGE);
                    }
//                            checkPermissionForCamera();
                } else {
                    captureImage();
                }
            } else {
                Utils.showAlert(CameraScreen.this, "Satellite communication not available to get GPS Co-ordination Please Capture Photo in Open Area..");
            }
        } else {
            Utils.showAlert(CameraScreen.this, "GPS is not turned on...");
        }
    }

    private void requestCameraPermission(final int type) {
        Dexter.withActivity(this)
                .withPermissions(Manifest.permission.CAMERA,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if (report.areAllPermissionsGranted()) {

                            if (type == MEDIA_TYPE_IMAGE) {
                                // capture picture
                                captureImage();
                            } else {
//                                captureVideo();
                            }

                        } else if (report.isAnyPermissionPermanentlyDenied()) {
                            showPermissionsAlert();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();
    }


    private void showPermissionsAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Permissions required!")
                .setMessage("Camera needs few permissions to work properly. Grant them in settings.")
                .setPositiveButton("GOTO SETTINGS", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        CameraUtils.openSettings(CameraScreen.this);
                    }
                })
                .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).show();
    }

    public void previewCapturedImage() {
        try {
            // hide video preview
            Bitmap bitmap = CameraUtils.optimizeBitmap(BITMAP_SAMPLE_SIZE, imageStoragePath);
            cameraScreenBinding.imageViewPreview.setVisibility(View.GONE);
            cameraScreenBinding.imageView.setVisibility(View.VISIBLE);
            Matrix mtx = new Matrix();
            // As Front camera is Mirrored so Fliping the Orientation

            if (Build.VERSION.SDK_INT == Build.VERSION_CODES.N_MR1 || Build.VERSION.SDK_INT == Build.VERSION_CODES.N ) {
                mtx.postRotate(90);
            } else {
                mtx.postRotate(0);
            }
            Log.d("buildversion",""+ Build.VERSION.SDK_INT);
            Log.d("buildversion",""+ Build.VERSION_CODES.N);
            bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), mtx, true);
            cameraScreenBinding.imageView.setImageBitmap(bitmap);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // if the result is capturing Image
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_CAPTURE_IMAGE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // Refreshing the gallery
                CameraUtils.refreshGallery(getApplicationContext(), imageStoragePath);

                // successfully captured the image
                // display it in image view
                previewCapturedImage();
            } else if (resultCode == RESULT_CANCELED) {
                // user cancelled Image capture
                Toast.makeText(getApplicationContext(),
                        "User cancelled image capture", Toast.LENGTH_SHORT)
                        .show();
            } else {
                // failed to capture image
                Toast.makeText(getApplicationContext(),
                        "Sorry! Failed to capture image", Toast.LENGTH_SHORT)
                        .show();
            }
        } else if (requestCode == CAMERA_CAPTURE_VIDEO_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // Refreshing the gallery
                CameraUtils.refreshGallery(getApplicationContext(), imageStoragePath);

                // video successfully recorded
                // preview the recorded video
//                previewVideo();
            } else if (resultCode == RESULT_CANCELED) {
                // user cancelled recording
                Toast.makeText(getApplicationContext(),
                        "User cancelled video recording", Toast.LENGTH_SHORT)
                        .show();
            } else {
                // failed to record video
                Toast.makeText(getApplicationContext(),
                        "Sorry! Failed to record video", Toast.LENGTH_SHORT)
                        .show();
            }
        }
    }



    @Override
    public void OnMyResponse(ServerResponse serverResponse) {

    }

    @Override
    public void OnError(VolleyError volleyError) {

    }

    public void homePage() {
        Intent intent = new Intent(this, HomePage.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("Home", "Home");
        startActivity(intent);
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_enter, R.anim.slide_exit);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_enter, R.anim.slide_exit);
    }

    public void onBackPress() {
        super.onBackPressed();
        setResult(Activity.RESULT_CANCELED);
        overridePendingTransition(R.anim.slide_enter, R.anim.slide_exit);
    }

    public void checkSave() {
        if (!"Select Condition".equalsIgnoreCase(ConditionList.get(cameraScreenBinding.condition.getSelectedItemPosition()).getMiTankConditionName())) {
            saveImage();
        } else {
            Utils.showAlert(this, "Select Condition!");
        }
    }
    public void saveImage() {
        dbData.open();
        long id = 0; String whereClause = "";String[] whereArgs = null;
        String mi_tank_structure_detail_id = getIntent().getStringExtra(AppConstant.MI_TANK_STRUCTURE_DETAIL_ID);
        String mi_tank_survey_id = getIntent().getStringExtra(AppConstant.MI_TANK_SURVEY_ID);
        String mi_tank_structure_id = getIntent().getStringExtra(AppConstant.MI_TANK_STRUCTURE_ID);
        String dcode = prefManager.getDistrictCode();
        String bcode = prefManager.getBlockCode();
        String pvcode = prefManager.getPvCode();
        String habcode = prefManager.getHabCode();

        ImageView imageView = (ImageView) findViewById(R.id.image_view);
        byte[] imageInByte = new byte[0];
        String image_str = "";
        try {
            Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, baos);
            imageInByte = baos.toByteArray();
            image_str = Base64.encodeToString(imageInByte, Base64.DEFAULT);

            ContentValues values = new ContentValues();
            values.put(AppConstant.DISTRICT_CODE, dcode);
            values.put(AppConstant.BLOCK_CODE, bcode);
            values.put(AppConstant.PV_CODE, pvcode);
            values.put(AppConstant.HAB_CODE, habcode);
            values.put(AppConstant.MI_TANK_STRUCTURE_DETAIL_ID, mi_tank_structure_detail_id);
            values.put(AppConstant.MI_TANK_STRUCTURE_ID, mi_tank_structure_id);
            values.put(AppConstant.MI_TANK_SURVEY_ID, mi_tank_survey_id);
            values.put(AppConstant.MI_TANK_CONDITION_ID,ConditionList.get(cameraScreenBinding.condition.getSelectedItemPosition()).getMiTankConditionId() );
            values.put(AppConstant.KEY_LATITUDE, offlatTextValue.toString());
            values.put(AppConstant.KEY_LONGITUDE, offlongTextValue.toString());
            values.put(AppConstant.KEY_IMAGE,image_str.trim());
           // values.put(AppConstant.KEY_CREATED_DATE,sdf.format(new Date()));


                whereClause = "dcode = ? and bcode = ? and pvcode = ? and habcode = ? and mi_tank_structure_detail_id = ?";
                whereArgs = new String[]{dcode,bcode,pvcode,habcode,mi_tank_structure_detail_id};
                dbData.open();
                ArrayList<MITank> imageOffline = dbData.selectImage(dcode,bcode,pvcode,habcode,mi_tank_structure_detail_id);

                if(imageOffline.size() < 1) {
                    id = db.insert(DBHelper.SAVE_MI_TANK_IMAGES, null, values);
                }
                else {
                    id = db.update(DBHelper.SAVE_MI_TANK_IMAGES, values, whereClause, whereArgs);
                }

            if(id > 0){
                Toasty.success(this, "Success!", Toast.LENGTH_LONG, true).show();
                onBackPressed();
            }
            Log.d("insIdsaveImageLatLong", String.valueOf(id));

        } catch (Exception e) {
            Utils.showAlert(CameraScreen.this, "Please Capture Photo");
            //e.printStackTrace();
        }
    }


}
