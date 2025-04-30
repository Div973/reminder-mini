package com.example.reminder.reminder_mini.UserInterfaces.NotificationPage.TaskAlarm.LaterTask;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;

import androidx.fragment.app.Fragment;

import com.reminder.mini.Other.ApplicationCustomInterfaces;
import com.reminder.mini.R;

import java.util.Calendar;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.example.reminder/dex-files/0.dex */
public class DatePickerA extends Fragment implements ApplicationCustomInterfaces.EnableDateTimePicker {
    private final Calendar calendar = Calendar.getInstance();
    private final Calendar calendarAnother = Calendar.getInstance();
    private DatePicker datePicker;
    private ApplicationCustomInterfaces.DateTime2 dateTime;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.dateTime = (ApplicationCustomInterfaces.DateTime2) requireContext();
        return inflater.inflate(R.layout.date_picker, container);
    }

    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.calendarAnother.set(1, this.calendar.get(1) + 1);
        this.datePicker = (DatePicker) view.findViewById(R.id.datePicker);
        this.datePicker.setMinDate(this.calendar.getTimeInMillis());
        this.datePicker.setMaxDate(this.calendarAnother.getTimeInMillis());
        this.dateTime.setDate(null, this);
        this.datePicker.setOnDateChangedListener(new DatePicker.OnDateChangedListener() { // from class: com.example.reminder.UserInterfaces.NotificationPage.TaskAlarm.LaterTask.DatePickerA$$ExternalSyntheticLambda0
            @Override // android.widget.DatePicker.OnDateChangedListener
            public final void onDateChanged(DatePicker datePicker, int i, int i2, int i3) {
                DatePickerA.this.m51lambda$onViewCreated$0$comexamplereminderUserInterfacesNotificationPageTaskAlarmLaterTaskDatePickerA(datePicker, i, i2, i3);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: lambda$onViewCreated$0$com-example-reminder-UserInterfaces-NotificationPage-TaskAlarm-LaterTask-DatePickerA  reason: not valid java name */
    public /* synthetic */ void m51lambda$onViewCreated$0$comexamplereminderUserInterfacesNotificationPageTaskAlarmLaterTaskDatePickerA(DatePicker view1, int year, int monthOfYear, int dayOfMonth) {
        this.dateTime.setDate(new int[]{year, monthOfYear, dayOfMonth}, null);
    }

    @Override // com.example.reminder.Other.ApplicationCustomInterfaces.EnableDateTimePicker
    public void enable(boolean condition, long timeInMillis) {
        this.datePicker.setEnabled(condition);
    }
}
