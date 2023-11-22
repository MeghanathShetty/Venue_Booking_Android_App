package com.example.myapplication.database;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

public class venueDBHandler extends SQLiteOpenHelper {

//    Venue Table details
    private static final int DATABASE_VERSION = 5;
    private static final String DATABASE_NAME = "exploreMitDB";
    private static final String VENUE_TABLE_NAME = "venues";
    private static final String KEY_VENUE_NAME = "name";
    private static final String KEY_VENUE_TYPE ="type";
    private static final String KEY_VENUE_DESCRIPTION = "description";
    private static final String KEY_VENUE_LOC ="location";
    private static final String KEY_VENUE_RATINGS ="ratings";
    private static final String KEY_VENUE_NO_RATINGS ="no_ratings";
    private static final String KEY_VENUE_STATUS = "status";
    private static final String KEY_VENUE_IMG = "img_path";


//    User Table details
    private static final String USER_TABLE_NAME = "user";
    private static final String KEY_USER_EMAIL = "email";
    private static final String KEY_USER_DEPT = "dept";
    private static final String KEY_USER_NAME = "name";
    private static final String KEY_USER_PH_NO = "phone";
    private static final String KEY_USER_PASS = "pass";

//    BookedDetails Table details
    private static final String BOOKED_TABLE_NAME = "booked_details";
    private static final String KEY_BOOKED_ID ="id";
    private static final String KEY_BOOKED_VENUE_NAME = "venue_name";
    private static final String KEY_BOOKED_USER_EMAIL ="user_email";
    private static final String KEY_BOOKED_DEPARTMENT="department";
    private static final String KEY_BOOKED_USER_PH_NO="user_phone";
    private static final String KEY_BOOKED_EVENT ="event";
    private static final String KEY_BOOKED_START_DATE_TIME = "start_date";
    private static final String KEY_BOOKED_END_DATE_TIME ="end_date";
    private static final String KEY_BOOKED_TIME_SLOT="time_slot";
    private static final String KEY_BOOKED_STATUS = "status";
    private static final String KEY_BOOKED_APPROVAL ="approval";
    private static final String KEY_BOOKED_VENUE_IMG="img_path";


    public venueDBHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

//        Venue Table Create
        String CREATE_VENUE_TABLE = "CREATE TABLE " + VENUE_TABLE_NAME + " ("
                + KEY_VENUE_NAME + " TEXT PRIMARY KEY,"
                + KEY_VENUE_TYPE + " TEXT,"
                + KEY_VENUE_DESCRIPTION + " TEXT,"
                + KEY_VENUE_LOC + " TEXT,"
                + KEY_VENUE_RATINGS + " INTEGER DEFAULT 0,"
                + KEY_VENUE_STATUS + " TEXT DEFAULT 'Available',"
                + KEY_VENUE_NO_RATINGS + " INTEGER DEFAULT 0,"
                + KEY_VENUE_IMG + " TEXT DEFAULT 'https://images.indianexpress.com/2021/03/Manipal.jpg'"
                + ")";
        db.execSQL(CREATE_VENUE_TABLE);

//        User Table Create
        String CREATE_USER_TABLE = "CREATE TABLE " + USER_TABLE_NAME + "("
                + KEY_USER_EMAIL + " TEXT PRIMARY KEY,"
                + KEY_USER_NAME + " TEXT,"
                + KEY_USER_DEPT + " TEXT,"
                + KEY_USER_PH_NO + " TEXT,"
                + KEY_USER_PASS + " TEXT" + ")";
        db.execSQL(CREATE_USER_TABLE);

//        BookedDetails Table Create
        String CREATE_BOOKED_TABLE = "CREATE TABLE " + BOOKED_TABLE_NAME + " ("
                + KEY_BOOKED_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + KEY_BOOKED_VENUE_NAME + " TEXT,"
                + KEY_BOOKED_USER_EMAIL + " TEXT,"
                + KEY_BOOKED_DEPARTMENT + " TEXT,"
                + KEY_BOOKED_USER_PH_NO + " TEXT,"
                + KEY_BOOKED_EVENT + " TEXT,"
                + KEY_BOOKED_START_DATE_TIME + " TEXT,"
                + KEY_BOOKED_END_DATE_TIME + " TEXT,"
                + KEY_BOOKED_TIME_SLOT + " TEXT DEFAULT 'na',"
                + KEY_BOOKED_STATUS + " TEXT DEFAULT 'na',"    //by default status is 'na' (Not Available) because venue not yet booked
                + KEY_BOOKED_APPROVAL + " TEXT DEFAULT 'na',"//by default approval is 'na' (Not Available) because admin didn't update it yet
                + KEY_BOOKED_VENUE_IMG + " TEXT "
                + ")";
        db.execSQL(CREATE_BOOKED_TABLE);

