package com.example.reminder.reminder_mini.UserInterfaces.NotificationPage.TaskAlarm.NotificationTypes;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowInsetsControllerCompat;

import com.reminder.mini.R;
import com.reminder.mini.SqLite.Tasks.TaskConstants;
import com.reminder.mini.UserInterfaces.NotificationPage.TaskAlarm.BroadCasts.TaskAlertCancelBroadcast;
import com.reminder.mini.UserInterfaces.NotificationPage.TaskAlarm.LaterTask.LaterTask;
import com.reminder.mini.UserInterfaces.TaskViewPage.TaskViewMain;

import java.util.Objects;

public class NotificationIntent extends AppCompatActivity {
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notification_intent);
        getWindow().addFlags(128);
        WindowInsetsControllerCompat window = new WindowInsetsControllerCompat(getWindow(), getWindow().getDecorView());

        final long taskID = Long.parseLong(Objects.requireNonNull(getIntent().getStringExtra(TaskConstants.TASK_ID)));
        String topic = (String) Objects.requireNonNull(getIntent().getStringExtra(TaskConstants.TOPIC));
        ((TextView) findViewById(R.id.setTopic)).setText(topic);
        // from class: com.example.reminder.UserInterfaces.NotificationPage.TaskAlarm.NotificationTypes.NotificationIntent$$ExternalSyntheticLambda0
// android.view.View.OnClickListener
        findViewById(R.id.closeBTN).setOnClickListener(view -> NotificationIntent.this.m59lambda$onCreate$0$comexamplereminderUserInterfacesNotificationPageTaskAlarmNotificationTypesNotificationIntent(view));
        findViewById(R.id.laterBTN).setOnClickListener(view -> NotificationIntent.this.m60lambda$onCreate$1$comexamplereminderUserInterfacesNotificationPageTaskAlarmNotificationTypesNotificationIntent(taskID, view));
        findViewById(R.id.viewBTN).setOnClickListener(view -> NotificationIntent.this.m61lambda$onCreate$2$comexamplereminderUserInterfacesNotificationPageTaskAlarmNotificationTypesNotificationIntent(taskID, view));
    }

    public /* synthetic */ void m59lambda$onCreate$0$comexamplereminderUserInterfacesNotificationPageTaskAlarmNotificationTypesNotificationIntent(View v) {
        sendBroadcast(new Intent((Context) this, (Class<?>) TaskAlertCancelBroadcast.class));
        finish();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* JADX WARN: Multi-variable type inference failed */
    /* renamed from: lambda$onCreate$1$com-example-reminder-UserInterfaces-NotificationPage-TaskAlarm-NotificationTypes-NotificationIntent  reason: not valid java name */
    public /* synthetic */ void m60lambda$onCreate$1$comexamplereminderUserInterfacesNotificationPageTaskAlarmNotificationTypesNotificationIntent(long taskID, View v) {
        Intent intent = new Intent((Context) this, (Class<?>) LaterTask.class);
        intent.putExtra(TaskConstants.TASK_ID, String.valueOf(taskID));
        sendBroadcast(new Intent((Context) this, (Class<?>) TaskAlertCancelBroadcast.class));
        finish();
        startActivity(intent);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* JADX WARN: Multi-variable type inference failed */
    /* renamed from: lambda$onCreate$2$com-example-reminder-UserInterfaces-NotificationPage-TaskAlarm-NotificationTypes-NotificationIntent  reason: not valid java name */
    public /* synthetic */ void m61lambda$onCreate$2$comexamplereminderUserInterfacesNotificationPageTaskAlarmNotificationTypesNotificationIntent(long taskID, View v) {
        Intent intent = new Intent((Context) this, (Class<?>) TaskViewMain.class);
        intent.putExtra(TaskConstants.TASK_ID, String.valueOf(taskID));
        finish();
        startActivity(intent);
        sendBroadcast(new Intent((Context) this, (Class<?>) TaskAlertCancelBroadcast.class));
    }
}
