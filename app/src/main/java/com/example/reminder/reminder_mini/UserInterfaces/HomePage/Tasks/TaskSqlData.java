package com.example.reminder.reminder_mini.UserInterfaces.HomePage.Tasks;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

import androidx.preference.PreferenceManager;

import com.reminder.mini.Other.ApplicationCustomInterfaces;
import com.reminder.mini.SqLite.CommonDB.CommonDB;
import com.reminder.mini.SqLite.Tasks.TaskConstants;
import com.reminder.mini.SqLite.Tasks.TaskData;

import java.util.ArrayList;
import java.util.Calendar;

public class TaskSqlData {


    private final SQLiteDatabase database;
    private final CommonDB commonDB;
    private final ApplicationCustomInterfaces.SqlData sqlData;
    private final ArrayList<NavBarDateTemplate> navDateArray = new ArrayList<>();
    private final ArrayList<ArrayList<TaskData>> navDateArrayAn = new ArrayList<>();
    private ArrayList<TaskData> childArray;
    private String navDateCon = null;
    private final Context context;
    private Cursor cursor = null;
    int dayOfWeek, date, month, year;
    String sum;
    private boolean upcomingAlarmDate = false;
    private final Calendar calendar = Calendar.getInstance();
    boolean pinTaskOnTop;


    public TaskSqlData(Context context, ApplicationCustomInterfaces.SqlData sqlData, boolean pinTaskOnTop) {
        this.sqlData = sqlData;
        this.commonDB = new CommonDB(context);
        this.context = context;
        this.database = commonDB.getReadableDatabase();
        this.pinTaskOnTop = pinTaskOnTop;
    }


