package com.thymikee.nautictracker.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;

public class LocationService extends Service implements
        GooglePlayServicesClient.ConnectionCallbacks,
        GooglePlayServicesClient.OnConnectionFailedListener,
        LocationListener {

    private static final String TAG = "LocationService";

    // use the websmithing defaultUploadWebsite for testing and then check your
    // location with your browser here: https://www.websmithing.com/gpstracker/displaymap.php
    private String defaultUploadWebsite;

    private boolean currentlyProcessingLocation = false;
    private LocationRequest locationRequest;
    private LocationClient locationClient;
    private Location location;
    private final Handler handler = new Handler();
    Intent intent;

    public static final String BROADCAST_ACTION = "com.thymikee.nautictracker.displayevent";

    public LocationClient getLocationClient() {
        return locationClient;
    }

    private Runnable sendUpdates = new Runnable() {
        public void run() {
            updateUI();
            updateDB();
        }
    };

    private void updateUI() {
        Log.d(TAG, "entered DisplayLoggingInfo");

        if (location == null) { return; }

        Log.e(TAG, "Display position: " + location.getLatitude() + ", " + location.getLongitude() + " accuracy: " + location.getAccuracy());
//intent.putExtra()
        // TODO Parcelable
        intent.putExtra("latitude", String.valueOf(location.getLatitude()));
        intent.putExtra("longitude", String.valueOf(location.getLongitude()));
        intent.putExtra("accuracy", String.valueOf(location.getAccuracy()));
        intent.putExtra("speed", String.valueOf(location.getSpeed()));

        intent.putExtra("latitude_in_seconds", String.valueOf(Location.convert(location.getLatitude(), Location.FORMAT_SECONDS)));
        intent.putExtra("longitude_in_seconds", String.valueOf(Location.convert(location.getLongitude(), Location.FORMAT_SECONDS)));
        sendBroadcast(intent);



            // we have our desired accuracy of 500 meters so lets quit this service,
            // onDestroy will be called and stop our location uodates
//            if (location.getAccuracy() < 500.0f) {
//                stopLocationUpdates();
//            }


    }

    private void updateDB() {
        if (location == null) { return; }

//        TripRepository.getById(getBaseContext(), 1);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        intent = new Intent(BROADCAST_ACTION);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // if we are currently trying to get a location and the alarm manager has called this again,
        // no need to start processing a new location.
        Log.d(TAG, "Start");
        handler.removeCallbacks(sendUpdates);
        handler.postDelayed(sendUpdates, 1000); // 1 second

        if (!currentlyProcessingLocation) {
            currentlyProcessingLocation = true;
            startTracking();
        }

        return START_NOT_STICKY;
    }

    private void startTracking() {
        Log.d(TAG, "startTracking");

        if (GooglePlayServicesUtil.isGooglePlayServicesAvailable(this) == ConnectionResult.SUCCESS) {
            locationClient = new LocationClient(this,this,this);

            if (!locationClient.isConnected() || !locationClient.isConnecting()) {
                locationClient.connect();
            }
        } else {
            Log.e(TAG, "unable to connect to google play services.");
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onLocationChanged(Location location) {
        if (location != null) {
            this.location = location;

            // we have our desired accuracy of 500 meters so lets quit this service,
            // onDestroy will be called and stop our location uodates
//            if (location.getAccuracy() < 500.0f) {
//                stopLocationUpdates();
////                sendLocationDataToWebsite(location);
//            }
        }
    }

    public void stopLocationUpdates() {
        if (locationClient != null && locationClient.isConnected()) {
            locationClient.removeLocationUpdates(this);
            locationClient.disconnect();
        }
    }

    /**
     * Called by Location Services when the request to connect the
     * client finishes successfully. At this point, you can
     * request the current location or start periodic updates
     */
    @Override
    public void onConnected(Bundle bundle) {
        Log.d(TAG, "onConnected");
        SharedPreferences sharedPreferences = this.getSharedPreferences("com.thymikee.nautictracker.prefs", Context.MODE_PRIVATE);
        int updateInterval = Integer.parseInt(sharedPreferences.getString("update_interval", "3000"));

        locationRequest = LocationRequest.create();
        locationRequest.setInterval(updateInterval); // milliseconds
        locationRequest.setFastestInterval(1000); // the fastest rate in milliseconds at which your app can handle location updates
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        locationClient.requestLocationUpdates(locationRequest, this);

        Toast.makeText(this, "Connected", Toast.LENGTH_SHORT).show();
    }

    /**
     * Called by Location Services if the connection to the
     * location client drops because of an error.
     */
    @Override
    public void onDisconnected() {
        Log.e(TAG, "onDisconnected");

        stopLocationUpdates();
        stopSelf();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.e(TAG, "onConnectionFailed");
        Toast.makeText(this, "Disconnected. Please re-connect.",
                Toast.LENGTH_SHORT).show();

        stopLocationUpdates();
        stopSelf();
    }
}