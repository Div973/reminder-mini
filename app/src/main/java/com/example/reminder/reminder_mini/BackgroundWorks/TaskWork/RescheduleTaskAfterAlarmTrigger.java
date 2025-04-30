package com.example.reminder.reminder_mini.BackgroundWorks.TaskWork;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;

import com.reminder.mini.SqLite.CommonDB.CommonDB;
import com.reminder.mini.SqLite.Tasks.TaskConstants;
import com.reminder.mini.SqLite.Tasks.TaskData;
import com.reminder.mini.SqLite.Tasks.TasksDB;
import com.reminder.mini.UserInterfaces.NotificationPage.TaskAlarm.BroadCasts.TaskAlertBroadcast;
import com.reminder.mini.UserInterfaces.NotificationPage.TaskAlarm.NotificationTypes.NotificationIntent;
import com.reminder.mini.UserInterfaces.ReschedulePage.ReSchedulePage;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Calendar;





public class RescheduleTaskAfterAlarmTrigger extends BroadcastReceiver {







    @Override
    public void onReceive(Context context, Intent intent) {


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





            CommonDB commonDb = new CommonDB(context);
            SQLiteDatabase database = commonDb.getReadableDatabase();

            Cursor repeatingCursor = database.rawQuery(query, null);
            Cursor laterCursor = database.rawQuery(lateQuery, null);





            if (repeatingCursor.getCount() > 0 && laterCursor.getCount() > 0) {
                repeatingCursor.moveToFirst();
                laterCursor.moveToFirst();

                long repeatingAlarmDate = repeatingCursor.getLong(2);
                long laterAlarmDate = laterCursor.getLong(2);

                if (repeatingAlarmDate < laterAlarmDate) {
                    reschedule(context, repeatingCursor);
                }
                else {
                    reschedule(context, laterCursor);
                }

            }

            else if (repeatingCursor.getCount() > 0) {
                repeatingCursor.moveToFirst();
                reschedule(context, repeatingCursor);
            }

            else if (laterCursor.getCount() > 0) {
                laterCursor.moveToFirst();
                reschedule(context, laterCursor);
            }




            repeatingCursor.close();
            laterCursor.close();
            database.close();
            commonDb.close();

        }



