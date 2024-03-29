package com.thymikee.nautictracker;

import android.app.Application;
import android.database.sqlite.SQLiteDatabase;

import nautictracker.DaoMaster;
import nautictracker.DaoSession;

/**
 * Created by thymikee on 03.12.14.
 */
public class NauticTrackerApplication extends Application {
    public DaoSession daoSession;

    @Override
    public void onCreate() {
        super.onCreate();
        setupDatabase();
    }

    private void setupDatabase() {
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, "example-db", null);
        SQLiteDatabase db = helper.getWritableDatabase();
        DaoMaster daoMaster = new DaoMaster(db);
        daoSession = daoMaster.newSession();
    }

    public DaoSession getDaoSession() {
        return daoSession;
    }
}
