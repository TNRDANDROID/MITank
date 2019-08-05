package com.nic.MITank.activity;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.nic.MITank.R;
import com.nic.MITank.constant.AppConstant;
import com.nic.MITank.dataBase.dbData;
import com.nic.MITank.model.MITank;
import com.nic.MITank.session.PrefManager;
import com.nic.MITank.support.MyLocationListener;
import com.nic.MITank.utils.Utils;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.CAMERA;

public class TrackingScreen extends AppCompatActivity implements LocationListener, View.OnClickListener {
    private PrefManager prefManager;
    private ImageView back_img, home_img;

    private TextView tank_pond_name, marquee_tv,title_tank_pond;
    private Button start_lat_long_click_view, middle_lat_long_click_view, end_lat_long_click_view, save_btn;

    Handler myHandler = new Handler();
    LocationManager mlocManager = null;
    LocationListener mlocListener;
    private static final int PERMISSION_REQUEST_CODE = 200;
    public dbData dbData = new dbData(this);
    Double offlatTextValue, offlongTextValue;
    double latitude = 0.0d;
    double longitude = 0.0d;

    private String pointType;
    private int pointSerialNo;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.track_layout);
        intializeUI();
    }

    public void intializeUI() {
        prefManager = new PrefManager(this);
        home_img = (ImageView) findViewById(R.id.home_img);
        back_img = (ImageView) findViewById(R.id.back_img);
        start_lat_long_click_view = (Button) findViewById(R.id.start_lat_long_click_view);
        middle_lat_long_click_view = (Button) findViewById(R.id.middle_lat_long_click_view);
        end_lat_long_click_view = (Button) findViewById(R.id.end_lat_long_click_view);
        tank_pond_name = (TextView) findViewById(R.id.tank_pond_name);
        marquee_tv = (TextView) findViewById(R.id.marquee_tv);
        title_tank_pond = (TextView) findViewById(R.id.title_tank_pond);


        back_img.setOnClickListener(this);
        start_lat_long_click_view.setOnClickListener(this);
        middle_lat_long_click_view.setOnClickListener(this);
        end_lat_long_click_view.setOnClickListener(this);

        marquee_tv.setVisibility(View.GONE);
        home_img.setOnClickListener(this);
        tank_pond_name.setText(getIntent().getStringExtra(AppConstant.NAME_OF_THE_MI_TANK));
        middle_lat_long_click_view.setBackgroundResource(R.drawable.middle_disable_button);
        end_lat_long_click_view.setBackgroundResource(R.drawable.end_disable_button);
        middle_lat_long_click_view.setClickable(false);
        end_lat_long_click_view.setClickable(false);
        if(!prefManager.getSchemeName().equalsIgnoreCase("ALL")) {
            title_tank_pond.setText("Name of the" +" "+ prefManager.getSchemeName());
        }else{
            title_tank_pond.setText("Name of the Tanks/Ponds");
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back_img:
                onBackPress();
                break;
            case R.id.start_lat_long_click_view:
                pointType = "1";
                pointSerialNo = 1;
                showAlert(pointType);
                break;
            case R.id.middle_lat_long_click_view:
                pointType = "2";
                pointSerialNo = pointSerialNo + 1;
                getLocationPermissionWithLatLong();
                break;
            case R.id.end_lat_long_click_view:
                pointType = "3";
                pointSerialNo = pointSerialNo + 1;
                showAlert(pointType);
                break;
            case R.id.home_img:
                dashboard();
                break;

        }
    }

    public void dashboard() {
        Intent intent = new Intent(this, HomePage.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("Home", "Home");
        startActivity(intent);
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_enter, R.anim.slide_exit);
    }

    public void showAlert(String pointType) {
        String startAlert = "Capturing of Tank/Ponds Starting GPS-co-ordinate is allowed only once.So stand on the correct starting point of this Tank/Ponds and capture Gps co-ordinate";
        String endAlert = "Capturing of Tank/Ponds Ending GPS-co-ordinate is allowed only once.So stand on the correct ending point of this Tank/Ponds and capture Gps co-ordinate";
        String alert = "";
        if (pointType.equalsIgnoreCase("1")) {
            alert = startAlert;
        } else {
            alert = endAlert;
        }

        new AlertDialog.Builder(this)
                .setTitle("Alert")
                .setMessage(alert)
                .setIcon(R.mipmap.alert)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        getLocationPermissionWithLatLong();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.cancel();
                    }
                }).show();

    }

    private void getLocationPermissionWithLatLong() {
        mlocManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        mlocListener = new MyLocationListener();


        // permission was granted, yay! Do the
        // location-related task you need to do.
        if (ContextCompat.checkSelfPermission(TrackingScreen.this,
                ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {

            //Request location updates:
            mlocManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, mlocListener);

        }

        if (mlocManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (ActivityCompat.checkSelfPermission(TrackingScreen.this, ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(TrackingScreen.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
                    requestPermissions(new String[]{CAMERA, ACCESS_FINE_LOCATION}, PERMISSION_REQUEST_CODE);
            } else {
                if (ActivityCompat.checkSelfPermission(TrackingScreen.this, ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(TrackingScreen.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(TrackingScreen.this, new String[]{ACCESS_FINE_LOCATION}, 1);

                }
            }
            if (MyLocationListener.latitude > 0) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (MyLocationListener.latitude > 0) {
                        offlatTextValue = MyLocationListener.latitude;
                        offlongTextValue = MyLocationListener.longitude;
                    }
//                            checkPermissionForCamera();
                } else {
//                    captureImage();
                }
            } else {
                Utils.showAlert(TrackingScreen.this, "Satellite communication not available to get GPS Co-ordination Please Capture Photo in Open Area..");
            }
        } else {
            Utils.showAlert(TrackingScreen.this, "GPS is not turned on...");
        }

        if (!pointType.equalsIgnoreCase("") && offlatTextValue != null) {
            dbData.open();
            MITank trackData = new MITank();

            if (pointType.equalsIgnoreCase("1")) {
                trackData.setPointType(pointType);
                trackData.setPointSerialNo(pointSerialNo);
                marquee_tv.setVisibility(View.VISIBLE);
                start_lat_long_click_view.setBackgroundResource(R.drawable.start_disable_button);
                middle_lat_long_click_view.setBackgroundResource(R.drawable.middle_button);
                start_lat_long_click_view.setClickable(false);
                middle_lat_long_click_view.setClickable(true);
                Animation marquee = AnimationUtils.loadAnimation(this, R.anim.marquee);
                marquee_tv.startAnimation(marquee);
            } else if (pointType.equalsIgnoreCase("2")) {
                trackData.setPointType(pointType);
                trackData.setPointSerialNo(pointSerialNo);
                end_lat_long_click_view.setClickable(true);
                end_lat_long_click_view.setBackgroundResource(R.drawable.end_button);
            } else {
                trackData.setPointType(pointType);
                trackData.setPointSerialNo(pointSerialNo);
                start_lat_long_click_view.setBackgroundResource(R.drawable.start_button);
                start_lat_long_click_view.setClickable(true);
            }

            trackData.setDistictCode(prefManager.getDistrictCode());
            trackData.setBlockCode(prefManager.getBlockCode());
            trackData.setPvCode(prefManager.getPvCode());
            trackData.setHabCode(prefManager.getHabCode());

            trackData.setMiTankSurveyId(getIntent().getStringExtra(AppConstant.MI_TANK_SURVEY_ID));
            trackData.setLatitude(offlatTextValue.toString());
            trackData.setLongitude(offlongTextValue.toString());

            dbData.saveLatLong(trackData);
        }
    }


    @Override
    public void onLocationChanged(final Location location) {
        latitude = location.getLatitude();
        longitude = location.getLongitude();
        Toast.makeText(this, "Start Move on the Road", Toast.LENGTH_LONG).show();

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

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

}
