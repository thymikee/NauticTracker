package com.thymikee.nautictracker;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;

import java.util.UUID;


public class MainActivity extends FragmentActivity {
//    private GPSTracker gps;
    private static final String TAG = "MainActivity";

    // use the websmithing defaultUploadWebsite for testing and then check your
    // location with your browser here: https://www.websmithing.com/gpstracker/displaymap.php
    private String defaultUploadWebsite;

    private static Button trackingButton;

    private boolean currentlyTracking;
    private int intervalInMinutes = 1;
    private AlarmManager alarmManager;
    private Intent trackerIntent;
    private Intent intent;
    private PendingIntent pendingIntent;
    private TextView textLatitude;
    private TextView textLongitude;
    private TextView textAccuracy;
    private LocationService locationService;
    private LocationClient mLocationClient;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        intent = new Intent(this, LocationService.class);

        trackingButton = (Button) findViewById(R.id.button1);
        textLatitude = (TextView) findViewById(R.id.val_latitude);
        textLongitude = (TextView) findViewById(R.id.val_longitude);
        textAccuracy = (TextView) findViewById(R.id.val_accuracy);

        SharedPreferences sharedPreferences = this.getSharedPreferences("com.thymikee.nautictracker.prefs", Context.MODE_PRIVATE);
        currentlyTracking = sharedPreferences.getBoolean("currentlyTracking", false);

        boolean firstTimeLoadindApp = sharedPreferences.getBoolean("firstTimeLoadindApp", true);

        if (firstTimeLoadindApp) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("firstTimeLoadindApp", false);
            editor.putString("appID",  UUID.randomUUID().toString());
            editor.apply();
        }

        locationService = new LocationService();
        mLocationClient = locationService.getLocationClient();

        trackingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                trackLocation(view);
            }
        });

        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction().commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        switch (item.getItemId()) {
            case R.id.menu_settings:
                startActivity(new Intent(this, SettingsActivity.class));
                return true;
            case R.id.menu_maps:
                startActivity(new Intent(this, MapActivity.class));
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void startAlarmManager() {
        Log.d(TAG, "startAlarmManager");

        Context context = getBaseContext();
        alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        trackerIntent = new Intent(context, AlarmReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(context, 0, trackerIntent, 0);

        alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                SystemClock.elapsedRealtime(),
                8000, // 60000 = 1 minute
                pendingIntent);

    }

    private void cancelAlarmManager() {
        Log.d(TAG, "cancelAlarmManager");

        Context context = getBaseContext();
        Intent trackerIntent = new Intent(context, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, trackerIntent, 0);
        AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);
        locationService.stopLocationUpdates();
    }


    protected void trackLocation(View v) {
        SharedPreferences sharedPreferences = this.getSharedPreferences("com.thymikee.nautictracker.prefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        if (!saveUserSettings()) {
            return;
        }

        if (!checkIfGooglePlayEnabled()) {
            return;
        }

        if (currentlyTracking) {
            cancelAlarmManager();

            currentlyTracking = false;
            editor.putBoolean("currentlyTracking", false);
            editor.putString("sessionID", "");
        } else {
            startAlarmManager();

            currentlyTracking = true;
            editor.putBoolean("currentlyTracking", true);
            editor.putFloat("totalDistanceInMeters", 0f);
            editor.putBoolean("firstTimeGettingPosition", true);
            editor.putString("sessionID",  UUID.randomUUID().toString());
        }

        editor.apply();
        setTrackingButtonState();

    }

    private boolean saveUserSettings() {
        SharedPreferences sharedPreferences = this.getSharedPreferences("com.thymikee.nautictracker.prefs", Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = sharedPreferences.edit();

        return true;
    }

    private void displayUserSettings() {
        SharedPreferences sharedPreferences = this.getSharedPreferences("com.thymikee.nautictracker.prefs", Context.MODE_PRIVATE);
        intervalInMinutes = sharedPreferences.getInt("interval", 1);

//        updateInterval.setSelected(intervalInMinutes);

    }

    private boolean checkIfGooglePlayEnabled() {
        if (GooglePlayServicesUtil.isGooglePlayServicesAvailable(this) == ConnectionResult.SUCCESS) {
            Log.v(TAG, "koko");
            return true;
        } else {
            Log.e(TAG, "unable to connect to google play services.");
            Toast.makeText(getApplicationContext(), R.string.google_play_services_unavailable, Toast.LENGTH_LONG).show();
            return false;
        }
    }

    private void setTrackingButtonState() {
        if (currentlyTracking) {
            trackingButton.setText(R.string.button_stop);
        } else {
            trackingButton.setText(R.string.button_start);
        }
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            updateUI(intent);
        }
    };

    private void updateUI(Intent intent) {
        String latitude = intent.getStringExtra("latitude");
        String longitude = intent.getStringExtra("longitude");
        String accuracy = intent.getStringExtra("accuracy");

        if(latitude != null && longitude != null && accuracy != null) {
            textLatitude.setText(latitude);
            textLongitude.setText(longitude);
            textAccuracy.setText(accuracy + " m");
        }
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        Log.d(TAG, "onResume");
        super.onResume();

        displayUserSettings();
        startService(intent);
        registerReceiver(broadcastReceiver,
                new IntentFilter(LocationService.BROADCAST_ACTION));
        setTrackingButtonState();
    }

    @Override
    public void onPause() {
        Log.d(TAG, "onPause");
        super.onPause();
        unregisterReceiver(broadcastReceiver);
        stopService(intent);
    }

    @Override
    protected void onStop() {
        Log.d(TAG, "onStop");
        super.onStop();
//        cancelAlarmManager();
    }
}
