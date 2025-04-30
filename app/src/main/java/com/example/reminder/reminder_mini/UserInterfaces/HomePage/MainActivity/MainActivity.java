package com.example.reminder.reminder_mini.UserInterfaces.HomePage.MainActivity;


import static com.reminder.mini.UserInterfaces.Ppp.Ppp.FOR_PAGE;
import static com.reminder.mini.UserInterfaces.Ppp.Ppp.LOCKED_PAGE;
import static com.reminder.mini.UserInterfaces.Ppp.Ppp.SETTINGS_PAGE;

import android.Manifest;
import android.app.AlarmManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ActionMode;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.preference.PreferenceManager;
import androidx.viewpager2.widget.ViewPager2;

import com.reminder.mini.BackgroundWorks.CheckNotificationService;
import com.reminder.mini.Other.ApplicationCustomInterfaces;
import com.reminder.mini.R;
import com.reminder.mini.SqLite.Tasks.TaskConstants;
import com.reminder.mini.SqLite.Tasks.TaskData;
import com.reminder.mini.UserInterfaces.AboutPage.AboutPage;
import com.reminder.mini.UserInterfaces.AddTaskPage.AddTask;
import com.reminder.mini.UserInterfaces.Ppp.Ppp;
import com.reminder.mini.UserInterfaces.PrivatePage.PrivatePages;
import com.reminder.mini.UserInterfaces.SettingsPage.TaskSettings.TaskSetting;


