package com.thymikee.nautictracker;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;

/**
 * Created by thymikee on 08.11.14.
 */
public class MapActivity extends FragmentActivity
{

    private static final int DIALOG_ABOUT_ID = 1;
    private static final String MAP_FRAGMENT_TAG = "org.osmdroid.MAP_FRAGMENT_TAG";

    // ===========================================================
    // Constructors
    // ===========================================================
    /** Called when the activity is first created. */
    @Override
    public void onCreate(final Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        this.setContentView(R.layout.activity_map);

        FragmentManager fm = this.getSupportFragmentManager();

        if (fm.findFragmentByTag(MAP_FRAGMENT_TAG) == null) {
            MapFragment mapFragment = MapFragment.newInstance();
            fm.beginTransaction().add(R.id.map_container, mapFragment, MAP_FRAGMENT_TAG).commit();
        }
    }
}