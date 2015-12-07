package com.picopwr.plop.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.picopwr.plop.model.LogEntry;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by duy on 12/4/15.
 */
public class LogDBHelper extends SQLiteOpenHelper {

    private static final String TAG = "LogDBHelper";

    private static final String DATABASE_NAME = "logs.db";
    private static final String TABLE_NAME = "logs";
    private static final int DATABASE_VERSION = 1;

    public static final String COLUMN_ID = "id";
    public static final String COLUMN_USER = "user";
    public static final String COLUMN_DATE = "date";
    public static final String COLUMN_TIME = "time";
    public static final String COLUMN_BRISTOL = "bristol";
    public static final String COLUMN_AMOUNT = "amount";
    public static final String COLUMN_COLOR = "color";
    public static final String COLUMN_DIFFICULTY = "difficulty";
    public static final String COLUMN_NOTE = "note";

    private static final String DATABASE_CREATE = "create table if not exists logs (" +
            "id integer primary key autoincrement," +
            " user text not null," +
            " date text not null," +
            " time text not null,"+
            " bristol text not null,"+
            " amount text not null,"+
            " color text not null,"+
            " difficulty text not null,"+
            " note text not null);";

    private DatabaseHelper mDbHelper;
    private SQLiteDatabase db;

    public LogDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DATABASE_CREATE);
        this.db = db;
    }


    public void close() {
        mDbHelper.close();
    }

    public long createLog(LogEntry logEntry) {
        db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_USER, logEntry.getUser());
        values.put(COLUMN_DATE, logEntry.getDate());
        values.put(COLUMN_TIME, logEntry.getTime());
        values.put(COLUMN_BRISTOL, logEntry.getBristol());
        values.put(COLUMN_AMOUNT, logEntry.getAmount());
        values.put(COLUMN_COLOR, logEntry.getColor());
        values.put(COLUMN_DIFFICULTY, logEntry.getDifficulty());
        values.put(COLUMN_NOTE, logEntry.getNote());

        return db.insert(TABLE_NAME, null, values); // Returns an id since it's autoincrementing
    }

    public boolean deleteLog(long rowId) {

        return db.delete(TABLE_NAME, COLUMN_ID + "=" + rowId, null) > 0;
    }

    public List<ArrayList<String>> getLogEntries(String email) {
        db = this.getReadableDatabase();

        List<ArrayList<String>> entries = new ArrayList<>();
        ArrayList<String> entry = new ArrayList<>();

        String selectQuery = "SELECT  * FROM " + TABLE_NAME + " WHERE user = '" + email + "'";
        Cursor cursor = db.rawQuery(selectQuery, null);
        String[] data = null;
        if (cursor.moveToFirst()) {
            do {
                entry.add(cursor.getString(2));
                entry.add(cursor.getString(3));
                entry.add(cursor.getString(4));
                entry.add(cursor.getString(5));
                entry.add(cursor.getString(6));
                entry.add(cursor.getString(7));
                entry.add(cursor.getString(8));
                entries.add(entry);
            } while (cursor.moveToNext());
        }
        return entries;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String query = "DROP TABLE IF EXISTS " + TABLE_NAME;
        db.execSQL(query);
        this.onCreate(db);
    }

}