        catch (SQLiteException e){
            Log.e("!! ERROR !!", e.getMessage() != null ? e.getMessage()  : "");
        }




    }





    private void reschedule(Context context, Cursor cursor) {

        long taskID = cursor.getLong(0);
        String topic = cursor.getString(1);
        long repeatingAlarmDate = cursor.getLong(2);
        int locked = cursor.getInt(3);
        int priority = cursor.getInt(4);

        Log.d("TAG", "doWork: ** REPEATING ALARM **");
        Log.d("TAG", "doWork: **" + topic + "**" );
        Log.d("TAG", "doWork: **" + taskID + "**" );
        Log.d("TAG", "doWork PRIVATE: **" + locked + "**" );

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(repeatingAlarmDate);


        Intent alarmIntent = new Intent(context, TaskAlertBroadcast.class)
                .putExtra(TaskConstants.TOPIC, topic)
                .putExtra(TaskConstants.TASK_ID, taskID)
                .putExtra(TaskConstants.PRIVATE, locked)
                .putExtra(TaskConstants.PRIORITY, priority);

        PendingIntent alarmPendingIntent = PendingIntent.getBroadcast(context, (int) taskID, alarmIntent, PendingIntent.FLAG_IMMUTABLE);

        PendingIntent alarmIntentActivity = PendingIntent.getActivity(context, (int) taskID, new Intent(context, NotificationIntent.class), PendingIntent.FLAG_IMMUTABLE);


        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        AlarmManager.AlarmClockInfo info = new AlarmManager.AlarmClockInfo(repeatingAlarmDate, alarmIntentActivity);
        alarmManager.setAlarmClock(info, alarmPendingIntent);


    }













    public static void rescheduleCurrentTask(Context context, long taskID) {
        CommonDB commonDB = new CommonDB(context);
        Cursor cursor = commonDB.getReadableDatabase().rawQuery(" SELECT " +
                TaskConstants.REPEAT_STATUS + ", " + // 0
                TaskConstants.ALARM_DATE + ", " + // 1
                TaskConstants.REPEATING_ALARM_DATE + ", " + // 2
                TaskConstants.DATE_ARRAY + // 3
                " FROM " + TaskConstants.TASK_TABLE_NAME +
                " WHERE " + TaskConstants.TASK_ID + " = " + taskID,null);

        cursor.moveToFirst();

        TaskData taskData = new TaskData();
        taskData.setRepeatStatus((byte) cursor.getInt(0));
        taskData.setAlarmDate(cursor.getLong(1));
        taskData.setRepeatingAlarmDate(cursor.getLong(2));
        taskData.setDateArray(cursor.getString(3));

        Calendar alarmDate = Calendar.getInstance();
        alarmDate.setTimeInMillis(taskData.getAlarmDate());



        if (taskData.getRepeatStatus() != TaskConstants.REPEAT_STATUS_NO_REPEAT){

            try {
                ArrayList<Integer> daysInWeek = new ArrayList<>();
                JSONArray dateArray = taskData.getDateArray();
                for (int i = 0; i < dateArray.length(); i++){
                    daysInWeek.add((Integer) dateArray.get(i));
                }
                long finalRepeatingAlarmDate = rescheduleDateAndTIme(
                        daysInWeek,
                        taskData.getAlarmDate(),
                        taskData.getRepeatStatus()
                );

                if (finalRepeatingAlarmDate != ReSchedulePage.RESCHEDULING_NOT_REQUIRED) {
                    ContentValues values = new ContentValues();
                    values.put(TaskConstants.REPEATING_ALARM_DATE, finalRepeatingAlarmDate);
                    TasksDB.updateTask(context, values, String.valueOf(taskID));
                }


                cursor.close();
                commonDB.close();
            }
            catch (JSONException ignored){

            }

        }



    }







    private static long rescheduleDateAndTIme(ArrayList<Integer> finalDaysInWeekArray, long givenAlarmDate, int finalRepeatStatus) {

        Calendar currentCal = Calendar.getInstance();
        int currentDayOfWeek = currentCal.get(Calendar.DAY_OF_WEEK);
        int currentDayOfMonth = currentCal.get(Calendar.DAY_OF_MONTH);

        Calendar alarm = Calendar.getInstance();
        alarm.setTimeInMillis(givenAlarmDate);


        Calendar finalCalendar = Calendar.getInstance();
        finalCalendar.set(Calendar.HOUR_OF_DAY, alarm.get(Calendar.HOUR_OF_DAY));
        finalCalendar.set(Calendar.MINUTE, alarm.get(Calendar.MINUTE));
        finalCalendar.set(Calendar.SECOND, 0);

        int dateGap;
        int diffDays;



        switch (finalRepeatStatus) {
            case TaskConstants.REPEAT_STATUS_FROM_AD /* FROM GD */:
                dateGap = ReSchedulePage.getDateGap(finalDaysInWeekArray, currentDayOfMonth, currentDayOfWeek);

                diffDays = dateGap - currentCal.getActualMaximum(Calendar.DAY_OF_MONTH);
                if (diffDays > 0){
                    finalCalendar.set(Calendar.DAY_OF_MONTH, diffDays);
                    int month = currentCal.get(Calendar.MONTH) + 1;
                    if (month > 11) {
                        finalCalendar.set(Calendar.YEAR, currentCal.get(Calendar.YEAR) + 1);
                        finalCalendar.set(Calendar.MONTH, 0);
                    }
                    else {
                        finalCalendar.set(Calendar.MONTH, month);
                    }

                }
                else {
                    finalCalendar.set(Calendar.DAY_OF_MONTH, dateGap);
                }
                break;
            case TaskConstants.REPEAT_STATUS_UP_TO_AD /* UP TO GD*/:
                if (finalCalendar.getTimeInMillis() > alarm.getTimeInMillis()) {
                    return ReSchedulePage.RESCHEDULING_NOT_REQUIRED;
                }

                dateGap = ReSchedulePage.getDateGap(finalDaysInWeekArray, currentDayOfMonth, currentDayOfWeek);

                diffDays = dateGap - currentCal.getActualMaximum(Calendar.DAY_OF_MONTH);
                if (diffDays > 0){
                    finalCalendar.set(Calendar.DAY_OF_MONTH, diffDays);
                    int month = currentCal.get(Calendar.MONTH) + 1;
                    if (month > 11) {
                        finalCalendar.set(Calendar.YEAR, currentCal.get(Calendar.YEAR) + 1);
                        finalCalendar.set(Calendar.MONTH, 0);
                    }
                    else {
                        finalCalendar.set(Calendar.MONTH, month);
                    }

                }
                else {
                    finalCalendar.set(Calendar.DAY_OF_MONTH, dateGap);
                }

                break;

            default:
                return  ReSchedulePage.RESCHEDULING_NOT_REQUIRED;
        }



        return finalCalendar.getTimeInMillis();
    }









}
