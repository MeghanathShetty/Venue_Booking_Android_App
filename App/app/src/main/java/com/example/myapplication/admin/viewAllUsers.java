package com.example.myapplication.admin;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.UserHandle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.myapplication.R;
import com.example.myapplication.database.bookedDetailsDBHandler;
import com.example.myapplication.database.userDBHandler;

import java.util.ArrayList;
import java.util.List;
import androidx.appcompat.widget.SearchView;

public class viewAllUsers extends AppCompatActivity {
    LinearLayout mainLayout;
    List<LinearLayout> searchLayout_list = new ArrayList<>();
    SearchView searchView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_all_users);
        mainLayout = findViewById(R.id.Layout);
        searchView = findViewById(R.id.searchView);

        userDBHandler db=new userDBHandler(this);
        Cursor cur= db.getAllUsers();
        if(cur.getCount()>0)
        {
            cur.moveToFirst();
            do
            {
                @SuppressLint("Range") String userName = cur.getString(cur.getColumnIndex("name"));
                @SuppressLint("Range") String userEmail = cur.getString(cur.getColumnIndex("email"));
                @SuppressLint("Range") String userDept = cur.getString(cur.getColumnIndex("dept"));
                @SuppressLint("Range") String userPhone = cur.getString(cur.getColumnIndex("phone"));


                LinearLayout.LayoutParams main_layoutParams = (new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                main_layoutParams.setMargins(0,7,0,0);
                LinearLayout main=new LinearLayout(this);
                main.setLayoutParams(main_layoutParams);
                main.setBackgroundColor(getResources().getColor(R.color.white));
                main.setOrientation(LinearLayout.VERTICAL);
                mainLayout.addView(main);

                LinearLayout.LayoutParams txtParam = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                txtParam.setMargins(30, 10, 0, 10);

                //User Name textview
                TextView t1 = new TextView(this);
                t1.setText("Name : "+userName);
//                t1.setGravity(Gravity.CENTER);
                t1.setLayoutParams(txtParam);
                t1.setTextSize(16);
                main.addView(t1);

                //User Department textview
                TextView t2 = new TextView(this);
                t2.setText("Department : "+userDept);
//                t2.setGravity(Gravity.CENTER);
                t2.setLayoutParams(txtParam);
                t2.setTextSize(16);
                main.addView(t2);

                //User Phone No textview
                TextView t3 = new TextView(this);
                t3.setText("Phone No : "+userPhone);
//                t3.setGravity(Gravity.CENTER);
                t3.setLayoutParams(txtParam);
                t3.setTextSize(16);
                main.addView(t3);

                //User Email textview
                TextView t4 = new TextView(this);
                t4.setText("Email : "+userEmail);
//                t4.setGravity(Gravity.CENTER);
                t4.setLayoutParams(txtParam);
                t4.setTextSize(16);
                main.addView(t4);

                //User Booked Count textview
                TextView t5 = new TextView(this);
                bookedDetailsDBHandler b_db=new bookedDetailsDBHandler(this);
                Cursor b_cur=b_db.retreiveUser_bookedVenue(userEmail,"yes");
                t5.setText("Currently Booked Venues Count : "+b_cur.getCount());;
                b_cur.close();
                b_db.close();
//                t5.setGravity(Gravity.CENTER);
                t5.setTextColor(getResources().getColor(R.color.Blue2));
                t5.setLayoutParams(txtParam);
                t5.setTextSize(16);
                main.addView(t5);

                // add the layout to list for searching purpose
                searchLayout_list.add(main);

            }while(cur.moveToNext());
        }
        else {
            TextView t=new TextView(this);
            t.setPadding(20, 20, 20, 20);
            LinearLayout.LayoutParams t7LayoutParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            t.setLayoutParams(t7LayoutParams);
            t.setTextSize(26);
            t.setGravity(Gravity.CENTER);
            t.setText("No Users to Display");
            mainLayout.addView(t);
        }
        // Close
        cur.close();
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
    public List<LinearLayout> search(String str)
    {
        List<LinearLayout> list = new ArrayList<>();
        for (LinearLayout e : searchLayout_list)  // iterate through all the stored layouts
        {
            if (e instanceof LinearLayout) {
                for (int i = 0; i < ((LinearLayout) e).getChildCount(); i++) {
                    View childView = ((LinearLayout) e).getChildAt(i);

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
}