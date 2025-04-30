package com.example.reminder.reminder_mini.UserInterfaces.HomePage.Tasks;


import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.reminder.mini.Other.ApplicationCustomInterfaces;
import com.reminder.mini.R;
import com.reminder.mini.SqLite.CommonDB.CommonDB;
import com.reminder.mini.SqLite.Tasks.TaskConstants;
import com.reminder.mini.SqLite.Tasks.TaskData;
import com.reminder.mini.UserInterfaces.HomePage.MainActivity.MainActivity;
import com.reminder.mini.UserInterfaces.PinnedTaskPage.PinnedTask;
import com.reminder.mini.UserInterfaces.TaskViewPage.TaskViewMain;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;


public class Tasks extends Fragment implements
        ApplicationCustomInterfaces.FilterTask,
        ApplicationCustomInterfaces.SqlData,
        ApplicationCustomInterfaces.RefreshLayout {

    private RecyclerView recyclerView;
    private View view;
    private ApplicationCustomInterfaces.NestedScroll nestedScroll;
    private final DecimalFormat decimalFormat = new DecimalFormat("00");
    private MaterialButton clearFilterBtn;
    private ArrayList<ArrayList<TaskData>> navDateArray;
    private boolean pinTaskOnTop;
    private MaterialButton pinned;
    private ImageView noTaskImage;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.eventtask_page, container, false);
        recyclerView = view.findViewById(R.id.task_recycler_view);
        clearFilterBtn = view.findViewById(R.id.clearFilterButton);

        pinTaskOnTop = PreferenceManager.getDefaultSharedPreferences(requireContext()).getBoolean("pinTaskOnTop", true);

        pinned = view.findViewById(R.id.seePinned);

        return view;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        nestedScroll = (ApplicationCustomInterfaces.NestedScroll) requireContext();
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {
                nestedScroll.scrollIntercept(true);
                return false;
            }

            @Override
            public void onTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {

            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

            }
        });
        clearFilterBtn.setOnClickListener(v -> {
            getSqlData();
            v.setVisibility(View.GONE);
        });


        noTaskImage = view.findViewById(R.id.noTaskFoundImage);
    }





    @Override
    public void onStart() {
        super.onStart();
        pinned.setOnClickListener(v -> startActivity(new Intent(requireContext(), PinnedTask.class)));

    }

    @Override
    public void onResume() {
        super.onResume();
        getSqlData();
    }

    private void getSqlData() {
        new TaskSqlData(requireContext(), this, pinTaskOnTop).getSQLData();
    }


    @Override
    public void getSQLCursorData(ArrayList<TaskData> taskData) {

        if (taskData.isEmpty()) {
            noTaskImage.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
            pinned.setVisibility(View.GONE);
        }
        else {

            TasksAdapter tasksAdapter = new TasksAdapter(taskData, getActivity(), this);

            recyclerView.setAdapter(tasksAdapter);
            noTaskImage.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);

        }

        if (pinTaskOnTop) {
            CommonDB commonDB = new CommonDB(requireContext());
            Cursor cursor = commonDB.getReadableDatabase().rawQuery("SELECT COUNT(*) FROM " + TaskConstants.TASK_TABLE_NAME + " WHERE " + TaskConstants.PINNED + "=" + TaskConstants.PINNED_YES, null);
            cursor.moveToFirst();
            if (cursor.getInt(0) > 0) pinned.setVisibility(View.VISIBLE);
            commonDB.close();
        }

    }


    @Override
    public void setUpComingAlarmView(TaskData tasks) {
        LinearLayout unComingTaskLayout = view.findViewById(R.id.unComingTask_taskEventPage);
        if (tasks != null) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(tasks.getRepeatingAlarmDate());
            int date = calendar.get(Calendar.DAY_OF_MONTH);
            int month = calendar.get(Calendar.MONTH);
            String[] array = getResources().getStringArray(R.array.monthsInYearFull);
            int hour = calendar.get(Calendar.HOUR);
            int minute = calendar.get(Calendar.MINUTE);


            ((TextView) ((TableRow) unComingTaskLayout.getChildAt(1)).getChildAt(0))
                    .setText(
                            getString(
                                    R.string.set_upcoming_task_format,
                                    date,
                                    array[month],
                                    hour == 0 ? 12 : hour,
                                    decimalFormat.format(minute),
                                    getResources().getStringArray(R.array.amPm)[calendar.get(Calendar.AM_PM)]
                            )
                    );

            (((TableRow) unComingTaskLayout.getChildAt(1)).getChildAt(1)).setOnClickListener(v -> MainActivity.redirectToPage(tasks, getContext(), TaskViewMain.class));

        } else {
            unComingTaskLayout.getChildAt(0).setVisibility(View.GONE);
            unComingTaskLayout.getChildAt(1).setVisibility(View.GONE);
        }
    }


    @Override
    public void setNavDateView(ArrayList<NavBarDateTemplate> navDateArray) {
        RecyclerView recyclerView1 = view.findViewById(R.id.navBarDate_eventTaskPage);
        recyclerView1.setAdapter(new NavBarDateAdapter(navDateArray, this, index -> ((LinearLayoutManager) recyclerView1.getLayoutManager()).scrollToPositionWithOffset(index, 0)));
        recyclerView1.requestDisallowInterceptTouchEvent(true);
        recyclerView1.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {
                nestedScroll.scrollIntercept(false);
                return false;
            }

            @Override
            public void onTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {

            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

            }
        });

    }


    @Override
    public void setFilterDate(ArrayList<ArrayList<TaskData>> navDateArray) {
        this.navDateArray = navDateArray;
    }


    @Override
    public void setFilterTask(int adapterPosition) {
        recyclerView.setAdapter(new TasksAdapter(navDateArray.get(adapterPosition), getActivity(), this));
        clearFilterBtn.setVisibility(View.VISIBLE);
    }

    @Override
    public void refreshLayout() {
        getSqlData();
    }


