package com.example.myapplication.user;

import android.annotation.SuppressLint;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TimePicker;
import android.widget.Toast;



import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.example.myapplication.R;
import com.example.myapplication.Utils.dateDecorator;
import com.example.myapplication.Utils.dateDecorator2;
import com.example.myapplication.Utils.emailHelper;
import com.example.myapplication.database.bookedDetailsDBHandler;
import com.example.myapplication.database.userDBHandler;
import com.example.myapplication.database.venueDBHandler;
import com.example.myapplication.venueSelect;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class bookVenue extends AppCompatActivity {

    private EditText editTextTextStartDate;
    private EditText editTextTextEndDate;
    private EditText emailEditText;
    private EditText venue_nameEditText;
    private Button confirmBtn;
    private Calendar calendar_start,calendar_end;
    private MaterialCalendarView start_calendarView,end_calendarView;
    private RelativeLayout start_layout,end_layout;
    private int start_year=-1,start_month=-1,start_day=-1;
    private int end_year=-1,end_month=-1,end_day=-1;
    private EditText dpt_name,phone,event;
    LinearLayout timeLayout,timeLayout2;
    RelativeLayout main_time_layout;
    dateDecorator dateDecorator;
    dateDecorator2 dateDecorator2;
    String  user_dept="NA";
    String user_phno="NA";
    String user_name="NA";
    List<CalendarDay> datesToMark = new ArrayList<>();
    List<String> redColoredDates = new ArrayList<>();
    List<String> blueColoredDates = new ArrayList<>();
    List<LocalDate> redColoredDates_toCheckMiddle = new ArrayList<>();
    List<String> redColoredTimes = new ArrayList<>();
    boolean start_flag=true,end_flag=true;
    List<String> time_slot_list = new ArrayList<>();
    List<String> markRed_timeSlot_list = new ArrayList<>();
    private List<Button> markRed_time_buttonList = new ArrayList<>();

    @SuppressLint("Range")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_venue);

        editTextTextStartDate = findViewById(R.id.editTextTextStartDate);
        editTextTextEndDate = findViewById(R.id.editTextTextEndDate);
        confirmBtn=findViewById(R.id.confirmButton);
        ImageButton imageButtonStartDate = findViewById(R.id.imageButtonStartDate);
        ImageButton imageButtonEndDate = findViewById(R.id.imageButtonEndDate);

        start_calendarView=findViewById(R.id.start_calendarView);
        end_calendarView=findViewById(R.id.end_calendarView);
        start_layout=findViewById(R.id.start_layout);
        end_layout=findViewById(R.id.end_layout);

        timeLayout=findViewById(R.id.timeLayout);
        timeLayout2=findViewById(R.id.timeLayout2);
        main_time_layout=findViewById(R.id.main_time_textview_layout);

        dpt_name=findViewById(R.id.editTextTextDeptName);
        phone=findViewById(R.id.editTextTextPhone);
        event=findViewById(R.id.editTextTextEventName);

        emailEditText=findViewById(R.id.editTextTextEmailAddress2);
        venue_nameEditText=findViewById(R.id.editTextTextName);

        calendar_start = Calendar.getInstance();
        calendar_end = Calendar.getInstance();

        Calendar currentDate = Calendar.getInstance();
        CalendarDay today = CalendarDay.from(
                currentDate.get(Calendar.YEAR),
                currentDate.get(Calendar.MONTH) + 1,
                currentDate.get(Calendar.DAY_OF_MONTH)
        );

        start_calendarView.state().edit()
                .setMinimumDate(today)
                .commit();

        end_calendarView.state().edit()
                .setMinimumDate(today)
                .commit();

        // get venue name from previous page
        Intent venueSelect_page=getIntent();
        String venue_name=venueSelect_page.getStringExtra("venue_name");

        //call markDates functions onCreate
        markDatesFun(venue_name);
        markDatesFun2(venue_name);

        // retreive image path
        venueDBHandler v_db=new venueDBHandler(this);
        Cursor v_cur=v_db.retreiveVenues(venue_name);
        v_cur.moveToFirst();
        @SuppressLint("Range") String imgPath = v_cur.getString(v_cur.getColumnIndex("img_path"));
        v_cur.close();
        v_db.close();


