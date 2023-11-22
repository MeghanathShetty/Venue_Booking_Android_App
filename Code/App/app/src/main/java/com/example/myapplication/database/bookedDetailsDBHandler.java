package com.example.myapplication.database;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import com.example.myapplication.R;
import com.example.myapplication.Utils.emailHelper;
import com.example.myapplication.Utils.notificationHelper;
import com.example.myapplication.homePage;
import com.example.myapplication.receivers.StatusUpdateWorker;
import com.example.myapplication.user.bookVenue;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;


public class bookedDetailsDBHandler extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 5;
    private static final String DATABASE_NAME = "exploreMitDB";
    private static final String BOOKED_TABLE_NAME = "booked_details";
    private static final String KEY_BOOKED_ID ="id";
    private static final String KEY_BOOKED_VENUE_NAME = "venue_name";
    private static final String KEY_BOOKED_USER_EMAIL ="user_email";
    private static final String KEY_BOOKED_EVENT ="event";
    private static final String KEY_BOOKED_START_DATE_TIME = "start_date";
    private static final String KEY_BOOKED_END_DATE_TIME ="end_date";
    private static final String KEY_BOOKED_TIME_SLOT="time_slot";
    private static final String KEY_BOOKED_STATUS = "status";
    private static final String KEY_BOOKED_APPROVAL ="approval";
    private static final String KEY_BOOKED_DEPARTMENT="department";
    private static final String KEY_BOOKED_USER_PH_NO="user_phone";
    private static final String KEY_BOOKED_VENUE_IMG="img_path";

    String notification_title="Notification-Title";
    String notification_content="Notification-Content";
    Context con;
    public bookedDetailsDBHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        con=context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
//        String CREATE_TABLE = "CREATE TABLE " + BOOKED_TABLE_NAME + " ("
//                + KEY_BOOKED_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
//                + KEY_BOOKED_VENUE_NAME + " TEXT,"
//                + KEY_BOOKED_USER_EMAIL + " TEXT,"
//                + KEY_BOOKED_EVENT + " TEXT,"
//                + KEY_BOOKED_START_DATE_TIME + " TEXT,"
//                + KEY_BOOKED_END_DATE_TIME + " TEXT,"
//                + KEY_BOOKED_STATUS + " TEXT DEFAULT 'na',"  //by default status is 'na' (Not Available) because venue not yet booked
//                + KEY_BOOKED_APPROVAL + " TEXT DEFAULT 'na'" //by default approval is 'na' (Not Available) because admin didn't update it yet
//                + ")";
//        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db,int oldVersion, int newVersion) {
//        db.execSQL("DROP TABLE IF EXISTS "+ BOOKED_TABLE_NAME);
//        Log.d("Deleted","Table deleted");
    }

//    add book venue request to database (does not book) booking is done in updateApproval()
    public boolean bookVenue(String v_email,String v_venue_name,String v_user_dept,String v_user_phno,String v_event,String v_strt_dt,String v_end_dt,String v_time_slot,String v_imgPath)
    {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_BOOKED_USER_EMAIL, v_email);
        values.put(KEY_BOOKED_VENUE_NAME, v_venue_name);
        values.put(KEY_BOOKED_DEPARTMENT, v_user_dept);
        values.put(KEY_BOOKED_USER_PH_NO, v_user_phno);
        values.put(KEY_BOOKED_EVENT, v_event);
        values.put(KEY_BOOKED_START_DATE_TIME, v_strt_dt);
        values.put(KEY_BOOKED_END_DATE_TIME,v_end_dt);
        values.put(KEY_BOOKED_VENUE_IMG,v_imgPath);
        if(!v_time_slot.isEmpty())
            values.put(KEY_BOOKED_TIME_SLOT,v_time_slot);

        long result = db.insert(BOOKED_TABLE_NAME, null, values);
        db.close();

        // Check if the insertion was successful
        if(result == -1) {
            return false;
        } else {
            //send notification to user
            notificationHelper.showNotification(con,"Venue request sent","Your request has been sent to the admin");
            return true;
        }
    }



