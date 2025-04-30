package com.example.reminder.reminder_mini.SqLite.Tasks;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.reminder.mini.BackgroundWorks.TaskWork.RescheduleTaskAfterAlarmTrigger;
import com.reminder.mini.SqLite.CommonDB.CommonDB;

import java.util.ArrayList;

public class TasksDB {
    public static void insertTask(Context context, TaskData taskData) {
        ContentValues values = new ContentValues();

        values.put(TaskConstants.PRIVATE, taskData.getPrivateTask());
        values.put(TaskConstants.TOPIC, taskData.getTopic());
        values.put(TaskConstants.DESCRIPTION, taskData.getDescription());
        values.put(TaskConstants.ALARM_DATE, taskData.getAlarmDate());
        values.put(TaskConstants.REPEAT_STATUS, taskData.getRepeatStatus());
        values.put(TaskConstants.DATE_ARRAY, taskData.getDateArray().toString());
        values.put(TaskConstants.REPEATING_ALARM_DATE, taskData.getRepeatingAlarmDate());
        values.put(TaskConstants.LATER_ALARM_DATE, taskData.getLaterAlarmDate());
        values.put(TaskConstants.ALREADY_DONE, taskData.getAlreadyDone());
        values.put(TaskConstants.PINNED, taskData.getPinned());
        values.put(TaskConstants.PRIORITY, taskData.getPriority());
        values.put(TaskConstants.TASK_ID, taskData.getTaskId());
        values.put(TaskConstants.TASK_WEB_ID, taskData.getTaskWebId());

        CommonDB commonDB = new CommonDB(context);
        commonDB.getWritableDatabase().insert(TaskConstants.TASK_TABLE_NAME, null, values);
        commonDB.close();

        context.sendBroadcast(new Intent(context, RescheduleTaskAfterAlarmTrigger.class));
    }


    public static void insertTask(Context context, ContentValues values) {
        CommonDB commonDB = new CommonDB(context);
        commonDB.getWritableDatabase().insert(TaskConstants.TASK_TABLE_NAME, null, values);
        commonDB.close();
    }


    public static void updateTask(Context context, ContentValues values, String taskID) {
        Log.d("TAG", "updateTask: **UPDATING**");
        CommonDB commonDB = new CommonDB(context);
        Log.d("TAG", "updateTask: " + commonDB.getWritableDatabase().update(TaskConstants.TASK_TABLE_NAME, values, TaskConstants.TASK_ID + "=?", new String[]{taskID}));
        commonDB.close();

        context.sendBroadcast(new Intent(context, RescheduleTaskAfterAlarmTrigger.class));
    }


    public static void updateTask(Context context, ContentValues values, long taskID) {
        Log.d("TAG", "updateTask: **UPDATING**");
        CommonDB commonDB = new CommonDB(context);
        Log.d("TAG", "updateTask: " + commonDB.getWritableDatabase().update(TaskConstants.TASK_TABLE_NAME, values, TaskConstants.TASK_ID + "=" + taskID, null));
        commonDB.close();
    }


    public static void updateTask(Context context, ContentValues values, int taskID) {
        CommonDB commonDB = new CommonDB(context);
        commonDB.getWritableDatabase().update(TaskConstants.TASK_TABLE_NAME, values, TaskConstants.TASK_ID + "=?", new String[]{(String.valueOf(taskID))});
        commonDB.close();
    }


    public static void deleteTask(Context context, String taskID) {
        CommonDB commonDB = new CommonDB(context);
        commonDB.getWritableDatabase().delete(TaskConstants.TASK_TABLE_NAME, TaskConstants.TASK_ID + "=?", new String[]{taskID});
        commonDB.close();

        context.sendBroadcast(new Intent(context, RescheduleTaskAfterAlarmTrigger.class));
    }

    public static void deleteMultipleTask(Context context, ArrayList<Long> taskIDs) {
        CommonDB commonDB = new CommonDB(context);

        SQLiteDatabase db = commonDB.getWritableDatabase();
        db.beginTransaction();

        for (long taskID: taskIDs) {
            db.delete(TaskConstants.TASK_TABLE_NAME, TaskConstants.TASK_ID + "=?", new String[]{taskID+""});
        }

        db.setTransactionSuccessful();
        db.endTransaction();
        db.close();
        commonDB.close();

        context.sendBroadcast(new Intent(context, RescheduleTaskAfterAlarmTrigger.class));
    }


    public static void insertMultipleTask(Context context, ArrayList<TaskData> data) {

        CommonDB commonDB = new CommonDB(context);

        SQLiteDatabase db = commonDB.getWritableDatabase();
        db.beginTransaction();

        for (TaskData data1 : data) {
            ContentValues values = new ContentValues();

            values.put(TaskConstants.PRIVATE, data1.getPrivateTask());
            values.put(TaskConstants.TOPIC, data1.getTopic());
            values.put(TaskConstants.DESCRIPTION, data1.getDescription());
            values.put(TaskConstants.ALARM_DATE, data1.getAlarmDate());
            values.put(TaskConstants.REPEAT_STATUS, data1.getRepeatStatus());
            values.put(TaskConstants.DATE_ARRAY, data1.getDateArray().toString());
            values.put(TaskConstants.REPEATING_ALARM_DATE, data1.getRepeatingAlarmDate());
            values.put(TaskConstants.LATER_ALARM_DATE, data1.getLaterAlarmDate());
            values.put(TaskConstants.ALREADY_DONE, data1.getAlreadyDone());
            values.put(TaskConstants.PINNED, data1.getPinned());
            values.put(TaskConstants.PRIORITY, data1.getPriority());
            values.put(TaskConstants.TASK_ID, data1.getTaskId());
            values.put(TaskConstants.TASK_WEB_ID, data1.getTaskWebId());

            db.insert(TaskConstants.TASK_TABLE_NAME, null, values);
        }

        db.setTransactionSuccessful();
        db.endTransaction();
        db.close();
        commonDB.close();

        context.sendBroadcast(new Intent(context, RescheduleTaskAfterAlarmTrigger.class));

    }


    public static void updateSingleTask(Context context, ContentValues taskData, int id) {

        CommonDB commonDB = new CommonDB(context);

        Log.d("TAG", "updateSingleTask: **updating**: " + id);

        SQLiteDatabase db = commonDB.getWritableDatabase();

        db.update(TaskConstants.TASK_TABLE_NAME, taskData, TaskConstants.ID + "=?", new String[]{String.valueOf(id)});
        commonDB.close();

    }


    public static void updateMultipleTask(Context context, ArrayList<ContentValues> taskDataList) {

        CommonDB commonDB = new CommonDB(context);

        SQLiteDatabase db = commonDB.getWritableDatabase();
        db.beginTransaction();

        for (ContentValues values : taskDataList) {

            db.update(TaskConstants.TASK_TABLE_NAME, values, TaskConstants.TASK_ID + "=?", new String[]{values.getAsString(TaskConstants.TASK_ID)});

        }

        db.setTransactionSuccessful();
        db.endTransaction();
        db.close();
        commonDB.close();

    }


}