        Log.d("Tables Created","All Tables Created");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db,int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+ VENUE_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS "+ USER_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS "+ BOOKED_TABLE_NAME);
        Log.d("Tables Deleted","All Tables deleted");
    }

//     Add Venue function
    public boolean addVenue(String v_name,String v_type,String v_des,String v_loc,String v_img_path)
    {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_VENUE_NAME, v_name);
        values.put(KEY_VENUE_TYPE, v_type);
        values.put(KEY_VENUE_DESCRIPTION, v_des);
        values.put(KEY_VENUE_LOC, v_loc);
        if(!v_img_path.isEmpty())
            values.put(KEY_VENUE_IMG, v_img_path);

        long result = db.insert(VENUE_TABLE_NAME, null, values);
        db.close();

        // Check if the insertion was successful
        if(result == -1) {
            // Insertion failed
            return false;
        } else {
            // Insertion successful
            Log.d("My-Log","addVenue successful!!!");
            return true;
        }
    }

//    Check if Venue already exists (exists if v_name already used)
    public boolean checkVenue(String v_name)
    {
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT * FROM " + VENUE_TABLE_NAME + " WHERE " + KEY_VENUE_NAME + " = '" + v_name + "'";

        Cursor cursor = db.rawQuery(query, null);

        boolean venueExists = cursor.getCount() > 0;

        cursor.close();
        db.close();

        return venueExists;
    }

//    Set Venue Status = "Available" or "Unavailable"
    public boolean updateStatus(String v_name, String v_status) {
        SQLiteDatabase db = this.getWritableDatabase();
        String updateQuery = "UPDATE " + VENUE_TABLE_NAME + " SET " + KEY_VENUE_STATUS + " = '" + v_status + "' WHERE " + KEY_VENUE_NAME + " = '" + v_name + "'";
        try {
            db.execSQL(updateQuery);
            db.close();
            return true; // return successful
        } catch (SQLException e) {
            e.printStackTrace();
            db.close();
            return false; // return failed
        }
    }


    //    Update Venue Details
    public boolean updateVenue(String v_name, String v_type, String v_desc, String v_loc, String v_imgPath) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_VENUE_TYPE, v_type);
        values.put(KEY_VENUE_DESCRIPTION, v_desc);
        values.put(KEY_VENUE_LOC, v_loc);
        if(v_imgPath.isEmpty())
            values.put(KEY_VENUE_IMG, "https://images.indianexpress.com/2021/03/Manipal.jpg");
        else
            values.put(KEY_VENUE_IMG, v_imgPath);

        String whereClause = KEY_VENUE_NAME + " = ?";
        String[] whereArgs = { v_name };

        try {
            int rowsAffected = db.update(VENUE_TABLE_NAME, values, whereClause, whereArgs);
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


    //    Delete venue function
    public boolean deleteVenue(String v_name) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
                    String deleteQuery = "DELETE FROM " + VENUE_TABLE_NAME + " WHERE " + KEY_VENUE_NAME + " = '" + v_name + "'";
                    db.execSQL(deleteQuery);
                    db.close();
                    return true;
        } catch (SQLException e) {
            e.printStackTrace();
            db.close();
            return false;
        }
    }

