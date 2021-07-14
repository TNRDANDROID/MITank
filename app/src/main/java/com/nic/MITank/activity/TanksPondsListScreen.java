package com.nic.MITank.activity;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.VolleyError;
import com.google.android.material.appbar.AppBarLayout;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.nic.MITank.R;
import com.nic.MITank.adapter.TanksPondsListAdapter;
import com.nic.MITank.adapter.TanksPondsTitleAdapter;
import com.nic.MITank.api.Api;
import com.nic.MITank.api.ApiService;
import com.nic.MITank.api.ServerResponse;
import com.nic.MITank.constant.AppConstant;
import com.nic.MITank.dataBase.DBHelper;
import com.nic.MITank.databinding.TanksPondsListScreenBinding;
import com.nic.MITank.databinding.TanksPondsTitleScreenBinding;
import com.nic.MITank.model.MITank;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import com.nic.MITank.dataBase.dbData;
import com.nic.MITank.session.PrefManager;
import com.nic.MITank.support.MyLocationListener;
import com.nic.MITank.utils.CameraUtils;
import com.nic.MITank.utils.UrlGenerator;
import com.nic.MITank.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import es.dmoral.toasty.Toasty;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.CAMERA;
import static com.nic.MITank.activity.CameraScreen.BITMAP_SAMPLE_SIZE;
import static com.nic.MITank.activity.CameraScreen.MEDIA_TYPE_IMAGE;
import static com.nic.MITank.activity.CameraScreen.dbHelper;

public class TanksPondsListScreen extends AppCompatActivity implements Api.ServerResponseListener {

    public TanksPondsListScreenBinding tanksPondsListScreenBinding;
    boolean ExpandedActionBar = true;
    private TanksPondsListAdapter tanksPondsListAdapter;
    private ArrayList<MITank> tankDataList;
    public dbData dbData = new dbData(this);
    private PrefManager prefManager;
    String checkboxvalue;
    int centerImagePosition;
    Dialog dialog;
    private List<View> viewArrayList = new ArrayList<>();
    public static SQLiteDatabase db;
    LocationManager mlocManager = null;
    LocationListener mlocListener;
    Double offlatTextValue, offlongTextValue;
    private static final int PERMISSION_REQUEST_CODE = 200;
    private static String imageStoragePath;
    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 2500;
    boolean back_press_flag=true;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tanksPondsListScreenBinding = DataBindingUtil.setContentView(this, R.layout.tanks_ponds_list_screen);
        tanksPondsListScreenBinding.setActivity(this);
        tanksPondsListScreenBinding.toolbarTitle.setText(getIntent().getStringExtra("Title"));
        setSupportActionBar(tanksPondsListScreenBinding.toolbar);
        prefManager = new PrefManager(this);
        try {
            dbHelper = new DBHelper(this);
            db = dbHelper.getWritableDatabase();
        } catch (Exception e) {
            e.printStackTrace();
        }

        tankDataList = new ArrayList<>();
        tanksPondsListAdapter = new TanksPondsListAdapter(this,tankDataList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        tanksPondsListScreenBinding.recyclerView.setLayoutManager(mLayoutManager);
        tanksPondsListScreenBinding.recyclerView.setItemAnimator(new DefaultItemAnimator());
        tanksPondsListScreenBinding.recyclerView.setAdapter(tanksPondsListAdapter);
        checkboxvalue = getIntent().getStringExtra(AppConstant.CHECK_BOX_CLICKED);
        new fetchtankDatatask().execute();

        mlocManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        mlocListener = new MyLocationListener();
    }

    @Override
    public void OnMyResponse(ServerResponse serverResponse) {
        try {
            String urlType = serverResponse.getApi();
            JSONObject responseObj = serverResponse.getJsonResponse();

            if ("GetCenterImage".equals(urlType) && responseObj != null) {


            }

        }catch (JSONException e){

        }

            }

    @Override
    public void OnError(VolleyError volleyError) {

    }

    public class fetchtankDatatask extends AsyncTask<Void, Void,
                ArrayList<MITank>> {
        @Override
        protected ArrayList<MITank> doInBackground(Void... params) {
            dbData.open();
            tankDataList = new ArrayList<>();
            tankDataList = dbData.getAllMITankData("fetch",prefManager.getDistrictCode(),prefManager.getBlockCode(),prefManager.getPvCode(),prefManager.getHabCode(),checkboxvalue);
            Log.d("TANKS_DATA_COUNT", String.valueOf(tankDataList.size()));
            return tankDataList;
        }

        @Override
        protected void onPostExecute(ArrayList<MITank> tanksDataList) {
            super.onPostExecute(tanksDataList);
            if(tanksDataList.size() > 0){
                tanksPondsListAdapter = new TanksPondsListAdapter(TanksPondsListScreen.this,tankDataList);
                tanksPondsListScreenBinding.recyclerView.setAdapter(tanksPondsListAdapter);
            }
            else {
                 tanksPondsListScreenBinding.notFoundTv.setVisibility(View.VISIBLE);
            }

        }
    }