//      Use shared-data and retrieve user-email
        SharedPreferences sharedPreferences = getSharedPreferences("login_details", MODE_PRIVATE);
        String user_email = sharedPreferences.getString("user_email", "someone");

        userDBHandler user_db=new userDBHandler(bookVenue.this);
        Cursor user_cur=user_db.getUserDetails(user_email);

        if (user_cur.moveToFirst()) {
            user_dept = user_cur.getString(user_cur.getColumnIndex("dept"));
            user_phno = user_cur.getString(user_cur.getColumnIndex("phone"));
            user_name = user_cur.getString(user_cur.getColumnIndex("name"));
        } else
        {
            Log.e("My-Error:","Couldn't fetch the user_dept and phone");
            emailEditText.setError("Something went wrong,please re-login");
        }

        //set info to inputs
        emailEditText.setText(user_email);
        venue_nameEditText.setText(venue_name);
        dpt_name.setText(user_dept);
        phone.setText(user_phno);

        //load time-slots
        for(int i=9;i<=22;i++) {
            LinearLayout.LayoutParams btnParams = (new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            btnParams.setMargins(20, 5, 0, 20);
            Button btn = new Button(this);
            // default-color
            btn.setBackgroundResource(R.drawable.round_button_background);
            String btnTxt=i + "-" + (i+1);
            btn.setText(btnTxt);
            btn.setLayoutParams(btnParams);
            markRed_time_buttonList.add(btn);
            if(i<=15) {
                timeLayout.addView(btn);
            }
            else {
                timeLayout2.addView(btn);
            }

            final boolean[] click = {false};
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if(redColoredTimes.contains(btnTxt))
                    {
                        // do nothing
                    }
                    else
                    {
                        if(!click[0])
                        {
                            time_slot_list.add(btn.getText().toString());
                            btn.setBackgroundResource(R.drawable.clicked_round_button_background);
                            click[0] =true;
                        }else if(click[0])
                        {
                            time_slot_list.remove(btn.getText().toString());
                            btn.setBackgroundResource(R.drawable.round_button_background);
                            click[0] =false;
                        }
                    }
                }
            });
        }


        imageButtonStartDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(start_flag) {
                    start_flag=false;
                    start_layout.setVisibility(View.VISIBLE);
                }
                else
                {
                    start_flag=true;
                    start_layout.setVisibility(View.GONE);
                }
            }
        });

        imageButtonEndDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(end_flag)
                {
                    end_flag=false;
                    end_layout.setVisibility(View.VISIBLE);

                }
                else
                {
                    end_flag=true;
                    end_layout.setVisibility(View.GONE);
                }
            }
        });

        editTextTextStartDate.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // do nothing
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // do nothing
            }
            @Override
            public void afterTextChanged(Editable editable) {
                // clear marked time
                for (Button btn : markRed_time_buttonList) {
                    btn.setBackgroundResource(R.drawable.round_button_background);
                }
                String s_date=editTextTextStartDate.getText().toString();
                String e_date=editTextTextEndDate.getText().toString();
                if(s_date.equals(e_date))
                {
                    String[] dateParts = s_date.split("/");
//                    String selectedDate = dateParts[2] + "-" + dateParts[1] + "-" + dateParts[0];
                    String selectedDate = String.format("%s-%02d-%02d", dateParts[2], Integer.parseInt(dateParts[1]), Integer.parseInt(dateParts[0]));
                    markTimeSlots(venue_name,selectedDate);
                    main_time_layout.setVisibility(View.VISIBLE);
                }
                else
                {
                    main_time_layout.setVisibility(View.GONE);
                }
            }
        });

        editTextTextEndDate.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // do nothing
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // do nothing
            }
            @Override
            public void afterTextChanged(Editable editable) {
                // clear marked time
                for (Button btn : markRed_time_buttonList) {
                    btn.setBackgroundResource(R.drawable.round_button_background);
                }
                String s_date=editTextTextStartDate.getText().toString();
                String e_date=editTextTextEndDate.getText().toString();
                if(s_date.equals(e_date))
                {
                    String[] dateParts = s_date.split("/");

//                    String selectedDate = dateParts[2] + "-" + dateParts[1] + "-" + dateParts[0];
                    String selectedDate = String.format("%s-%02d-%02d", dateParts[2], Integer.parseInt(dateParts[1]), Integer.parseInt(dateParts[0]));
                    markTimeSlots(venue_name,selectedDate);
                    main_time_layout.setVisibility(View.VISIBLE);
                }
                else
                {
                    main_time_layout.setVisibility(View.GONE);
                }
            }
        });

