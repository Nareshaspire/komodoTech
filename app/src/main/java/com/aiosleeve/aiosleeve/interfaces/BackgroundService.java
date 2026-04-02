package com.aiosleeve.aiosleeve.interfaces;

import android.app.Service;
import android.content.ContentValues;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;


import com.aiosleeve.aiosleeve.MainActivity;

/**
 * Created by oneclick-android on 10/1/18.
 */

public class BackgroundService extends Service {
    String tablename;
    ContentValues contentValues;

    public BackgroundService() {
        super();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e("SERVICE","onCreate");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //return super.onStartCommand(intent, flags, startId);
        Intent intent1 = new Intent(MainActivity.CUSTOM_INTENT);
        intent1.putExtra("state", "connected");
        sendBroadcast(intent1);

        Log.e("SERVICE","onStartCommand");
        return Service.START_STICKY;
    }

    @Override
    public void onDestroy() {
        Log.e("SERVICE","ON DESTROY STOP");
        Intent intent = new Intent(MainActivity.CUSTOM_INTENT);
        intent.putExtra("state", "destroyed");
        sendBroadcast(intent);

        super.onDestroy();

    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        Log.e("SERVICE","onTaskRemoved");
        Intent intent = new Intent(MainActivity.CUSTOM_INTENT);
        intent.putExtra("state", "removed");
        sendBroadcast(intent);

        super.onTaskRemoved(rootIntent);

    }
}
