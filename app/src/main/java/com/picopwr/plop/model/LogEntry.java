package com.picopwr.plop.model;

/**
 * Create a LogEntry containing the entry of a logging session. Holds just data and attributes for now.
 */
public class LogEntry {

    String mUser;
    String mDate;
    String mTime;
    String mBristol;
    String mAmount;
    String mColor;
    String mDifficulty;
    String mNote;

    public LogEntry(String user, String date, String time, String bristol, String amount, String color, String difficulty, String note) {
        mUser = user;
        mDate = date;
        mTime = time;
        mBristol = bristol;
        mAmount = amount;
        mColor = color;
        mDifficulty = difficulty;
        mNote = note;
    }

    public String getUser() {
        return mUser;
    }

    public void setUser(String user) {
        mUser = user;
    }

    public String getDate() {
        return mDate;
    }

    public void setDate(String date) {
        mDate = date;
    }

    public String getTime() {
        return mTime;
    }

    public void setTime(String time) {
        mTime = time;
    }

    public String getBristol() {
        return mBristol;
    }

    public void setBristol(String bristol) {
        mBristol = bristol;
    }

    public String getAmount() {
        return mAmount;
    }

    public void setAmount(String amount) {
        mAmount = amount;
    }

    public String getColor() {
        return mColor;
    }

    public void setColor(String color) {
        mColor = color;
    }

    public String getDifficulty() {
        return mDifficulty;
    }

    public void setDifficulty(String difficulty) {
        mDifficulty = difficulty;
    }

    public String getNote() {
        return mNote;
    }

    public void setNote(String note) {
        mNote = note;
    }
}

