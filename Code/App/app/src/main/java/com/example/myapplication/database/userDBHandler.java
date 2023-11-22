package com.example.myapplication.database;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class userDBHandler extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 5;
    private static final String DATABASE_NAME = "exploreMitDB";
    private static final String USER_TABLE_NAME = "user";
    private static final String KEY_USER_EMAIL = "email";
    private static final String KEY_USER_NAME = "name";
    private static final String KEY_USER_DEPT = "dept";
    private static final String KEY_USER_PH_NO = "phone";
    private static final String KEY_USER_PASS = "pass";


    public userDBHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
//
//        String CREATE_USER_TABLE = "CREATE TABLE " + USER_TABLE_NAME + "("
//                + KEY_USER_EMAIL + " TEXT PRIMARY KEY,"
//                + KEY_USER_NAME + " TEXT,"
//                + KEY_USER_PH_NO + " TEXT,"
//                + KEY_USER_PASS + " TEXT" + ")";
//        db.execSQL(CREATE_USER_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db,int oldVersion, int newVersion) {
//        db.execSQL("DROP TABLE IF EXISTS "+USER_TABLE_NAME);
//        Log.d("Deleted","Table deleted");
    }

//    Add User function
    public boolean addUser(String v_email,String v_name,String v_dept,String v_phno,String v_pass)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_USER_EMAIL, v_email);
        values.put(KEY_USER_NAME, v_name);
        values.put(KEY_USER_DEPT, v_dept);
        values.put(KEY_USER_PH_NO, v_phno);
        values.put(KEY_USER_PASS, v_pass);

        long result = db.insert(USER_TABLE_NAME, null, values);
        db.close();

        // Check if the insertion was successful
        if(result == -1) {
            // Insertion failed
            return false;
        } else {
            // Insertion successful
            Log.d("My-Log","addUser successful!!!");
            return true;
        }
    }

//    Check if user already exists
    public boolean checkUser(String v_email)
    {
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT * FROM " + USER_TABLE_NAME + " WHERE " + KEY_USER_EMAIL + " = '" + v_email + "'";

        Cursor cursor = db.rawQuery(query, null);

        boolean userExists = cursor.getCount() > 0;

        cursor.close();
        db.close();

        return userExists;
    }

//    Get the password of v_email (to compare)
    public String getPass(String v_email)
    {
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT "+ KEY_USER_PASS + " FROM " + USER_TABLE_NAME + " WHERE " + KEY_USER_EMAIL + " = '" + v_email + "'";

        Cursor cursor = db.rawQuery(query, null);

        cursor.moveToFirst();

        @SuppressLint("Range") String password = cursor.getString(cursor.getColumnIndex(KEY_USER_PASS));

        cursor.close();
        db.close();

        return password;
    }

    public Cursor getUserDetails(String v_email)
    {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + USER_TABLE_NAME + " WHERE " + KEY_USER_EMAIL + " = '" + v_email + "'";
        Cursor cursor = null;
        try
        {
            cursor = db.rawQuery(query, null);
        } catch (SQLiteException e) {
            Log.e("Our-Error = ","getUserDetails function error");
        }
        return cursor;
    }

    public Cursor getAllUsers()
    {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + USER_TABLE_NAME ;
        Cursor cursor = null;
        try
        {
            cursor = db.rawQuery(query, null);
        } catch (SQLiteException e) {
            Log.e("Our-Error = ","getAllUsers function error");
        }
        return cursor;
    }

    //    Update User Details
    public boolean updateUser(String v_name, String v_email, String v_dept, String v_phone, String v_pass) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_USER_NAME, v_name);
        values.put(KEY_USER_DEPT, v_dept);
        values.put(KEY_USER_PH_NO, v_phone);
        values.put(KEY_USER_PASS, v_pass);

        String whereClause = KEY_USER_EMAIL + " = ?";
        String[] whereArgs = { v_email };

        try {
            int rowsAffected = db.update(USER_TABLE_NAME, values, whereClause, whereArgs);
            db.close();

            // Check if any rows were affected to determine if the update was successful
            if (rowsAffected > 0) {
                return true; // return successful
            } else {
                return false; // return failed
            }
        } catch (SQLException e) {
            e.printStackTrace();
            db.close();
            return false; // return failed
        }
    }

}
