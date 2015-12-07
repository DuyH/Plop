package com.picopwr.plop.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.picopwr.plop.model.User;

/**
 * Created by Duy on 12/3/2015.
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    private static String TAG = "DatabaseHelper";

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "users.db";
    private static final String TABLE_NAME = "users";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_NAME = "name";
    private static final String COLUMN_EMAIL = "email";
    private static final String COLUMN_PASS = "pass";
    SQLiteDatabase db;

    private static final String TABLE_CREATE = "create table if not exists users (" +
            "id integer primary key not null," +
            " name text not null," +
            " email text not null," +
            " pass text not null);";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TABLE_CREATE);
        this.db = db;
    }



    public void insertUser(User user) {
        db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        // Grab how many ids there are so we can determine what new id to insert
        String query = "select * from users";
        Cursor cursor = db.rawQuery(query, null);
        int userCount = cursor.getCount();

        // Insert new user attributes
        values.put(COLUMN_ID, userCount); // Insert new id
        values.put(COLUMN_NAME, user.getName());
        values.put(COLUMN_EMAIL, user.getEmail());
        values.put(COLUMN_PASS, user.getPassword());

        // insert the user:
        Log.d(TAG, "" + userCount + " " + user.getName() + " " + user.getEmail() + " " + user.getPassword());
        db.insert(TABLE_NAME, null, values);
        db.close();
    }

    /**
     * Search the database for the given email
     * @param email
     * @return
     */
    public String searchPass(String email) {
        Log.d(TAG, email);
        db = this.getReadableDatabase();
        // select email and password
        String query = "select email, pass from " + TABLE_NAME;
        Cursor cursor = db.rawQuery(query, null);
        String entry, result;
        result = "not found";
        if (cursor.moveToFirst()) {
            do {
                entry = cursor.getString(0);
                Log.d(TAG, "Entry " + entry);

                if (entry.equals(email)) {
                    result = cursor.getString(1);
                    Log.d(TAG, "Returning result " + result);
                    break;
                }
            } while (cursor.moveToNext());
        }
        db.close();

        return result;
    }


    /**
     * Checks if email already exists in database
     * @param email
     * @return
     */
    public boolean emailExists(String email) {
        db = this.getReadableDatabase();
        String query = "select email from " + TABLE_NAME;
        Cursor cursor = db.rawQuery(query, null);
        String entry;
        if (cursor.moveToFirst()) {
            do {
                entry = cursor.getString(0);
                if (entry.equals(email)) {
                    return true;
                }
            } while (cursor.moveToNext());
        }
        return false;
    }



    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String query = "DROP TABLE IF EXISTS " + TABLE_NAME;
        db.execSQL(query);
        this.onCreate(db);
    }
}
