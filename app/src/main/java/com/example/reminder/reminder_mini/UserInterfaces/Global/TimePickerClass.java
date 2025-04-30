package com.example.reminder.reminder_mini.UserInterfaces.Global;

import androidx.fragment.app.FragmentManager;

import com.google.android.material.timepicker.MaterialTimePicker;
import com.reminder.mini.Other.ApplicationCustomInterfaces;

import java.util.Calendar;

public class TimePickerClass {
    private int[] time = null;
    private final FragmentManager fragmentManager;

    public TimePickerClass(FragmentManager fragmentManager, ApplicationCustomInterfaces.DateTime dateTime) {
        this.fragmentManager = fragmentManager;

        materialTimePicker.addOnPositiveButtonClickListener(v -> {
            time = new int[]{materialTimePicker.getHour(), materialTimePicker.getMinute()};
            dateTime.setTime(time, (byte) (materialTimePicker.getHour() > 11 ? Calendar.PM : Calendar.AM));
        });
    }

    MaterialTimePicker materialTimePicker = new MaterialTimePicker.Builder()
            .setHour(Calendar.getInstance().get(Calendar.HOUR_OF_DAY))
            .setMinute(Calendar.getInstance().get(Calendar.MINUTE) + 5)
            .build();

    public void timeFunction() {
        materialTimePicker.show(fragmentManager, "fragmentManager");
    }


}