//        main book button
        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String v_dpt=dpt_name.getText().toString();
                String v_phno= phone.getText().toString();
                String v_event=event.getText().toString();
                String s_date=editTextTextStartDate.getText().toString();
                String e_date=editTextTextEndDate.getText().toString();

                String[] time_slot_arr = time_slot_list.toArray(new String[time_slot_list.size()]);
                String time_slot_string = "";
                // concatinate and store it in a string
                for (int i = 0; i < time_slot_arr.length; i++) {
                    // concatinate and store it in a string
                    time_slot_string += time_slot_arr[i];
                    // Check if it's not the last element before adding a comma
                    if (i < time_slot_arr.length - 1) {
                        time_slot_string += ",";
                    }
                }

                // check if user skipped time-slots in the middle
                boolean check_time_skipped=false;
                List<Integer> c_list = new ArrayList<>();
                String k1[]=time_slot_string.split("-");
                for(int k=1;k<k1.length-1;k++)
                {
                    String k2[]=k1[k].split(",");
                    if(k2.length>1)
                    {
                        if(k2[0].equals(k2[1]))
                            c_list.add(1);
                        else
                            c_list.add(0);
                    }
                }

                if(c_list.contains(0))
                    check_time_skipped=true;

                boolean date_val=true;
                boolean other_val=true;

                // Other validations
                if (v_dpt.isEmpty())
                {
                    dpt_name.setError("Required field");
                    other_val = false;
                    return;
                }
                if( v_phno.isEmpty())
                {
                    phone.setError("Required field");
                    other_val = false;
                    return;
                }
                if (v_phno.length() != 10)
                {
                    phone.setError("Phone number must be 10 digits");
                    other_val = false;
                    return;
                }
                //date and time validations
                if(start_year==-1)
                {
                    Toast.makeText(bookVenue.this,"Please select a start date",Toast.LENGTH_SHORT).show();
                    date_val=false;
                    return;
                }
                if(end_year==-1)
                {
                    Toast.makeText(bookVenue.this,"Please select a end date",Toast.LENGTH_SHORT).show();
                    date_val=false;
                    return;
                }

