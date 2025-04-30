package com.example.reminder.reminder_mini.BackgroundWorks.TaskWork;

import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.reminder.mini.SqLite.CommonDB.CommonDB;
import com.reminder.mini.SqLite.Tasks.TaskConstants;
import com.reminder.mini.SqLite.Tasks.TasksDB;
import com.reminder.mini.UserInterfaces.AddTaskPage.AddTask;
import com.reminder.mini.UserInterfaces.ReschedulePage.ReSchedulePage;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;


public class SystemRescheduleTaskAfterAlarmTrigger extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("TAG", "onReceive: **SYSTEM RESCHEDULE TRIGGERED**");

        Log.d("TAG", "onReceive: **ACTION = " + intent.getAction() + "**");

        if (intent.getAction()!= null) {

            //if (
            //        intent.getAction().equals("APP_STARTED") ||
            //        intent.getAction().equals("android.intent.action.BOOT_COMPLETED") ||
            //        intent.getAction().equals("android.intent.action.REBOOT") ||
            //        intent.getAction().equals("android.intent.action.ACTION_POWER_DISCONNECTED") ||
            //        intent.getAction().equals("android.intent.action.ACTION_POWER_CONNECTED") ||
            //        intent.getAction().equals("android.app.action.SCHEDULE_EXACT_ALARM_PERMISSION_STATE_CHANGED")
            //)
            //{

                Log.d("TAG", "onReceive: **BOOT_COMPLETED**");

                rescheduleAllTask(context);

                context.sendBroadcast(new Intent(context, RescheduleTaskAfterAlarmTrigger.class));

            //}





        }




    }



    private void rescheduleAllTask(Context context) {
        CommonDB commonDB = new CommonDB(context);

        SQLiteDatabase database = commonDB.getReadableDatabase();

        Cursor cursor = database.rawQuery(
                " SELECT " +
                        TaskConstants.TASK_ID + ", " +  // 0
                        TaskConstants.ALARM_DATE + ", " +  // 1
                        TaskConstants.DATE_ARRAY + ", " +  // 2
                        TaskConstants.REPEAT_STATUS +  // 3
                        " FROM " + TaskConstants.TASK_TABLE_NAME

                , null);

        Log.d("TAG", "rescheduleAllTask: **RESCHEDULING STARTED**");

        if (cursor.getCount() > 0) {
            ArrayList<ContentValues> taskData = new ArrayList<>();

            ArrayList<Integer> dateArrayList;
            ContentValues values;
            int i = 0;

            cursor.moveToFirst();

            do {

                long id = cursor.getLong(0);
                long alarmDate = cursor.getLong(1);
                int repeatStatus = cursor.getInt(3);

                //Log.d("TAG", "rescheduleAllTask: " + cursor.getString(4) + " ___ " + repeatStatus);

                dateArrayList = new ArrayList<>();

                try {
                    JSONArray jsonArray = new JSONArray(cursor.getString(2));

                    for (; i < jsonArray.length(); i++) {
                        dateArrayList.add(jsonArray.getInt(i));
                    }

                    i = 0;
                }
                catch (JSONException ignored) {
                }


                long repeatingAlarmDateAndTIme = AddTask.rescheduleDateAndTIme(dateArrayList, alarmDate, repeatStatus);

                if (repeatingAlarmDateAndTIme == ReSchedulePage.RESCHEDULING_NOT_REQUIRED) continue;

                values = new ContentValues();
                values.put(TaskConstants.TASK_ID, id);
                values.put(TaskConstants.REPEATING_ALARM_DATE, repeatingAlarmDateAndTIme);

                taskData.add(values);


            }

            while (cursor.moveToNext());


            TasksDB.updateMultipleTask(context, taskData);


        }

        cursor.close();
        commonDB.close();
    }



}
