package com.example.myapplication.user;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.R;
import com.example.myapplication.database.bookedDetailsDBHandler;
import androidx.appcompat.widget.SearchView;
import com.example.myapplication.database.venueDBHandler;
import com.example.myapplication.user.bookVenue;
import com.example.myapplication.venueSelect;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class userTransactions extends AppCompatActivity {
    LinearLayout mainLayout1;
    List<LinearLayout> searchLayout_list = new ArrayList<>();
    SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_transactions);
        mainLayout1 = findViewById(R.id.mainLayout1);
        searchView = findViewById(R.id.searchView);

        // Custom Toast for large imageView
        LayoutInflater img_inflater = getLayoutInflater();
        View img_customToastLayout = img_inflater.inflate(R.layout.custom_image_toast, null);
        ImageView custom_imageView = img_customToastLayout.findViewById(R.id.imageView);
        // Create an AlertDialog and set the custom layout
        AlertDialog.Builder img_builder = new AlertDialog.Builder(this);
        img_builder.setView(img_customToastLayout);
        AlertDialog img_customToastDialog = img_builder.create();

        //retrieve email&pass from memory,if present
        SharedPreferences retrieveShared = getSharedPreferences("login_details", MODE_PRIVATE);
        String shared_email = retrieveShared.getString("user_email", "");
//        String shared_pass = retrieveShared.getString("user_pass", "");

        bookedDetailsDBHandler db=new bookedDetailsDBHandler(this);
        Cursor cur=db.retreiveUser_CurrentBookedVenueAndRequests(shared_email);

        if(cur.getCount()>0)
        {
            cur.moveToFirst();
            do
            {

                @SuppressLint("Range") String venueName = cur.getString(cur.getColumnIndex("venue_name"));
                @SuppressLint("Range") String bookedStatus = cur.getString(cur.getColumnIndex("status"));
                @SuppressLint("Range") String approval = cur.getString(cur.getColumnIndex("approval"));
                @SuppressLint("Range") String endDate = cur.getString(cur.getColumnIndex("end_date"));
                @SuppressLint("Range") String startDate = cur.getString(cur.getColumnIndex("start_date"));
                @SuppressLint("Range") String timeSlot = cur.getString(cur.getColumnIndex("time_slot"));
                @SuppressLint("Range") String imgPath = cur.getString(cur.getColumnIndex("img_path"));

                LinearLayout.LayoutParams layoutParams = (new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                layoutParams.setMargins(0, 7, 0, 0);
                LinearLayout main = new LinearLayout(this);
                main.setLayoutParams(layoutParams);
                main.setBackgroundColor(getResources().getColor(R.color.white));
                main.setOrientation(LinearLayout.HORIZONTAL);
                mainLayout1.addView(main);

                CardView cardView = new CardView(this);
                LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );
                cardParams.gravity = Gravity.CENTER_VERTICAL;
                cardParams.setMargins(20, 20, 0, 20);
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
                subParams.gravity = Gravity.CENTER_VERTICAL;
                LinearLayout subMain = new LinearLayout(this);
                subMain.setLayoutParams(subParams);
                subMain.setOrientation(LinearLayout.VERTICAL);
                subMain.setBackgroundColor(getResources().getColor(R.color.white));
                main.addView(subMain);

                //Venue Name textview
                LinearLayout.LayoutParams txtParam = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                txtParam.setMargins(0, 10, 0, 0);
                TextView t1 = new TextView(this);
//            t1.setText("Venue name");
                t1.setText(venueName);
//            t1.setId("");
                t1.setGravity(Gravity.CENTER);
                t1.setLayoutParams(txtParam);
//            t1.setBackgroundResource(R.color.lightGrey);
                t1.setTextSize(16);
                subMain.addView(t1);

                // Approval status textview
                TextView t3 = new TextView(this);

                if(approval.equals("yes"))
                {
                    String text = "Approval : Accepted";
                    SpannableString spannableString = new SpannableString(text);
                    spannableString.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.green)), 10, text.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    t3.setText(spannableString);
                }
                else if(approval.equals("no"))
                {
                    String text = "Approval : Rejected";
                    SpannableString spannableString = new SpannableString(text);
                    spannableString.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.red)), 10, text.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    t3.setText(spannableString);
                }
                else if(approval.equals("na"))
                {
                    String text = "Approval : Pending";
                    SpannableString spannableString = new SpannableString(text);
                    spannableString.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.Blue2)), 10, text.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    t3.setText(spannableString);
                }
                else
                {
                    String text = "Approval : "+approval;
                    SpannableString spannableString = new SpannableString(text);
                    spannableString.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.red)), 10, text.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    t3.setText(spannableString);
                }
                t3.setPadding(20, 0, 0, 0);
//            t2.setId(resId2);
                t3.setLayoutParams(txtParam);
//                t3.setTextColor(getResources().getColor(R.color.green));
//            t2.setBackgroundResource(R.color.lightGrey);
                t3.setTextSize(16);
                subMain.addView(t3);

                // Booking status textview
                TextView t2 = new TextView(this);
                t2.setText("Booking Status : " +bookedStatus);
                t2.setPadding(20, 0, 0, 0);
//            t2.setId(resId2);
                t2.setLayoutParams(txtParam);
//                t2.setTextColor(getResources().getColor(R.color.green));
//            t2.setBackgroundResource(R.color.lightGrey);
                t2.setTextSize(16);
                subMain.addView(t2);

                if(!startDate.equals(endDate))
                {
                    // from date
                    TextView t6 = new TextView(this);
                    t6.setText("From : "+startDate);
                    t6.setPadding(20, 10, 0, 0);
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
                    t8.setPadding(20, 10, 0, 20);
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
                    t6.setPadding(20, 10, 0, 0);
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
                    t7.setPadding(20, 10, 0, 20);
                    LinearLayout.LayoutParams t7LayoutParams = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                    );
                    t7.setLayoutParams(t7LayoutParams);
                    t7.setTextSize(16);
                    subMain.addView(t7);

                }


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
            }while (cur.moveToNext());
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
            mainLayout1.addView(t);
        }
        cur.close();
        db.close();

        // search
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                mainLayout1.removeAllViews(); // clear the layout
                List<LinearLayout> list = new ArrayList<>();
                list=search(query);
                for (LinearLayout e : list) {
                    mainLayout1.addView(e); // add to the layout
                }
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                mainLayout1.removeAllViews(); // clear the layout
                List<LinearLayout> list = new ArrayList<>();
                list=search(newText);
                for (LinearLayout e : list) {
                    mainLayout1.addView(e); // add to the layout
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