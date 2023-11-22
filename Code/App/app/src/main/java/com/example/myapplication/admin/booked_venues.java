package com.example.myapplication.admin;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.cardview.widget.CardView;
import android.graphics.Paint;
import android.os.Build;
import android.view.Gravity;
import android.widget.ImageView;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;

import com.example.myapplication.R;
import com.example.myapplication.database.bookedDetailsDBHandler;
import com.example.myapplication.database.userDBHandler;
import com.example.myapplication.database.venueDBHandler;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import androidx.appcompat.widget.SearchView;

import java.util.ArrayList;
import java.util.List;

public class booked_venues extends AppCompatActivity {
    LinearLayout mainLayout;
    List<LinearLayout> searchLayout_list = new ArrayList<>();
    SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booked_venues);
        mainLayout = findViewById(R.id.Layout);
        searchView = findViewById(R.id.searchView);

        // Custom toast
        LayoutInflater inflater = getLayoutInflater();
        View customToastLayout = inflater.inflate(R.layout.custom_toast, null);
        TextView c_txt1 = customToastLayout.findViewById(R.id.custom_txt1);
        TextView c_txt2 = customToastLayout.findViewById(R.id.custom_txt2);


        LinearLayout.LayoutParams statusParams = null;
        LinearLayout.LayoutParams cardParams = null;

        // Create an AlertDialog and set the custom layout
        AlertDialog.Builder builder = new AlertDialog.Builder(booked_venues.this);
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

        bookedDetailsDBHandler db=new bookedDetailsDBHandler(this);
        Cursor cursor= db.retreiveAllCurrent_BookedVenues();

        if(cursor.getCount()>0)
        {
            cursor.moveToFirst();
            do
            {
                @SuppressLint("Range") String venueName = cursor.getString(cursor.getColumnIndex("venue_name"));
                @SuppressLint("Range") String bookedStatus = cursor.getString(cursor.getColumnIndex("status"));
                @SuppressLint("Range") String department = cursor.getString(cursor.getColumnIndex("department"));
                @SuppressLint("Range") String phone = cursor.getString(cursor.getColumnIndex("user_phone"));
                @SuppressLint("Range") String userEmail = cursor.getString(cursor.getColumnIndex("user_email"));
                @SuppressLint("Range") String endDate = cursor.getString(cursor.getColumnIndex("end_date"));
                @SuppressLint("Range") String startDate = cursor.getString(cursor.getColumnIndex("start_date"));
                @SuppressLint("Range") String timeSlot = cursor.getString(cursor.getColumnIndex("time_slot"));
                @SuppressLint("Range") String imgPath = cursor.getString(cursor.getColumnIndex("img_path"));

//                venueDBHandler v_db=new venueDBHandler(this);
//                Cursor v_cur=v_db.retreiveVenues(venueName);
//                v_cur.moveToFirst();
//                @SuppressLint("Range") String imgPath = v_cur.getString(v_cur.getColumnIndex("img_path"));
//                v_cur.close();
//                v_db.close();

                userDBHandler u_db=new userDBHandler(this);
                Cursor u_cur=u_db.getUserDetails(userEmail);
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
                mainLayout.addView(main);

                // image margins
                cardParams = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );
                cardParams.setMargins(20, 20, 20, 20);

                // set image
                CardView cardView = new CardView(this);
                cardParams.gravity = Gravity.CENTER_VERTICAL;
                cardView.setLayoutParams(cardParams);
                cardView.setRadius(20);
                cardView.setCardBackgroundColor(getResources().getColor(R.color.white));

                ImageView img = new ImageView(this);
                LinearLayout.LayoutParams imgParams = new LinearLayout.LayoutParams(350,300);
                imgParams.setMargins(0, 0, 0, 0);
                img.setLayoutParams(imgParams);
                img.setBackgroundColor(getResources().getColor(R.color.white));
                Picasso.get().load(imgPath).into(img);
                img.setScaleType(ImageView.ScaleType.CENTER_CROP);
                cardView.addView(img);
                main.addView(cardView);

                LinearLayout.LayoutParams subParams = (new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                subParams.setMargins(0, 0, 10, 0);
                LinearLayout subMain = new LinearLayout(this);
                subMain.setLayoutParams(subParams);
                subMain.setOrientation(LinearLayout.VERTICAL);
                subMain.setBackgroundColor(getResources().getColor(R.color.white));
                main.addView(subMain);

                //border
                ShapeDrawable shapeDrawable = new ShapeDrawable();
                shapeDrawable.setShape(new RectShape());
                shapeDrawable.getPaint().setColor(getResources().getColor(R.color.Blue1));
                shapeDrawable.getPaint().setStrokeWidth(5);
                shapeDrawable.getPaint().setStyle(Paint.Style.STROKE);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    subMain.setBackground(shapeDrawable);
                } else {
                    subMain.setBackgroundDrawable(shapeDrawable);
                }

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

                //User Name textview
                TextView t2 = new TextView(this);
                t2.setText(userName);
                t2.setPadding(20, 0, 0, 0);
                t2.setLayoutParams(txtParam);
                t2.setTextSize(16);
                subMain.addView(t2);

                TextView t3 = new TextView(this);
                t3.setText(department);
                t3.setPadding(20, 0, 0, 0);
                t3.setLayoutParams(txtParam);
                t3.setTextSize(16);
                subMain.addView(t3);

                if(!startDate.equals(endDate))
                {
                    // from date
                    TextView t6 = new TextView(this);
                    t6.setText("From : "+startDate);
                    t6.setPadding(20, 20, 0, 0);
                    LinearLayout.LayoutParams t6LayoutParams = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                    );
                    t6.setLayoutParams(t6LayoutParams);
                    t6.setTextSize(16);
                    subMain.addView(t6);

                    // to date
                    TextView t8 = new TextView(this);
                    t8.setText("To : "+endDate);
                    t8.setPadding(20, 20, 0, 0);
                    LinearLayout.LayoutParams t8LayoutParams = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                    );
                    t8.setLayoutParams(t8LayoutParams);
                    t8.setTextSize(16);
                    subMain.addView(t8);

                }
                else
                {
                    // On date
                    LinearLayout linearLayout = new LinearLayout(this);
                    linearLayout.setOrientation(LinearLayout.HORIZONTAL);
                    LinearLayout.LayoutParams linearLayoutParams = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                    );
                    linearLayout.setLayoutParams(linearLayoutParams);

                    TextView t6 = new TextView(this);
                    t6.setText("Date : "+startDate);
                    t6.setPadding(20, 20, 0, 0);
                    LinearLayout.LayoutParams t6LayoutParams = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                    );
                    t6.setLayoutParams(t6LayoutParams);
                    t6.setTextSize(16);
                    linearLayout.addView(t6);
                    subMain.addView(linearLayout);


                    //retreive the first and last hour from the timeSlot list
                    char[] char_timeSlot=timeSlot.toCharArray();
                    int st_hour=Integer.parseInt(String.valueOf(char_timeSlot[0]));
                    if(st_hour!=9)
                        st_hour=Integer.parseInt(String.valueOf(char_timeSlot[0])+String.valueOf(char_timeSlot[1]));
                    String end_hour=String.valueOf(char_timeSlot[char_timeSlot.length-2])+String.valueOf(char_timeSlot[char_timeSlot.length-1]);

                    TextView t7 = new TextView(this);
                    t7.setText("Time Slot : "+String.valueOf(st_hour) + ":00 - " + end_hour + ":00");
                    t7.setPadding(20, 20, 0, 0);
                    LinearLayout.LayoutParams t7LayoutParams = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                    );
                    t7.setLayoutParams(t7LayoutParams);
                    t7.setTextSize(16);
                    subMain.addView(t7);

                }

                // Ongoing/Prebook textview
                statusParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

                // Ongoing/Prebook textview
                TextView t9 = new TextView(this);
                t9.setPadding(25, 15, 0, 20);
                if(bookedStatus.equals("ongoing"))
                    t9.setText("Ongoing");
                else if(bookedStatus.equals("pre-order"))
                    t9.setText("Pre-Order");
                else
                    t9.setText(bookedStatus);
                t9.setTextColor(getResources().getColor(R.color.green));
                t9.setLayoutParams(statusParams);
                t9.setTextSize(16);
                subMain.addView(t9);

                // handle click events
                subMain.setOnClickListener(new View.OnClickListener() {       // show extra details of user in custom toast
                    @Override
                    public void onClick(View view) {
                        // Display the AlertDialog
                        // set texts for custom layout
                        c_txt1.setText(phone);
                        c_txt2.setText(userEmail);
                        customToastDialog.show();
                    }
                });

                // show image
                img.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // Display toast here
                        Picasso.get().load(imgPath).into(custom_imageView);
                        img_customToastDialog.show();
                    }
                });
                // add the layout to list for searching purpose
                searchLayout_list.add(main);
            }while (cursor.moveToNext());
        }
        else
        {
            TextView t=new TextView(this);
            t.setPadding(20, 20, 20, 20);
            LinearLayout.LayoutParams t7LayoutParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            t.setLayoutParams(t7LayoutParams);
            t.setTextSize(26);
            t.setGravity(Gravity.CENTER);
            t.setText("No Currently Booked Venues");
            mainLayout.addView(t);
        }
        // close
        cursor.close();
        db.close();

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
//                        System.out.println(new_checkStr);
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
}