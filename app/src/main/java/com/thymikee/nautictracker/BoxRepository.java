package com.thymikee.nautictracker;

import android.content.Context;

import java.util.List;

import nautictracker.Box;
import nautictracker.BoxDao;

/**
 * Created by thymikee on 03.12.14.
 */
public class BoxRepository {
    public static void insertOrUpdate(Context context, Box box) {
        getBoxDao(context).insertOrReplace(box);
    }

    public static void clearBoxes(Context context) {
        getBoxDao(context).deleteAll();
    }

    public static void deleteBoxWithId(Context context, long id) {
        getBoxDao(context).delete(getBoxForId(context, id));
    }

    public static List<Box> getAllBoxes(Context context) {
        return getBoxDao(context).loadAll();
    }

    public static Box getBoxForId(Context context, long id) {
        return getBoxDao(context).load(id);
    }

    private static BoxDao getBoxDao(Context c) {
        return ((NauticTrackerApplication) c.getApplicationContext()).getDaoSession().getBoxDao();

    }
}