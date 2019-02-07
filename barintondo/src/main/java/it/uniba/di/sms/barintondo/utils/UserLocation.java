package it.uniba.di.sms.barintondo.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

public class UserLocation implements Constants {

    private String TAG_CLASS = getClass().getSimpleName();
    private Activity activity;
    private boolean mLocationPermissionGranted;
    private final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    public static final int REQUEST_CHECK_SETTINGS = 2;
    private FusedLocationProviderClient mFusedLocationClient;
    LocationRequest mLocationRequest;
    private LocationCallback mLocationCallback;
    private boolean isLocationUpdatesActive;
    private MyListners.UserLocationCallback mlocationListner;

    public UserLocation(Activity activity, MyListners.UserLocationCallback locationListner) {
        this.activity = activity;
        mLocationPermissionGranted = false;
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient( activity );
        getLocationPermission();
        createLocationRequest();
        isLocationUpdatesActive = false;
        this.mlocationListner = locationListner;
        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    //Log.d( TAG , TAG_CLASS + ": entered onLocationResult()"+location );
                    UserUtils.myLocation=location;
                    UserUtils.myLocationIsSetted=true;
                    mlocationListner.onLocation( location );
                }
            }

            ;
        };
    }

    private void getLocationPermission() {
        Log.d( TAG , TAG_CLASS + ": entered getLocationPermission()" );
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission( activity ,
                android.Manifest.permission.ACCESS_FINE_LOCATION )
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
            getDeviceLocation();
        } else {
            ActivityCompat.requestPermissions( activity ,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION} ,
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION );
        }
    }

    public void onRequestPermissionsResult(int requestCode ,
                                           @NonNull String permissions[] ,
                                           @NonNull int[] grantResults) {
        Log.d( TAG , TAG_CLASS + ": entered onRequestPermissionResult()" );
        mLocationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true;
                }
            }
        }
        getDeviceLocation();
    }


    private void getDeviceLocation() {
        Log.d( TAG , TAG_CLASS + ": entered getDeviceLocation()" );
        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */
        try {
            if (mLocationPermissionGranted) {
                mFusedLocationClient.getLastLocation()
                        .addOnSuccessListener( activity , new OnSuccessListener<Location>() {
                            @Override
                            public void onSuccess(Location location) {
                                // Got last known location. In some rare situations this can be null.
                                if (location != null) {
                                    //Log.d( TAG , TAG_CLASS + ": get location()" + location );
                                    UserUtils.myLocation = location;
                                    UserUtils.myLocationIsSetted = true;
                                } else
                                    Log.d( TAG , TAG_CLASS + ": get location() but location was null" );
                            }
                        } );
            }
        } catch (SecurityException e) {
            Log.e( "Exception: %s" , e.getMessage() );
        }
    }

    protected void createLocationRequest() {
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setInterval( 60000 );
        mLocationRequest.setFastestInterval( 40000 );
        mLocationRequest.setPriority( LocationRequest.PRIORITY_HIGH_ACCURACY );
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest( mLocationRequest );
        SettingsClient client = LocationServices.getSettingsClient( activity );
        Task<LocationSettingsResponse> task = client.checkLocationSettings( builder.build() );
        task.addOnSuccessListener( activity , new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                if (!isLocationUpdatesActive) {
                    startLocationUpdates();                }
            }
        } );

        task.addOnFailureListener( activity , new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if (e instanceof ResolvableApiException && !UserUtils.positionPermissionRequested) {
                    // Location settings are not satisfied, but this can be fixed
                    // by showing the user a dialog.
                    try {
                        // Show the dialog by calling startResolutionForResult(),
                        // and check the result in onActivityResult().
                        UserUtils.positionPermissionRequested=true;
                        ResolvableApiException resolvable = (ResolvableApiException) e;
                        resolvable.startResolutionForResult( activity ,
                                REQUEST_CHECK_SETTINGS );
                    } catch (IntentSender.SendIntentException sendEx) {
                        // Ignore the error.
                    }
                }
            }
        } );
    }

    public void startLocationUpdates() {
        //controlla che siano stati dati tutti i permessi
        if (ActivityCompat.checkSelfPermission( activity ,
                Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission( activity ,
                Manifest.permission.ACCESS_COARSE_LOCATION ) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        if (!isLocationUpdatesActive) {
            mFusedLocationClient.requestLocationUpdates( mLocationRequest ,
                    mLocationCallback ,
                    null /* Looper */ );
            isLocationUpdatesActive = true;
        }
    }

    public void stopLocationUpdates() {
        if (isLocationUpdatesActive) {
            mFusedLocationClient.removeLocationUpdates( mLocationCallback );
            isLocationUpdatesActive=false;
        }
    }
}
