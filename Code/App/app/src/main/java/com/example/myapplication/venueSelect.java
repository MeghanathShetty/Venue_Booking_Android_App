package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.Utils.dateDecorator;
import com.example.myapplication.Utils.dateDecorator2;
import com.example.myapplication.admin.addVenue;
import com.example.myapplication.database.bookedDetailsDBHandler;
import com.example.myapplication.database.venueDBHandler;
import com.example.myapplication.user.bookVenue;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.CalendarMode;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import androidx.appcompat.widget.SearchView;
import androidx.cardview.widget.CardView;

public class venueSelect extends AppCompatActivity {
    LinearLayout mainLayout,timeLayout,timeLayout2;
    TextView red_text,blue_text;
    String venueType=null;
    int cursor_count=0;
    MaterialCalendarView calendarView;
    Button closeCalenderBtn;
    RelativeLayout datePickerLayout;
    dateDecorator dateDecorator;
    dateDecorator2 dateDecorator2;
    RelativeLayout main_time_layout;
    List<CalendarDay> datesToMark = new ArrayList<>();
    List<String> blueColoredDates = new ArrayList<>();
    List<String> startDates_list = new ArrayList<>();
    List<String> endDates_list = new ArrayList<>();
    List<String> timeSlot_list = new ArrayList<>();
    private List<Button> time_buttonList = new ArrayList<>();
    String global_venueName="";
    List<LinearLayout> searchLayout_list = new ArrayList<>();
    SearchView searchView;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_venue_select);

        mainLayout=findViewById(R.id.mainLayout);
        calendarView=findViewById(R.id.calendarView);
        closeCalenderBtn=findViewById(R.id.closeCalenderBtn);
        datePickerLayout=findViewById(R.id.datePickerlayout);
        main_time_layout=findViewById(R.id.main_time_slot_layout);
        timeLayout=findViewById(R.id.timeLayout);
        timeLayout2=findViewById(R.id.timeLayout2);
        red_text=findViewById(R.id.red_text);
        blue_text=findViewById(R.id.blue_text);
        searchView = findViewById(R.id.searchView);

        Calendar cur_date = Calendar.getInstance();
        CalendarDay today = CalendarDay.from(
                cur_date.get(Calendar.YEAR),
                cur_date.get(Calendar.MONTH) + 1,
                cur_date.get(Calendar.DAY_OF_MONTH)
        );
        calendarView.state().edit()
                .setMinimumDate(today)  // set minimum date for calendar
                .commit();

        //load time-slots
        for(int j=9;j<=22;j++) {
            LinearLayout.LayoutParams btnParams = (new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            btnParams.setMargins(20, 5, 0, 20);
            Button btn = new Button(venueSelect.this);
            // default-color
            btn.setBackgroundResource(R.drawable.round_button_background);
            String btnText = j + "-" + (j + 1);
            btn.setText(btnText);
            btn.setLayoutParams(btnParams);
            time_buttonList.add(btn);
            if (j <= 15) {
                timeLayout.addView(btn);
            } else {
                timeLayout2.addView(btn);
            }
        }

        Intent intent=getIntent();
        venueType=intent.getStringExtra("type");
        venueDBHandler db=new venueDBHandler(venueSelect.this);

        bookedDetailsDBHandler check_db=new bookedDetailsDBHandler(venueSelect.this);
        Cursor check_cur=null;

        // Custom Toast for large imageView
        LayoutInflater img_inflater = getLayoutInflater();
        View img_customToastLayout = img_inflater.inflate(R.layout.custom_image_toast, null);
        ImageView custom_imageView = img_customToastLayout.findViewById(R.id.imageView);
        // Create an AlertDialog and set the custom layout
        AlertDialog.Builder img_builder = new AlertDialog.Builder(this);
        img_builder.setView(img_customToastLayout);
        AlertDialog img_customToastDialog = img_builder.create();

        Cursor cursor=db.showVenues_on_type(venueType);
        cursor_count=cursor.getCount();
        if(cursor_count>0)
        {
            cursor.moveToFirst();
            do
            {
                boolean onClick_flag=true;

                @SuppressLint("Range") String venueName = cursor.getString(cursor.getColumnIndex("name"));
                @SuppressLint("Range") String location = cursor.getString(cursor.getColumnIndex("location"));
                @SuppressLint("Range") String venueStatus = cursor.getString(cursor.getColumnIndex("status"));
                @SuppressLint("Range") int ratings = cursor.getInt(cursor.getColumnIndex("ratings"));
                @SuppressLint("Range") String img_path = cursor.getString(cursor.getColumnIndex("img_path"));


                LinearLayout.LayoutParams layoutParams = (new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                layoutParams.setMargins(0, 7, 0, 0);
                LinearLayout main = new LinearLayout(this);
                main.setLayoutParams(layoutParams);
                main.setBackgroundColor(getResources().getColor(R.color.white));
                main.setOrientation(LinearLayout.HORIZONTAL);
                mainLayout.addView(main);

                CardView cardView = new CardView(this);
                LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );
                cardParams.setMargins(20, 20, 0, 20);
                cardParams.gravity = Gravity.CENTER_VERTICAL;
                cardView.setLayoutParams(cardParams);
                cardView.setRadius(20);
                cardView.setCardBackgroundColor(getResources().getColor(R.color.white));

                ImageView img = new ImageView(this);
                LinearLayout.LayoutParams imgParams = new LinearLayout.LayoutParams(300,250);
                imgParams.setMargins(0, 0, 0, 0);
                img.setLayoutParams(imgParams);
                img.setBackgroundColor(getResources().getColor(R.color.white));
                Picasso.get().load(img_path).into(img);
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


                //Venue Name textview
                LinearLayout.LayoutParams txtParam = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                txtParam.setMargins(0, 20, 0, 0);
                TextView t1 = new TextView(this);
                t1.setText(venueName);
                t1.setGravity(Gravity.CENTER);
                t1.setLayoutParams(txtParam);
                t1.setTextSize(16);
                subMain.addView(t1);


                //location textview
                TextView t2 = new TextView(this);
                t2.setText(location);
                t2.setPadding(20,0,0,0);
                t2.setLayoutParams(txtParam);
                t2.setTextSize(16);
                subMain.addView(t2);


                //Available/Unavailable textview
                LinearLayout.LayoutParams layoutParams2 = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                layoutParams2.setMargins(20, 50, 0, 0);
                TextView t3 = new TextView(this);
                check_cur=check_db.retreiveVenue_bookedVenue(venueName,"yes");
                if(check_cur.getCount()>0)
                {
                    t3.setText("Check Availabilty");
                    Typeface italicTypeface = Typeface.create("sans-serif", Typeface.ITALIC);
                    t3.setTypeface(italicTypeface);
                    t3.setTextColor(getResources().getColor(R.color.red));
                    onClick_flag=true;
                }
                else
                {
                    t3.setText(venueStatus);
                    t3.setTextColor(getResources().getColor(R.color.green));
                    onClick_flag=false;
                }
                t3.setLayoutParams(layoutParams2);
                t3.setTextSize(16);
                subMain.addView(t3);
                check_cur.close();

//                //rating icon
//                LinearLayout.LayoutParams imgParams2 = (new LinearLayout.LayoutParams(50, 50));
//                imgParams2.setMargins(610, -160, 0, 0);
//                ImageView imgStar=new ImageView(this);
//                imgStar.setLayoutParams(imgParams2);
//                imgStar.setImageResource(R.drawable.star);
//                imgStar.setScaleType(ImageView.ScaleType.FIT_CENTER);
//                subMain.addView(imgStar);
//
//                //ratings textview
//                LinearLayout.LayoutParams txtParams2 = (new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
//                txtParams2.setMargins(675, -51, 0, 0);
//                TextView t4 = new TextView(this);
//                t4.setText(String.valueOf(ratings));
//                t4.setLayoutParams(txtParams2);
//                t4.setTextSize(16);
//                subMain.addView(t4);

                //book now button
                LinearLayout.LayoutParams btnParams = (new LinearLayout.LayoutParams(220,62));
                btnParams.setMargins(490, -60, 0, 40);
                Button booknowBtn = new Button(this);
                booknowBtn.setLayoutParams(btnParams);
                booknowBtn.setPadding(-15,-15,-15,-15);
                booknowBtn.setBackgroundColor(venueSelect.this.getColor(R.color.Blue1));
                booknowBtn.setTextColor(venueSelect.this.getColor(R.color.white));
                booknowBtn.setText("Book Now");
                booknowBtn.setTextSize(13);
                subMain.addView(booknowBtn);

                //Handle Click events ========================================
                main.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
//                        Toast.makeText(venueSelect.this,venueName,Toast.LENGTH_SHORT).show();
                        Intent nxtPage=new Intent(venueSelect.this, venueDescriptionPage.class);
                        nxtPage.putExtra("venue_name",venueName);
                        startActivity(nxtPage);
                    }
                });

                booknowBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
