package com.aiosleeve.aiosleeve;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;

import androidx.core.app.NotificationCompat;

public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent == null) {
            return;
        }
        if (intent.getAction() != null) {
            if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
                AlarmHelper.setMorningAndEveningAlarm(context);
            }
        }
        String title="";
        String messageBody="";

        if(intent.hasExtra(AlarmHelper.ALARM_CODE_KEY)){
            int keyInt=intent.getExtras().getInt(AlarmHelper.ALARM_CODE_KEY,AlarmHelper.REQUEST_CODE_ALARM_1);
            if(keyInt==AlarmHelper.REQUEST_CODE_ALARM_1){
                title = "Morning Readiness";
                messageBody = "Don't forgot to record your morning HRV!";
            }else if(keyInt==AlarmHelper.REQUEST_CODE_ALARM_2){
                title = "Evening Stress Level";
                messageBody = "Don't forgot to record your evening HRV!";
            }
        }else {
            title = "Morning Readiness";
            messageBody = "Don't forgot to record your morning HRV!";
        }

        sendNotification(context, title, messageBody);
    }

    private void sendNotification(Context context, String title, String messageBody) {
        Intent intent = new Intent(context, SplashActivity.class);
        intent.setAction("FOO_ACTION");
//        intent.putExtra("notification_id", notification_id);


        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_ONE_SHOT);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context, context.getResources().getString(R.string.notification_channel_id))
                .setContentTitle(title)
                .setContentText(messageBody)
                .setSmallIcon(R.drawable.ic_launcher)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(messageBody))//
                .setTicker(context.getResources().getString(R.string.app_name))
                .setWhen(System.currentTimeMillis())
                .setAutoCancel(true)
                .setDefaults(NotificationCompat.DEFAULT_LIGHTS)
                .setContentIntent(pendingIntent);


        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            /* Create or update. */
            NotificationChannel channel = new NotificationChannel(context.getResources().getString(R.string.notification_channel_id), "OC Holiday", NotificationManager.IMPORTANCE_HIGH);

            channel.enableLights(true);
            channel.setLightColor(Color.BLUE);
            channel.enableLights(true);
            channel.setLightColor(Color.BLUE);
            notificationBuilder.setChannelId(context.getResources().getString(R.string.notification_channel_id));
            notificationManager.createNotificationChannel(channel);
        }
        notificationManager.notify((int) System.currentTimeMillis() / 1000, notificationBuilder.build());
    }
}