package com.thymikee.nautictracker;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.model.CameraPosition;

import org.osmdroid.ResourceProxy;
import org.osmdroid.events.DelayedMapListener;
import org.osmdroid.events.MapListener;
import org.osmdroid.events.ScrollEvent;
import org.osmdroid.events.ZoomEvent;
import org.osmdroid.tileprovider.MapTileProviderBasic;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.util.ResourceProxyImpl;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.MinimapOverlay;
import org.osmdroid.views.overlay.PathOverlay;
import org.osmdroid.views.overlay.ScaleBarOverlay;
import org.osmdroid.views.overlay.compass.CompassOverlay;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.util.ArrayList;

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
    private PathOverlay mPathOverlay;
    private CameraPosition cp;
    private Bundle bundle;
    private GeoPoint mMapCenter;
    private ArrayList<GeoPoint> routePoints;
    private int mMapZoom;

    public static MapFragment newInstance() {
        MapFragment fragment = new MapFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        if (savedInstanceState != null &&
//                savedInstanceState.getSerializable("center") != null &&
//                savedInstanceState.getSerializable("zoom") != null) {
//            mMapView.getController().setCenter((GeoPoint)savedInstanceState.getSerializable("center"));
//            mMapView.getController().setZoom((Integer)savedInstanceState.getSerializable("zoom"));
//        }
        Log.d(TAG, "onCreate");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");
        mResourceProxy = new ResourceProxyImpl(inflater.getContext().getApplicationContext());
        mScaleBarOverlay = new ScaleBarOverlay(inflater.getContext(), mResourceProxy);
        mPathOverlay = new PathOverlay(Color.RED, inflater.getContext());
        mMapView = new MapView(inflater.getContext(), 256, mResourceProxy);
        mMinimapOverlay = new MinimapOverlay(inflater.getContext(), mMapView.getTileRequestCompleteHandler());

        Paint pPaint = mPathOverlay.getPaint();
        pPaint.setStrokeWidth(40);
        mPathOverlay.setPaint(pPaint);

        if (savedInstanceState == null) {
            Log.d(TAG, "instance null");
            mMapZoom = 8;
            mMapCenter = new GeoPoint(51500000, 5400000);
            mMapView.getController().setZoom(mMapZoom);
            mMapView.getController().setCenter(mMapCenter);
        }

        mMapView.setBuiltInZoomControls(true);
        mMapView.getOverlays().add(mScaleBarOverlay);
        mMapView.getOverlays().add(mPathOverlay);
        mMapView.getOverlays().add(mMinimapOverlay);

        mMapView.setMapListener(new DelayedMapListener(new MapListener() {
            public boolean onZoom(final ZoomEvent e) {
                mMapZoom = mMapView.getZoomLevel();
                Log.d(TAG, "zoom + " + mMapZoom);
                return true;
            }

            public boolean onScroll(final ScrollEvent e) {
                return true;
            }
        }, 300));

        // Add tiles layer
//        mProvider = new MapTileProviderBasic(getApplicationContext());
//        mProvider.setTileSource(TileSourceFactory.FIETS_OVERLAY_NL);
//        this.mTilesOverlay = new TilesOverlay(mProvider, this.getBaseContext());
//        mMapView.getOverlays().add(this.mTilesOverlay);\

        return mMapView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        Log.d(TAG, "activity created");
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            Log.d(TAG, "activity created: " + Boolean.toString(savedInstanceState.containsKey("map_zoom")));
            mMapView.getController().setZoom(savedInstanceState.getInt("map_zoom"));
            mMapView.getController().setCenter((GeoPoint)savedInstanceState.getSerializable("map_center"));
        }
    }

    public void setCenter(GeoPoint geoPoint) {
        Log.d(TAG, "geo set");
        mMapView.getController().setCenter(geoPoint);
        mMapCenter = geoPoint;
    }

    public void addPointToPath(GeoPoint geoPoint) {
        Log.d(TAG, "point added");
//        routePoints.add(geoPoint);
        mPathOverlay.addPoint(geoPoint);
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
//        restoreMapState();
    }

    @Override
    public void onDestroy() {
//        mMapView.setBuiltInZoomControls(false);
        super.onDestroy();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        Log.d(TAG, "onSaveInstanceState");
        super.onSaveInstanceState(outState);
        outState.putSerializable("map_center", mMapCenter);
        outState.putInt("map_zoom", mMapZoom);
    }

    private void restoreMapState() {
        if (mMapView != null) {
            Log.d(TAG, "restore map, center: " + mMapCenter.toString() + ", zoom: " + Integer.toString(mMapZoom));
            mMapView.getController().setCenter(mMapCenter);
            mMapView.getController().setZoom(mMapZoom);
        }
    }
}