//                        Toast.makeText(venueSelect.this,venueName,Toast.LENGTH_SHORT).show();
                        Intent nxtPage=new Intent(venueSelect.this, bookVenue.class);
                        nxtPage.putExtra("venue_name",venueName);
                        startActivity(nxtPage);
                    }
                });

                // show image in large view using custom
                img.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // Display toast here
                        Picasso.get().load(img_path).into(custom_imageView);
                        img_customToastDialog.show();
                    }
                });

                //Check Availibility and mark dates
                boolean finalOnClick_flag = onClick_flag;
                t3.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(finalOnClick_flag)
                        {
                            global_venueName=venueName;
                            markDatesFun(venueName);
                            markDatesFun2(venueName);
                        }
                        else
                        {
                            // do nothing
                        }
                    }
                });

                // add the layout to list for searching purpose
                searchLayout_list.add(main);
            }while (cursor.moveToNext());
        }
        else
        {
            Toast.makeText(venueSelect.this,"No venues to display!!!",Toast.LENGTH_LONG).show();
        }

        cursor.close();
        db.close();
        check_db.close();

        // search
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                mainLayout.removeAllViews(); // clear the layout
                List<LinearLayout> list = new ArrayList<>();
                list=search(query);
                for (LinearLayout e : list) {
                    mainLayout.addView(e); // add to the layout
                }
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                mainLayout.removeAllViews(); // clear the layout
                List<LinearLayout> list = new ArrayList<>();
                list=search(newText);
                for (LinearLayout e : list) {
                    mainLayout.addView(e); // add to the layout
                }
                return true;
            }
        });

        closeCalenderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //clear the previously marked dates and time on the calendar and the list
                datesToMark.clear();
                timeSlot_list.clear();
                blueColoredDates.clear();
                calendarView.removeDecorator(dateDecorator);
                calendarView.removeDecorator(dateDecorator2);
                calendarView.invalidateDecorators();

                // clear marked time
                for (Button btn : time_buttonList) {
                    btn.setBackgroundResource(R.drawable.round_button_background);
                }

                // calendar texts
                red_text.setText(venueSelect.this.getString(R.string.red_txt1));
                blue_text.setVisibility(View.VISIBLE);

                main_time_layout.setVisibility(View.GONE);
                datePickerLayout.setVisibility(View.GONE);
            }
        });

        // mark time-slots if present
        calendarView.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {

                int year = date.getYear();
                int month = date.getMonth();
                int day = date.getDay();
                String selectedDate = String.format("%04d-%02d-%02d", year, month, day);

                System.out.println(selectedDate);
                if(!blueColoredDates.contains(selectedDate))
                {
                    main_time_layout.setVisibility(View.GONE);
                    red_text.setText(venueSelect.this.getString(R.string.red_txt1));
                    blue_text.setVisibility(View.VISIBLE);
                }
                else   // if time-slot present
                {
                    red_text.setText(venueSelect.this.getString(R.string.red_txt1));
                    blue_text.setVisibility(View.VISIBLE);

                    // clear previously marked time-slots
                    for (Button btn : time_buttonList) {
                        btn.setBackgroundResource(R.drawable.round_button_background);
                    }
                    timeSlot_list.clear();

                    markTimeSlots(global_venueName,selectedDate);
                    main_time_layout.setVisibility(View.VISIBLE);
                    red_text.setText("Red indicates that the time-slot is occupied");
                    blue_text.setVisibility(View.GONE);
                }
            }
        });
    }

    // search function
    public List<LinearLayout> search(String str) {
        List<LinearLayout> list = new ArrayList<>();

        for (LinearLayout e : searchLayout_list)  // iterate through all the stored layouts
        {
            View childE = e.getChildAt(1); // get the second child (which holds all the TextViews)

            if (childE instanceof LinearLayout) {
                for (int i = 0; i < ((LinearLayout) childE).getChildCount(); i++) {
                    View childView = ((LinearLayout) childE).getChildAt(i);

                    if (childView instanceof TextView) { // Check if it's a TextView
                        String checkStr = ((TextView) childView).getText().toString();
                        String new_checkStr=checkStr.toLowerCase();
                        String new_str=str.toLowerCase();
                        if (new_checkStr.indexOf(new_str) != -1)   // check if search string is a part of textview's string
                        {
                            list.add(e);
                            break;
                        }
                    }
                }
            }
        }
        return list;
    }
    @SuppressLint("Range")
    public void markTimeSlots(String v_venueName,String v_selectDate)
    {

        bookedDetailsDBHandler db = new bookedDetailsDBHandler(venueSelect.this);
        Cursor cur = db.retreiveBookedVenue_onSameDate(v_venueName,"yes",v_selectDate);
        if(cur.moveToFirst())
        {
            do {
                timeSlot_list.add(cur.getString(cur.getColumnIndex("time_slot")));
            }while (cur.moveToNext());
        }
        cur.close();
        db.close();
        String[] time_slot_arr = timeSlot_list.toArray(new String[timeSlot_list.size()]);
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
            for (Button button : time_buttonList) {
                String buttonText = button.getText().toString();
                if (str.equals(buttonText)) {
                    button.setBackgroundResource(R.drawable.red_round_button_background);
                }
            }
        }

    }

    public void markDatesFun(String v_venueName) {

        datePickerLayout.setVisibility(View.VISIBLE);
        bookedDetailsDBHandler db = new bookedDetailsDBHandler(venueSelect.this);
        Cursor cur = db.retreiveVenue_bookedVenue(v_venueName, "yes");

        if (cur.moveToFirst()) {
            do {
                @SuppressLint("Range") String startDt = cur.getString(cur.getColumnIndex("start_date"));
                @SuppressLint("Range") String endDt = cur.getString(cur.getColumnIndex("end_date"));

                // store them in seperate lists for later use while marking time slots
                startDates_list.add(startDt);
                endDates_list.add(endDt);

                LocalDate startDate = null;
                LocalDate endDate = null;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    startDate = LocalDate.parse(startDt);
                    endDate = LocalDate.parse(endDt);
                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    while (!startDate.isAfter(endDate)) {
                        String[] partsDate = startDate.toString().split("-");

                        int year = Integer.parseInt(partsDate[0]);
                        int month = Integer.parseInt(partsDate[1]);
                        int day = Integer.parseInt(partsDate[2]);

                        datesToMark.add(CalendarDay.from(year, month, day));

                        startDate = startDate.plusDays(1);
                    }
                }
            } while (cur.moveToNext());
        }
        dateDecorator = new dateDecorator(venueSelect.this, datesToMark);
        calendarView.addDecorator(dateDecorator);
        calendarView.invalidateDecorators();
        cur.close();
        db.close();
    }

    public void markDatesFun2(String v_venueName) {
        datesToMark.clear();
        datePickerLayout.setVisibility(View.VISIBLE);
        bookedDetailsDBHandler db = new bookedDetailsDBHandler(venueSelect.this);
        Cursor cur = db.retreiveVenue_bookedVenue(v_venueName, "yes");

        if (cur.moveToFirst()) {
            do {
                @SuppressLint("Range") String startDt = cur.getString(cur.getColumnIndex("start_date"));
                @SuppressLint("Range") String endDt = cur.getString(cur.getColumnIndex("end_date"));
                if (startDt.equals(endDt)) {
                    LocalDate startDate = null;
                    LocalDate endDate = null;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        startDate = LocalDate.parse(startDt);
                        endDate = LocalDate.parse(endDt);
                    }

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        while (!startDate.isAfter(endDate)) {
                            String[] partsDate = startDate.toString().split("-");

                            int year = Integer.parseInt(partsDate[0]);
                            int month = Integer.parseInt(partsDate[1]);
                            int day = Integer.parseInt(partsDate[2]);

                            datesToMark.add(CalendarDay.from(year, month, day));

                            // add to seperate list for use in disabling it
//                            String dt = year + "-" + month + "-" + day;
                            String dt = String.format("%04d-%02d-%02d", year, month, day);
                            blueColoredDates.add(dt);

                            startDate = startDate.plusDays(1);
                        }
                    }
                }
            } while (cur.moveToNext());
        }

        dateDecorator2 = new dateDecorator2(venueSelect.this, datesToMark);
        calendarView.addDecorator(dateDecorator2);
        calendarView.invalidateDecorators();
        cur.close();
        db.close();
    }

    protected void onPause() {
        super.onPause();
        //clear the previously marked dates and time on the calendar and the list
        datesToMark.clear();
        timeSlot_list.clear();
        blueColoredDates.clear();
        calendarView.removeDecorator(dateDecorator);
        calendarView.removeDecorator(dateDecorator2);
        calendarView.invalidateDecorators();

        // clear marked time
        for (Button btn : time_buttonList) {
            btn.setBackgroundResource(R.drawable.round_button_background);
        }

        red_text.setText(venueSelect.this.getString(R.string.red_txt1));

        main_time_layout.setVisibility(View.GONE);
        blue_text.setVisibility(View.VISIBLE);
    }
}