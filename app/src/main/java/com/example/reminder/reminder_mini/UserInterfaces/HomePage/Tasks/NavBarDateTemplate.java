package com.example.reminder.reminder_mini.UserInterfaces.HomePage.Tasks;

public class NavBarDateTemplate {
    private final int dayOfWeek, date, month;
    private final long alarmDate;


    public NavBarDateTemplate(int dayOfWeek, int date, int month, long alarmDate) {
        this.dayOfWeek = dayOfWeek;
        this.date = date;
        this.month = month;
        this.alarmDate = alarmDate;
    }

    public int getDay() {
        return dayOfWeek;
    }

    public int getDate() {
        return date;
    }

    public int getMonth() {
        return month;
    }


    public long getAlarmDate() {
        return alarmDate;
    }
}
