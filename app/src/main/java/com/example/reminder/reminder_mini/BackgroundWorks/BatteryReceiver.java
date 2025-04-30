package com.example.reminder.reminder_mini.BackgroundWorks;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.BatteryManager;

import com.reminder.mini.BackgroundWorks.TaskWork.RescheduleTaskAfterAlarmTrigger;

public class BatteryReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);

        int batteryPct = (int) ((level / (float) scale) * 100);
        context.sendBroadcast(new Intent(context, RescheduleTaskAfterAlarmTrigger.class));
        //Toast.makeText(context, "Battery Level: " + batteryPct + "%", Toast.LENGTH_SHORT).show();

    }
}