    public void onBackPress() {
        super.onBackPressed();
        setResult(Activity.RESULT_CANCELED);
        overridePendingTransition(R.anim.slide_enter, R.anim.slide_exit);
    }

    @Override
    public void onBackPressed() {
        if(!back_press_flag){
            dialog.dismiss();
            dialog.cancel();
        }
        else {
            super.onBackPressed();
            overridePendingTransition(R.anim.slide_enter, R.anim.slide_exit);
        }
    }

    public void homePage() {
        Intent intent = new Intent(this, HomePage.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("Home", "Home");
        startActivity(intent);
        overridePendingTransition(R.anim.slide_enter, R.anim.slide_exit);
    }
    public void openCamera(int position) {
        imageWithDescription(position);
       /* Intent intent = new Intent(this, CameraScreen.class);
        intent.putExtra("KEY","TanksPondsListAdapter");
        intent.putExtra(AppConstant.MI_TANK_STRUCTURE_SERIAL_ID,"");
        intent.putExtra(AppConstant.MI_TANK_STRUCTURE_DETAIL_ID,tankDataList.get(position).getMiTankStructureDetailId());
        intent.putExtra(AppConstant.MI_TANK_SURVEY_ID,tankDataList.get(position).getMiTankSurveyId());
        intent.putExtra(AppConstant.MI_TANK_STRUCTURE_ID,tankDataList.get(position).getMiTankStructureId());
        startActivityForResult(intent,1);
        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);*/
    }
    public void imageWithDescription(final int position) {
        back_press_flag=false;
        dialog = new Dialog(this,R.style.AppTheme);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.add_photo);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
        lp.dimAmount = 0.7f;
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.show();


        final LinearLayout mobileNumberLayout = (LinearLayout) dialog.findViewById(R.id.camera_layout);

        Button done = (Button) dialog.findViewById(R.id.btn_save_inspection);
        done.setGravity(Gravity.CENTER);
        done.setVisibility(View.VISIBLE);
        TextView close_id=dialog.findViewById(R.id.close_id);
        close_id.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                back_press_flag=true;
                dialog.dismiss();
            }
        });

        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        updateView(TanksPondsListScreen.this, mobileNumberLayout);

        done.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                int childCount = mobileNumberLayout.getChildCount();
                if (childCount > 0) {
                        View vv1 = mobileNumberLayout.getChildAt(childCount-1);
                        ImageView imageView1 = vv1.findViewById(R.id.image_view);
                        ImageView image_view_preview1 = vv1.findViewById(R.id.image_view_preview);
                        TextView latitude1 = vv1.findViewById(R.id.latitude);
                        TextView longtitude1 = vv1.findViewById(R.id.longtitude);
                        if(latitude1.getText().toString() != null && !latitude1.getText().toString().equals("")){
                            for (int i = 0; i < childCount; i++) {
                                View vv = mobileNumberLayout.getChildAt(i);
                                ImageView imageView = vv.findViewById(R.id.image_view);
                                ImageView image_view_preview = vv.findViewById(R.id.image_view_preview);
                                TextView latitude = vv.findViewById(R.id.latitude);
                                TextView longtitude = vv.findViewById(R.id.longtitude);

                                    dbData.open();
                                    long id = 0;
                                    String whereClause = "";
                                    String[] whereArgs = null;
                                    String mi_tank_survey_id = tankDataList.get(position).getMiTankSurveyId();
                                    String dcode = prefManager.getDistrictCode();
                                    String bcode = prefManager.getBlockCode();
                                    String pvcode = prefManager.getPvCode();
                                    String habcode = prefManager.getHabCode();

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
                                        values.put(AppConstant.KEY_LATITUDE, latitude.getText().toString());
                                        values.put(AppConstant.KEY_LONGITUDE, longtitude.getText().toString());
                                        values.put(AppConstant.KEY_IMAGE, image_str.trim());
                                        // values.put(AppConstant.KEY_CREATED_DATE,sdf.format(new Date()));
                                        dbData.open();
                                        ArrayList<MITank> imageOffline = dbData.getCenterImageData(mi_tank_survey_id);
                                        id = db.insert(DBHelper.SAVE_MI_TANK_CENTER_IMAGES, null, values);
                                        if (id > 0) {
                                            Toasty.success(TanksPondsListScreen.this, "Inserted Success!", Toast.LENGTH_LONG, true).show();
//                                onBackPressed();

                                        }
                           /* if (imageOffline.size() < 1) {
                                id = db.insert(DBHelper.SAVE_MI_TANK_CENTER_IMAGES, null, values);
                                if (id > 0) {
                                    Toasty.success(TanksPondsListScreen.this, "Inserted Success!", Toast.LENGTH_LONG, true).show();
                                    onBackPressed();
                                }
                            } else {
                                id = db.update(DBHelper.SAVE_MI_TANK_CENTER_IMAGES, values, "mi_tank_survey_id  = ? ", new String[]{mi_tank_survey_id});
                                if (id > 0) {
                                    Toasty.success(TanksPondsListScreen.this, "Updated Success!", Toast.LENGTH_LONG, true).show();
                                    onBackPressed();
                                }
                            }
*/

                                    } catch (Exception e) {
                                        Utils.showAlert(TanksPondsListScreen.this, "Please Capture Photo");
                                        //e.printStackTrace();
                                    }


                                }new fetchtankDatatask().execute();
                            back_press_flag=true;
                            dialog.dismiss();
                            }else {
                            Utils.showAlert(TanksPondsListScreen.this,"Please Capture Photo!");
                        }

                        }else {
                            Utils.showAlert(TanksPondsListScreen.this,"Please Capture Photo!");
                        }

                //focusOnView(scrollView);

            }
        });

        Button btnAddMobile = (Button) dialog.findViewById(R.id.btn_add);

        viewArrayList.clear();
        btnAddMobile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int childCount = mobileNumberLayout.getChildCount();
                if(childCount<10){
                    View vv = mobileNumberLayout.getChildAt(childCount-1);
                    ImageView image_view_preview=vv.findViewById(R.id.image_view_preview);
                    ImageView image_view=vv.findViewById(R.id.image_view);
                    TextView latitude=vv.findViewById(R.id.latitude);
                    TextView longtitude=vv.findViewById(R.id.longtitude);
                    if(latitude.getText().toString() != null && !latitude.getText().toString().equals("")){
                dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
                updateView(TanksPondsListScreen.this, mobileNumberLayout);
                    }else {
                        Utils.showAlert(TanksPondsListScreen.this,"Please Capture Previous Photo!");

                    }
                }

                else {
                    Utils.showAlert(TanksPondsListScreen.this,"Limit Exceed!");

                }
            }
        });

    }
    public View updateView(final Activity activity, final LinearLayout emailOrMobileLayout) {
        final View hiddenInfo = activity.getLayoutInflater().inflate(R.layout.add_photo_view, emailOrMobileLayout, false);
        final ImageView imageView_close = (ImageView) hiddenInfo.findViewById(R.id.imageView_close);
        final CardView camera = (CardView) hiddenInfo.findViewById(R.id.district_card);
        final ImageView image_view_preview=hiddenInfo.findViewById(R.id.image_view_preview);

        image_view_preview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               getLatLong();
            }

            public void getLatLong() {

                mlocManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                mlocListener = new MyLocationListener();


                // permission was granted, yay! Do the
                // location-related task you need to do.
                if (ContextCompat.checkSelfPermission(TanksPondsListScreen.this,
                        ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED) {

                    //Request location updates:
                    mlocManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, mlocListener);

                }

                if (mlocManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (ActivityCompat.checkSelfPermission(TanksPondsListScreen.this, ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(TanksPondsListScreen.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
                            requestPermissions(new String[]{CAMERA, ACCESS_FINE_LOCATION}, PERMISSION_REQUEST_CODE);
                    } else {
                        if (ActivityCompat.checkSelfPermission(TanksPondsListScreen.this, ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(TanksPondsListScreen.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions(TanksPondsListScreen.this, new String[]{ACCESS_FINE_LOCATION}, 1);

                        }
                    }
                    if (MyLocationListener.latitude > 0) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            if (CameraUtils.checkPermissions(TanksPondsListScreen.this)) {
                                captureImage();
                            } else {
                                requestCameraPermission(MEDIA_TYPE_IMAGE);
                            }
//                            checkPermissionForCamera();
                        } else {
                            captureImage();
                        }
                    } else {
                        Utils.showAlert(TanksPondsListScreen.this, "Satellite communication not available to get GPS Co-ordination Please Capture Photo in Open Area..");
                    }
                } else {
                    Utils.showAlert(TanksPondsListScreen.this, "GPS is not turned on...");
                }
            }

            private void captureImage() {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                File file = CameraUtils.getOutputMediaFile(MEDIA_TYPE_IMAGE);
                if (file != null) {
                    imageStoragePath = file.getAbsolutePath();
                }

                Uri fileUri = CameraUtils.getOutputMediaFileUri(TanksPondsListScreen.this, file);

                intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);

                // start the image capture Intent
                startActivityForResult(intent, CAMERA_CAPTURE_IMAGE_REQUEST_CODE);
                if (MyLocationListener.latitude > 0) {
                    offlatTextValue = MyLocationListener.latitude;
                    offlongTextValue = MyLocationListener.longitude;
                }
            }

            private void requestCameraPermission(final int type) {
                Dexter.withActivity(TanksPondsListScreen.this)
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
                AlertDialog.Builder builder = new AlertDialog.Builder(TanksPondsListScreen.this);
                builder.setTitle("Permissions required!")
                        .setMessage("Camera needs few permissions to work properly. Grant them in settings.")
                        .setPositiveButton("GOTO SETTINGS", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                CameraUtils.openSettings(TanksPondsListScreen.this);
                            }
                        })
                        .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        }).show();
            }
        });
        imageView_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    final LinearLayout mobileNumberLayout = (LinearLayout) dialog.findViewById(R.id.camera_layout);

                    int childCount = mobileNumberLayout.getChildCount();
                    if (childCount > 1) {
                        ((LinearLayout) hiddenInfo.getParent()).removeView(hiddenInfo);
                        viewArrayList.remove(hiddenInfo);
                    }

                } catch (IndexOutOfBoundsException a) {
                    a.printStackTrace();
                }
            }
        });
        emailOrMobileLayout.addView(hiddenInfo);

        View vv = emailOrMobileLayout.getChildAt(viewArrayList.size());
        //myEditTextView1.setSelection(myEditTextView1.length());
        viewArrayList.add(hiddenInfo);
        return hiddenInfo;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // if the result is capturing Image
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            new fetchtankDatatask().execute();
        } else if (requestCode == CAMERA_CAPTURE_IMAGE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // Refreshing the gallery
                CameraUtils.refreshGallery(getApplicationContext(), imageStoragePath);

                // successfully captured the image
                // display it in image view
                final LinearLayout mobileNumberLayout = (LinearLayout) dialog.findViewById(R.id.camera_layout);
                View vv = mobileNumberLayout.getChildAt(viewArrayList.size());
                ImageView image_view_preview=vv.findViewById(R.id.image_view_preview);
                ImageView image_view=vv.findViewById(R.id.image_view);
                TextView latitude=vv.findViewById(R.id.latitude);
                TextView longtitude=vv.findViewById(R.id.longtitude);
                image_view_preview.setVisibility(View.GONE);
                image_view.setVisibility(View.VISIBLE);
                Bitmap bitmap = CameraUtils.optimizeBitmap(BITMAP_SAMPLE_SIZE, imageStoragePath);
                image_view.setImageBitmap(bitmap);
                latitude.setText(String.valueOf(offlatTextValue));
                longtitude.setText(String.valueOf(offlongTextValue));

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
        } else {
                // failed to record video
            }
        }

    public void getCenterImage(int ImagePosition) {
        centerImagePosition=ImagePosition;
        try {
            new ApiService(this).makeJSONObjectRequest("GetCenterImage", Api.Method.POST, UrlGenerator.getTankPondListUrl(), encryptImageJsonParams(), "not cache", this);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public JSONObject encryptImageJsonParams() throws JSONException {
        String authKey = Utils.encrypt(prefManager.getUserPassKey(), getResources().getString(R.string.init_vector), getCenterImageJsonParams().toString());
        JSONObject dataSet = new JSONObject();
        dataSet.put(AppConstant.KEY_USER_NAME, prefManager.getUserName());
        dataSet.put(AppConstant.DATA_CONTENT, authKey);
        Log.d("tankInletStructureType", "" + authKey);
        return dataSet;
    }

    public  JSONObject getCenterImageJsonParams() throws JSONException {
        JSONObject dataSet = new JSONObject();
        dataSet.put(AppConstant.KEY_SERVICE_ID, "m_tank_center_image");
        dataSet.put("mi_tank_survey_id", tankDataList.get(centerImagePosition).getMiTankSurveyId());
        dataSet.put("pvcode", tankDataList.get(centerImagePosition).getPvCode());
        dataSet.put("habcode", tankDataList.get(centerImagePosition).getHabCode());
        Log.d("object", "" + dataSet);
        return dataSet;
    }



}
