package com.example.reminder.reminder_mini.UserInterfaces.AddTaskPage;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.reminder.mini.Other.ApplicationCustomInterfaces;
import com.reminder.mini.R;
import com.reminder.mini.SqLite.Tasks.TaskConstants;
import com.reminder.mini.SqLite.Tasks.TaskData;
import com.reminder.mini.SqLite.Tasks.TasksDB;
import com.reminder.mini.UserInterfaces.Global.DatePickerClass;
import com.reminder.mini.UserInterfaces.Global.TimePickerClass;
import com.reminder.mini.UserInterfaces.HomePage.MainActivity.MainActivity;
import com.reminder.mini.UserInterfaces.ReschedulePage.ReSchedulePage;
import com.reminder.mini.UserInterfaces.ReschedulePage.RepeatStatusSpinnerClass;

import org.json.JSONArray;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;






public class AddTask extends AppCompatActivity implements
        ApplicationCustomInterfaces.DateTime,
        ApplicationCustomInterfaces.RepeatStatus {
    private final Calendar finalAlarmDate = Calendar.getInstance();
    private final ArrayList<Integer> finalDaysInWeek = new ArrayList<>();
    private final DecimalFormat format = new DecimalFormat("00");
    private LinearLayout daysInWeek;
    private EditText setHour, setMinute;
    private Button amBtn, pmBtn;
    private byte finalRepeatStatus = TaskConstants.REPEAT_STATUS_NO_REPEAT;
    private int AM_OR_PM;
    private LinearLayout repeatStatusView;
    private MaterialButton addDateBtn;

    public static long rescheduleDateAndTIme(ArrayList<Integer> finalDaysInWeekArray, long givenAlarmDate, int finalRepeatStatus) {

        Calendar currentCal = Calendar.getInstance();
        int currentDayOfWeek = currentCal.get(Calendar.DAY_OF_WEEK);
        int currentDayOfMonth = currentCal.get(Calendar.DAY_OF_MONTH);

        Calendar alarm = Calendar.getInstance();
        alarm.setTimeInMillis(givenAlarmDate);
        int alarmDayOfMonth = alarm.get(Calendar.DAY_OF_MONTH);
        int alarmDayOfWeek = alarm.get(Calendar.DAY_OF_WEEK);


        Calendar finalCalendar = Calendar.getInstance();
        finalCalendar.set(Calendar.HOUR_OF_DAY, alarm.get(Calendar.HOUR_OF_DAY));
        finalCalendar.set(Calendar.MINUTE, alarm.get(Calendar.MINUTE));
        finalCalendar.set(Calendar.SECOND, 0);

        int dateGap;
        int diffDays;


        switch (finalRepeatStatus) {
            case TaskConstants.REPEAT_STATUS_FROM_AD:
                if (currentCal.getTimeInMillis() < givenAlarmDate /*TIME NOT PASSED, FOLLOW ALARM CAL*/) {

                    if (finalDaysInWeekArray.contains(alarmDayOfWeek)) {
                        //dateGap = alarmDayOfMonth;
                        if (currentCal.get(Calendar.HOUR_OF_DAY) < alarm.get(Calendar.HOUR_OF_DAY)) {
                            dateGap = alarmDayOfMonth;
                        } else if (currentCal.get(Calendar.HOUR_OF_DAY) == alarm.get(Calendar.HOUR_OF_DAY)) {
                            if (currentCal.get(Calendar.MINUTE) < alarm.get(Calendar.MINUTE)) {
                                dateGap = alarmDayOfMonth;
                            } else {
                                dateGap = ReSchedulePage.getDateGap(finalDaysInWeekArray, alarmDayOfMonth, alarmDayOfWeek);
                            }
                        } else {
                            dateGap = ReSchedulePage.getDateGap(finalDaysInWeekArray, alarmDayOfMonth, alarmDayOfWeek);
                        }

                    } else {
                        dateGap = ReSchedulePage.getDateGap(finalDaysInWeekArray, alarmDayOfMonth, alarmDayOfWeek);
                    }


                    diffDays = dateGap - alarm.getActualMaximum(Calendar.DAY_OF_MONTH);
                    if (diffDays > 0) {
                        finalCalendar.set(Calendar.DAY_OF_MONTH, diffDays);
                        int month = alarm.get(Calendar.MONTH) + 1;
                        if (month > 11) {
                            finalCalendar.set(Calendar.YEAR, alarm.get(Calendar.YEAR) + 1);
                            finalCalendar.set(Calendar.MONTH, 0);
                        } else {
                            finalCalendar.set(Calendar.YEAR, alarm.get(Calendar.YEAR));
                            finalCalendar.set(Calendar.MONTH, alarm.get(Calendar.MONTH));
                        }

                    } else {
                        finalCalendar.set(Calendar.YEAR, alarm.get(Calendar.YEAR));
                        finalCalendar.set(Calendar.MONTH, alarm.get(Calendar.MONTH));
                        finalCalendar.set(Calendar.DAY_OF_MONTH, dateGap);
                    }
                } else {
                    if (finalDaysInWeekArray.contains(currentDayOfWeek)) {
                        //dateGap = currentDayOfMonth;
                        if (currentCal.get(Calendar.HOUR_OF_DAY) < alarm.get(Calendar.HOUR_OF_DAY)) {
                            dateGap = currentDayOfMonth;
                        } else if (currentCal.get(Calendar.HOUR_OF_DAY) == alarm.get(Calendar.HOUR_OF_DAY)) {
                            if (currentCal.get(Calendar.MINUTE) < alarm.get(Calendar.MINUTE)) {
                                dateGap = currentDayOfMonth;
                            } else {
                                dateGap = ReSchedulePage.getDateGap(finalDaysInWeekArray, currentDayOfMonth, currentDayOfWeek);
                            }
                        } else {
                            dateGap = ReSchedulePage.getDateGap(finalDaysInWeekArray, currentDayOfMonth, currentDayOfWeek);
                        }
                    } else {
                        dateGap = ReSchedulePage.getDateGap(finalDaysInWeekArray, currentDayOfMonth, currentDayOfWeek);
                    }

                    diffDays = dateGap - currentCal.getActualMaximum(Calendar.DAY_OF_MONTH);
                    if (diffDays > 0) {
                        finalCalendar.set(Calendar.DAY_OF_MONTH, diffDays);
                        int month = currentCal.get(Calendar.MONTH) + 1;
                        if (month > 11) {
                            finalCalendar.set(Calendar.YEAR, currentCal.get(Calendar.YEAR) + 1);
                            finalCalendar.set(Calendar.MONTH, 0);
                        } else {
                            finalCalendar.set(Calendar.MONTH, month);
                        }

                    } else {
                        finalCalendar.set(Calendar.DAY_OF_MONTH, dateGap);
                    }
                }

                break;
            case TaskConstants.REPEAT_STATUS_UP_TO_AD:
                if (currentCal.getTimeInMillis() < givenAlarmDate /*TIME NOT PASSED, FOLLOW ALARM CAL*/) {
                    if (finalDaysInWeekArray.contains(currentDayOfWeek)) {
                        Log.d("TAG", "rescheduleDateAndTime: contain true");
                        if (currentCal.get(Calendar.HOUR_OF_DAY) < alarm.get(Calendar.HOUR_OF_DAY)) {
                            dateGap = currentDayOfMonth;
                        } else if (currentCal.get(Calendar.HOUR_OF_DAY) == alarm.get(Calendar.HOUR_OF_DAY)) {
                            if (currentCal.get(Calendar.MINUTE) < alarm.get(Calendar.MINUTE)) {
                                dateGap = currentDayOfMonth;
                            } else {
                                dateGap = ReSchedulePage.getDateGap(finalDaysInWeekArray, currentDayOfMonth, currentDayOfWeek);
                            }
                        } else {
                            dateGap = ReSchedulePage.getDateGap(finalDaysInWeekArray, currentDayOfMonth, currentDayOfWeek);
                        }
                    } else {
                        dateGap = ReSchedulePage.getDateGap(finalDaysInWeekArray, currentDayOfMonth, currentDayOfWeek);
                    }

                    diffDays = dateGap - currentCal.getActualMaximum(Calendar.DAY_OF_MONTH);
                    if (diffDays > 0) {
                        finalCalendar.set(Calendar.DAY_OF_MONTH, diffDays);
                        int month = currentCal.get(Calendar.MONTH) + 1;
                        if (month > 11) {
                            finalCalendar.set(Calendar.YEAR, currentCal.get(Calendar.YEAR) + 1);
                            finalCalendar.set(Calendar.MONTH, 0);
                        } else {
                            finalCalendar.set(Calendar.MONTH, month);
                        }

                    } else {
                        finalCalendar.set(Calendar.DAY_OF_MONTH, dateGap);
                    }
                } else return ReSchedulePage.RESCHEDULING_NOT_REQUIRED;

                break;

            default /* REPEAT STATUS NO REPEAT */:
                return ReSchedulePage.RESCHEDULING_NOT_REQUIRED;
        }


        return finalCalendar.getTimeInMillis();
    }






    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.add_task_page);

        declare();
        initiate();

    }





    private void declare() {
        setHour = findViewById(R.id.setHour);
        setMinute = findViewById(R.id.setMinute);
        daysInWeek = findViewById(R.id.daysInWeek);

        amBtn = findViewById(R.id.setAM);
        pmBtn = findViewById(R.id.setPM);

        addDateBtn = findViewById(R.id.selectDateButton);


        repeatStatusView = findViewById(R.id.repeatStatusView);

    }





    private void initiate() {
        //SET DEFAULT ALARM TIME
        finalAlarmDate.setTimeInMillis(finalAlarmDate.getTimeInMillis() + 300000);

        setAmPm(finalAlarmDate.get(Calendar.AM_PM));
        AM_OR_PM = finalAlarmDate.get(Calendar.AM_PM);

        setHour.setText(format.format(finalAlarmDate.get(Calendar.HOUR) == 0 ? 12 : finalAlarmDate.get(Calendar.HOUR)));
        setMinute.setText(format.format(finalAlarmDate.get(Calendar.MINUTE)));

        daysInWeek.setVisibility(finalRepeatStatus == TaskConstants.REPEAT_STATUS_NO_REPEAT ? View.GONE : View.VISIBLE);
        new RepeatStatusSpinnerClass(this, finalRepeatStatus, findViewById(R.id.setRepeatType));


        addDateBtn.setText(
                new StringBuilder(finalAlarmDate.get(Calendar.DAY_OF_MONTH) + "-" + (finalAlarmDate.get(Calendar.MONTH) + 1) + "-" + finalAlarmDate.get(Calendar.YEAR))
        );

        addDateBtn.setOnClickListener(v -> {
            DatePickerClass datePickerClass = new DatePickerClass(this);
            datePickerClass.showCalendar();
        });


        findViewById(R.id.selectTimeButton).setOnClickListener(v -> {
            TimePickerClass timePickerClass = new TimePickerClass(getSupportFragmentManager(), this);
            timePickerClass.timeFunction();
        });


        findViewById(R.id.expandTimeView).setOnClickListener(v -> MainActivity.expandHideView(v, repeatStatusView));


        amBtn.setOnClickListener(v -> {
            setAmPm(Calendar.AM);
            AM_OR_PM = Calendar.AM;
        });
        pmBtn.setOnClickListener(v -> {
            setAmPm(Calendar.PM);
            AM_OR_PM = Calendar.PM;
        });


        for (int i = 0; i < 7; i++) {
            finalDaysInWeek.add(i + 1);
            Chip chip = (Chip) daysInWeek.getChildAt(i);
            int finalI = i;
            chip.setChecked(true);
            chip.setOnClickListener(v -> {
                if (finalDaysInWeek.contains(finalI + 1))
                    finalDaysInWeek.remove((Object) (finalI + 1));
                else finalDaysInWeek.add(finalI + 1);
            });
        }


        ((MaterialButton) findViewById(R.id.rescheduleAddTask)).setText(getString(R.string.add_task));
        findViewById(R.id.rescheduleAddTask).setOnClickListener(v -> {
            if (setTaskFunction()) finish();
        });


    }





    private void setAmPm(int amPm) {
        AM_OR_PM = amPm;
        if (amPm == Calendar.AM) {
            amBtn.setTextColor(getResources().getColor(R.color.blue_violet, null));

            pmBtn.setTextColor(getResources().getColor(R.color.grey, null));
        } else {
            pmBtn.setTextColor(getResources().getColor(R.color.blue_violet, null));

            amBtn.setTextColor(getResources().getColor(R.color.grey, null));
        }

    }





    @Override
    public void setTime(@NonNull int[] timeArr, byte amPm) {
        setAmPm(amPm);

        setHour.setText(
                format.format(
                        timeArr[0] > 12 ?
                                timeArr[0] - 12 :
                                timeArr[0] == 0 ? 12 : timeArr[0]
                )
        );

        setMinute.setText(format.format(timeArr[1]));
    }





    @Override
    public void setDate(@NonNull int[] setDate) {
        Log.d("TAG", "setDate: " + setDate[0]);
        finalAlarmDate.set(Calendar.YEAR, setDate[0]);
        finalAlarmDate.set(Calendar.MONTH, setDate[1]);
        finalAlarmDate.set(Calendar.DAY_OF_MONTH, setDate[2]);
        addDateBtn.setText(new StringBuilder(setDate[2] + "-" + (setDate[1] + 1) + "-" + setDate[0]));
    }







    @Override
    public void setRepeatStatus(byte status) {
        daysInWeek.setVisibility(status == TaskConstants.REPEAT_STATUS_NO_REPEAT ? View.GONE : View.VISIBLE);
        finalRepeatStatus = status;
    }






    public boolean setTaskFunction() {

        String finalTopic = ((EditText) findViewById(R.id.setTopic)).getText().toString().trim();

        switch (finalTopic.length()) {
            case 0:
                Toast.makeText(this, "Please enter topic", Toast.LENGTH_SHORT).show();
                return false;
            case 1:
                finalTopic = finalTopic.toUpperCase();
                break;
            default:
                finalTopic = finalTopic.substring(0, 1).toUpperCase() + finalTopic.substring(1);
        }

        int finalHour = Integer.parseInt(setHour.getText().toString());
        int finalMinute = Integer.parseInt(setMinute.getText().toString());
        if (finalHour > 12 || finalHour < 1 || finalMinute > 59 || finalMinute < 0) {
            Toast.makeText(this, "Please select valid time", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            finalAlarmDate.set(Calendar.HOUR, finalHour == 12 ? 0 : finalHour);
            finalAlarmDate.set(Calendar.MINUTE, finalMinute);
            finalAlarmDate.set(Calendar.SECOND, 0);
            finalAlarmDate.set(Calendar.MILLISECOND, 0);
            finalAlarmDate.set(Calendar.AM_PM, AM_OR_PM);

            if (
                    (
                            finalAlarmDate.getTimeInMillis() > Calendar.getInstance().getTimeInMillis()
                    ) || (
                            finalAlarmDate.getTimeInMillis() < Calendar.getInstance().getTimeInMillis() && finalRepeatStatus == TaskConstants.REPEAT_STATUS_FROM_AD
                    )
            ) {
                String finalDescription = ((EditText) findViewById(R.id.setDescription)).getText().toString();
                if (finalDescription.length() > 1)
                    finalDescription = finalDescription.substring(0, 1).toUpperCase() + finalDescription.substring(1);

                Collections.sort(finalDaysInWeek);

                TaskData taskData = new TaskData(
                        TaskConstants.PRIVATE_NO,
                        finalTopic,
                        finalDescription,
                        finalAlarmDate.getTimeInMillis(),
                        finalRepeatStatus,
                        new JSONArray(finalDaysInWeek).toString(),
                        finalRepeatStatus == TaskConstants.REPEAT_STATUS_NO_REPEAT ?
                                finalAlarmDate.getTimeInMillis() :
                                rescheduleDateAndTIme(
                                        finalDaysInWeek,
                                        finalAlarmDate.getTimeInMillis(),
                                        finalRepeatStatus
                                ),
                        0,
                        TaskConstants.ALREADY_DONE_NO,
                        TaskConstants.PINNED_NO,
                        null,
                        Calendar.getInstance().getTimeInMillis(),
                        TaskConstants.PRIORITY_NORMAL,
                        null
                );


                TasksDB.insertTask(this, taskData);
                return true;
            } else {
                Toast.makeText(this, "Please select a valid time", Toast.LENGTH_SHORT).show();
                return false;
            }


        }


    }






}
