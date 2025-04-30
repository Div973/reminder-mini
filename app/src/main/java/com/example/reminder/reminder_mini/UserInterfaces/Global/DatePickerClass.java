package com.example.reminder.reminder_mini.UserInterfaces.Global;

import android.app.DatePickerDialog;
import android.content.Context;

import com.reminder.mini.Other.ApplicationCustomInterfaces;

import java.util.Calendar;

public class DatePickerClass {
    private final ApplicationCustomInterfaces.DateTime dateTime;
    private final DatePickerDialog datePickerDialog;
    private int[] date;

    public DatePickerClass(Context context) {
        this.dateTime = (ApplicationCustomInterfaces.DateTime) context;

        Calendar newCalendar = Calendar.getInstance();
        Calendar newCalendarAn = Calendar.getInstance();
        newCalendarAn.add(Calendar.YEAR, 1);

        this.datePickerDialog = new DatePickerDialog(context, (view, year, month, dayOfMonth) -> {
            newCalendar.set(Calendar.YEAR, year);
            newCalendar.set(Calendar.MONTH, month);
            newCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        },
                newCalendar.get(Calendar.YEAR),
                newCalendar.get(Calendar.MONTH),
                newCalendar.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.getDatePicker().setMaxDate(newCalendarAn.getTimeInMillis());
        datePickerDialog.getDatePicker().setMinDate(Calendar.getInstance().getTimeInMillis());
        datePickerDialog.setOnDateSetListener((view, year, month, dayOfMonth) -> {
            date = new int[]{year, month, dayOfMonth};
            dateTime.setDate(date);
        });
    }


    public void showCalendar() {
        datePickerDialog.show();
    }
}
