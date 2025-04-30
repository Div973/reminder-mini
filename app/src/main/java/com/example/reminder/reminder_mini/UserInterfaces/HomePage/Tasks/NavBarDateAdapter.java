package com.example.reminder.reminder_mini.UserInterfaces.HomePage.Tasks;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.reminder.mini.Other.ApplicationCustomInterfaces;
import com.reminder.mini.R;

import java.util.ArrayList;
import java.util.Calendar;


public class NavBarDateAdapter extends RecyclerView.Adapter<NavBarDateAdapter.ViewHolder> {

    private final String
            NORMAL = "n",
            FADEOUT = "fo",
            HIGHLIGHT = "hlt";

    private final Calendar calendar = Calendar.getInstance();
    private final ArrayList<NavBarDateTemplate> dateArray;
    private final ApplicationCustomInterfaces.FilterTask filterTask;
    private String[] monthOfYear, dayInWeek;
    private Resources resources;
    private MaterialCardView previousHighlightView;
    private long previousAlarmTime = -1;
    private final Calendar conCalendar = Calendar.getInstance();
    private boolean shouldSetCursor = false;
    private final ApplicationCustomInterfaces.SetCursorAt setCursorAt;



    public NavBarDateAdapter(ArrayList<NavBarDateTemplate> dateArray, ApplicationCustomInterfaces.FilterTask filterTask, ApplicationCustomInterfaces.SetCursorAt setCursorAt) {
        this.dateArray = dateArray;
        this.filterTask = filterTask;
        this.setCursorAt = setCursorAt;
        setHasStableIds(true);
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();

        resources = context.getResources();
        dayInWeek = context.getResources().getStringArray(R.array.daysInWeek);
        monthOfYear = context.getResources().getStringArray(R.array.monthsInYear);

        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.nav_bar_date, parent, false);
        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        NavBarDateTemplate item = dateArray.get(position);
        MaterialCardView view = (MaterialCardView) holder.itemView;
        ((TextView) view.getChildAt(0)).setText(monthOfYear[item.getMonth()]);
        ((TextView) view.getChildAt(1)).setText(String.valueOf(item.getDate()));
        ((TextView) view.getChildAt(2)).setText(dayInWeek[item.getDay() - 1]);
        viewHighlight(item.getAlarmDate(), view, position);
        view.setOnClickListener(v -> filterTaskOperation(view, position, item.getAlarmDate()));
    }


    private void filterTaskOperation(MaterialCardView view /* CURRENT VIEW TO BE HIGHLIGHTED */, int position, long alarmDate) {
        filterTask.setFilterTask(position);
        //currentHighlightView = view;
        viewHighlightMain(view, HIGHLIGHT);
        if (previousHighlightView != null && previousAlarmTime != -1) {
            setFade(previousAlarmTime, previousHighlightView);
        }
        previousAlarmTime = alarmDate;
        previousHighlightView = view;
    }


    private void viewHighlight(long alarmTime, MaterialCardView view, int position) {

        conCalendar.setTimeInMillis(alarmTime);

        int year = conCalendar.get(Calendar.YEAR);
        int month = conCalendar.get(Calendar.MONTH);
        int day = conCalendar.get(Calendar.DAY_OF_MONTH);

        int currentYear = calendar.get(Calendar.YEAR);
        int currentMonth = calendar.get(Calendar.MONTH);
        int currentDay = calendar.get(Calendar.DAY_OF_MONTH);

        if (currentYear > year) {
            //fade out
            viewHighlightMain(view, FADEOUT);
        } else if (currentYear < year) {
            //normal
            if (!shouldSetCursor) {
                shouldSetCursor = true;
                setCursorAt.setCursorAt(position);
            }
            viewHighlightMain(view, NORMAL);
        } else {
            if (currentMonth > month) {
                //fade out
                viewHighlightMain(view, FADEOUT);
            } else if (currentMonth < month) {
                //normal
                if (!shouldSetCursor) {
                    shouldSetCursor = true;
                    setCursorAt.setCursorAt(position);
                }
                viewHighlightMain(view, NORMAL);
            } else {
                if (currentDay > day) {
                    //fade out
                    viewHighlightMain(view, FADEOUT);
                } else if (currentDay < day) {
                    //normal
                    if (!shouldSetCursor) {
                        shouldSetCursor = true;
                        setCursorAt.setCursorAt(position);
                    }
                    viewHighlightMain(view, NORMAL);
                } else {
                    //highlight
                    if (!shouldSetCursor) {
                        shouldSetCursor = true;
                        setCursorAt.setCursorAt(position);
                    }
                    viewHighlightMain(view, HIGHLIGHT);
                    previousAlarmTime = alarmTime;  // FOR CURRENTLY HIGHLIGHTED VIEW
                    previousHighlightView = view;  // FOR CURRENTLY HIGHLIGHTED VIEW
                }
            }
        }
    }


    private void setFade(long alarmTime, MaterialCardView view) {
        Calendar conCalendar = Calendar.getInstance();
        conCalendar.setTimeInMillis(alarmTime);

        int alarmYear = conCalendar.get(Calendar.YEAR);
        int alarmMonth = conCalendar.get(Calendar.MONTH);
        int alarmDay = conCalendar.get(Calendar.DAY_OF_MONTH);

        int currentYear = calendar.get(Calendar.YEAR);
        int currentMonth = calendar.get(Calendar.MONTH);
        int currentDay = calendar.get(Calendar.DAY_OF_MONTH);

        if (currentYear > alarmYear) {
            //fade out
            viewHighlightMain(view, FADEOUT);
        } else if (currentYear < alarmYear) {
            //normal
            viewHighlightMain(view, NORMAL);
        } else {
            if (currentMonth > alarmMonth) {
                //fade out
                viewHighlightMain(view, FADEOUT);
            } else if (currentMonth < alarmMonth) {
                //normal
                viewHighlightMain(view, NORMAL);
            } else {
                if (currentDay > alarmDay) {
                    //fade out
                    viewHighlightMain(view, FADEOUT);
                } else {
                    //normal
                    viewHighlightMain(view, NORMAL);
                }
            }
        }

    }


    private void viewHighlightMain(MaterialCardView view, String con) {

        switch (con) {
            case NORMAL:
                for (int i = 0; i < view.getChildCount(); i++) {
                    ((TextView) view.getChildAt(i)).setTextColor(resources.getColor(R.color.dateNormalText, null));
                }
                view.getBackground().setTint(resources.getColor(R.color.transparent, null));
                break;
            case FADEOUT:
                for (int i = 0; i < view.getChildCount(); i++) {
                    ((TextView) view.getChildAt(i)).setTextColor(resources.getColor(R.color.datePassedText, null));
                }
                view.getBackground().setTint(resources.getColor(R.color.transparent, null));
                break;
            case HIGHLIGHT:
                view.getBackground().setTint(resources.getColor(R.color.dateClickHighlight, null));
                break;
            default:

        }
    }


    @Override
    public int getItemViewType(int position) {
        return position;
    }


    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return dateArray.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
