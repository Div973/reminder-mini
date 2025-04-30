package com.example.reminder.reminder_mini.BackgroundWorks.TaskWork;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.provider.Settings;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.reminder.mini.SqLite.CommonDB.CommonDB;
import com.reminder.mini.SqLite.Tasks.TaskConstants;
import com.reminder.mini.UserInterfaces.NotificationPage.TaskAlarm.BroadCasts.TaskAlertBroadcast;
import com.reminder.mini.UserInterfaces.NotificationPage.TaskAlarm.BroadCasts.TaskAlertCancelBroadcast;

import java.util.Calendar;


public class AlarmWork {

    public static void triggerTaskAlarm(Context context){
        WorkRequest request = new OneTimeWorkRequest.Builder(TaskNotification.class).build();
        WorkManager.getInstance(context).enqueue(request);
    }






    public static class TaskNotification extends Worker{
        public TaskNotification(@NonNull Context context, @NonNull WorkerParameters workerParams) {
            super(context, workerParams);
        }




        @NonNull
        @Override
        public Result doWork() {

            try {
                String query = " SELECT " +
                        TaskConstants.TASK_ID  + ", "  +
                        TaskConstants.TOPIC  + ", "  +
                        TaskConstants.REPEATING_ALARM_DATE  + ", "  +
                        TaskConstants.PRIVATE + ", "  +
                        TaskConstants.PRIORITY +
                        " FROM " + TaskConstants.TASK_TABLE_NAME + " WHERE " + TaskConstants.REPEATING_ALARM_DATE + " > " + Calendar.getInstance().getTimeInMillis() + " ORDER BY " + TaskConstants.REPEATING_ALARM_DATE + " LIMIT 1";

                String lateQuery = " SELECT " +
                        TaskConstants.TASK_ID  + ", "  +
                        TaskConstants.TOPIC  + ", "  +
                        TaskConstants.LATER_ALARM_DATE  + ", "  +
                        TaskConstants.PRIVATE + ", "  +
                        TaskConstants.PRIORITY +
                        " FROM " + TaskConstants.TASK_TABLE_NAME + " WHERE " + TaskConstants.LATER_ALARM_DATE + " > " + Calendar.getInstance().getTimeInMillis() + " ORDER BY " + TaskConstants.LATER_ALARM_DATE + " LIMIT 1";


                CommonDB commonDb = new CommonDB(getApplicationContext());
                SQLiteDatabase database = commonDb.getReadableDatabase();
                Cursor repeatingCursor = database.rawQuery(query, null);
                Cursor laterCursor = database.rawQuery(lateQuery, null);



                if (repeatingCursor != null && repeatingCursor.getCount() != 0){
                    AlarmManager alarmManager = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);

                    repeatingCursor.moveToFirst();
                    long taskID = repeatingCursor.getLong(0);
                    String topic = repeatingCursor.getString(1);
                    long repeatingAlarmDate = repeatingCursor.getLong(2);
                    int locked = repeatingCursor.getInt(3);
                    int priority = repeatingCursor.getInt(4);

                    Log.d("TAG", "doWork: ** REPEATING ALARM **");
                    Log.d("TAG", "doWork: **" + topic + "**" );
                    Log.d("TAG", "doWork: **" + taskID + "**" );
                    Log.d("TAG", "doWork PRIVATE: **" + locked + "**" );


                    Calendar calendar = Calendar.getInstance();
                    calendar.setTimeInMillis(repeatingAlarmDate);


                    Intent alarmIntent = new Intent(getApplicationContext(), TaskAlertBroadcast.class)
                            .putExtra(TaskConstants.TOPIC, topic)
                            .putExtra(TaskConstants.TASK_ID, taskID)
                            .putExtra(TaskConstants.PRIVATE, locked)
                            .putExtra(TaskConstants.PRIORITY, priority);
                    alarmIntent.setAction(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS);

                    PendingIntent alarmPendingIntent = PendingIntent.getBroadcast(getApplicationContext(), (int) taskID, alarmIntent, PendingIntent.FLAG_IMMUTABLE);
                    alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, repeatingAlarmDate, alarmPendingIntent);

                    Intent cancelAlarmIntent = new Intent(getApplicationContext(), TaskAlertCancelBroadcast.class);
                    PendingIntent cancelAlarmPendingIntent = PendingIntent.getBroadcast(getApplicationContext(), (int) taskID, cancelAlarmIntent, PendingIntent.FLAG_IMMUTABLE);
                    alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, repeatingAlarmDate + 61000, cancelAlarmPendingIntent);

                    repeatingCursor.close();

                }



                if (laterCursor != null && laterCursor.getCount() != 0){
                    AlarmManager alarmManager = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
                    laterCursor.moveToFirst();

                    long taskID = laterCursor.getLong(0);
                    String topic = laterCursor.getString(1);
                    long laterAlarmDate = laterCursor.getLong(2);
                    int locked = laterCursor.getInt(3);
                    int priority = laterCursor.getInt(4);

                    Log.d("TAG", "doWork: ** LATER ALARM **");
                    Log.d("TAG", "doWork: **" + topic + "**" );
                    Log.d("TAG", "doWork: **" + taskID + "**" );
                    Log.d("TAG", "doWork PRIVATE: **" + locked + "**" );

                    Intent laterIntent = new Intent(getApplicationContext(), TaskAlertBroadcast.class)
                            .putExtra(TaskConstants.TOPIC, topic)
                            .putExtra(TaskConstants.TASK_ID, taskID)
                            .putExtra(TaskConstants.PRIVATE, locked)
                            .putExtra(TaskConstants.PRIORITY, priority);
                    laterIntent.setAction(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS);

                    PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), (int) taskID, laterIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                    alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, laterAlarmDate, pendingIntent);

                    Intent cancelAlarmIntent = new Intent(getApplicationContext(), TaskAlertCancelBroadcast.class);
                    PendingIntent cancelAlarmPendingIntent = PendingIntent.getBroadcast(getApplicationContext(), (int) taskID, cancelAlarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                    alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, laterAlarmDate + 61000, cancelAlarmPendingIntent);

                    laterCursor.close();

                }


                database.close();
                commonDb.close();

            }
            catch (SQLiteException e){
                e.printStackTrace();
            }



            return Result.success();
        }




    }



}



