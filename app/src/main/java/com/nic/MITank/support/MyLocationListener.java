package com.nic.MITank.support;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;

public class MyLocationListener implements LocationListener {
    public static double accuracy;
    public static double latitude;
    public static double longitude;

    public void onLocationChanged(Location loc) {
        latitude = loc.getLatitude();
        longitude = loc.getLongitude();
        accuracy = (double) loc.getAccuracy();
    }

    public void onProviderDisabled(String provider) {
        latitude = 0.0d;
        longitude = 0.0d;
    }

    public void onProviderEnabled(String provider) {
    }

    public void onStatusChanged(String provider, int status, Bundle extras) {
        switch (status) {
            case 0:
                latitude = 0.0d;
                longitude = 0.0d;
                return;
            case 1:
                latitude = 0.0d;
                longitude = 0.0d;
                return;
            default:
                return;
        }
    }
}
