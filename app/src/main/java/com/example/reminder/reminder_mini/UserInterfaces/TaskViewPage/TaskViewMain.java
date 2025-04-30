package com.example.reminder.reminder_mini.UserInterfaces.TaskViewPage;

import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.appbar.MaterialToolbar;
import com.reminder.mini.BackgroundWorks.TaskWork.RescheduleTaskAfterAlarmTrigger;
import com.reminder.mini.Other.ApplicationCustomInterfaces;
import com.reminder.mini.R;
import com.reminder.mini.SqLite.CommonDB.CommonDB;
import com.reminder.mini.SqLite.Tasks.TaskConstants;
import com.reminder.mini.SqLite.Tasks.TaskData;
import com.reminder.mini.SqLite.Tasks.TasksDB;
import com.reminder.mini.UserInterfaces.HomePage.MainActivity.MainActivity;
import com.reminder.mini.UserInterfaces.NotificationPage.TaskAlarm.BroadCasts.TaskAlertBroadcast;
import com.reminder.mini.UserInterfaces.ReschedulePage.ReSchedulePage;

import org.json.JSONArray;
import org.json.JSONException;

import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Objects;


public class TaskViewMain extends AppCompatActivity implements ApplicationCustomInterfaces.ManipulateTask {
    private String[] monthArray;
    private TaskData taskData;
    int setText = R.string.setText;
    private MaterialToolbar toolbar;
    private MenuItem menuItemPin, menuItemDone, menuItemLock;
    private final ApplicationCustomInterfaces.ManipulateTask manipulateTask = this;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.task_view);

        toolbar = findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);

        long taskID = getIntent().getLongExtra(TaskConstants.TASK_ID, 0);
        if (taskID == 0)
            taskID = Long.parseLong(Objects.requireNonNull(getIntent().getStringExtra(TaskConstants.TASK_ID)));

        taskData = getCurrentData(this, taskID);

        //Log.d("TAG", "onCreate: -------->>>>>>>>>>"  + taskData.getRepeatingAlarmDate());

        monthArray = getResources().getStringArray(R.array.monthsInYear);

        findViewById(R.id.seeMore).setOnClickListener(v -> MainActivity.expandHideView(v, findViewById(R.id.info)));

        seeDetails();


    }


    public void seeDetails() {

        toolbar.setTitle(getString(setText, taskData.getTopic()));

        if (taskData.getDescription() != null && !taskData.getDescription().trim().isEmpty())
            ((TextView) findViewById(R.id.setDescription)).setText(getString(setText, taskData.getDescription()));

        DecimalFormat decimalFormat = new DecimalFormat("00");
        Calendar calendar = Calendar.getInstance();
        String dateString;

        calendar.setTimeInMillis(taskData.getTaskId());


        dateString = getString(
                R.string.set_date_task_view_format,
                calendar.get(Calendar.DAY_OF_MONTH),
                monthArray[calendar.get(Calendar.MONTH)],
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.HOUR) == 0 ? 12 : calendar.get(Calendar.HOUR),
                decimalFormat.format(calendar.get(Calendar.MINUTE)),
                getResources().getStringArray(R.array.amPm)[calendar.get(Calendar.AM_PM)]
        );

        Log.d("TAG", "seeDetails: -------" + dateString);


        ((TextView) findViewById(R.id.setAddedOn)).setText(dateString);


        calendar.setTimeInMillis(taskData.getRepeatingAlarmDate());
        dateString = getString(
                R.string.set_date_task_view_format,
                calendar.get(Calendar.DAY_OF_MONTH),
                monthArray[calendar.get(Calendar.MONTH)],
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.HOUR) == 0 ? 12 : calendar.get(Calendar.HOUR),
                decimalFormat.format(calendar.get(Calendar.MINUTE)),
                getResources().getStringArray(R.array.amPm)[calendar.get(Calendar.AM_PM)]
        );
        if (calendar.getTimeInMillis() < Calendar.getInstance().getTimeInMillis()) {
            dateString = dateString + " (Expired)";
        }
        ((TextView) findViewById(R.id.setScheduledDate)).setText(dateString);


        LinearLayout layout = findViewById(R.id.setRepeatDays);
        if (taskData.getRepeatStatus() != TaskConstants.REPEAT_STATUS_NO_REPEAT) {
            ((TextView) findViewById(R.id.setRepeatStatus)).setText(getString(setText, getResources().getStringArray(R.array.repeatType)[taskData.getRepeatStatus() - 1]));
            try {
                JSONArray dateArray = taskData.getDateArray();
                Typeface typeface = getResources().getFont(R.font.sans_serif_medium);

                for (byte i = 0; i < dateArray.length(); i++) {
                    TextView days = (TextView) layout.getChildAt((int) dateArray.get(i) - 1);
                    days.setTypeface(typeface);
                    days.setEnabled(true);
                }
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }


    }


    public static TaskData getCurrentData(Context context, long taskID) {

        CommonDB commonDB = new CommonDB(context);

        Cursor cursor = commonDB.getReadableDatabase().rawQuery(
                " SELECT * FROM " + TaskConstants.TASK_TABLE_NAME +
                        " WHERE " + TaskConstants.TASK_ID + " == " + taskID,
                null
        );

        cursor.moveToFirst();

        /*

                PRIVATE + " BYTE NOT NULL, " +                 // 1
                TOPIC + " TEXT NOT NULL, " +                   // 2
                DESCRIPTION + " TEXT, " +                      // 3
                ALARM_DATE + " LONG NOT NULL, " +              // 4
                REPEAT_STATUS + " BYTE NOT NULL, " +           // 5
                DATE_ARRAY + " JSON, " +                       // 6
                REPEATING_ALARM_DATE + " LONG NOT NULL, " +    // 7
                LATER_ALARM_DATE + " LONG, " +                 // 8
                ALREADY_DONE + " BYTE NOT NULL, " +            // 9
                PINNED + " BYTE NOT NULL, " +                  // 10
                TASK_ID + " LONG NOT NULL UNIQUE, " +          // 11
                PRIORITY + " BYTE NOT NULL" +                  // 12


        */

        TaskData data = new TaskData();
        data.setPrivateTask((byte) cursor.getInt(1));
        data.setTopic(cursor.getString(2));
        data.setDescription(cursor.getString(3));
        data.setAlarmDate(cursor.getLong(4));
        data.setRepeatStatus((byte) cursor.getInt(5));
        data.setDateArray(cursor.getString(6));
        data.setRepeatingAlarmDate(cursor.getLong(7));
        data.setLaterAlarmDate(cursor.getLong(8));
        data.setAlreadyDone((byte) cursor.getInt(9));
        data.setPinned((byte) cursor.getInt(10));
        data.setTaskId(cursor.getLong(11));
        data.setPriority((byte) cursor.getInt(12));

        commonDB.close();
        cursor.close();

        return data;

    }
  

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edit_task_menu, menu);

        menuItemLock = menu.getItem(0);
        menuItemPin = menu.getItem(2);
        menuItemDone = menu.getItem(3);

        menuItemLock.setTitle(getString(taskData.getPrivateTask() == TaskConstants.PRIVATE_YES ? R.string.unlock : R.string.lock));
        menuItemPin.setTitle(getString(taskData.getPinned() == TaskConstants.PINNED_YES ? R.string.unpin : R.string.pin));
        menuItemDone.setTitle(getString(taskData.getAlreadyDone() == TaskConstants.ALREADY_DONE_YES ? R.string.done : R.string.notDone));

        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();


        if (id == R.id.menu_lock) manipulateTask.lockTask();
        else if (id == R.id.menu_reschedule) manipulateTask.reschedule();
        else if (id == R.id.menu_pin) manipulateTask.pinTask();
        else if (id == R.id.menu_mark_done) manipulateTask.markTaskDone();
        else if (id == R.id.menu_delete) manipulateTask.deleteTask();

        return super.onOptionsItemSelected(item);

    }





    @Override
    public void deleteTask() {
        TasksDB.deleteTask(this, String.valueOf(taskData.getTaskId()));
        finish();
    }

    @Override
    public void pinTask() {

        ContentValues contentValues = new ContentValues();

        if (taskData.getPinned() == TaskConstants.PINNED_YES) {
            taskData.setPinned(TaskConstants.PINNED_NO);
            menuItemPin.setTitle(getString(R.string.pin));
            contentValues.put(TaskConstants.PINNED, TaskConstants.PINNED_NO);
        }
        else {
            contentValues.put(TaskConstants.PINNED, TaskConstants.PINNED_YES);
            menuItemPin.setTitle(getString(R.string.unpin));
            taskData.setPinned(TaskConstants.PINNED_YES);
        }

        TasksDB.updateTask(this, contentValues, String.valueOf(taskData.getTaskId()));

    }

    @Override
    public void markTaskDone() {

        ContentValues values = new ContentValues();

        if (taskData.getAlreadyDone() == TaskConstants.ALREADY_DONE_YES) {
            values.put(TaskConstants.ALREADY_DONE, TaskConstants.ALREADY_DONE_NO);
            taskData.setAlreadyDone(TaskConstants.ALREADY_DONE_NO);
            menuItemDone.setTitle(getString(R.string.notDone));
        }
        else {
            values.put(TaskConstants.ALREADY_DONE, TaskConstants.ALREADY_DONE_YES);
            taskData.setAlreadyDone(TaskConstants.ALREADY_DONE_YES);
            menuItemDone.setTitle(getString(R.string.done));
        }

        TasksDB.updateTask(this, values, String.valueOf(taskData.getTaskId()));

    }

    @Override
    public void lockTask() {

        ContentValues contentValues = new ContentValues();

        if (taskData.getPrivateTask() == TaskConstants.PRIVATE_YES) {
            contentValues.put(TaskConstants.PRIVATE, TaskConstants.PRIVATE_NO);
            taskData.setPrivateTask(TaskConstants.PRIVATE_NO);
            menuItemLock.setTitle(getString(R.string.lock));

        }
        else {
            contentValues.put(TaskConstants.PRIVATE, TaskConstants.PRIVATE_YES);
            taskData.setPrivateTask(TaskConstants.PRIVATE_YES);
            menuItemLock.setTitle(getString(R.string.unlock));
        }

        TasksDB.updateTask(this, contentValues, String.valueOf(taskData.getTaskId()));

        Intent alarmIntent = new Intent(this, TaskAlertBroadcast.class);
        PendingIntent alarmPendingIntent = PendingIntent.getBroadcast(this, (int) taskData.getTaskId(), alarmIntent, PendingIntent.FLAG_IMMUTABLE);
        alarmPendingIntent.cancel();

        sendBroadcast(new Intent(this, RescheduleTaskAfterAlarmTrigger.class));

    }

    @Override
    public void reschedule(){
        startActivity(new Intent(this, ReSchedulePage.class).putExtra(TaskConstants.TASK_ID, taskData.getTaskId()));
    }

    @Override
    public void unPinTask() {

    }

    @Override
    public void markTaskUnDone() {

    }

    @Override
    public void unlockTask() {

    }


}