package com.example.reminder.reminder_mini.UserInterfaces.NotificationPage.TaskAlarm.NotificationTypes;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.preference.PreferenceManager;

import com.reminder.mini.R;
import com.reminder.mini.SqLite.Tasks.TaskConstants;
import com.reminder.mini.UserInterfaces.NotificationPage.NotificationConstants;
import com.reminder.mini.UserInterfaces.NotificationPage.TaskAlarm.BroadCasts.TaskAlertCancelBroadcast;
import com.reminder.mini.UserInterfaces.NotificationPage.TaskAlarm.LaterTask.LaterTask;
import com.reminder.mini.UserInterfaces.Ppp.Ppp;
import com.reminder.mini.UserInterfaces.TaskViewPage.TaskViewMain;


public class TaskNotificationGeneralClass {
    public static int NOTIFICATION_ID = 1;
    private final Context context;

    public TaskNotificationGeneralClass(Context context) {
        this.context = context;
    }

    public void taskAlert(String topic, long taskID, int locked) {

        if (ActivityCompat.checkSelfPermission(context, "android.permission.POST_NOTIFICATIONS") != PackageManager.PERMISSION_GRANTED) {
            return;
        }


        boolean shouldNotify = PreferenceManager.getDefaultSharedPreferences(context).getBoolean(context.getString(R.string.notifyForPrivate), true);
        int shouldNotifyInt = shouldNotify ? NotificationCompat.VISIBILITY_PUBLIC : NotificationCompat.VISIBILITY_SECRET;


        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context, NotificationConstants.TASK_NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(R.drawable.app_logo)
                .setContentTitle(locked == TaskConstants.PRIVATE_YES ? "Locked" : topic)
                .setContentIntent(getTaskIntent(context, locked, taskID))
                .setCategory(NotificationCompat.CATEGORY_CALL)
                .setTimeoutAfter(60000L)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setVisibility(shouldNotifyInt)
                .addAction(R.drawable.close_cross, "Close", getCancelIntent(context, taskID))
                .addAction(R.drawable.access_time, "Later", getLaterIntent(context, taskID))
                .setAutoCancel(true)
                .setFullScreenIntent(shouldNotify ? getNotificationIntent(context, locked, taskID, topic) : null, true);

        NotificationManagerCompat manager = NotificationManagerCompat.from(context);
        manager.notify(NOTIFICATION_ID, notificationBuilder.build());


    }


    private PendingIntent getNotificationIntent(Context context, int lockedStatus, long taskID, String topic) {

        Intent intent = new Intent(context, lockedStatus == TaskConstants.PRIVATE_YES ? PrivateNotificationIntent.class : NotificationIntent.class);
        intent.putExtra(TaskConstants.TASK_ID, String.valueOf(taskID));
        intent.putExtra(TaskConstants.TOPIC, topic);

        return PendingIntent.getActivity(context, ((int) taskID) + 3, intent, PendingIntent.FLAG_IMMUTABLE);


    }


    private PendingIntent getTaskIntent(Context context, int lockedStatus, long taskID) {

        Intent intent = new Intent(context, TaskViewMain.class);

        if (lockedStatus == TaskConstants.PRIVATE_YES) {
            intent = new Intent(context, Ppp.class);
            intent.putExtra(Ppp.FOR_PAGE, Ppp.TASK_MAIN_PAGE);
        }
        intent.putExtra(TaskConstants.TASK_ID, String.valueOf(taskID));
        return PendingIntent.getActivity(context, ((int) taskID) + 2, intent, PendingIntent.FLAG_IMMUTABLE);


    }


    private PendingIntent getCancelIntent(Context context, long taskID) {

        Intent cancelIntent = new Intent(context, TaskAlertCancelBroadcast.class);
        return PendingIntent.getBroadcast(context, (int) taskID, cancelIntent, PendingIntent.FLAG_IMMUTABLE);


    }


    private PendingIntent getLaterIntent(Context context, long taskID) {

        Intent laterIntent = new Intent(context, LaterTask.class);
        laterIntent.putExtra(TaskConstants.TASK_ID, String.valueOf(taskID));
        return PendingIntent.getActivity(context, ((int) taskID) + 1, laterIntent, PendingIntent.FLAG_IMMUTABLE);


    }


}