public class MainActivity extends AppCompatActivity implements
        ApplicationCustomInterfaces.NestedScroll,
        ApplicationCustomInterfaces.ContextualActionBar {


    private ViewPager2 viewPager2;
    private ActionMode actionMode;
    private boolean pinTaskOnTop;



    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        viewPager2 = findViewById(R.id.frameLayout);

        findViewById(R.id.tskBtn).setOnClickListener(v -> startActivity(new Intent(this, AddTask.class)));

        checkPermissions();

        viewPager2.setAdapter(new MainPagePagerAdapter(this));



        Intent intent = new Intent("com.reminder.mini.APP_STARTED");
        intent.setComponent(new ComponentName("com.reminder.mini", "com.reminder.mini.BackgroundWorks.TaskWork.SystemRescheduleTaskAfterAlarmTrigger"));
        sendBroadcast(intent);

    }


    @Override
    protected void onResume() {
        super.onResume();
        viewPager2.setAdapter(new MainPagePagerAdapter(this));
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_file, menu);
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menuSettings) {
            if (PreferenceManager.getDefaultSharedPreferences(this).getBoolean(getString(R.string.get_settings_lock_state), true)) {
                startActivity(new Intent(this, Ppp.class).putExtra(FOR_PAGE, SETTINGS_PAGE));
            } else {
                startActivity(new Intent(this, TaskSetting.class));
            }
        } else if (id == R.id.menuPrivate) {
            if (PreferenceManager.getDefaultSharedPreferences(this).getBoolean(getString(R.string.get_private_lock_and_message_lock_state), true)) {
                startActivity(new Intent(this, Ppp.class).putExtra(FOR_PAGE, LOCKED_PAGE));
            } else {
                startActivity(new Intent(this, PrivatePages.class));
            }
        } else if (id == R.id.menuAbout) {
            startActivity(new Intent(this, AboutPage.class));
        }


        return true;
    }


    private void checkPermissions() {

        if (Build.VERSION.SDK_INT >= 33) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_DENIED) {

                ActivityResultLauncher<String> activityResultLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                    if (!isGranted) {
                        finish();
                    }
                });

                activityResultLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);

            }
        }

        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        String packageName = getPackageName();

        if (!pm.isIgnoringBatteryOptimizations(packageName)) {
            Intent intent = new Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
            intent.setData(Uri.parse("package:" + packageName));
            startActivity(intent);
        }

        startService(new Intent(this, CheckNotificationService.class));

        if (!Settings.canDrawOverlays(this)) {
            Intent intent = new Intent();
            intent.setAction(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
            intent.setData(Uri.parse("package:" + getPackageName()));
            startActivity(intent);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            if (!alarmManager.canScheduleExactAlarms()) {
                Intent intent = new Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM);
                startActivity(intent);
            }
        }

    }


    @Override
    public void scrollIntercept(boolean con) {
        viewPager2.setUserInputEnabled(con);
    }


    public static void redirectToPage(TaskData template, Context context, Class<?> page) {
        Intent intent = new Intent(context, page);
        if (template != null) {
            intent.putExtra(TaskConstants.TOPIC, template.getTopic());
            intent.putExtra(TaskConstants.DESCRIPTION, template.getDescription());
            intent.putExtra(TaskConstants.REPEAT_STATUS, String.valueOf(template.getRepeatStatus()));
            intent.putExtra(TaskConstants.REPEATING_ALARM_DATE, template.getRepeatingAlarmDate());
            intent.putExtra(TaskConstants.DATE_ARRAY, String.valueOf(template.getDateArray()));
            intent.putExtra(TaskConstants.ALREADY_DONE, template.getAlreadyDone());
            intent.putExtra(TaskConstants.TASK_ID, template.getTaskId());
        }
        context.startActivity(intent);
    }


    public static void expandHideView(View button, View viewToHideOrShow) {

        button.animate()
                .rotationX(button.getRotationX() == 180f ? 0f : 180f)
                .setDuration(300)
                .start();

        viewToHideOrShow.setVisibility(viewToHideOrShow.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);

    }





    @Override
    protected void onStart() {
        super.onStart();

        pinTaskOnTop = PreferenceManager.getDefaultSharedPreferences(this).getBoolean("pinTaskOnTop", true);
    }



    @Override
    public void setContextualActionBarVisible(ApplicationCustomInterfaces.ContextualActionBarCallback contextualActionBarCallback, ApplicationCustomInterfaces.ManipulateTask manipulateTask) {


            actionMode = startSupportActionMode(new ActionMode.Callback() {
                @Override
                public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                    mode.getMenuInflater().inflate(R.menu.select_task_menu, menu);
                    if (!pinTaskOnTop) menu.getItem(2).getSubMenu().getItem(2).setVisible(true);
                    menu.getItem(1).getIcon().mutate().setTint(ContextCompat.getColor(MainActivity.this, R.color.blue_violet));
                    return true;
                }

                @Override
                public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                    return true;
                }

                @Override
                public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                    int id = item.getItemId();

                    if (id == R.id.selectAllItem) {
                        item.setVisible(false);
                        actionMode.getMenu().getItem(1).setVisible(true);
                        if (contextualActionBarCallback != null) contextualActionBarCallback.selectAll();
                    }
                    else if (id == R.id.unSelectAllItem) {
                        item.setVisible(false);
                        actionMode.getMenu().getItem(0).setVisible(true);
                        if (contextualActionBarCallback != null) contextualActionBarCallback.unSelectAll(false);
                    }

                    else if (id == R.id.menu_lock) {
                        manipulateTask.lockTask();
                        actionMode.finish();
                    }
                    else if (id == R.id.menu_delete) {
                        manipulateTask.deleteTask();
                        actionMode.finish();
                    }
                    else if (id == R.id.menu_pin) {
                        manipulateTask.pinTask();
                        actionMode.finish();
                    }
                    else if (id == R.id.menu_unpin) {
                        manipulateTask.unPinTask();
                        actionMode.finish();
                    }
                    else if (id == R.id.menu_mark_done) {
                        manipulateTask.markTaskDone();
                        actionMode.finish();
                    }
                    else if (id == R.id.menu_mark_not_done) {
                        manipulateTask.markTaskUnDone();
                        actionMode.finish();
                    }



                    return true;
                }

                @Override
                public void onDestroyActionMode(ActionMode mode) {
                    if (contextualActionBarCallback != null) contextualActionBarCallback.unSelectAll(true);
                }
            });




    }




    @Override
    public void setContextualActionBarInVisible() {
        actionMode.finish();
    }



    @Override
    public void changeTitle(String value) {
        actionMode.setTitle(value);
    }







}