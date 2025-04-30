package com.example.reminder.reminder_mini.UserInterfaces.HomePage.Tasks;


import static android.text.Html.FROM_HTML_MODE_LEGACY;
import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.os.Handler;
import android.os.Looper;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.reminder.mini.Other.ApplicationCustomInterfaces;
import com.reminder.mini.R;
import com.reminder.mini.SqLite.Tasks.TaskConstants;
import com.reminder.mini.SqLite.Tasks.TaskData;
import com.reminder.mini.SqLite.Tasks.TasksDB;
import com.reminder.mini.UserInterfaces.TaskViewPage.TaskViewMain;

import org.json.JSONException;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.stream.IntStream;



public class TasksAdapter extends RecyclerView.Adapter<TasksAdapter.ViewHolder> implements
        ApplicationCustomInterfaces.BindView,
        ApplicationCustomInterfaces.ContextualActionBarCallback,
        ApplicationCustomInterfaces.ManipulateTask{

    private final ArrayList<TaskData> taskList;
    private final ApplicationCustomInterfaces.BindView bindView = this;
    private Context context;
    private Resources resources;
    private Typeface typeface;
    private final DecimalFormat minuteFormat = new DecimalFormat("00");
    private boolean longClickSelected = false;
    private ArrayList<Long> itemsSelectedIDs = new ArrayList<>();
    private ArrayList<Integer> itemsSelectedIndex = new ArrayList<>();

    private final int
            timeStringFormat = R.string.task_card_time_format,
            repeatStringFormat = R.string.task_card_repeat_status_format,
            setTopicStringFormat = R.string.set_topic_task_card_format;

    private String[] repeatType, amPm;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private final ApplicationCustomInterfaces.ContextualActionBar contextualActionBar;
    private final ApplicationCustomInterfaces.RefreshLayout refreshLayout;


    public TasksAdapter(
            ArrayList<TaskData> list,
            Context mainActivityContext,
            ApplicationCustomInterfaces.RefreshLayout refreshLayout
    ) {
        this.taskList = list;
        contextualActionBar = (ApplicationCustomInterfaces.ContextualActionBar) mainActivityContext;
        this.refreshLayout = refreshLayout;
        setHasStableIds(true);
    }







    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        this.context = parent.getContext();
        this.resources = context.getResources();
        typeface = context.getResources().getFont(R.font.sans_serif_medium);
        repeatType = resources.getStringArray(R.array.repeatType);
        amPm = resources.getStringArray(R.array.amPm);


        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.task_card, parent, false);
        return new ViewHolder(view);
    }


    @Override
    public int getItemCount() {
        return taskList.size();
    }


    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public int getItemViewType(int position) {
        return position;
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        try {
            bindView.primaryViewBinding(
                    (LinearLayout) holder.itemView,
                    taskList.get(position),
                    holder.getAdapterPosition()
            );
        } catch (Exception ignored) {
        }


    }


    @Override
    public void primaryViewBinding(LinearLayout view, TaskData itemView, int position) {
        MaterialCardView cardView = (MaterialCardView) view.getChildAt(1);
        ((TextView) cardView.getChildAt(2)).setText(resources.getString(setTopicStringFormat, itemView.getTopic()));

        handler.post(() -> bindView.secondaryViewBinding(cardView, itemView, position));


        ((TextView) view.getChildAt(0))
                .setText(
                        resources.getString(
                                timeStringFormat,
                                itemView.getHour(),
                                minuteFormat.format(itemView.getMinute()),
                                amPm[itemView.getAmPm()]
                        )
                );


        ((TextView) cardView.getChildAt(3)).setText(
                Html.fromHtml(
                        resources.getString(
                                repeatStringFormat,
                                repeatType[itemView.getRepeatStatus() - 1]),
                        FROM_HTML_MODE_LEGACY)

        );


    }


    private byte i = 0;
    @Override
    public void secondaryViewBinding(MaterialCardView view, TaskData itemView, int position) {

        if (itemView.getRepeatStatus() != TaskConstants.REPEAT_STATUS_NO_REPEAT) {
            LinearLayout daysInWeek = (LinearLayout) view.getChildAt(4);

            try {
                // FOR E.G:- [1, 2, 4, 5, 7]
                for (; i < itemView.getDateArray().length(); i++) {
                    TextView days = (TextView) daysInWeek.getChildAt((int) itemView.getDateArray().get(i) - 1);
                    days.setTypeface(typeface);
                    days.setEnabled(true);
                }
                i = 0;
            } catch (JSONException ignored) {
            }
        }

        if (itemView.getPinned() == TaskConstants.PINNED_YES)
            view.getChildAt(0).setVisibility(VISIBLE);

        view.setOnLongClickListener(v -> {
            startSelection(itemView.getTaskId(), view, position);
            //startSelection(itemView.getTaskId(), view, position);
            return true;
        });

        view.setOnClickListener(v -> {
            if (longClickSelected) {
                startSelection(itemView.getTaskId(), view, position);
                //startSelection(itemView.getTaskId(), view, position);
            }
            else {
                context.startActivity(new Intent(context, TaskViewMain.class).putExtra(TaskConstants.TASK_ID, itemView.getTaskId()));
            }
        });

        setTaskCardSelected(itemView.getSelected(), view);


        if (itemView.getAlreadyDone() == TaskConstants.ALREADY_DONE_YES) {
            view.getChildAt(1).setVisibility(VISIBLE);
        }

    }








    private void startSelection(long taskID, MaterialCardView view, int index) {

        if (itemsSelectedIDs.contains(taskID)) {
            itemsSelectedIDs.remove(taskID);
            itemsSelectedIndex.remove((Object) index);
            setTaskCardSelected(false, view);
        }
        else {
            itemsSelectedIDs.add(taskID);
            itemsSelectedIndex.add(index);
            setTaskCardSelected(true, view);
        }


        if (!longClickSelected) {
            longClickSelected = true;
            contextualActionBar.setContextualActionBarVisible(this, this);
        }


        Log.d("TAG", "startSelection: " + itemsSelectedIDs.toString());

        if (itemsSelectedIDs.isEmpty()) {
            longClickSelected = false;
            contextualActionBar.setContextualActionBarInVisible();
        }
        else {
            // change topic
            contextualActionBar.changeTitle(itemsSelectedIDs.size()+"");
        }







    }







    public void setTaskCardSelected(boolean condition, MaterialCardView taskCard) {
         if (condition) {
             taskCard.getChildAt(5).setVisibility(VISIBLE);
             ((LinearLayout) taskCard.getParent()).setBackground(ContextCompat.getDrawable(context, R.drawable.selected_background_card));
         }
         else {
             taskCard.getChildAt(5).setVisibility(GONE);
             ((LinearLayout) taskCard.getParent()).setBackground(null);
         }

    }


    public void setTaskCardSelected(boolean condition, int index, RecyclerView recyclerView) {
        MaterialCardView taskCard = getCardViewFromRecyclerView(recyclerView, index);
        if (condition) {
            taskCard.getChildAt(5).setVisibility(VISIBLE);
            ((LinearLayout) taskCard.getParent()).setBackground(ContextCompat.getDrawable(context, R.drawable.selected_background_card));
        }
        else {
            taskCard.getChildAt(5).setVisibility(GONE);
            ((LinearLayout) taskCard.getParent()).setBackground(null);
        }

    }



    public MaterialCardView getCardViewFromRecyclerView(RecyclerView recyclerView, int index) {
        return (MaterialCardView) ((LinearLayout) recyclerView.getChildAt(index)).getChildAt(1);
    }










    @Override
    public void deleteTask() {

        TasksDB.deleteMultipleTask(context, itemsSelectedIDs);
        //getSqlData();
        itemsSelectedIndex.clear();
        itemsSelectedIDs.clear();
        refreshLayout.refreshLayout();

    }

    @Override
    public void pinTask() {
        ContentValues contentValues;
        ArrayList<ContentValues> values = new ArrayList<>();

        for (int i = 0; i < itemsSelectedIDs.size(); i++) {

            contentValues = new ContentValues();
            contentValues.put(TaskConstants.TASK_ID, itemsSelectedIDs.get(i));
            contentValues.put(TaskConstants.PINNED, TaskConstants.PINNED_YES);

            values.add(contentValues);

        }

        //contextualActionBarCallback.unSelectSome(itemsSelectedIndex);

        TasksDB.updateMultipleTask(context, values);
        //getSqlData();
        itemsSelectedIndex.clear();
        itemsSelectedIDs.clear();

        refreshLayout.refreshLayout();
    }

    @Override
    public void markTaskDone() {
        ContentValues contentValues;
        ArrayList<ContentValues> values = new ArrayList<>();


        for (int i = 0; i < itemsSelectedIDs.size(); i++) {

            contentValues = new ContentValues();
            contentValues.put(TaskConstants.TASK_ID, itemsSelectedIDs.get(i));
            contentValues.put(TaskConstants.ALREADY_DONE, TaskConstants.ALREADY_DONE_YES);

            values.add(contentValues);

        }

        //contextualActionBarCallback.unSelectSome(itemsSelectedIndex);


        TasksDB.updateMultipleTask(context, values);
        //getSqlData();
        itemsSelectedIndex.clear();
        itemsSelectedIDs.clear();

        refreshLayout.refreshLayout();

    }

    @Override
    public void lockTask() {
        ContentValues contentValues;
        ArrayList<ContentValues> values = new ArrayList<>();


        for (int i = 0; i < itemsSelectedIDs.size(); i++) {

            contentValues = new ContentValues();
            contentValues.put(TaskConstants.TASK_ID, itemsSelectedIDs.get(i));
            contentValues.put(TaskConstants.PRIVATE, TaskConstants.PRIVATE_YES);

            values.add(contentValues);

        }

        TasksDB.updateMultipleTask(context, values);
        itemsSelectedIndex.clear();
        itemsSelectedIDs.clear();
        refreshLayout.refreshLayout();

    }

    @Override
    public void reschedule() {

    }

    @Override
    public void unPinTask() {
        ContentValues contentValues;
        ArrayList<ContentValues> values = new ArrayList<>();

        for (int i = 0; i < itemsSelectedIDs.size(); i++) {

            contentValues = new ContentValues();
            contentValues.put(TaskConstants.TASK_ID, itemsSelectedIDs.get(i));
            contentValues.put(TaskConstants.PINNED, TaskConstants.PINNED_NO);

            values.add(contentValues);

        }

        //contextualActionBarCallback.unSelectSome(itemsSelectedIndex);

        itemsSelectedIndex.clear();
        itemsSelectedIDs.clear();
        TasksDB.updateMultipleTask(context, values);
        refreshLayout.refreshLayout();
    }

    @Override
    public void markTaskUnDone() {
        ContentValues contentValues;
        ArrayList<ContentValues> values = new ArrayList<>();


        for (int i = 0; i < itemsSelectedIDs.size(); i++) {

            contentValues = new ContentValues();
            contentValues.put(TaskConstants.TASK_ID, itemsSelectedIDs.get(i));
            contentValues.put(TaskConstants.ALREADY_DONE, TaskConstants.ALREADY_DONE_NO);

            values.add(contentValues);

        }

        //contextualActionBarCallback.unSelectSome(itemsSelectedIndex);

        itemsSelectedIndex.clear();
        itemsSelectedIDs.clear();
        TasksDB.updateMultipleTask(context, values);
        refreshLayout.refreshLayout();
    }

    @Override
    public void unlockTask() {

        ContentValues contentValues;
        ArrayList<ContentValues> values = new ArrayList<>();


        for (int i = 0; i < itemsSelectedIDs.size(); i++) {

            contentValues = new ContentValues();
            contentValues.put(TaskConstants.TASK_ID, itemsSelectedIDs.get(i));
            contentValues.put(TaskConstants.PRIVATE, TaskConstants.PRIVATE_NO);

            values.add(contentValues);

        }


        TasksDB.updateMultipleTask(context, values);
        itemsSelectedIndex.clear();
        itemsSelectedIDs.clear();
        refreshLayout.refreshLayout();

    }

    @Override
    public void selectAll() {
        itemsSelectedIDs = taskList.stream().map(TaskData::getTaskId).collect(Collectors.toCollection(ArrayList::new));
        itemsSelectedIndex = IntStream.range(0, taskList.size()).boxed().collect(Collectors.toCollection(ArrayList::new));

        for (TaskData taskData : taskList) {
            taskData.setSelected(true);
        }
        notifyDataSetChanged();

        contextualActionBar.changeTitle(itemsSelectedIDs.size()+"");


    }

    @Override
    public void unSelectAll(boolean actionBarDestroy) {
        itemsSelectedIDs.clear();
        itemsSelectedIndex.clear();

        for (TaskData taskData : taskList) {
            taskData.setSelected(false);
        }
        contextualActionBar.changeTitle("0");
        notifyDataSetChanged();

        if (actionBarDestroy) {
            longClickSelected = false;
        }
    }

//    @Override
//    public void unSelectSome(ArrayList<Integer> indexes) {
//
//    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        public View itemView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.itemView = itemView;
        }

    }


}