//                check if there are redColored dates in between of start and end dates ( Not neeeded)
//                boolean ifRedPresentInMiddle = checkIfRedPresentInMiddle(st_dt, end_dt);

                String[] st_dt_arr = s_date.split("/");
                String st_dt = st_dt_arr[2] + "-" + String.format("%02d", Integer.parseInt(st_dt_arr[1])) + "-" + String.format("%02d", Integer.parseInt(st_dt_arr[0]));
                String[] end_dt_arr = e_date.split("/");
                String end_dt = end_dt_arr[2] + "-" + String.format("%02d", Integer.parseInt(end_dt_arr[1])) + "-" + String.format("%02d", Integer.parseInt(end_dt_arr[0]));

                SimpleDateFormat dateFormat1 = new SimpleDateFormat("dd/MM/yyyy");
                String todaysDate = dateFormat1.format(new Date());

                if(blueColoredDates.contains(st_dt) && !blueColoredDates.contains(end_dt))
                {
                    Toast.makeText(bookVenue.this,"There are booked slots in between of your start and end date",Toast.LENGTH_SHORT).show();
                    date_val=false;
                }
                else if(blueColoredDates.contains(end_dt) && !blueColoredDates.contains(st_dt))
                {
                    Toast.makeText(bookVenue.this,"There are booked slots in between of your start and end date",Toast.LENGTH_SHORT).show();
                    date_val=false;
                }
                else if(!s_date.equals(e_date) && s_date.equals(todaysDate))
                {
                    Toast.makeText(bookVenue.this,"You cannot start today for multiple days booking",Toast.LENGTH_SHORT).show();
                    date_val=false;
                }
                else if(s_date.equals(e_date) && time_slot_list.isEmpty())
                {
                    Toast.makeText(bookVenue.this,"Please select a time-slot",Toast.LENGTH_SHORT).show();
                    date_val=false;
                }
                else if(check_time_skipped)
                {
                    Toast.makeText(bookVenue.this,"Please select time-slots in consecutive order",Toast.LENGTH_LONG).show();
                    date_val=false;
                }
                else
                {

                    calendar_start.set(start_year, start_month, start_day);
                    calendar_end.set(end_year, end_month, end_day);

                    LocalDate startDate = null;
                    LocalDate endDate = null;
                    LocalDate currentDate = null;
                    long cuurrent_startdaysDiff = 0;
                    long start_endDaysDiff = 0;
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {

                        startDate = LocalDate.of(start_year, start_month, start_day);
                        endDate = LocalDate.of(end_year, end_month, end_day);
                        currentDate = LocalDate.now();
                        cuurrent_startdaysDiff = ChronoUnit.DAYS.between(currentDate, startDate);
                        start_endDaysDiff=ChronoUnit.DAYS.between(startDate, endDate);
                    }
                    if (calendar_start.after(calendar_end))
                    {
                        Toast.makeText(bookVenue.this, "Ending date must be greater than Starting date", Toast.LENGTH_LONG).show();
                        date_val=false;
                    }
                    else if(st_dt.equals(end_dt)==false && checkIfRedPresentInMiddle(st_dt,end_dt))
                    {
                        Toast.makeText(bookVenue.this, "There are booked slots in between of your start and end date", Toast.LENGTH_LONG).show();
                        date_val=false;
                    }
                    else if (cuurrent_startdaysDiff > 7) {
                        Toast.makeText(bookVenue.this, "Cannot pre-book before 7 days", Toast.LENGTH_SHORT).show();
                        date_val = false;
                    }
                    else if (start_endDaysDiff > 5) {
                        Toast.makeText(bookVenue.this, "Booking cannot be for more than 5 days", Toast.LENGTH_SHORT).show();
                        date_val = false;
                    }

                }

                if(date_val && other_val)
                {
                    calendar_start.set(start_year, start_month-1, start_day);
                    calendar_end.set(end_year, end_month-1, end_day);

                    // Format the date
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                    String startDateString = dateFormat.format(calendar_start.getTime());
                    String endDateString = dateFormat.format(calendar_end.getTime());

                    bookedDetailsDBHandler db=new bookedDetailsDBHandler(bookVenue.this);
                     if(v_event.isEmpty())
                         v_event="na";
                    boolean check=db.bookVenue(user_email,venue_name,user_dept,user_phno,v_event,startDateString,endDateString,time_slot_string,imgPath);
                    if(!check)
                        Toast.makeText(bookVenue.this, "Something went wrong,Please re-login!", Toast.LENGTH_LONG).show();
                    else
                    {
                        //send email to notify admin about the new request
                        emailHelper.sendEmailInBackground(bookVenue.this,bookVenue.this,
                                getString(R.string.admin_email), //to email
                                "New Request", //email title
                                "There is a new booking request by "+user_name+" for venue "+venue_name+"."); //email content
                        Toast.makeText(bookVenue.this, "Request successfully sent", Toast.LENGTH_LONG).show();

//                        Intent i=new Intent(bookVenue.this, userTransactions.class);
//                        startActivity(i);
                    }
                }
            }
        });

        start_calendarView.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
                start_flag=false;
                int year = date.getYear();
                int month = date.getMonth();
                int day = date.getDay();

                // check if user is pressing red color dates
//                String dt = year + "-" + month + "-" + day;
                String dt = String.format("%04d-%02d-%02d", year, month, day);

                if(redColoredDates.contains(dt))
                {
                    // do nothing
                }
                else
                {
                    String selectedDate = day + "/" + month + "/" + year;

                    start_year=year;start_month=month;start_day=day;
                    editTextTextStartDate.setText(selectedDate);
                }
            }
        });

        end_calendarView.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
                end_flag=false;
                int year = date.getYear();
                int month = date.getMonth();
                int day = date.getDay();

                // check if user is pressing red color dates
