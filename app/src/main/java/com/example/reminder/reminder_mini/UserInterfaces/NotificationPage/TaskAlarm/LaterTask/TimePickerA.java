package com.example.reminder.reminder_mini.UserInterfaces.NotificationPage.TaskAlarm.LaterTask;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.reminder.mini.Other.ApplicationCustomInterfaces;
import com.reminder.mini.R;

import java.util.Calendar;

public class TimePickerA extends Fragment implements ApplicationCustomInterfaces.EnableDateTimePicker {
    private final Calendar currentCal = Calendar.getInstance();
    private ApplicationCustomInterfaces.DateTime2 dateTime;
    private TimePicker timePicker;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.dateTime = (ApplicationCustomInterfaces.DateTime2) requireContext();
        return inflater.inflate(R.layout.time_picker, container);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.timePicker = view.findViewById(R.id.timePicker);
        int setMinute = this.currentCal.get(12) + 10;
        this.timePicker.setMinute(setMinute > 60 ? setMinute - 60 : setMinute);
        this.dateTime.setTime(null, (byte) 0, this);

        this.timePicker.setOnTimeChangedListener((view1, hourOfDay, minute) -> dateTime.setTime(new int[]{hourOfDay, minute}, (byte) (hourOfDay < 12 ? 0 : 1), null));
    }


    @Override
    public void enable(boolean condition, long timeInMillis) {
        if (timeInMillis != 0) {
            this.currentCal.setTimeInMillis(timeInMillis);
            this.timePicker.setHour(this.currentCal.get(11));
            this.timePicker.setMinute(this.currentCal.get(12));
        }
        timePicker.setEnabled(condition);
    }
}
