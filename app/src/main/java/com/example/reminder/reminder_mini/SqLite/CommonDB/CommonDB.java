package com.example.reminder.reminder_mini.SqLite.CommonDB;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.reminder.mini.SqLite.Tasks.TaskConstants;

public class CommonDB extends SQLiteOpenHelper {
    public CommonDB(@Nullable Context context) {
        super(context, CommonDbConstants.DB_NAME, null, CommonDbConstants.VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TaskConstants.TASK_TABLE_QUERY);

    }



    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TaskConstants.TASK_TABLE_NAME);

    }
}