//    Retreive the status(Available/Unavailable) of the venues
    @SuppressLint("Range")
    public String checkStatus(String v_name) {
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT " + KEY_VENUE_STATUS + " FROM " + VENUE_TABLE_NAME + " WHERE " + KEY_VENUE_NAME + " = '" + v_name + "'";

        Cursor cursor = db.rawQuery(query, null);

        String status = null;

        if (cursor.moveToFirst()) {
            status = cursor.getString(cursor.getColumnIndex(KEY_VENUE_STATUS));
        }

        cursor.close();
        db.close();

        return status;
    }


//    Retrieve all booked venues.(booked = venue status is "Unavailable")
    public Cursor retreiveVenues(String v_venue_name)
    {
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT * FROM " + VENUE_TABLE_NAME + " WHERE " + KEY_VENUE_NAME + " = '" + v_venue_name + "'";

        Cursor cursor = db.rawQuery(query, null);

        return cursor;
    }

    //Retreive venues based on "KEY_VENUE_TYPE"
    public Cursor showVenues_on_type(String v_type)
    {
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT * FROM " + VENUE_TABLE_NAME + " WHERE " + KEY_VENUE_TYPE + " = '" + v_type + "' ORDER BY " + KEY_VENUE_NAME;

        Cursor cursor = db.rawQuery(query, null);
//        db.close();

        return cursor;
    }

    public Cursor showAllVenues()
    {
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT * FROM " + VENUE_TABLE_NAME + " ORDER BY " + KEY_VENUE_NAME;

        Cursor cursor = db.rawQuery(query, null);
//        db.close();

        return cursor;
    }

    @SuppressLint("Range")
    public boolean giveRating(String v_name, int new_rating) {
        try {
            SQLiteDatabase db = this.getWritableDatabase();

            // Retrieve the old rating and number of ratings from the venue table
            String query = "SELECT " + KEY_VENUE_RATINGS + ", " + KEY_VENUE_NO_RATINGS +
                    " FROM " + VENUE_TABLE_NAME + " WHERE " + KEY_VENUE_NAME + " = '" + v_name + "'";
            Cursor cursor = db.rawQuery(query, null);
            int old_rating = 0, num_ratings = 0;
            if (cursor.moveToFirst()) {
                old_rating = cursor.getInt(cursor.getColumnIndex(KEY_VENUE_RATINGS));
                num_ratings = cursor.getInt(cursor.getColumnIndex(KEY_VENUE_NO_RATINGS));
            }
            cursor.close();

//            Log.d("My-Log:", "New Ratings = " + new_rating);
//            Log.d("My-Log:", "Old Ratings = " + old_rating);
//            Log.d("My-Log:", "No of Ratings = " + num_ratings);

            // Calculate the new rating
            float updated_rating = (float) ((old_rating * num_ratings + new_rating) / (num_ratings + 1));

            // Round the calculated_rating to the nearest integer
            int int_updated_rating = Math.round(updated_rating);

            if(old_rating<new_rating)
            {
                if(int_updated_rating<=old_rating)
                    int_updated_rating+=1;
            }
            // Ensure the calculated rating is within the 0 to 5 range
            int_updated_rating = Math.max(0, Math.min(5, int_updated_rating));
//            Log.d("My-Log:", "Updated Rating = " + int_updated_rating);

            // Update the venue table with the new rating and increment the number of ratings
            ContentValues values = new ContentValues();
            values.put(KEY_VENUE_RATINGS, int_updated_rating);
            values.put(KEY_VENUE_NO_RATINGS, num_ratings + 1);

            int rowsUpdated = db.update(
                    VENUE_TABLE_NAME,
                    values,
                    KEY_VENUE_NAME + " = '" + v_name + "'",
                    null
            );
            db.close();
            // Check if the update was successful
            return rowsUpdated > 0;
        } catch (SQLException e) {
            Log.e("My-Error:", "Error on giveRating function");
            return false;
        }
    }

}
