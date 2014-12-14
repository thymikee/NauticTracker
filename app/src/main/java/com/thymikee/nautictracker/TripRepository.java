package com.thymikee.nautictracker;

import android.content.Context;

import java.util.List;

import nautictracker.Trip;
import nautictracker.TripDao;

/**
 * Created by thymikee on 03.12.14.
 */
public class TripRepository {
    public static void insertOrUpdate(Context context, Trip trip) {
        getTripDao(context).insertOrReplace(trip);
    }

    public static void deleteAll(Context context) {
        getTripDao(context).deleteAll();
    }

    public static void deleteWithId(Context context, long id) {
        getTripDao(context).delete(getById(context, id));
    }

    public static List<Trip> getAllTrips(Context context) {
        return getTripDao(context).loadAll();
    }

    public static Trip getById(Context context, long id) {
        return getTripDao(context).load(id);
    }

//    public static Trip getByTitle(Context context, String title) {
//        return getTripDao(context).queryBuilder().where(TripDao.Properties.Title.eq(title)).unique();
//    }

//    public static long getLatestId(Context context) {
//        return getTripDao(context).queryBuilder().where(TripDao.Properties.Id.isNotNull()).count();
//    }

    private static TripDao getTripDao(Context c) {
        return ((NauticTrackerApplication) c.getApplicationContext()).getDaoSession().getTripDao();

    }
}