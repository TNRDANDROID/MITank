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
import android.media.ExifInterface;
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
import java.io.IOException;
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
    private List<MITank> InletTypeList = new ArrayList<>();
    private List<MITank> SluiceTypeList = new ArrayList<>();

    public static DBHelper dbHelper;
    public static SQLiteDatabase db;
    private com.nic.MITank.dataBase.dbData dbData = new dbData(this);
    private List<MITank> ConditionList = new ArrayList<>();
    String Title="";



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
        if(getIntent().getStringExtra("KEY").equals("TanksPondsListAdapter")){
            cameraScreenBinding.selectionLayout.setVisibility(View.GONE);
        }
        else {
            Title=getIntent().getStringExtra("Title");
        }

        if(!Title.equals("")){
            if(Title.equals("Inlet channels")){
                loadInletTypeSpinnerValue();
                cameraScreenBinding.type.setAdapter(new CommonAdapter(this, InletTypeList, "TypeList"));
                cameraScreenBinding.sillLevelLayout.setVisibility(View.GONE);
                cameraScreenBinding.typeSpinnerLayout.setVisibility(View.VISIBLE);
                cameraScreenBinding.conditionLayout.setVisibility(View.VISIBLE);
            }
            else if(Title.equals("Surplus Weirs / Outlet structure")){
                cameraScreenBinding.sillLevelLayout.setVisibility(View.GONE);
                cameraScreenBinding.typeSpinnerLayout.setVisibility(View.GONE);
                cameraScreenBinding.conditionLayout.setVisibility(View.VISIBLE);
            }
            else if(Title.equals("Bathing Ghat")){
                cameraScreenBinding.sillLevelLayout.setVisibility(View.GONE);
                cameraScreenBinding.typeSpinnerLayout.setVisibility(View.GONE);
                cameraScreenBinding.conditionLayout.setVisibility(View.VISIBLE);
            }
            else if(Title.equals("Ramp structures")){
                cameraScreenBinding.sillLevelLayout.setVisibility(View.GONE);
                cameraScreenBinding.typeSpinnerLayout.setVisibility(View.GONE);
                cameraScreenBinding.conditionLayout.setVisibility(View.VISIBLE);
            }
            else if(Title.equals("Sluices")){
                loadSluiceTypeSpinnerValue();
                cameraScreenBinding.type.setAdapter(new CommonAdapter(this, SluiceTypeList, "TypeList"));
                cameraScreenBinding.sillLevelLayout.setVisibility(View.VISIBLE);
                cameraScreenBinding.typeSpinnerLayout.setVisibility(View.VISIBLE);
                cameraScreenBinding.conditionLayout.setVisibility(View.VISIBLE);
            }

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

    public void loadInletTypeSpinnerValue() {
        Cursor conditionCursor = null;
        conditionCursor = db.rawQuery("SELECT * FROM " + DBHelper.MI_TANK_TYPE_INLET_STRUCTURE, null);

        InletTypeList.clear();
        MITank conditionListValue = new MITank();
        conditionListValue.setMiTankTypeName("Select Type");
        InletTypeList.add(conditionListValue);
        if (conditionCursor.getCount() > 0) {
            if (conditionCursor.moveToFirst()) {
                do {
                    MITank conditionList = new MITank();
                    String miTankTypeId = conditionCursor.getString(conditionCursor.getColumnIndexOrThrow(AppConstant.MI_TANK_type_ID));
                    String miTankTypeName = conditionCursor.getString(conditionCursor.getColumnIndexOrThrow(AppConstant.MI_TANK_TYPE_NAME));

                    conditionList.setMiTankTypeId(miTankTypeId);
                    conditionList.setMiTankTypeName(miTankTypeName);

                    InletTypeList.add(conditionList);
                    Log.d("conditonsize", "" + InletTypeList.size());
                } while (conditionCursor.moveToNext());
            }
        }
        cameraScreenBinding.type.setAdapter(new CommonAdapter(this, InletTypeList, "TypeList"));
    }
    public void loadSluiceTypeSpinnerValue() {
        Cursor conditionCursor = null;
        conditionCursor = db.rawQuery("SELECT * FROM " + DBHelper.MI_TANK_TYPE_SLUICE_STRUCTURE, null);

        SluiceTypeList.clear();
        MITank conditionListValue = new MITank();
        conditionListValue.setMiTankTypeName("Select Type");
        SluiceTypeList.add(conditionListValue);
        if (conditionCursor.getCount() > 0) {
            if (conditionCursor.moveToFirst()) {
                do {
                    MITank conditionList = new MITank();
                    String miTankTypeId = conditionCursor.getString(conditionCursor.getColumnIndexOrThrow(AppConstant.MI_TANK_type_ID));
                    String miTankTypeName = conditionCursor.getString(conditionCursor.getColumnIndexOrThrow(AppConstant.MI_TANK_TYPE_NAME));

                    conditionList.setMiTankTypeId(miTankTypeId);
                    conditionList.setMiTankTypeName(miTankTypeName);

                    SluiceTypeList.add(conditionList);
                    Log.d("conditonsize", "" + SluiceTypeList.size());
                } while (conditionCursor.moveToNext());
            }
        }
        cameraScreenBinding.type.setAdapter(new CommonAdapter(this, SluiceTypeList, "TypeList"));
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
            ExifInterface ei = null;
            try {
                ei = new ExifInterface(imageStoragePath);
            } catch (IOException e) {
                e.printStackTrace();
            }
            int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_UNDEFINED);

            Bitmap rotatedBitmap = null;
            switch (orientation) {

                case ExifInterface.ORIENTATION_ROTATE_90:
                    rotatedBitmap = rotateImage(bitmap, 90);
                    break;

                case ExifInterface.ORIENTATION_ROTATE_180:
                    rotatedBitmap = rotateImage(bitmap, 180);
                    break;

                case ExifInterface.ORIENTATION_ROTATE_270:
                    rotatedBitmap = rotateImage(bitmap, 270);
                    break;

                case ExifInterface.ORIENTATION_NORMAL:
                default:
                    rotatedBitmap = bitmap;
            }
            cameraScreenBinding.imageView.setImageBitmap(rotatedBitmap);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    public static Bitmap rotateImage(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(),
                matrix, true);
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
        if(getIntent().getStringExtra("KEY").equals("TanksPondsListAdapter")){
            setResult(RESULT_OK);
            super.onBackPressed();
            overridePendingTransition(R.anim.slide_enter, R.anim.slide_exit);
        }else {
        super.onBackPressed();
            setResult(Activity.RESULT_CANCELED);
        overridePendingTransition(R.anim.slide_enter, R.anim.slide_exit);
    }}

    public void onBackPress() {
        if(getIntent().getStringExtra("KEY").equals("TanksPondsListAdapter")){
            setResult(RESULT_OK);
            super.onBackPressed();
            overridePendingTransition(R.anim.slide_enter, R.anim.slide_exit);
        }else {
        super.onBackPressed();
        setResult(Activity.RESULT_CANCELED);
        overridePendingTransition(R.anim.slide_enter, R.anim.slide_exit);
    }}

    public void checkSave() {
        if(getIntent().getStringExtra("KEY").equals("TanksPondsListAdapter")){
            //saveImage();
            saveCentrePointImage();
        }
        else {
            /*if (!"Select Condition".equalsIgnoreCase(ConditionList.get(cameraScreenBinding.condition.getSelectedItemPosition()).getMiTankConditionName())) {
                saveImage();
            } else {
                Utils.showAlert(this, "Select Condition!");
            }*/
            if (!"Select Condition".equalsIgnoreCase(ConditionList.get(cameraScreenBinding.condition.getSelectedItemPosition()).getMiTankConditionName())) {
                if(typeVisibility()){
                    saveImage();
                }
                else {
                    saveImage();
                }
            } else {
                Utils.showAlert(this, "Select Condition!");
            }
        }

    }
    public boolean typeVisibility(){

            if (cameraScreenBinding.typeSpinnerLayout.getVisibility() == View.VISIBLE) {
                if(Title.equals("Sluices")) {
                    if (!"Select Type".equalsIgnoreCase(SluiceTypeList.get(cameraScreenBinding.type.getSelectedItemPosition()).getMiTankTypeName())) {
                        return true;
                    }
                    else {
                        Utils.showAlert(this, "Select Type!");
                        return false;
                    }
                }
                else {
                    if (!"Select Type".equalsIgnoreCase(InletTypeList.get(cameraScreenBinding.type.getSelectedItemPosition()).getMiTankTypeName())) {
                        return true;
                    }
                    else {
                        Utils.showAlert(this, "Select Type!");
                        return false;

                    }

                }
            } else {
                return false;
            }

    }
    public void saveImage() {
        dbData.open();
        long id = 0; String whereClause = "";String[] whereArgs = null;
        String mi_tank_structure_detail_id = getIntent().getStringExtra(AppConstant.MI_TANK_STRUCTURE_DETAIL_ID);
        String mi_tank_survey_id = getIntent().getStringExtra(AppConstant.MI_TANK_SURVEY_ID);
        String mi_tank_structure_id = getIntent().getStringExtra(AppConstant.MI_TANK_STRUCTURE_ID);
        String mi_tank_structure_serial_id = getIntent().getStringExtra(AppConstant.MI_TANK_STRUCTURE_SERIAL_ID);
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
            values.put(AppConstant.MI_TANK_STRUCTURE_SERIAL_ID, mi_tank_structure_serial_id);
            values.put(AppConstant.MI_TANK_SURVEY_ID, mi_tank_survey_id);
            values.put(AppConstant.MI_TANK_CONDITION_ID,ConditionList.get(cameraScreenBinding.condition.getSelectedItemPosition()).getMiTankConditionId() );
            values.put(AppConstant.MI_TANK_CONDITION_NAME,ConditionList.get(cameraScreenBinding.condition.getSelectedItemPosition()).getMiTankConditionName() );
            values.put(AppConstant.KEY_LATITUDE, offlatTextValue.toString());
            values.put(AppConstant.KEY_LONGITUDE, offlongTextValue.toString());
            values.put(AppConstant.KEY_IMAGE,image_str.trim());
           // values.put(AppConstant.KEY_CREATED_DATE,sdf.format(new Date()));


                whereClause = "dcode = ? and bcode = ? and pvcode = ? and habcode = ? and mi_tank_structure_detail_id = ? and mi_tank_structure_serial_id = ?";
                whereArgs = new String[]{dcode,bcode,pvcode,habcode,mi_tank_structure_detail_id,mi_tank_structure_serial_id};
                dbData.open();
                ArrayList<MITank> imageOffline = dbData.selectImage(dcode,bcode,pvcode,habcode,mi_tank_structure_detail_id,mi_tank_structure_serial_id);

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

    public void saveCentrePointImage() {
        dbData.open();
        long id = 0; String whereClause = "";String[] whereArgs = null;
        String mi_tank_survey_id = getIntent().getStringExtra(AppConstant.MI_TANK_SURVEY_ID);
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
            values.put(AppConstant.MI_TANK_SURVEY_ID, mi_tank_survey_id);
            values.put(AppConstant.KEY_LATITUDE, offlatTextValue.toString());
            values.put(AppConstant.KEY_LONGITUDE, offlongTextValue.toString());
            values.put(AppConstant.KEY_IMAGE,image_str.trim());
            // values.put(AppConstant.KEY_CREATED_DATE,sdf.format(new Date()));
            dbData.open();
            ArrayList<MITank> imageOffline = dbData.getCenterImageData(mi_tank_survey_id);
            if(imageOffline.size() < 1) {
                id = db.insert(DBHelper.SAVE_MI_TANK_CENTER_IMAGES, null, values);
                if(id > 0){
                    Toasty.success(this, "Inserted Success!", Toast.LENGTH_LONG, true).show();
                    onBackPressed();
                }
            }
            else {
                id= db.update(DBHelper.SAVE_MI_TANK_CENTER_IMAGES, values, "mi_tank_survey_id  = ? ", new String[]{mi_tank_survey_id});
                if(id > 0){
                    Toasty.success(this, "Updated Success!", Toast.LENGTH_LONG, true).show();
                    onBackPressed();
                }
            }




        } catch (Exception e) {
            Utils.showAlert(CameraScreen.this, "Please Capture Photo");
            //e.printStackTrace();
        }
    }



}
