package com.example.reminder.reminder_mini.BackgroundWorks;

import static com.reminder.mini.UserInterfaces.NotificationPage.NotificationConstants.TASK_NOTIFICATION_CHANNEL_ID;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.media.AudioAttributes;
import android.net.Uri;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.preference.PreferenceManager;

import com.reminder.mini.R;

public class CheckNotificationService extends Service {


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }




    @Override
    public void onCreate() {
        super.onCreate();



        new Thread(() -> {


            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
            String appStart = getString(R.string.application_started_for_the_first_time);

            if (!sharedPreferences.getBoolean(appStart, false)) {

                Log.d("TAG", "onCreate: **CREATING CHANNELS**");

                Uri taskAlertURI;
                NotificationChannel notificationChannel;
                NotificationManager compat;

                AudioAttributes audioAttributes = new AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build();

                //--->
                taskAlertURI = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.task_alert_ringtone_2);

                notificationChannel = new NotificationChannel(TASK_NOTIFICATION_CHANNEL_ID, "Task alert", NotificationManager.IMPORTANCE_HIGH);
                notificationChannel.setDescription("Alerts you for scheduled tasks");
                notificationChannel.setSound(taskAlertURI, new AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_ALARM).setContentType(AudioAttributes.CONTENT_TYPE_MUSIC).build());

                compat = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                compat.createNotificationChannel(notificationChannel);

                //--->

                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean(appStart, true);
                editor.apply();


                BatteryReceiver batteryReceiver = new BatteryReceiver();
                IntentFilter filter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
                registerReceiver(batteryReceiver, filter);


            }



        }).start();

    }



}