//                String dt = year + "-" + month + "-" + day;
                String dt = String.format("%04d-%02d-%02d", year, month, day);

                if(redColoredDates.contains(dt))
                {
                    // do nothing
                }
                else
                {
                    String selectedDate = day + "/" + month + "/" + year;

                    end_year=year;end_month=month;end_day=day;
                    editTextTextEndDate.setText(selectedDate);
                }
            }
        });

    }

    public boolean checkIfRedPresentInMiddle(String st_dt,String end_dt) // also checks blue
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

    public void markTimeSlots(String v_venueName,String v_selectDate)
    {
        redColoredTimes.clear();
        bookedDetailsDBHandler db = new bookedDetailsDBHandler(bookVenue.this);
        Cursor cur = db.retreiveBookedVenue_onSameDate(v_venueName,"yes",v_selectDate);
        if(cur.moveToFirst())
        {
            do {
                markRed_timeSlot_list.add(cur.getString(cur.getColumnIndex("time_slot")));
            }while (cur.moveToNext());
        }
        cur.close();
        db.close();
        String[] time_slot_arr = markRed_timeSlot_list.toArray(new String[markRed_timeSlot_list.size()]);
        String time_slot_string = "";
        // concatinate and store it in a string
        for (int i = 0; i < time_slot_arr.length; i++) {
            time_slot_string += time_slot_arr[i];
            if (i < time_slot_arr.length - 1)
                time_slot_string += ",";
        }
        String[] time_slot_arr2=time_slot_string.split(",");
        List<String> time_list = Arrays.asList(time_slot_arr2);

        for (String str : time_list) {
            for (Button button : markRed_time_buttonList) {
                String buttonText = button.getText().toString();
                if (str.equals(buttonText)) {
                    button.setBackgroundResource(R.drawable.red_round_button_background);

                    //store it,for making it unClickable
                    redColoredTimes.add(buttonText);
                }
            }
        }
        markRed_timeSlot_list.clear();
    }
    public void markDatesFun(String v_venueName)
    {
        bookedDetailsDBHandler db=new bookedDetailsDBHandler(bookVenue.this);
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
                        String[] partsDate = startDate.toString().split("-");

                        int year= Integer.parseInt(partsDate[0]);
                        int month=Integer.parseInt(partsDate[1]);
                        int day=Integer.parseInt(partsDate[2]);

                        datesToMark.add(CalendarDay.from(year,month,day));

                        // add to seperate list for use in disabling it
                        String dt = String.format("%04d-%02d-%02d", year, month, day);
//                        String dt = year + "-" + month + "-" + day;
                        redColoredDates.add(dt);
                        redColoredDates_toCheckMiddle.add(startDate);

                        startDate = startDate.plusDays(1);
                    }
                }
            } while (cur.moveToNext());
        }
        dateDecorator = new dateDecorator(bookVenue.this,datesToMark);
        Context con=bookVenue.this;
        start_calendarView.addDecorator(dateDecorator);
        start_calendarView.invalidateDecorators();

        end_calendarView.addDecorator(dateDecorator);
        end_calendarView.invalidateDecorators();
        cur.close();
        db.close();
    }

    public void markDatesFun2(String v_venueName)
    {
        datesToMark.clear();
        bookedDetailsDBHandler db=new bookedDetailsDBHandler(bookVenue.this);
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
                            String[] partsDate = startDate.toString().split("-");

                            int year= Integer.parseInt(partsDate[0]);
                            int month=Integer.parseInt(partsDate[1]);
                            int day=Integer.parseInt(partsDate[2]);

                            datesToMark.add(CalendarDay.from(year,month,day));

//                            String dt = year + "-" + month + "-" + day;
                            String dt = String.format("%04d-%02d-%02d", year, month, day);

                            // remove blue colored dates from list
                            blueColoredDates.add(dt);
                            if(redColoredDates.contains(dt))
                            {
                                redColoredDates.remove(dt);
                            }
                            startDate = startDate.plusDays(1);
                        }
                    }
                }
            } while (cur.moveToNext());
        }
        dateDecorator2 = new dateDecorator2(bookVenue.this,datesToMark);
        Context con=bookVenue.this;
        start_calendarView.addDecorator(dateDecorator2);
        start_calendarView.invalidateDecorators();

        end_calendarView.addDecorator(dateDecorator2);
        end_calendarView.invalidateDecorators();
        cur.close();
        db.close();
    }
}