    public void getSQLData() {
        ArrayList<TaskData> taskData = new ArrayList<>();

        try {
            cursor = database.rawQuery(
                    "SELECT " +

                            TaskConstants.PRIVATE + ", " + // 0
                            TaskConstants.TOPIC + ", " + // 1
                            TaskConstants.PRIORITY + ", " + // 2
                            TaskConstants.REPEAT_STATUS + ", " + // 3
                            TaskConstants.DATE_ARRAY + ", " + // 4
                            TaskConstants.REPEATING_ALARM_DATE + ", " + // 5
                            TaskConstants.PINNED + ", " + // 6
                            TaskConstants.ALREADY_DONE + ", " + // 7
                            TaskConstants.TASK_ID + ", " +  // 8
                            TaskConstants.ID + ", " +  // 9
                            TaskConstants.ALARM_DATE + ", " +  // 10
                            TaskConstants.TASK_WEB_ID +  // 11

                            " FROM " + TaskConstants.TASK_TABLE_NAME +
                            " WHERE " + TaskConstants.PRIVATE + " != " + TaskConstants.PRIVATE_YES
                            +
                            (
                                    PreferenceManager.getDefaultSharedPreferences(context).getBoolean("rmvTask", false)
                                            ?
                                            " AND " + TaskConstants.ALREADY_DONE + " != " + TaskConstants.ALREADY_DONE_YES
                                            :
                                            ""
                            )
                            +
                            (
                                    pinTaskOnTop
                                            ?
                                            " AND " + TaskConstants.PINNED + " = " + TaskConstants.PINNED_NO
                                            :
                                            ""
                            )
                            +
                            " ORDER BY " + TaskConstants.REPEATING_ALARM_DATE, null);
        } catch (SQLiteException e) {
            e.printStackTrace();
        }


        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();

            if (pinTaskOnTop) {
                do {
                    TaskData data = new TaskData();
                    data.setPrivateTask((byte) cursor.getInt(0));
                    data.setTopic(cursor.getString(1));
                    data.setPriority((byte) 2);
                    data.setRepeatStatus((byte) cursor.getInt(3));
                    data.setDateArray(cursor.getString(4));
                    data.setRepeatingAlarmDate(cursor.getLong(5));
                    data.setPinned((byte) cursor.getInt(6));
                    data.setAlreadyDone((byte) cursor.getInt(7));
                    data.setTaskId(cursor.getLong(8));

                    calendar.setTimeInMillis(data.getRepeatingAlarmDate());
                    data.setHour((byte) (calendar.get(Calendar.HOUR) == 0 ? 12 : calendar.get(Calendar.HOUR)));
                    data.setMinute((byte) calendar.get(Calendar.MINUTE));
                    data.setAmPm((byte) calendar.get(Calendar.AM_PM));


                    data.setId(cursor.getInt(9));
                    data.setAlarmDate(cursor.getLong(10));
                    data.setTaskWebId(cursor.getString(11));

                    if (data.getRepeatingAlarmDate() > Calendar.getInstance().getTimeInMillis() && !upcomingAlarmDate) {
                        upcomingAlarmDate = true;
                        sqlData.setUpComingAlarmView(data);
                    }

                    dayDateNavArray(cursor.getPosition(), data, calendar);
                    if (data.getPinned() != TaskConstants.PINNED_YES) taskData.add(data);

                } while (cursor.moveToNext());

            } else {
                do {
                    TaskData data = new TaskData();
                    data.setPrivateTask((byte) cursor.getInt(0));
                    data.setTopic(cursor.getString(1));
                    data.setPriority((byte) 2);
                    data.setRepeatStatus((byte) cursor.getInt(3));
                    data.setDateArray(cursor.getString(4));
                    data.setRepeatingAlarmDate(cursor.getLong(5));
                    data.setPinned((byte) cursor.getInt(6));
                    data.setAlreadyDone((byte) cursor.getInt(7));
                    data.setTaskId(cursor.getLong(8));

                    calendar.setTimeInMillis(data.getRepeatingAlarmDate());
                    data.setHour((byte) (calendar.get(Calendar.HOUR) == 0 ? 12 : calendar.get(Calendar.HOUR)));
                    data.setMinute((byte) calendar.get(Calendar.MINUTE));
                    data.setAmPm((byte) calendar.get(Calendar.AM_PM));


                    data.setId(cursor.getInt(9));
                    data.setAlarmDate(cursor.getLong(10));
                    data.setTaskWebId(cursor.getString(11));

                    if (data.getRepeatingAlarmDate() > Calendar.getInstance().getTimeInMillis() && !upcomingAlarmDate) {
                        upcomingAlarmDate = true;
                        sqlData.setUpComingAlarmView(data);
                    }

                    dayDateNavArray(cursor.getPosition(), data, calendar);
                    taskData.add(data);

                } while (cursor.moveToNext());

            }
            cursor.close();


        }


        if (!upcomingAlarmDate) sqlData.setUpComingAlarmView(null);

        sqlData.getSQLCursorData(taskData);
        sqlData.setNavDateView(navDateArray);
        sqlData.setFilterDate(navDateArrayAn);

        commonDB.close();
        database.close();

    }



    private void dayDateNavArray(int position, TaskData taskData, Calendar calendar) {

        dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        date = calendar.get(Calendar.DAY_OF_MONTH);
        month = calendar.get(Calendar.MONTH);
        year = calendar.get(Calendar.YEAR);

        sum = ((String.valueOf(dayOfWeek) + date) + month) + year;

        if (navDateCon == null) {
            childArray = new ArrayList<>();
            childArray.add(taskData);

            navDateArray.add(new NavBarDateTemplate(dayOfWeek, date, month, calendar.getTimeInMillis()));
            navDateCon = sum;
        } else if (sum.equals(navDateCon)) {
            childArray.add(taskData);
        } else {
            navDateArrayAn.add(childArray);
            childArray = new ArrayList<>();
            childArray.add(taskData);

            navDateCon = sum;
            navDateArray.add(new NavBarDateTemplate(dayOfWeek, date, month, calendar.getTimeInMillis()));

        }

        if (position == (cursor.getCount() - 1)) {
            navDateArrayAn.add(childArray);
        }


    }








}
