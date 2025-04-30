package com.example.reminder.reminder_mini.UserInterfaces.NotificationPage.TaskAlarm.BroadCasts;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.os.Build;
import android.util.Log;

import androidx.preference.PreferenceManager;

import com.reminder.mini.BackgroundWorks.TaskWork.RescheduleTaskAfterAlarmTrigger;
import com.reminder.mini.R;
import com.reminder.mini.SqLite.Tasks.TaskConstants;
import com.reminder.mini.UserInterfaces.NotificationPage.TaskAlarm.NotificationTypes.TaskNotificationGeneralClass;

public class TaskAlertBroadcast extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {

        String topic = intent.getStringExtra(TaskConstants.TOPIC);
        long taskID = intent.getLongExtra(TaskConstants.TASK_ID, 0L);
        int locked = intent.getIntExtra(TaskConstants.PRIVATE, TaskConstants.PRIVATE_NO);

        postNotification(context, topic, locked, taskID);
        rescheduleTask(context, taskID);


        Log.d("TAG", "onReceive: **ALARM TRIGGERED**");
        Log.d("TAG", "onReceive: **" + topic + "**");
        Log.d("TAG", "onReceive: **" + locked + "**");

        if (Build.VERSION.SDK_INT <= 11) {

        }



    }



    private void postNotification(Context context, String topic, int locked, long taskID) {

        TaskNotificationGeneralClass taskNotificationGeneralClass = new TaskNotificationGeneralClass(context);
        taskNotificationGeneralClass.taskAlert(topic, taskID, locked);

        if (PreferenceManager.getDefaultSharedPreferences(context).getBoolean(context.getString(R.string.turn_on_flash_for_task_alert), true)) {
            try {

                CameraManager cameraManager = (CameraManager) context.getSystemService(Context.CAMERA_SERVICE);
                cameraManager.setTorchMode(cameraManager.getCameraIdList()[0], true);

            } catch (CameraAccessException | RuntimeException ignored) {

            }
        }

    }




    private void rescheduleTask(Context context, long taskID) {

        RescheduleTaskAfterAlarmTrigger.rescheduleCurrentTask(context, taskID);
        context.sendBroadcast(new Intent(context, RescheduleTaskAfterAlarmTrigger.class));

    }




    private void playSound() {

    }



}
