package com.example.myapplication.receivers;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.example.myapplication.Utils.emailHelper;
import com.example.myapplication.Utils.notificationHelper;
import com.example.myapplication.database.bookedDetailsDBHandler;
import com.example.myapplication.database.venueDBHandler;
import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

public class StatusUpdateWorker extends Worker {
    Context con;
    String notification_content="Notification-Content";
    public StatusUpdateWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        con = context;
    }

    @NonNull
    @Override
    public Result doWork() {
        String action = getInputData().getString("action");

        if (action != null) {
            if (action.equals("MAKE_AVAILABLE")) {
                makeVenueAvailable();
            } else if (action.equals("MAKE_UNAVAILABLE")) {
                makeVenueUnavailable();
            }
        }
        // Indicate that the work was successful.
        return Result.success();
    }

    private void makeVenueAvailable()
    {
        String v_venue_name = getInputData().getString("v_venue_name");
        String v_email = getInputData().getString("v_email");
        int v_id = getInputData().getInt("v_id", -1);

        // Set venue to "Available"
        String v_venue_status = "Available";
        venueDBHandler db1 = new venueDBHandler(con);
        db1.updateStatus(v_venue_name, v_venue_status);
        db1.close();

        // Set bookedDetails status to "completed"
        String v_bookedDetails_status = "completed";
        bookedDetailsDBHandler db2 = new bookedDetailsDBHandler(con);
        db2.updateStatus(v_id, v_email, v_venue_name, v_bookedDetails_status);
        db2.close();
        Log.d("My-Log", "ACTION_MAKE_AVAILABLE invoked!!!");

        //send notification
        notificationHelper.showNotification(con,"Venue Time completed",
                "Your allowed time for the venue "+v_venue_name+" has been finished");
        //send email
        Activity a= (Activity) con;
        emailHelper.sendEmailInBackground(con,a,
                v_email, //to email
                "Venue Time completed", //email title
                "Your allowed time for the venue "+v_venue_name+" has been finished"); //email content
    }

    private void makeVenueUnavailable()
    {
        String v_venue_name = getInputData().getString("v_venue_name");
        String v_email = getInputData().getString("v_email");
        int v_id = getInputData().getInt("v_id", -1);

        //set booked status to "ongoing"
        String v_bookedDetails_status = "ongoing";
        bookedDetailsDBHandler db22 = new bookedDetailsDBHandler(con);
        db22.updateStatus(v_id, v_email, v_venue_name, v_bookedDetails_status);
        db22.close();
        Log.d("My-Log", "ACTION_MAKE_UNAVAILABLE invoked!!!");

        //send notification
        notificationHelper.showNotification(con,"Venue Time started",
                "Your start time for venue "+v_venue_name+" has started");
        Activity a= (Activity) con;
        //send email
        emailHelper.sendEmailInBackground(con,a,
                v_email, //to email
                "Venue Time started", //email title
                "Your start time for venue "+v_venue_name+" has started"); //email content
    }
}
