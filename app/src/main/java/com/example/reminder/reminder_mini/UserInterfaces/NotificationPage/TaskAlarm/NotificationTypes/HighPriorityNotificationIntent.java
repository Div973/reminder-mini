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

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.example.reminder/dex-files/0.dex */
public class HighPriorityNotificationIntent extends AppCompatActivity {
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.high_priority_notification_intent);
        WindowInsetsControllerCompat window = new WindowInsetsControllerCompat(getWindow(), getWindow().getDecorView());
        final long taskID = Long.parseLong((String) Objects.requireNonNull(getIntent().getStringExtra(TaskConstants.TASK_ID)));
        String topic = (String) Objects.requireNonNull(getIntent().getStringExtra(TaskConstants.TOPIC));
        ((TextView) findViewById(R.id.setTopic)).setText(topic);
        findViewById(R.id.closeBTN).setOnClickListener(new View.OnClickListener() { // from class: com.example.reminder.UserInterfaces.NotificationPage.TaskAlarm.NotificationTypes.HighPriorityNotificationIntent$$ExternalSyntheticLambda0
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                HighPriorityNotificationIntent.this.m56lambda$onCreate$0$comexamplereminderUserInterfacesNotificationPageTaskAlarmNotificationTypesHighPriorityNotificationIntent(view);
            }
        });
        findViewById(R.id.laterBTN).setOnClickListener(new View.OnClickListener() { // from class: com.example.reminder.UserInterfaces.NotificationPage.TaskAlarm.NotificationTypes.HighPriorityNotificationIntent$$ExternalSyntheticLambda1
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                HighPriorityNotificationIntent.this.m57lambda$onCreate$1$comexamplereminderUserInterfacesNotificationPageTaskAlarmNotificationTypesHighPriorityNotificationIntent(taskID, view);
            }
        });
        findViewById(R.id.viewBTN).setOnClickListener(new View.OnClickListener() { // from class: com.example.reminder.UserInterfaces.NotificationPage.TaskAlarm.NotificationTypes.HighPriorityNotificationIntent$$ExternalSyntheticLambda2
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                HighPriorityNotificationIntent.this.m58lambda$onCreate$2$comexamplereminderUserInterfacesNotificationPageTaskAlarmNotificationTypesHighPriorityNotificationIntent(taskID, view);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* JADX WARN: Multi-variable type inference failed */
    /* renamed from: lambda$onCreate$0$com-example-reminder-UserInterfaces-NotificationPage-TaskAlarm-NotificationTypes-HighPriorityNotificationIntent  reason: not valid java name */
    public /* synthetic */ void m56lambda$onCreate$0$comexamplereminderUserInterfacesNotificationPageTaskAlarmNotificationTypesHighPriorityNotificationIntent(View v) {
        sendBroadcast(new Intent((Context) this, (Class<?>) TaskAlertCancelBroadcast.class));
        finish();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* JADX WARN: Multi-variable type inference failed */
    /* renamed from: lambda$onCreate$1$com-example-reminder-UserInterfaces-NotificationPage-TaskAlarm-NotificationTypes-HighPriorityNotificationIntent  reason: not valid java name */
    public /* synthetic */ void m57lambda$onCreate$1$comexamplereminderUserInterfacesNotificationPageTaskAlarmNotificationTypesHighPriorityNotificationIntent(long taskID, View v) {
        Intent intent = new Intent((Context) this, (Class<?>) LaterTask.class);
        intent.putExtra(TaskConstants.TASK_ID, String.valueOf(taskID));
        sendBroadcast(new Intent((Context) this, (Class<?>) TaskAlertCancelBroadcast.class));
        startActivity(intent);
        finish();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* JADX WARN: Multi-variable type inference failed */
    /* renamed from: lambda$onCreate$2$com-example-reminder-UserInterfaces-NotificationPage-TaskAlarm-NotificationTypes-HighPriorityNotificationIntent  reason: not valid java name */
    public /* synthetic */ void m58lambda$onCreate$2$comexamplereminderUserInterfacesNotificationPageTaskAlarmNotificationTypesHighPriorityNotificationIntent(long taskID, View v) {
        Intent intent = new Intent((Context) this, (Class<?>) TaskViewMain.class);
        intent.putExtra(TaskConstants.TASK_ID, String.valueOf(taskID));
        startActivity(intent);
        sendBroadcast(new Intent((Context) this, (Class<?>) TaskAlertCancelBroadcast.class));
        finish();
    }
}