//
//    @Override
//    public void selectAll(boolean selectedAll, boolean actionBarDestroy) {
//        Log.d("TAG", "selectAll: ----------");
//        for (int i = 0; i < recyclerView.getChildCount(); i++) {
//            tasksAdapter.setTaskCardSelected(selectedAll, (MaterialCardView) ((LinearLayout) recyclerView.getChildAt(i)).getChildAt(1));
//        }
//    }
//
//    @Override
//    public void unSelectSome(ArrayList<Integer> indexes) {
//        for (Integer index: indexes) {
//            tasksAdapter.setTaskCardSelected(false, index, recyclerView);
//        }
//    }
//
//    @Override
//    public void deleteTask() {
//
//        TasksDB.deleteMultipleTask(requireContext(), itemsSelectedIDs);
//        getSqlData();
//        itemsSelectedIndex.clear();
//        itemsSelectedIDs.clear();
//
//    }
//
//    @Override
//    public void pinTask() {
//        ContentValues contentValues;
//        ArrayList<ContentValues> values = new ArrayList<>();
//
//        for (int i = 0; i < itemsSelectedIDs.size(); i++) {
//
//            contentValues = new ContentValues();
//            contentValues.put(TaskConstants.TASK_ID, itemsSelectedIDs.get(i));
//            contentValues.put(TaskConstants.PINNED, TaskConstants.PINNED_YES);
//
//            values.add(contentValues);
//
//        }
//
//        contextualActionBarCallback.unSelectSome(itemsSelectedIndex);
//
//        TasksDB.updateMultipleTask(requireContext(), values);
//        getSqlData();
//        itemsSelectedIndex.clear();
//        itemsSelectedIDs.clear();
//
//    }
//
//    @Override
//    public void markTaskDone() {
//        ContentValues contentValues;
//        ArrayList<ContentValues> values = new ArrayList<>();
//
//
//        for (int i = 0; i < itemsSelectedIDs.size(); i++) {
//
//            contentValues = new ContentValues();
//            contentValues.put(TaskConstants.TASK_ID, itemsSelectedIDs.get(i));
//            contentValues.put(TaskConstants.ALREADY_DONE, TaskConstants.ALREADY_DONE_YES);
//
//            values.add(contentValues);
//
//        }
//
//        contextualActionBarCallback.unSelectSome(itemsSelectedIndex);
//
//
//        TasksDB.updateMultipleTask(requireContext(), values);
//        getSqlData();
//        itemsSelectedIndex.clear();
//        itemsSelectedIDs.clear();
//
//    }
//
//    @Override
//    public void lockTask() {
//        ContentValues contentValues;
//        ArrayList<ContentValues> values = new ArrayList<>();
//
//
//        for (int i = 0; i < itemsSelectedIDs.size(); i++) {
//
//            contentValues = new ContentValues();
//            contentValues.put(TaskConstants.TASK_ID, itemsSelectedIDs.get(i));
//            contentValues.put(TaskConstants.PRIVATE, TaskConstants.PRIVATE_YES);
//
//            values.add(contentValues);
//
//        }
//
//        TasksDB.updateMultipleTask(requireContext(), values);
//        itemsSelectedIndex.clear();
//        itemsSelectedIDs.clear();
//        getSqlData();
//
//    }
//
//    @Override
//    public void reschedule() {
//
//    }
//
//    @Override
//    public void unPinTask() {
//        ContentValues contentValues;
//        ArrayList<ContentValues> values = new ArrayList<>();
//
//        for (int i = 0; i < itemsSelectedIDs.size(); i++) {
//
//            contentValues = new ContentValues();
//            contentValues.put(TaskConstants.TASK_ID, itemsSelectedIDs.get(i));
//            contentValues.put(TaskConstants.PINNED, TaskConstants.PINNED_NO);
//
//            values.add(contentValues);
//
//        }
//
//        contextualActionBarCallback.unSelectSome(itemsSelectedIndex);
//
//        itemsSelectedIndex.clear();
//        itemsSelectedIDs.clear();
//        TasksDB.updateMultipleTask(requireContext(), values);
//        getSqlData();
//    }
//
//    @Override
//    public void markTaskUnDone() {
//        ContentValues contentValues;
//        ArrayList<ContentValues> values = new ArrayList<>();
//
//
//        for (int i = 0; i < itemsSelectedIDs.size(); i++) {
//
//            contentValues = new ContentValues();
//            contentValues.put(TaskConstants.TASK_ID, itemsSelectedIDs.get(i));
//            contentValues.put(TaskConstants.ALREADY_DONE, TaskConstants.ALREADY_DONE_NO);
//
//            values.add(contentValues);
//
//        }
//
//        contextualActionBarCallback.unSelectSome(itemsSelectedIndex);
//
//        itemsSelectedIndex.clear();
//        itemsSelectedIDs.clear();
//        TasksDB.updateMultipleTask(requireContext(), values);
//        getSqlData();
//    }
//
//    @Override
//    public void unlockTask() {
//
//        ContentValues contentValues;
//        ArrayList<ContentValues> values = new ArrayList<>();
//
//
//        for (int i = 0; i < itemsSelectedIDs.size(); i++) {
//
//            contentValues = new ContentValues();
//            contentValues.put(TaskConstants.TASK_ID, itemsSelectedIDs.get(i));
//            contentValues.put(TaskConstants.PRIVATE, TaskConstants.PRIVATE_NO);
//
//            values.add(contentValues);
//
//        }
//
//
//        TasksDB.updateMultipleTask(requireContext(), values);
//        itemsSelectedIndex.clear();
//        itemsSelectedIDs.clear();
//        getSqlData();
//
//    }
//




}



