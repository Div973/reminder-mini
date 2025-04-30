package com.example.reminder.reminder_mini.UserInterfaces.PinnedTaskPage;


import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ActionMode;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.reminder.mini.Other.ApplicationCustomInterfaces;
import com.reminder.mini.R;
import com.reminder.mini.SqLite.CommonDB.CommonDB;
import com.reminder.mini.SqLite.Tasks.TaskConstants;
import com.reminder.mini.SqLite.Tasks.TaskData;
import com.reminder.mini.UserInterfaces.HomePage.Tasks.TasksAdapter;

import java.util.ArrayList;
import java.util.Calendar;


public class PinnedTask extends AppCompatActivity implements
        ApplicationCustomInterfaces.ContextualActionBar,
        ApplicationCustomInterfaces.RefreshLayout {

    private RecyclerView taskRecyclerView;
    private ImageView imageView;
    private ActionMode actionMode;
    private ArrayList<TaskData> taskList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pinned_tasks);

        taskRecyclerView = findViewById(R.id.recycler_view);
        imageView = findViewById(R.id.noTaskFoundImage);


    }


    @Override
    protected void onResume() {
        super.onResume();

        getSqlData();
        setImageVisibility(taskList.isEmpty());


    }


    private void getSqlData() {
        taskList = getTaskList();
        TasksAdapter tasksAdapter = new TasksAdapter(taskList, this, this);
        taskRecyclerView.setAdapter(tasksAdapter);
    }

    public ArrayList<TaskData> getTaskList() {
        CommonDB commonDB = new CommonDB(this);
        SQLiteDatabase database = commonDB.getReadableDatabase();
        ArrayList<TaskData> taskData = new ArrayList<>();

        Cursor cursor = database.rawQuery("SELECT " +
                TaskConstants.PRIVATE + ", " + // 0
                TaskConstants.TOPIC + ", " + // 1
                TaskConstants.DESCRIPTION + ", " + // 2
                TaskConstants.PRIORITY + ", " + // 3
                TaskConstants.ALARM_DATE + ", " + // 4
                TaskConstants.REPEAT_STATUS + ", " + // 5
                TaskConstants.DATE_ARRAY + ", " + // 6
                TaskConstants.REPEATING_ALARM_DATE + ", " + // 7
                TaskConstants.LATER_ALARM_DATE + ", " + // 8
                TaskConstants.PINNED + ", " + // 9
                TaskConstants.ALREADY_DONE + ", " + // 10
                TaskConstants.TASK_ID + // 11
                " FROM " + TaskConstants.TASK_TABLE_NAME +
                " WHERE " + TaskConstants.PRIVATE + " != " + TaskConstants.PRIVATE_YES +
                " AND " + TaskConstants.PINNED + " == " + TaskConstants.PINNED_YES +
                " ORDER BY " + TaskConstants.REPEATING_ALARM_DATE, null);


        if (cursor.getCount() != 0) {
            Calendar calendar = Calendar.getInstance();

            cursor.moveToFirst();
            do {
                TaskData data = new TaskData();
                data.setPrivateTask((byte) cursor.getInt(0));
                data.setTopic(cursor.getString(1));
                data.setDescription(cursor.getString(2));
                data.setPriority((byte) 3);
                data.setAlarmDate(cursor.getLong(4));
                data.setRepeatStatus((byte) cursor.getInt(5));
                data.setDateArray(cursor.getString(6));
                data.setRepeatingAlarmDate(cursor.getLong(7));
                data.setLaterAlarmDate(cursor.getLong(8));
                data.setPinned((byte) cursor.getInt(9));
                data.setAlreadyDone((byte) cursor.getInt(10));
                data.setTaskId(cursor.getLong(11));

                calendar.setTimeInMillis(data.getRepeatingAlarmDate());
                data.setHour((byte) (calendar.get(Calendar.HOUR) == 0 ? 12 : calendar.get(Calendar.HOUR)));
                data.setMinute((byte) (calendar.get(Calendar.MINUTE)));
                data.setAmPm((byte) (calendar.get(Calendar.AM_PM)));
                taskData.add(data);

            } while (cursor.moveToNext());
            cursor.close();
        }

        return taskData;

    }


    private void setImageVisibility(boolean status) {

        if (status) {
            taskRecyclerView.setVisibility(View.GONE);
            imageView.setVisibility(View.VISIBLE);
        } else {
            taskRecyclerView.setVisibility(View.VISIBLE);
            imageView.setVisibility(View.GONE);
        }

    }



    @Override
    public void setContextualActionBarVisible(ApplicationCustomInterfaces.ContextualActionBarCallback contextualActionBarCallback, ApplicationCustomInterfaces.ManipulateTask manipulateTask) {


        actionMode = startSupportActionMode(new ActionMode.Callback() {
            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                mode.getMenuInflater().inflate(R.menu.select_task_pinned_menu, menu);
                menu.getItem(1).getIcon().mutate().setTint(ContextCompat.getColor(PinnedTask.this, R.color.blue_violet));
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


    @Override
    public void refreshLayout() {
        getSqlData();
    }
}
