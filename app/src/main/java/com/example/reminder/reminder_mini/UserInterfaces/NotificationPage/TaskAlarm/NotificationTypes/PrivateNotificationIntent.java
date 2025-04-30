package com.example.reminder.reminder_mini.UserInterfaces.NotificationPage.TaskAlarm.NotificationTypes;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.reminder.mini.R;
import com.reminder.mini.SqLite.Tasks.TaskConstants;
import com.reminder.mini.UserInterfaces.NotificationPage.TaskAlarm.BroadCasts.TaskAlertCancelBroadcast;
import com.reminder.mini.UserInterfaces.NotificationPage.TaskAlarm.LaterTask.LaterTask;
import com.reminder.mini.UserInterfaces.Ppp.Ppp;

import java.util.Objects;

public class PrivateNotificationIntent extends AppCompatActivity implements View.OnClickListener {

    private final int viewBTN = R.id.viewBTN;
    private final int laterBTN = R.id.laterBTN;
    private long taskID;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.private_notification_intent);

        taskID = Long.parseLong(Objects.requireNonNull(getIntent().getStringExtra(TaskConstants.TASK_ID)));

        findViewById(viewBTN).setOnClickListener(this);
        findViewById(laterBTN).setOnClickListener(this);
        findViewById(R.id.closeBTN).setOnClickListener(this);


    }


    @Override
    public void onClick(View view) {

        Intent intent = null;

        if (view.getId() == viewBTN) {
            intent = new Intent(this, Ppp.class);
            intent.putExtra(Ppp.FOR_PAGE, Ppp.TASK_MAIN_PAGE);
        } else if (view.getId() == laterBTN) {
            intent = new Intent(this, LaterTask.class);
        }


        if (intent != null) {
            intent.putExtra(TaskConstants.TASK_ID, String.valueOf(taskID));
            startActivity(intent);
        }

        sendBroadcast(new Intent(this, TaskAlertCancelBroadcast.class));
        finish();

    }


}
