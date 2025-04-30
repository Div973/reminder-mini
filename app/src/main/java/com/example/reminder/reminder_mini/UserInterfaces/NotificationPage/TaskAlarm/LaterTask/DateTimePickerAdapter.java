package com.example.reminder.reminder_mini.UserInterfaces.NotificationPage.TaskAlarm.LaterTask;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.example.reminder/dex-files/0.dex */
public class DateTimePickerAdapter extends FragmentStateAdapter {
    private final DatePickerA datePickerA;
    private final TimePickerA timePickerA;

    public DateTimePickerAdapter(FragmentActivity fragmentActivity) {
        super(fragmentActivity);
        this.datePickerA = new DatePickerA();
        this.timePickerA = new TimePickerA();
    }

    public Fragment createFragment(int position) {
        return position == 1 ? this.datePickerA : this.timePickerA;
    }

    public int getItemCount() {
        return 2;
    }
}
