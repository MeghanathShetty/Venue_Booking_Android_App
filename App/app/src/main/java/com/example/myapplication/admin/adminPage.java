package com.example.myapplication.admin;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.R;
import com.example.myapplication.Utils.dateDecorator;
import com.example.myapplication.Utils.dateDecorator2;
import com.example.myapplication.database.bookedDetailsDBHandler;
import com.example.myapplication.database.userDBHandler;
import com.example.myapplication.database.venueDBHandler;
import com.example.myapplication.user.bookVenue;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class adminPage extends AppCompatActivity {
    LinearLayout downLayout;
    ImageView addVenue,bookedVenue,viewAllVenues,viewAllUsers;
    List<LocalDate> redColoredDates_toCheckMiddle = new ArrayList<>();
    List<Integer> redColoredTimesSlots = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_page);
        downLayout = findViewById(R.id.downLayout);
        addVenue = findViewById(R.id.addImageView);
        bookedVenue = findViewById(R.id.bookedImageView);
        viewAllVenues = findViewById(R.id.veiwAllVenue);
        viewAllUsers = findViewById(R.id.viewAllUsers);

        // Custom toast thingy
        LayoutInflater inflater = getLayoutInflater();
        View customToastLayout = inflater.inflate(R.layout.custom_toast, null);
        TextView c_txt1 = customToastLayout.findViewById(R.id.custom_txt1);
        TextView c_txt2 = customToastLayout.findViewById(R.id.custom_txt2);

        // Create an AlertDialog and set the custom layout
        AlertDialog.Builder builder = new AlertDialog.Builder(adminPage.this);
        builder.setView(customToastLayout);
        AlertDialog customToastDialog = builder.create();

        // Custom Toast for large imageView
        LayoutInflater img_inflater = getLayoutInflater();
        View img_customToastLayout = img_inflater.inflate(R.layout.custom_image_toast, null);
        ImageView custom_imageView = img_customToastLayout.findViewById(R.id.imageView);
        // Create an AlertDialog and set the custom layout
        AlertDialog.Builder img_builder = new AlertDialog.Builder(this);
        img_builder.setView(img_customToastLayout);
        AlertDialog img_customToastDialog = img_builder.create();

        // check if database is created
        SharedPreferences retrieveShared = getSharedPreferences("admin_login", MODE_PRIVATE);
        String adminLogin = retrieveShared.getString("logged_in", "");

        // check if app is opening for first time ( Assuming admin also has an user account in same phone )
        if (adminLogin.equals(null) || adminLogin.isEmpty() || adminLogin == null)
        {
                Toast.makeText(adminPage.this,"Welcome Admin",Toast.LENGTH_LONG).show();
        }
        else
        {
            bookedDetailsDBHandler db = new bookedDetailsDBHandler(this);
            Cursor cursor = db.showNewReqs();

            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                do {
                    @SuppressLint("Range") int id = cursor.getInt(cursor.getColumnIndex("id"));
                    @SuppressLint("Range") String venueName = cursor.getString(cursor.getColumnIndex("venue_name"));
                    @SuppressLint("Range") String userEmail = cursor.getString(cursor.getColumnIndex("user_email"));
                    @SuppressLint("Range") String userDpt = cursor.getString(cursor.getColumnIndex("department"));
                    @SuppressLint("Range") String userPhone = cursor.getString(cursor.getColumnIndex("user_phone"));
                    @SuppressLint("Range") String event = cursor.getString(cursor.getColumnIndex("event"));
                    @SuppressLint("Range") String s_date = cursor.getString(cursor.getColumnIndex("start_date"));
                    @SuppressLint("Range") String e_date = cursor.getString(cursor.getColumnIndex("end_date"));
                    @SuppressLint("Range") String timeSlot = cursor.getString(cursor.getColumnIndex("time_slot"));
                    @SuppressLint("Range") String imgPath = cursor.getString(cursor.getColumnIndex("img_path"));

                    String stt_time = "na";
                    String edd_time = "na";

    //                venueDBHandler v_db=new venueDBHandler(this);
    //                Cursor v_cur=v_db.retreiveVenues(venueName);
    //                v_cur.moveToFirst();
    //                @SuppressLint("Range") String imgPath = v_cur.getString(v_cur.getColumnIndex("img_path"));
    //                v_cur.close();
    //                v_db.close();

                    userDBHandler u_db = new userDBHandler(this);
                    Cursor u_cur = u_db.getUserDetails(userEmail);
                    u_cur.moveToFirst();
                    @SuppressLint("Range") String userName = u_cur.getString(u_cur.getColumnIndex("name"));
                    u_cur.close();
                    u_db.close();


                    LinearLayout.LayoutParams layoutParams = (new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                    layoutParams.setMargins(0, 7, 0, 0);
                    LinearLayout main = new LinearLayout(this);
                    main.setLayoutParams(layoutParams);
                    main.setBackgroundColor(getResources().getColor(R.color.white));
                    main.setOrientation(LinearLayout.HORIZONTAL);
                    downLayout.addView(main);


                    CardView cardView = new CardView(this);
                    LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                    );
                    cardParams.gravity = Gravity.CENTER_VERTICAL;
                    cardParams.setMargins(20, 20, 10, 20);
                    cardView.setLayoutParams(cardParams);
                    cardView.setRadius(20);
                    cardView.setCardBackgroundColor(getResources().getColor(R.color.white));

                    ImageView img = new ImageView(this);
                    LinearLayout.LayoutParams imgParams = new LinearLayout.LayoutParams(300,250);
                    imgParams.setMargins(0, 0, 0, 0);
                    img.setLayoutParams(imgParams);
                    img.setBackgroundColor(getResources().getColor(R.color.white));
                    Picasso.get().load(imgPath).into(img);
                    img.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    cardView.addView(img);
                    main.addView(cardView);

                    LinearLayout.LayoutParams subParams = (new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                    subParams.setMargins(0, 0, 0, 0);
                    LinearLayout subMain = new LinearLayout(this);
                    subMain.setLayoutParams(subParams);
                    subMain.setOrientation(LinearLayout.VERTICAL);
                    subMain.setBackgroundColor(getResources().getColor(R.color.white));
                    main.addView(subMain);

                    LinearLayout.LayoutParams txtParam = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    txtParam.setMargins(0, 20, 0, 0);

                    // Venue Name
                    TextView t02 = new TextView(this);
                    t02.setText("Venue : " + venueName);
                    t02.setPadding(20, 0, 0, 0);
                    t02.setLayoutParams(txtParam);
                    t02.setTextSize(16);
                    subMain.addView(t02);

                    // Event Name
                    TextView t2 = new TextView(this);
                    if (event.equals("na"))
                        event = "Not Available";
                    t2.setText("Event : " + event);
                    t2.setPadding(20, 0, 0, 0);
                    t2.setLayoutParams(txtParam);
                    t2.setTextSize(16);
                    subMain.addView(t2);

                    //User Name textview
                    TextView t1 = new TextView(this);
                    t1.setText("User : " + userName);
                    t1.setPadding(20, 0, 0, 0);
                    t1.setLayoutParams(txtParam);
                    t1.setTextSize(16);
                    subMain.addView(t1);

                    //Department Name
                    TextView t3 = new TextView(this);
                    t3.setText("Department : " + userDpt);
                    t3.setPadding(20, 0, 0, 0);
                    t3.setLayoutParams(txtParam);
                    t3.setTextSize(16);
                    subMain.addView(t3);

                    // Date
                    TextView t4 = new TextView(this);
                    t4.setPadding(20, 0, 0, 0);
                    t4.setLayoutParams(txtParam);
                    t4.setTextSize(16);

                    TextView t5 = new TextView(this);
                    t5.setPadding(20, 0, 0, 0);
                    t5.setLayoutParams(txtParam);
                    t5.setTextSize(16);

                    // Check expiry calculations
                    Calendar currentDate = Calendar.getInstance();

                    //accept reject buttons and expiry textview
                    // Create a horizontal LinearLayout for the buttons
                    LinearLayout buttonLayout = new LinearLayout(this);
                    buttonLayout.setLayoutParams(new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                    ));
                    buttonLayout.setOrientation(LinearLayout.HORIZONTAL);
//                    buttonLayout.setGravity(Gravity.CENTER);
    //                buttonLayout.setPadding(-160,20,0,20);

                    Button button1 = new Button(this);
                    button1.setText("Accept");

                    Button button2 = new Button(this);
                    button2.setText("Reject");

                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams( LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                    );
                    params.setMargins(20, 20, 0, 20);

                    button1.setLayoutParams(params);
                    button1.setBackgroundResource(R.color.green);
                    button1.setTextColor(getResources().getColor(R.color.Blue5));

                    button2.setLayoutParams(params);
                    button2.setBackgroundResource(R.color.red);
                    button2.setTextColor(getResources().getColor(R.color.Blue5));

                    LinearLayout.LayoutParams expParams = new LinearLayout.LayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                    expParams.setMargins(20, 10, 0, 20);
                    TextView exp = new TextView(this);
                    exp.setLayoutParams(expParams);
                    exp.setText("Request Expired");
                    exp.setTextColor(getResources().getColor(R.color.red));
                    exp.setTextSize(20);

                    //Date
                    if (!s_date.equals(e_date)) {
                        // set From date
                        t4.setText("From : " + s_date);
                        subMain.addView(t4);

                        // set To date
                        t5.setText("To : " + e_date);
                        subMain.addView(t5);

                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                        Date startDate = null;
                        try {
                            startDate = dateFormat.parse(s_date);
                        } catch (ParseException e) {
                            throw new RuntimeException(e);
                        }
                        Calendar startCalendar = Calendar.getInstance();
                        startCalendar.setTime(startDate);


                        // display accept or reject buttons based on request expiry
                        if (startCalendar.after(currentDate) || startCalendar.equals(currentDate)) {
                            buttonLayout.addView(button1);
                            buttonLayout.addView(button2);
                            subMain.addView(buttonLayout);
                        } else  // expired
                        {
                            buttonLayout.addView(exp);
                            subMain.addView(buttonLayout);

                            bookedDetailsDBHandler db1 = new bookedDetailsDBHandler(adminPage.this);
                            db1.updateApproval(id, userEmail, venueName, "expired", adminPage.this);
                            db1.close();

                            Toast.makeText(adminPage.this, "Expired for " + venueName, Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        // set From date
                        t4.setText("Date : " + s_date);
                        subMain.addView(t4);

                        //retreive the first and last hour from the timeSlot list
                        char[] char_timeSlot = timeSlot.toCharArray();
                        int st_hour = Integer.parseInt(String.valueOf(char_timeSlot[0]));
                        if (st_hour != 9)
                            st_hour = Integer.parseInt(String.valueOf(char_timeSlot[0]) + String.valueOf(char_timeSlot[1]));
                        String end_hour = String.valueOf(char_timeSlot[char_timeSlot.length - 2]) + String.valueOf(char_timeSlot[char_timeSlot.length - 1]);

                        // store in variable for later use
                        stt_time = String.valueOf(st_hour);
                        edd_time = end_hour;
                        // set Time slot
                        t5.setText("Time Slot : " + String.valueOf(st_hour) + ":00 -" + end_hour + ":00");
                        subMain.addView(t5);

                        String start_date = s_date + " " + st_hour + ":00:00";
                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                        Date startDate = null;
                        try {
                            startDate = dateFormat.parse(start_date);
                        } catch (ParseException e) {
                            throw new RuntimeException(e);
                        }
                        Calendar startCalendar = Calendar.getInstance();
                        startCalendar.setTime(startDate);

                        // display accept or reject buttons based on request expiry
                        if (startCalendar.after(currentDate) || startCalendar.equals(currentDate)) {
                            buttonLayout.addView(button1);
                            buttonLayout.addView(button2);
                            subMain.addView(buttonLayout);
                        } else  // expired
                        {
                            buttonLayout.addView(exp);
                            subMain.addView(buttonLayout);

                            bookedDetailsDBHandler db1 = new bookedDetailsDBHandler(adminPage.this);
                            db1.updateApproval(id, userEmail, venueName, "expired", adminPage.this);
                            db1.close();

                        }
                    }

                    // handle click events
                    subMain.setOnClickListener(new View.OnClickListener() {       // show extra details of user in custom toast
                        @Override
                        public void onClick(View view) {
                            // Display the AlertDialog
                            // set texts for custom layout
                            c_txt1.setText(userPhone);
                            c_txt2.setText(userEmail);
                            customToastDialog.show();
                        }
                    });

                    // show image in large view using custom
                    img.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Picasso.get().load(imgPath).into(custom_imageView);
                            img_customToastDialog.show();
                        }
                    });

                    // accept request
                    String finalStt_time = stt_time;
                    String finalEdd_time = edd_time;
                    button1.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            bookedDetailsDBHandler db = new bookedDetailsDBHandler(adminPage.this);

                            // first check if venue is occupied  on the requested dates
                            markTimeFun(venueName, s_date);
                            if (!s_date.equals(e_date)) {
                                markDatesFun(venueName); // mark red & blue dates
                                if (checkIfRedDatePresentInMiddle(s_date, e_date)) {
                                    db.updateApproval(id, userEmail, venueName, "no", adminPage.this);
                                    exp.setText("Venue Busy");
                                    exp.setTextColor(getResources().getColor(R.color.red));
                                } else {
                                    db.updateApproval(id, userEmail, venueName, "yes", adminPage.this);
                                    exp.setText("Accepted");
                                    exp.setTextColor(getResources().getColor(R.color.green));
                                }

                            } else if (s_date.equals(e_date)) {
    //                            System.out.println(finalStt_time);
    //                            System.out.println(finalEdd_time);

                                markDatesFun(venueName); // mark red & blue dates
                                markDatesFun2(venueName); // remove blue dates
                                markTimeFun(venueName, s_date);
                                if (checkIfRedDatePresentInMiddle(s_date, e_date)) {
                                    db.updateApproval(id, userEmail, venueName, "no", adminPage.this);
                                    exp.setText("Venue Busy");
                                    exp.setTextColor(getResources().getColor(R.color.red));
                                } else if (checkIfRedTimePresentInMiddle(finalStt_time, finalEdd_time)) {
                                    db.updateApproval(id, userEmail, venueName, "no", adminPage.this);
                                    exp.setText("Time Slot Occupied");
                                    exp.setTextColor(getResources().getColor(R.color.red));
                                } else {
                                    db.updateApproval(id, userEmail, venueName, "yes", adminPage.this);
                                    exp.setText("Accepted");
                                    exp.setTextColor(getResources().getColor(R.color.green));
                                }
                            }
    //                        else
    //                        {
    //                            db.updateApproval(id,userEmail,venueName,"yes",adminPage.this);
    //                            exp.setText("Accepted");
    //                            exp.setTextColor(getResources().getColor(R.color.green));
    //                            System.out.println("Hello4");
    //
    //                        }
                            db.close();

                            buttonLayout.removeAllViews();
                            buttonLayout.addView(exp);

                            // reset marked dates list and also time slots
                            redColoredDates_toCheckMiddle.clear();
                            redColoredTimesSlots.clear();
                        }
                    });

                    // reject request
                    button2.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            bookedDetailsDBHandler db = new bookedDetailsDBHandler(adminPage.this);
                            db.updateApproval(id, userEmail, venueName, "no", adminPage.this);
                            db.close();

                            buttonLayout.removeAllViews();

                            exp.setText("Rejected");
                            exp.setTextColor(getResources().getColor(R.color.red));
                            buttonLayout.addView(exp);
    //                        Toast.makeText(adminPage.this,"Request rejected for "+venueName,Toast.LENGTH_SHORT).show();

    //                        Intent releod= new Intent(adminPage.this,adminPage.class);
    //                        startActivity(releod);

                        }
                    });

                } while (cursor.moveToNext());
            } else {
                LinearLayout.LayoutParams txtParam = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                txtParam.setMargins(0, 500, 0, 0);

                TextView t1 = new TextView(this);
                t1.setLayoutParams(txtParam);
                t1.setBackgroundColor(this.getColor(R.color.Blue5));
                t1.setGravity(Gravity.CENTER);
                t1.setTextSize(24);
                t1.setText("No new Requests");
                downLayout.addView(t1);
            }

            // close
            db.close();
            cursor.close();

        }

