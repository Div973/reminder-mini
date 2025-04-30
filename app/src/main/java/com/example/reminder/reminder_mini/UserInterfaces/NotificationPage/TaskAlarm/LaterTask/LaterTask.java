package com.example.reminder.reminder_mini.UserInterfaces.NotificationPage.TaskAlarm.LaterTask;

import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayoutMediator;
import com.reminder.mini.BackgroundWorks.TaskWork.RescheduleTaskAfterAlarmTrigger;
import com.reminder.mini.Other.ApplicationCustomInterfaces;
import com.reminder.mini.R;
import com.reminder.mini.SqLite.Tasks.TaskConstants;
import com.reminder.mini.SqLite.Tasks.TasksDB;
import com.reminder.mini.UserInterfaces.NotificationPage.TaskAlarm.BroadCasts.TaskAlertCancelBroadcast;

import java.text.DecimalFormat;
import java.util.Calendar;

public class LaterTask extends AppCompatActivity implements ApplicationCustomInterfaces.DateTime2 {
    private final DecimalFormat format = new DecimalFormat("00");
    private ApplicationCustomInterfaces.EnableDateTimePicker datePickerContext;
    private ViewPager2 dateTimeView;
    private TextView setDate;
    private TextView setTime;
    private ApplicationCustomInterfaces.EnableDateTimePicker timePickerContext;
    private Calendar finalCalendar = Calendar.getInstance();


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.later_task);
        sendBroadcast(new Intent(this, TaskAlertCancelBroadcast.class));
        declare();
        initiate();
    }


    private void declare() {
        this.finalCalendar.setTimeInMillis(this.finalCalendar.getTimeInMillis() + 600000);
        this.setDate = findViewById(R.id.setDate);
        this.setTime = findViewById(R.id.setTime);
        findViewById(R.id.cancel).setOnClickListener(v -> finish());

        findViewById(R.id.confirm).setOnClickListener(v -> reSchedule());
        this.dateTimeView = findViewById(R.id.dateTimePicker);

        ((RadioGroup) findViewById(R.id.timeOption)).setOnCheckedChangeListener(this::optionChanged);


    }


    public void optionChanged(RadioGroup group, int checkedId) {
        if (checkedId == R.id.custom) {
            if (this.datePickerContext != null) {
                this.datePickerContext.enable(true, 0L);
            }
            if (this.timePickerContext != null) {
                this.timePickerContext.enable(true, 0L);
                return;
            }
            return;
        }
        this.finalCalendar = Calendar.getInstance();
        if (checkedId == R.id.min_10) {
            this.finalCalendar.setTimeInMillis(this.finalCalendar.getTimeInMillis() + 600000);
        } else if (checkedId == R.id.min_20) {
            this.finalCalendar.setTimeInMillis(this.finalCalendar.getTimeInMillis() + 1200000);
        } else {
            this.finalCalendar.setTimeInMillis(this.finalCalendar.getTimeInMillis() + 1800000);
        }
        if (this.datePickerContext != null) {
            this.datePickerContext.enable(false, this.finalCalendar.getTimeInMillis());
        }
        if (this.timePickerContext != null) {
            this.timePickerContext.enable(false, this.finalCalendar.getTimeInMillis());
        }
    }


    private void initiate() {
        setDateAndTime();
        this.dateTimeView.setAdapter(new DateTimePickerAdapter(this));
        new TabLayoutMediator(findViewById(R.id.dateTimeTab), this.dateTimeView, (tab, position) -> tab.setIcon(position == 0 ? R.drawable.access_time : R.drawable.calendar_month)).attach();
    }


    @Override
    public void setTime(int[] timeArr, byte amPm, ApplicationCustomInterfaces.EnableDateTimePicker context) {
        if (context != null) {
            this.timePickerContext = context;
            this.timePickerContext.enable(false, 0L);
            return;
        }
        int hour = timeArr[0];
        this.finalCalendar.set(11, hour);
        this.finalCalendar.set(12, timeArr[1]);
        this.setTime.setText(new StringBuilder(this.finalCalendar.get(10) + ":" + this.format.format(timeArr[1]) + " " + (amPm == 0 ? "am" : "pm")));
    }


    @Override
    public void setDate(int[] dateArr, ApplicationCustomInterfaces.EnableDateTimePicker context) {
        if (context != null) {
            this.datePickerContext = context;
            this.datePickerContext.enable(false, 0L);
            return;
        }
        TextView textView = this.setDate;
        StringBuilder append = new StringBuilder().append(format.format(dateArr[2])).append("/");
        int i = dateArr[1] + 1;
        dateArr[1] = i;
        textView.setText(new StringBuilder(append.append(format.format(i)).append("/").append(format.format(dateArr[0])).toString()));
        this.finalCalendar.set(1, dateArr[0]);
        this.finalCalendar.set(2, dateArr[1]);
        this.finalCalendar.set(5, dateArr[2]);
    }


    private void setDateAndTime() {
        this.setTime.setText(new StringBuilder((finalCalendar.get(10) == 0 ? 12 : finalCalendar.get(10)) + ":" + format.format(finalCalendar.get(12)) + " " + (finalCalendar.get(9) == Calendar.AM ? "am" : "pm")));
        this.setDate.setText(new StringBuilder(format.format(finalCalendar.get(5)) + "/" + format.format(finalCalendar.get(2) + 1) + "/" + format.format(finalCalendar.get(1))));
    }


    private void reSchedule() {

        finalCalendar.set(Calendar.SECOND, 0);
        finalCalendar.set(Calendar.MILLISECOND, 0);
        if (finalCalendar.getTimeInMillis() <= Calendar.getInstance().getTimeInMillis()) {
            Toast.makeText(this, "Please select a valid time!", Toast.LENGTH_SHORT).show();
            return;
        }
        ContentValues values = new ContentValues();
        values.put(TaskConstants.LATER_ALARM_DATE, finalCalendar.getTimeInMillis());
        String taskID = getIntent().getStringExtra(TaskConstants.TASK_ID);
        if (taskID == null) {
            taskID = String.valueOf(getIntent().getLongExtra(TaskConstants.TASK_ID, 0L));
        }
        TasksDB.updateTask(this, values, taskID);
        sendBroadcast(new Intent(this, RescheduleTaskAfterAlarmTrigger.class));
        finish();
    }


}
