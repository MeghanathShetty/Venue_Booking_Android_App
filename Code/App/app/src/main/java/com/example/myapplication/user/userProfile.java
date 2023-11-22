package com.example.myapplication.user;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.myapplication.R;
import com.example.myapplication.database.userDBHandler;

public class userProfile extends AppCompatActivity {

    EditText name,dept,email,phone;

    Button editProfilePageBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        name=findViewById(R.id.Name);
        dept=findViewById(R.id.DeptName);
        email=findViewById(R.id.email);
        phone=findViewById(R.id.phone);
        editProfilePageBtn=findViewById(R.id.btnEdit);

        //      Use shared-data and retrieve user-email
        SharedPreferences sharedPreferences = getSharedPreferences("login_details", MODE_PRIVATE);
        String shared_email = sharedPreferences.getString("user_email", "someone");
        email.setText(shared_email);

        userDBHandler u_db = new userDBHandler(this);
        Cursor u_cur = u_db.getUserDetails(shared_email);
        u_cur.moveToFirst();
        @SuppressLint("Range") String v_name = u_cur.getString(u_cur.getColumnIndex("name"));
        @SuppressLint("Range") String v_dept = u_cur.getString(u_cur.getColumnIndex("dept"));
        @SuppressLint("Range") String v_phone = u_cur.getString(u_cur.getColumnIndex("phone"));

        // set retreived details to textviews
        name.setText(v_name);
        dept.setText(v_dept);
        phone.setText(v_phone);

        u_cur.close();
        u_db.close();

        editProfilePageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(userProfile.this, updateUser.class);
                startActivity(i);
            }
        });

    }
}