package com.thymikee.nautictracker;

import android.content.Context;

import java.util.List;

import nautictracker.TrackPoint;
import nautictracker.TrackPointDao;

/**
 * Created by thymikee on 10.12.14.
 */
public class TrackPointRepository {

    public static void insertOrUpdate(Context context, TrackPoint trackPoint) {
        getTrackPointDao(context).insertOrReplace(trackPoint);
    }

    public static void deleteAll(Context context) {
        getTrackPointDao(context).deleteAll();
    }

    public static void deleteWithId(Context context, long id) {
        getTrackPointDao(context).delete(getById(context, id));
    }

    public static List<TrackPoint> getAll(Context context) {
        return getTrackPointDao(context).loadAll();
    }

    public static TrackPoint getById(Context context, long id) {
        return getTrackPointDao(context).load(id);
    }

    private static TrackPointDao getTrackPointDao(Context c) {
        return ((NauticTrackerApplication) c.getApplicationContext()).getDaoSession().getTrackPointDao();

    }
}