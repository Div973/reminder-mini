package com.example.reminder.reminder_mini.SqLite.Tasks;

import org.json.JSONArray;
import org.json.JSONException;


public class TaskData {

    private int id;
    private byte privateTask;
    private String topic;
    private String description;
    private long alarmDate;
    private byte repeatStatus;
    private String dateArray;
    private long repeatingAlarmDate;
    private long laterAlarmDate;
    private int alreadyDone;
    private byte pinned;
    private String userPrimaryId;
    private long taskId;
    private byte priority;
    private byte hour;
    private byte minute;
    private byte amPm;
    private String taskWebId;
    private boolean isSelected = false;


    public TaskData() {
    }

    public TaskData(byte privateTask, String topic, String description, long alarmDate, byte repeatStatus, String dateArray, long repeatingAlarmDate, long laterAlarmDate, int alreadyDone, byte pinned, String userPrimaryId, long taskId, byte priority, String taskWebId) {
        this.privateTask = privateTask;
        this.topic = topic;
        this.description = description;
        this.alarmDate = alarmDate;
        this.repeatStatus = repeatStatus;
        this.dateArray = dateArray;
        this.repeatingAlarmDate = repeatingAlarmDate;
        this.laterAlarmDate = laterAlarmDate;
        this.alreadyDone = alreadyDone;
        this.pinned = pinned;
        this.userPrimaryId = userPrimaryId;
        this.taskId = taskId;
        this.priority = priority;
        this.taskWebId = taskWebId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public byte getPrivateTask() {
        return privateTask;
    }

    public void setPrivateTask(byte privateTask) {
        this.privateTask = privateTask;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getAlarmDate() {
        return alarmDate;
    }

    public void setAlarmDate(long alarmDate) {
        this.alarmDate = alarmDate;
    }

    public byte getRepeatStatus() {
        return repeatStatus;
    }

    public void setRepeatStatus(byte repeatStatus) {
        this.repeatStatus = repeatStatus;
    }

    public JSONArray getDateArray() {
        JSONArray jsonArray = null;
        try {
            jsonArray = new JSONArray(this.dateArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonArray;
    }

    public void setDateArray(String dateArray) {
        this.dateArray = dateArray;
    }

    public long getRepeatingAlarmDate() {
        return repeatingAlarmDate;
    }

    public void setRepeatingAlarmDate(long repeatingAlarmDate) {
        this.repeatingAlarmDate = repeatingAlarmDate;
    }

    public long getLaterAlarmDate() {
        return laterAlarmDate;
    }

    public void setLaterAlarmDate(long laterAlarmDate) {
        this.laterAlarmDate = laterAlarmDate;
    }

    public int getAlreadyDone() {
        return alreadyDone;
    }

    public void setAlreadyDone(int alreadyDone) {
        this.alreadyDone = alreadyDone;
    }

    public byte getPinned() {
        return pinned;
    }

    public void setPinned(byte pinned) {
        this.pinned = pinned;
    }

    public String getUserPrimaryId() {
        return userPrimaryId;
    }

    public void setUserPrimaryId(String userPrimaryId) {
        this.userPrimaryId = userPrimaryId;
    }

    public long getTaskId() {
        return taskId;
    }

    public void setTaskId(long taskId) {
        this.taskId = taskId;
    }

    public byte getPriority() {
        return priority;
    }

    public void setPriority(byte priority) {
        this.priority = priority;
    }

    public byte getHour() {
        return hour;
    }

    public void setHour(byte hour) {
        this.hour = hour;
    }

    public byte getMinute() {
        return minute;
    }

    public void setMinute(byte minute) {
        this.minute = minute;
    }

    public byte getAmPm() {
        return amPm;
    }

    public void setAmPm(byte amPm) {
        this.amPm = amPm;
    }

    public String getTaskWebId() {
        return taskWebId;
    }

    public void setTaskWebId(String taskWebId) {
        this.taskWebId = taskWebId;
    }
    public void setSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }
    public boolean getSelected() {
        return this.isSelected;
    }

}

