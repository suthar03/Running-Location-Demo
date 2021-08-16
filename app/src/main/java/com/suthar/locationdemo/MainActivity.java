package com.suthar.locationdemo;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static final int Default_INTERVAL = 30;
    public static final int Default_FASTEST_INTERVAL = 5;
    public static final int PERMISSION_FINE_LOCATION = 1;
    public static final String TAG = "MainActivity";
    TextView tv_lat, tv_lon;

    FusedLocationProviderClient fusedLocationProviderClient;
    boolean updateOn = false;
    //Location request is configuration file of setting  related to fusedLocationProviderClient
    LocationRequest locationRequest;
    Location currentLocation;

    LocationCallback locationCallBack;


    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tv_lat = findViewById(R.id.tv_lat);
        tv_lon = findViewById(R.id.tv_lon);


        //setting up all LocationRequest properties
        locationRequest = new LocationRequest();
        locationRequest.setInterval(1000 * Default_INTERVAL);
        locationRequest.setFastestInterval(1000 * Default_FASTEST_INTERVAL);

        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);


        locationCallBack = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                super.onLocationResult(locationResult);
                Location location = locationResult.getLastLocation();
                UpdateUIValues(location);
                Log.d(TAG, "onLocationResult: Location Updated");
            }
        };


        updateGPS();
        startLocationUpdates();
    }//End of onCreate Method


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSION_FINE_LOCATION:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    updateGPS();
                } else {
                    Toast.makeText(this, "This application requires permission to access the location", Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
        }
    }

    private void updateGPS() {
        //get permission to use GPS
        //Get current location of device using fusedClient
        //Update UI
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(MainActivity.this);
        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(MainActivity.this, new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    //we got permission and location
                    String re = "Not Null";
                    if (location == null) re = "It's Null";
                    Log.d("Location_nully", "just location " + re);

                    UpdateUIValues(location);
                    currentLocation = location;

                }
            });
        } else {
            //Permission is not granted
            Log.d("Build.VERSION.SDK_INT", Build.VERSION.SDK_INT + "");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_FINE_LOCATION);
            }
        }
    }

    private void UpdateUIValues(Location location) {
        //updating all text view with new location
        tv_lat.setText(String.valueOf(location.getLatitude()));
        tv_lon.setText(String.valueOf(location.getLongitude()));

    }


    private void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallBack, null);
            updateGPS();
        }else{
            Log.d("Build.VERSION.SDK_INT", Build.VERSION.SDK_INT + "");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_FINE_LOCATION);
            }
        }
    }
    private void stopLocationUpdates(){
        tv_lat.setText("Location is NOT being tracked");
        tv_lon.setText("Location is NOT being tracked");

        fusedLocationProviderClient.removeLocationUpdates(locationCallBack);
    }


}
