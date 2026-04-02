package com.aiosleeve.aiosleeve;

import android.app.Application;
import android.content.Context;

import androidx.multidex.MultiDex;

/**
 * Created by oneclick on 10/4/17.
 */

public class AIOApplication extends Application {

    private static int activityVisible = 0;
    private static boolean activityDestroy = true;

    @Override
    public void onCreate() {
        super.onCreate();
//        Fabric.with(this, new Crashlytics());
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    public static boolean isActivityDestroy() {
        return activityDestroy;
    }

    public static void activityOpen() {
        activityDestroy = false;
    }

    public static void activityDestroyed() {
        activityDestroy = true;
    }

    public static int isActivityVisible() {
        return activityVisible;
    }

    public static void activityResumed() {
        ++activityVisible;
    }

    public static void activityPaused() {
        --activityVisible;
    }
}