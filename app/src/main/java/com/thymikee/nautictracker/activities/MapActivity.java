package com.thymikee.nautictracker.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.Log;

import com.thymikee.nautictracker.services.LocationService;
import com.thymikee.nautictracker.fragments.MapFragment;
import com.thymikee.nautictracker.R;

import org.osmdroid.util.GeoPoint;

/**
 * Created by thymikee on 08.11.14.
 */
public class MapActivity extends FragmentActivity {

    private static final String TAG = "MapActivity";
    private static final String MAP_FRAGMENT_TAG = "org.osmdroid.MAP_FRAGMENT_TAG";

    private MapFragment mapFragment;
    private Intent intent;

    // ===========================================================
    // Constructors
    // ===========================================================
    /** Called when the activity is first created. */
    @Override
    public void onCreate(final Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_map);

        intent = new Intent(this, LocationService.class);
        FragmentManager fm = this.getSupportFragmentManager();

        if (fm.findFragmentByTag(MAP_FRAGMENT_TAG) == null) {
            mapFragment = MapFragment.newInstance();
            fm.beginTransaction().add(R.id.map_container, mapFragment, MAP_FRAGMENT_TAG).commit();
        }

    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String latitude = intent.getStringExtra("latitude");
            String longitude = intent.getStringExtra("longitude");
            Log.e(TAG, latitude + " " + longitude);
            GeoPoint geo = new GeoPoint(Double.parseDouble(latitude), Double.parseDouble(longitude));
            if (mapFragment != null) {
                mapFragment.setCenter(geo);
                mapFragment.addPointToPath(geo);
            }
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
        startService(intent);
        registerReceiver(broadcastReceiver,
                new IntentFilter(LocationService.BROADCAST_ACTION));
    }

    @Override
    public void onPause() {
        super.onPause();
        unregisterReceiver(broadcastReceiver);
        stopService(intent);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }
}