package com.example.reminder.reminder_mini.SqLite.Tasks;


public class TaskConstants {
    public static final String
            ID = "_id",
            PRIVATE = "private",
            TOPIC = "topic",
            DESCRIPTION = "description",
            PRIORITY = "priority",
            ALARM_DATE = "alarm_date",
            REPEATING_ALARM_DATE = "repeating_alarm_date",
            TASK_SENT = "task_sent",
            LATER_ALARM_DATE = "later_alarm_date",
            DATE_ARRAY = "date_array",
            PINNED = "pinned",
            ALREADY_DONE = "already_done",
            REPEAT_STATUS = "repeat_status",
            USER_PRIMARY_ID = "user_primary_id",
            TASK_ID = "task_id",
            TASK_TABLE_NAME = "tasks",
            TASK_WEB_ID = "task_web_id";


    public static final String TASK_TABLE_QUERY =
            "CREATE TABLE " + TASK_TABLE_NAME +
                    " ( " +
                    ID + "  INTEGER PRIMARY KEY AUTOINCREMENT, " +              // 0
                    PRIVATE + " BYTE NOT NULL, " +                              // 1
                    TOPIC + " TEXT NOT NULL, " +                                // 2
                    DESCRIPTION + " TEXT, " +                                   // 3
                    ALARM_DATE + " LONG NOT NULL, " +                           // 4
                    REPEAT_STATUS + " BYTE NOT NULL, " +                        // 5
                    DATE_ARRAY + " JSON, " +                                    // 6
                    REPEATING_ALARM_DATE + " LONG NOT NULL, " +                 // 7
                    LATER_ALARM_DATE + " LONG, " +                              // 8
                    ALREADY_DONE + " BYTE NOT NULL, " +                         // 9
                    PINNED + " BYTE NOT NULL, " +                               // 10
                    TASK_ID + " LONG NOT NULL, " +                              // 11
                    PRIORITY + " BYTE NOT NULL," +                              // 12
                    TASK_WEB_ID + " TEXT " +                                    // 13
                    " )";


    public static final byte
            PRIVATE_NO = 1,
            PRIVATE_YES = 2,


    PRIORITY_HIGH = 2,
            PRIORITY_NORMAL = 1,

            REPEAT_STATUS_NO_REPEAT = 1,
            REPEAT_STATUS_FROM_AD = 2,
            REPEAT_STATUS_UP_TO_AD = 3,

    ALREADY_DONE_NO = 1,
            ALREADY_DONE_YES = 2,

    PINNED_NO = 1,
            PINNED_YES = 2;


    public static final Integer[] DAYS_OF_WEEK_ARRAY = {1, 2, 3, 4, 5, 6, 7, 1, 2, 3, 4, 5, 6, 7};
    public static final String DAYS_OF_WEEK = "[1,2,3,4,5,6,7]";


}