//         goto different pages
        addVenue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent nxtPage=new Intent(adminPage.this,com.example.myapplication.admin.addVenue.class);
                startActivity(nxtPage);
            }
        });

        bookedVenue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent nxtPage=new Intent(adminPage.this, booked_venues.class);
                startActivity(nxtPage);
            }
        });

        viewAllVenues.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent nxtPage=new Intent(adminPage.this, viewAllVenues.class);
                startActivity(nxtPage);
            }
        });

        viewAllUsers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent nxtPage=new Intent(adminPage.this,com.example.myapplication.admin.viewAllUsers.class);
                startActivity(nxtPage);
            }
        });
    }


    // check if the venue is occupied on that date ( or in middle )
    public boolean checkIfRedDatePresentInMiddle(String st_dt,String end_dt)
    {
        boolean check=false;

        // Parse the start and end dates as LocalDate objects
        LocalDate startDate = null;
        LocalDate endDate = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startDate = LocalDate.parse(st_dt);
            endDate = LocalDate.parse(end_dt);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            while (!startDate.isAfter(endDate)) {

                if(redColoredDates_toCheckMiddle.contains(startDate))
                {
                    check=true;
                    break;
                }

                startDate = startDate.plusDays(1);
            }
        }
        return check;
    }

    // check if the venue is occupied on that time slot ( or in middle )
    public boolean checkIfRedTimePresentInMiddle(String st_time,String ed_time)
    {
        boolean check=false;

        int s_time=Integer.parseInt(st_time);
        int e_time=Integer.parseInt(ed_time);

        for(int i=s_time;i<=e_time;i++)
        {
            if(redColoredTimesSlots.contains(i))
            {
                check=true;
                break;
            }
        }
        return check;
    }

    // store booked time slots in a list
    @SuppressLint("Range")
    public void markTimeFun(String v_venueName, String v_startDate)
    {
        bookedDetailsDBHandler db = new bookedDetailsDBHandler(adminPage.this);
        Cursor cur = db.retreiveBookedVenue_onSameDate(v_venueName,"yes",v_startDate);
        if(cur.moveToFirst())
        {
            do {

                String time=cur.getString(cur.getColumnIndex("time_slot"));
//                redColoredTimesSlots.add(cur.getString(cur.getColumnIndex("time_slot")));
                String[] parts = time.split(",");
                List<Integer> numbers = new ArrayList<>();

                for (String part : parts) {
                    String[] range = part.split("-");
                    if (range.length == 2) {
                        int start = Integer.parseInt(range[0]);
                        int end = Integer.parseInt(range[1]);
                        redColoredTimesSlots.add(start);
                        redColoredTimesSlots.add(end);
//                        System.out.println(String.valueOf(start)+String.valueOf(end));

                    }
                }
            }while (cur.moveToNext());

        }
        cur.close();
        db.close();

    }


    // get the booked dates for the venue and store in list ( red and blue colored dates ) ( remove blue in another function )
    public void markDatesFun(String v_venueName)
    {
        bookedDetailsDBHandler db=new bookedDetailsDBHandler(adminPage.this);
        Cursor cur=db.retreiveVenue_bookedVenue(v_venueName,"yes");
        if(cur.moveToFirst())
        {
            do
            {
                @SuppressLint("Range") String startDt = cur.getString(cur.getColumnIndex("start_date"));
                @SuppressLint("Range") String endDt = cur.getString(cur.getColumnIndex("end_date"));

                // Parse the start and end dates as LocalDate objects
                LocalDate startDate = null;
                LocalDate endDate = null;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    startDate = LocalDate.parse(startDt);
                    endDate = LocalDate.parse(endDt);
                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    while (!startDate.isAfter(endDate)) {

                        // add marked dates to list
                        redColoredDates_toCheckMiddle.add(startDate);

                        startDate = startDate.plusDays(1);
                    }
                }
            } while (cur.moveToNext());
        }
        cur.close();
        db.close();
    }

    // remove the blue colored dates from the "redColoredDates_toCheckMiddle" list
    public void markDatesFun2(String v_venueName)
    {
        bookedDetailsDBHandler db=new bookedDetailsDBHandler(adminPage.this);
        Cursor cur=db.retreiveVenue_bookedVenue(v_venueName,"yes");
        if(cur.moveToFirst())
        {
            do
            {
                @SuppressLint("Range") String startDt = cur.getString(cur.getColumnIndex("start_date"));
                @SuppressLint("Range") String endDt = cur.getString(cur.getColumnIndex("end_date"));

                if(startDt.equals(endDt))
                {
                    LocalDate startDate = null;
                    LocalDate endDate = null;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        startDate = LocalDate.parse(startDt);
                        endDate = LocalDate.parse(endDt);
                    }

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        while (!startDate.isAfter(endDate)) {

                            // remove blue colored dates from list
                            if(redColoredDates_toCheckMiddle.contains(startDate))
                            {
                                redColoredDates_toCheckMiddle.remove(startDate);
                            }
                            startDate = startDate.plusDays(1);
                        }
                    }
                }
            } while (cur.moveToNext());
        }
        cur.close();
        db.close();
    }

}