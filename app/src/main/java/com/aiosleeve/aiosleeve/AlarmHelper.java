package com.aiosleeve.aiosleeve;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AlarmHelper {

    public static final int REQUEST_CODE_ALARM_1 = 1;
    public static final int REQUEST_CODE_ALARM_2 = 2;
    public static final String ALARM_CODE_KEY = "ALARM_CODE_KEY";
    public static final String ACTION_ALARM_RECEIVER = "com.aiosleeve.aiosleeve.NOTIFY_ACTION";


    //This method is specific to project
    public static boolean areAlarmsScheduled(Context context) {
        Intent notificationIntent = new Intent(context,AlarmReceiver.class);
        notificationIntent.setAction(ACTION_ALARM_RECEIVER);
        return PendingIntent.getBroadcast(context, REQUEST_CODE_ALARM_1, notificationIntent, PendingIntent.FLAG_NO_CREATE) != null
                && PendingIntent.getBroadcast(context, REQUEST_CODE_ALARM_2, notificationIntent, PendingIntent.FLAG_NO_CREATE) != null;
    }


    public static void setMorningAndEveningAlarm(Context context) {
        enableBrodcastReceiver(context);

        Date morningTime = getTimeFromString("08:00:00");
        Date eveningTime = getTimeFromString("21:00:00");

        setAlarm(context, morningTime, REQUEST_CODE_ALARM_1, AlarmManager.INTERVAL_DAY);
        setAlarm(context,eveningTime,REQUEST_CODE_ALARM_2,AlarmManager.INTERVAL_DAY);


    }



    //This method is specific to project
    public static void cancelMorningAndEveningAlarm(Context context) {
        cancelAlarm(context, REQUEST_CODE_ALARM_1);
        cancelAlarm(context,REQUEST_CODE_ALARM_2);

        disableBrodcastReceiver(context);
    }

    private static void enableBrodcastReceiver(Context context) {
        ComponentName receiver = new ComponentName(context, AlarmReceiver.class);
        PackageManager pm = context.getPackageManager();

        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP);
    }

    private static void disableBrodcastReceiver(Context context) {
        ComponentName receiver = new ComponentName(context, AlarmReceiver.class);
        PackageManager pm = context.getPackageManager();

        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                PackageManager.DONT_KILL_APP);
    }


    private static void setAlarm(Context context, Date date, int requestCode, long repeatInterval) {
        Intent myIntent = new Intent(context,
                AlarmReceiver.class);
        myIntent.putExtra(ALARM_CODE_KEY, requestCode);
        myIntent.setAction(ACTION_ALARM_RECEIVER);//////////////////////

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context, requestCode, myIntent, PendingIntent.FLAG_UPDATE_CURRENT);


        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        if (date != null) {
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, date.getTime(), repeatInterval,
                    pendingIntent);
        }

    }

    private static void cancelAlarm(Context context, int requestCode) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent myIntent = new Intent(context,
                AlarmReceiver.class);
        myIntent.setAction(ACTION_ALARM_RECEIVER);//////////////////////
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context, requestCode, myIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        alarmManager.cancel(pendingIntent);
        pendingIntent.cancel();
    }


    private static Date getTimeFromString(String dateInString) {
        Date date = null;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss", Locale.US);
            date = sdf.parse(dateInString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }
}
