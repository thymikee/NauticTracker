package com.thymikee.nautictracker;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;

import java.util.UUID;

import me.alexrs.wavedrawable.WaveDrawable;


public class MainActivity extends FragmentActivity {
//    private GPSTracker gps;
    private static final String TAG = "MainActivity";

    // use the websmithing defaultUploadWebsite for testing and then check your
    // location with your browser here: https://www.websmithing.com/gpstracker/displaymap.php
    private String defaultUploadWebsite;

    private static Button trackingButton;

    private boolean currentlyTracking;
    private AlarmManager alarmManager;
    private Intent trackerIntent;
    private Intent intent;
    private PendingIntent pendingIntent;
    private TextView textLatitude;
    private TextView textLongitude;
    private TextView textAccuracy;
    private LocationService locationService;
    private LocationClient mLocationClient;
    private SharedPreferences sharedPreferences;
    private SharedPreferences prefs;
    private SharedPreferences.OnSharedPreferenceChangeListener listener;
    private int updateInterval;

    private static final int NOTIFY_ID = 1;
    private NotificationManager notificationManager;

    private WaveDrawable waveDrawable;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        intent = new Intent(this, LocationService.class);

        trackingButton = (Button) findViewById(R.id.button1);
        textLatitude = (TextView) findViewById(R.id.val_latitude);
        textLongitude = (TextView) findViewById(R.id.val_longitude);
        textAccuracy = (TextView) findViewById(R.id.val_accuracy);

        sharedPreferences = this.getSharedPreferences("com.thymikee.nautictracker.prefs", Context.MODE_PRIVATE);
        prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());

        currentlyTracking = sharedPreferences.getBoolean("currentlyTracking", false);
        locationService = new LocationService();
        mLocationClient = locationService.getLocationClient();

        updateInterval = Integer.parseInt(prefs.getString("update_interval", "8000"));

        listener = new SharedPreferences.OnSharedPreferenceChangeListener() {
            public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
                if (key.equals("update_interval")) {
                    updateInterval = Integer.parseInt(prefs.getString(key, "8000"));
                    Log.d(TAG, "update changed " + Integer.toString(updateInterval));
                    if(currentlyTracking) {
                        cancelAlarmManager();
                        startAlarmManager(updateInterval);
                    }
                }
            }
        };

        trackingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                trackLocation(view);
            }
        });

        prefs.registerOnSharedPreferenceChangeListener(listener);

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

    public void startAlarmManager(int updateInterval) {
        Log.d(TAG, "startAlarmManager");

        Context context = getBaseContext();
        alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        trackerIntent = new Intent(context, AlarmReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(context, 0, trackerIntent, 0);
        Log.d(TAG, Integer.toString(updateInterval));
        alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                SystemClock.elapsedRealtime(),
                updateInterval,
                pendingIntent);


        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        int icon = R.drawable.ic_launcher;
        String contentTitle = getResources().getString(R.string.app_name);
        String contentText = getResources().getString(R.string.notification_text);

        NotificationCompat.Builder mNotifyBuilder = new NotificationCompat.Builder(context)
                .setContentTitle(contentTitle)
                .setContentText(contentText)
                .setSmallIcon(icon);

        notificationManager.notify(NOTIFY_ID, mNotifyBuilder.build());

    }

    public void cancelAlarmManager() {
        Log.d(TAG, "cancelAlarmManager");

        Context context = getBaseContext();
        Intent trackerIntent = new Intent(context, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, trackerIntent, 0);
        AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);
        locationService.stopLocationUpdates();
        if(notificationManager != null) {
            notificationManager.cancel(NOTIFY_ID);
        }

    }

    private void startWaveAnimation() {
        ImageView imageView = (ImageView) findViewById(R.id.wave);
        waveDrawable = new WaveDrawable(Color.parseColor("#9e0c0c"), 200);
        imageView.setBackground(waveDrawable);

        Interpolator interpolator = new AccelerateDecelerateInterpolator();
        waveDrawable.setWaveInterpolator(interpolator);
        waveDrawable.startAnimation();
    }

    private void stopWaveAnimation(WaveDrawable waveDrawable) {
        if(waveDrawable == null) { return; }
        waveDrawable.stopAnimation();
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
            stopWaveAnimation(waveDrawable);

            currentlyTracking = false;
            editor.putBoolean("currentlyTracking", false);
            editor.putString("sessionID", "");
        } else {
            startAlarmManager(updateInterval);
            startWaveAnimation();

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

//        startService(intent);
        registerReceiver(broadcastReceiver,
                new IntentFilter(LocationService.BROADCAST_ACTION));
        setTrackingButtonState();
        if(waveDrawable != null && !waveDrawable.isAnimationRunning()) {
            startWaveAnimation();
        }
    }

    @Override
    public void onPause() {
        Log.d(TAG, "onPause");
        super.onPause();
        unregisterReceiver(broadcastReceiver);
        stopWaveAnimation(waveDrawable);
       // stopService(intent);
    }

    @Override
    protected void onStop() {
        Log.d(TAG, "onStop");
        super.onStop();
//        cancelAlarmManager();
    }
}
