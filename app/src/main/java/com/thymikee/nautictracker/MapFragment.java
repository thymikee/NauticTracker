package com.thymikee.nautictracker;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;

import org.osmdroid.ResourceProxy;
import org.osmdroid.tileprovider.MapTileProviderBasic;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.util.ResourceProxyImpl;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.MinimapOverlay;
import org.osmdroid.views.overlay.ScaleBarOverlay;
import org.osmdroid.views.overlay.compass.CompassOverlay;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

/**
 * Created by thymikee on 08.11.14.
 */
public class MapFragment extends Fragment {

    // Constants
    // ===========================================================
    private static final String TAG = "MapFragment";
    private static final int DIALOG_ABOUT_ID = 1;

    private static final int MENU_SAMPLES = Menu.FIRST + 1;
    private static final int MENU_ABOUT = MENU_SAMPLES + 1;

    private static final int MENU_LAST_ID = MENU_ABOUT + 1; // Always set to last unused id

    // Fields
    // ===========================================================

    private SharedPreferences mPrefs;
    private MapView mMapView;
    private MyLocationNewOverlay mLocationOverlay;
    private CompassOverlay mCompassOverlay;
    private MinimapOverlay mMinimapOverlay;
    private ScaleBarOverlay mScaleBarOverlay;
    private ResourceProxy mResourceProxy;

    public static MapFragment newInstance() {
        MapFragment fragment = new MapFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mResourceProxy = new ResourceProxyImpl(inflater.getContext().getApplicationContext());
        mMapView = new MapView(inflater.getContext(), 256, mResourceProxy);


        mMapView.setBuiltInZoomControls(true);
        

        // zoom to the netherlands
        mMapView.getController().setZoom(8);
        mMapView.getController().setCenter(new GeoPoint(51500000, 5400000));

        // Add tiles layer
//        mProvider = new MapTileProviderBasic(getApplicationContext());
//        mProvider.setTileSource(TileSourceFactory.FIETS_OVERLAY_NL);
//        this.mTilesOverlay = new TilesOverlay(mProvider, this.getBaseContext());
//        mMapView.getOverlays().add(this.mTilesOverlay);

        return mMapView;
    }

    public void setCenter(GeoPoint geoPoint) {
        Log.d(TAG, "geo set");
        mMapView.getController().setCenter(geoPoint);
    }


}