//    Retrieve all new requests made by Users.(new requests = approval is na)
    public Cursor showNewReqs() {
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT * FROM " + BOOKED_TABLE_NAME + " WHERE " + KEY_BOOKED_APPROVAL + " = 'na'";

        Cursor cursor = db.rawQuery(query, null);
//        db.close();

        return cursor;
    }


//    set approval to "yes" or "no" and other things if "yes"
    @SuppressLint("Range")
    public boolean updateApproval(int v_id, String v_email, String v_venue_name, String v_approval, Activity c_activity) {
        SQLiteDatabase db_w = this.getWritableDatabase();
        try
        {

            //update approval to "yes" or "no" according to admins command
            String query = "UPDATE " + BOOKED_TABLE_NAME +
                    " SET " + KEY_BOOKED_APPROVAL + " = '" + v_approval + "'" +
                    " WHERE " + KEY_BOOKED_ID + " = " + v_id +
                    " AND " + KEY_BOOKED_USER_EMAIL + " = '" + v_email + "'" +
                    " AND " + KEY_BOOKED_VENUE_NAME + " = '" + v_venue_name + "'";
            db_w.execSQL(query);

            //if approval is "yes"
            if(v_approval.equals("yes"))
            {
//              set bookedDetails status to "pre-order"  based on time++++++++++++++++++++++++++++++++++++++++++++++
                boolean check_upStatus=updateStatus(v_id,v_email,v_venue_name,"pre-order");
                if(!check_upStatus)
                    return false;
//              ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

                //retrieve start-date-time and end-date-time for calculating
                String qry = "SELECT * " +
                        "FROM " + BOOKED_TABLE_NAME +
                        " WHERE " + KEY_BOOKED_ID + " = " + v_id +
                        " AND " + KEY_BOOKED_VENUE_NAME + " = '" + v_venue_name + "'" +
                        " AND " + KEY_BOOKED_USER_EMAIL + " = '" + v_email + "'";

                SQLiteDatabase db_r = this.getReadableDatabase();
                Cursor dt_cursor = db_r.rawQuery(qry, null);
                String strt_dt="NA";
                String end_dt="NA";
                String time_slot="na";
                if(dt_cursor.moveToFirst())
                {
                    strt_dt=dt_cursor.getString(dt_cursor.getColumnIndex("start_date"));
                    end_dt=dt_cursor.getString(dt_cursor.getColumnIndex("end_date"));
                    time_slot=dt_cursor.getString(dt_cursor.getColumnIndex("time_slot"));
                }
//                else
//                    Toast.makeText(con,"Something went wrong,Please re-login!",Toast.LENGTH_LONG).show();
                dt_cursor.close();
                db_r.close();

                Calendar currentCalendar = Calendar.getInstance();
                long make_unavailable_delay = 0;
                long make_available_delay = 0;
                if(!time_slot.equals("na"))
                {
                    // split and store the individual dates in Integer list
                    String[] pairs = time_slot.split(",");
                    List<Integer> timeList = new ArrayList<>();
                    for (String pair : pairs) {
                        String[] times = pair.split("-");
                        for (String time : times) {
                            int number = Integer.parseInt(time);
                            timeList.add(number);
                        }
                    }
                    // Convert the list to array
                    Integer[] timeArray = timeList.toArray(new Integer[timeList.size()]);
                    int endHour = Collections.max(Arrays.asList(timeArray));
                    int startHour = Collections.min(Arrays.asList(timeArray));

                    //for notification and email use
                    String start_only_date=strt_dt;
                    String start_date=strt_dt + " " + startHour + ":00:00";
                    String end_date=end_dt + " " + endHour + ":00:00";

                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                    Date startDate = dateFormat.parse(start_date);
                    Calendar startCalendar = Calendar.getInstance();

                    startCalendar.setTime(startDate);
                    // Calculate the delay based on the time difference
                    make_unavailable_delay = startCalendar.getTimeInMillis() - currentCalendar.getTimeInMillis();

                    //Calculate time delay for making venue available
                    // Convert the end date and time string to a Calendar object
                    Date endDate = dateFormat.parse(end_date);
                    Calendar endCalendar = Calendar.getInstance();
                    endCalendar.setTime(endDate);
                    // Calculate the delay based on the time difference
                    make_available_delay = endCalendar.getTimeInMillis() - currentCalendar.getTimeInMillis();

                    // Set venue to "Unavailable"
                    String v_venue_status = "Unavailable";
                    venueDBHandler db11 = new venueDBHandler(con);
                    db11.updateStatus(v_venue_name, v_venue_status);
                    db11.close();

                    //send notification
                    notificationHelper.showNotification(con,"Admin approval for venue request",
                            "Admin has accepted your request,venue "+v_venue_name+" will booked on "+ start_only_date+" at "+startHour+".");
                    //send email
                    emailHelper.sendEmailInBackground(con,c_activity,
                            v_email, //to email
                            "Admin approval for venue request", //email title
                            "Admin has accepted your request,venue "+v_venue_name+" will booked on "+ start_only_date+" at "+startHour+" hour (24 hour Format)."); //email content

                }else
                {
                    //Calculate time delay for making venue unavailable
                    // Convert the start date and time string to a Calendar object
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                    Date startDate = dateFormat.parse(strt_dt);
                    Calendar startCalendar = Calendar.getInstance();
                    startCalendar.setTime(startDate);

                    // Calculate the delay based on the time difference
                    make_unavailable_delay = startCalendar.getTimeInMillis() - currentCalendar.getTimeInMillis();

                    // Convert the end date and time string to a Calendar object
                    Date endDate = dateFormat.parse(end_dt);
                    Calendar endCalendar = Calendar.getInstance();
                    endCalendar.setTime(endDate);

                    // Add one day to the end date ( For calculation purpose)
                    endCalendar.add(Calendar.DAY_OF_MONTH, 1);

                    // Calculate the delay based on the time difference
                    make_available_delay = endCalendar.getTimeInMillis() - currentCalendar.getTimeInMillis();


                    //send notification
                    notificationHelper.showNotification(con,"Admin approval for venue request","Admin has accepted your request,venue "+v_venue_name+" will booked on "+ strt_dt+".");
                    //send email
                    emailHelper.sendEmailInBackground(con,c_activity,
                            v_email, //to email
                            "Admin approval for venue request", //email title
                            "Admin has accepted your request,venue "+v_venue_name+" will be booked on "+ strt_dt+"."); //email content
                }

                //                Setting when booking started........................................................................
                //automatically set venue to "Unavailable" and booked status to "ongoing" after start time has started
                Data dt1 = new Data.Builder()
                        .putString("action", "MAKE_UNAVAILABLE")
                        .putString("v_venue_name", v_venue_name)
                        .putInt("v_id", v_id)
                        .putString("v_email", v_email)
                        .build();
                // Create a OneTimeWorkRequest to schedule the worker with the initial delay.
                OneTimeWorkRequest workRequest1 = new OneTimeWorkRequest.Builder(StatusUpdateWorker.class)
                        .setInitialDelay(make_unavailable_delay, TimeUnit.MILLISECONDS) // Set the initial delay.
                        .setInputData(dt1)
                        .build();
                // Enqueue the work request.
                WorkManager.getInstance(con).enqueue(workRequest1);

                LifecycleOwner lifecycleOwner = (LifecycleOwner) c_activity;
                // listen to the workers action
                WorkManager.getInstance(con).getWorkInfoByIdLiveData(workRequest1.getId())
                        .observe(lifecycleOwner, new Observer<WorkInfo>() {
                            @Override
                            public void onChanged(WorkInfo workInfo) {
                                if (workInfo.getState() == WorkInfo.State.SUCCEEDED)
                                {
                                    //send email
                                    emailHelper.sendEmailInBackground(con,c_activity,
                                            v_email, //to email
                                            "Venue Time started", //email title
                                            "Your start time for venue "+v_venue_name+" has started"); //email content
                                }
                                else if (workInfo.getState() == WorkInfo.State.FAILED)
                                {
                                    //send email
                                    emailHelper.sendEmailInBackground(con,c_activity,
                                            v_email, //to email
                                            "Venue Time started", //email title
                                            "Your start time for venue "+v_venue_name+" has started"); //email content
                                    Log.e("Our-Error:","Something went wrong in MAKE_UNAVAILABLE action");
                                }
                            }
                        });
                //....................................................................................................................

                //                Setting when booking ended..........................................................................
                //automatically update the venue to "Available" after time has been spent
                //And also update bookedDetails status to "completed".
                Data dt2 = new Data.Builder()
                        .putString("action", "MAKE_AVAILABLE")
                        .putString("v_venue_name", v_venue_name)
                        .putInt("v_id", v_id)
                        .putString("v_email", v_email)
                        .build();
                // Create a OneTimeWorkRequest to schedule the worker with the initial delay.
                OneTimeWorkRequest workRequest2 = new OneTimeWorkRequest.Builder(StatusUpdateWorker.class)
                        .setInitialDelay(make_available_delay, TimeUnit.MILLISECONDS) // Set the initial delay.
                        .setInputData(dt2)
                        .build();
                // Enqueue the work request.
                WorkManager.getInstance(con).enqueue(workRequest2);

                // listen to the workers action
                WorkManager.getInstance(con).getWorkInfoByIdLiveData(workRequest2.getId())
                        .observe(lifecycleOwner, new Observer<WorkInfo>() {
                            @Override
                            public void onChanged(WorkInfo workInfo) {
                                if (workInfo.getState() == WorkInfo.State.SUCCEEDED)
                                {
                                    //send email
                                    emailHelper.sendEmailInBackground(con,c_activity,
                                            v_email, //to email
                                            "Venue Time completed", //email title
                                            "Your allowed time for the venue "+v_venue_name+" has been finished"); //email content
                                }
                                else if (workInfo.getState() == WorkInfo.State.FAILED)
                                {
                                    //send email
                                    emailHelper.sendEmailInBackground(con,c_activity,
                                            v_email, //to email
                                            "Venue Time completed", //email title
                                            "Your allowed time for the venue "+v_venue_name+" has been finished"); //email content
                                    Log.e("Our-Error:","Something went wrong in MAKE_AVAILABLE action");
                                }
                            }
                        });
                //....................................................................................................................
                dt_cursor.close();
            }
            else if(v_approval.equals("no"))
            {
                //send notification
                notificationHelper.showNotification(con,"Admin approval for venue request","Admin has rejected your request.");
                //send email
                emailHelper.sendEmailInBackground(con,c_activity,
                        v_email, //to email
                        "Admin approval for venue request", //email title
                        "Admin has rejected your request for booking venue "+v_venue_name+"."); //email content
            }
            else if(v_approval.equals("expired"))
            {
                //send notification
                notificationHelper.showNotification(con,"Request expired","Your request has been expired before the admin could do anything." +
                        " You can resend the request if you would like.");
                //send email
                emailHelper.sendEmailInBackground(con,c_activity,
                        v_email, //to email
                        "Request expired", //email title
                        "Your request for venue " + v_venue_name + " has been expired before the admin could do anything." +
                                " You can resend the request if you would like."); //email content
            }
//            db_w.close();
            return true;
        }catch (SQLException e)
        {
            notificationHelper.showNotification(con,"My-Error-check","SQL EXCEPTION");
            return false;
        } catch (ParseException e) {
            Log.e("My-Error=","Error while Parsing date and time");
            notificationHelper.showNotification(con,"App-Error","DATE PARSING ERROR");
            return false;
        }
        finally {
            if (db_w.isOpen()) {
                db_w.close();
            }
        }
    }

    //update status to "pre-order" or "completed"
    public boolean updateStatus(int v_id, String v_email,String v_venue_name,String v_status) {
        SQLiteDatabase db = this.getWritableDatabase();
        try
        {
            String query = "UPDATE " + BOOKED_TABLE_NAME +
                    " SET " + KEY_BOOKED_STATUS + " = '" + v_status + "'" +
                    " WHERE " + KEY_BOOKED_ID + " = " + v_id +
                    " AND " + KEY_BOOKED_USER_EMAIL + " = '" + v_email + "'" +
                    " AND " + KEY_BOOKED_VENUE_NAME + " = '" + v_venue_name + "'";

            db.execSQL(query);
            db.close();
            return true;
        }catch (SQLException e)
        {
            db.close();
            return false;
        }
    }

    public Cursor retreiveVenue_bookedVenue(String v_venue_name,String v_approval)
    {
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT * FROM " + BOOKED_TABLE_NAME + " WHERE "
                + KEY_BOOKED_VENUE_NAME + " = '" + v_venue_name
                + "' AND " + KEY_BOOKED_APPROVAL + " = '" + v_approval
                + "' AND " + KEY_BOOKED_STATUS + " != 'completed'";

        Cursor cursor = db.rawQuery(query, null);
//        db.close();

        return cursor;
    }

    public Cursor retreiveUser_bookedVenue(String v_user_email,String v_approval)
    {
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT * FROM " + BOOKED_TABLE_NAME + " WHERE "
                + KEY_BOOKED_USER_EMAIL + " = '" + v_user_email
                + "' AND " + KEY_BOOKED_APPROVAL + " = '" + v_approval
                + "' AND " + KEY_BOOKED_STATUS + " != 'completed'";

        Cursor cursor = db.rawQuery(query, null);
//        db.close();

        return cursor;
    }

    public Cursor retreiveUser_CurrentBookedVenueAndRequests(String v_user_email)
    {
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT * FROM " + BOOKED_TABLE_NAME + " WHERE "
                + KEY_BOOKED_USER_EMAIL + " = '" + v_user_email
                + "' AND " + KEY_BOOKED_STATUS + " != 'completed' ORDER BY " + KEY_BOOKED_ID + " DESC";


        Cursor cursor = db.rawQuery(query, null);
//        db.close();

        return cursor;
    }

    public Cursor retreive_UserCompletedTransactions(String v_user_email)
    {
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT * FROM " + BOOKED_TABLE_NAME + " WHERE "
                + KEY_BOOKED_USER_EMAIL + " = '" + v_user_email
                + "' AND " + KEY_BOOKED_STATUS + " = 'completed' ORDER BY " + KEY_BOOKED_ID + " DESC";


        Cursor cursor = db.rawQuery(query, null);
//        db.close();

        return cursor;
    }

    public Cursor retreiveAllCurrent_BookedVenues()
    {
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT * FROM " + BOOKED_TABLE_NAME +
                " WHERE " + KEY_BOOKED_APPROVAL + " = 'yes'" +
                " AND " + KEY_BOOKED_STATUS + " != 'completed'";

        Cursor cursor = db.rawQuery(query, null);
//        db.close();

        return cursor;
    }

    public Cursor retreiveBookedVenue_onSameDate(String v_venue_name,String v_approval,String v_date)
    {
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT * FROM " + BOOKED_TABLE_NAME + " WHERE " +
                KEY_BOOKED_VENUE_NAME + " = '" + v_venue_name + "' " +
                "AND " + KEY_BOOKED_APPROVAL + " = '" + v_approval + "' " +
                "AND " + KEY_BOOKED_STATUS + " != 'completed' " +
                "AND " + KEY_BOOKED_START_DATE_TIME + " = '" + v_date + "' " +
                "AND " + KEY_BOOKED_END_DATE_TIME + " = '" + v_date + "'";

        Cursor cursor = db.rawQuery(query, null);

        return cursor;
    }
}
