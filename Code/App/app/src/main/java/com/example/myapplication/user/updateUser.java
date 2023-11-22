package com.example.myapplication.user;


import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.R;
import com.example.myapplication.database.bookedDetailsDBHandler;
import com.example.myapplication.database.userDBHandler;

public class updateUser extends AppCompatActivity {

    private EditText email;
    private EditText name;
    private EditText dept;
    private EditText phno;
    private EditText pass;
    private EditText cpass;
    private Button updateBtn;
    private ImageButton viewPassBtn,viewCPassBtn;
    String u_name,u_email,u_dept,u_phone,u_pass;

    @SuppressLint("Range")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_user);
        email = findViewById(R.id.editTextTextEmailAddress2);
        name = findViewById(R.id.editTextTextName);
        phno = findViewById(R.id.editTextTextPhone);
        dept=findViewById(R.id.editTextTextDept);
        pass = findViewById(R.id.editTextTextPassword);
        cpass = findViewById(R.id.editTextTextConfirmPassword);
        updateBtn = findViewById(R.id.updateBtn);
        viewPassBtn = findViewById(R.id.imageButtonTogglePassword);
        viewCPassBtn = findViewById(R.id.imageButtonTogglePassword2);


        //retrieve email&pass from memory,if present
        SharedPreferences retrieveShared = getSharedPreferences("login_details", MODE_PRIVATE);
        String shared_email = retrieveShared.getString("user_email", "");
//        String shared_pass = retrieveShared.getString("user_pass", "");

        userDBHandler db = new userDBHandler(this);

        // retrieve remaining details from the database
        Cursor u_cur=db.getUserDetails(shared_email);
        if(u_cur.getCount()>0)
        {
            u_cur.moveToFirst();
            u_name=u_cur.getString(u_cur.getColumnIndex("name"));
            u_dept=u_cur.getString(u_cur.getColumnIndex("dept"));
            u_email=u_cur.getString(u_cur.getColumnIndex("email"));
            u_phone=u_cur.getString(u_cur.getColumnIndex("phone"));
            u_pass=u_cur.getString(u_cur.getColumnIndex("pass"));
        }
        u_cur.close();
        // set the retrieved details to the textviews
        email.setText(u_email);
        name.setText(u_name);
        dept.setText(u_dept);
        phno.setText(u_phone);

        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String v_email = email.getText().toString();
                String v_name = name.getText().toString();
                String v_dept=dept.getText().toString();
                String v_phno = phno.getText().toString();
                String v_pass = pass.getText().toString();
                String v_cpass = cpass.getText().toString();

                // check if user has any current/ongoing transaction
                boolean checkCurrentTransaction=false;
                bookedDetailsDBHandler b_db=new bookedDetailsDBHandler(updateUser.this);
                Cursor b_cur=b_db.retreiveUser_bookedVenue(v_email,"yes");
                if(b_cur.getCount()>0)
                    checkCurrentTransaction=true;
                b_cur.close();
                b_db.close();

                if (v_name.isEmpty()) {
                    name.setError("Name is required");
                }
                else if(v_dept.isEmpty())
                {
                    dept.setError("Department name is required");
                }
                else if (v_phno.isEmpty() || v_phno.length() != 10) {
                    phno.setError("Please enter an valid phone number");
                } else if (v_pass.isEmpty()) {
                    pass.setError("Password is required");
                    Toast.makeText(updateUser.this,"Password is required",Toast.LENGTH_SHORT).show();
                } else if (!v_pass.equals(v_cpass)) {
                    cpass.setError("Passwords do not match");
                    Toast.makeText(updateUser.this,"Passwords do not match",Toast.LENGTH_SHORT).show();
                }
                else if(checkCurrentTransaction)
                {
                    Toast.makeText(updateUser.this,"Please finish current transactions to edit profile",Toast.LENGTH_LONG).show();
                }else {
                    boolean result = db.updateUser(v_name, v_email,v_dept, v_phno, v_pass);
                    if (result) {
                        Toast.makeText(updateUser.this, "Profile Updated", Toast.LENGTH_SHORT).show();
                        Intent loginPage=new Intent(updateUser.this,loginUser.class);
                        startActivity(loginPage);
                        finish();
                    } else {
                        Toast.makeText(updateUser.this, "Update Failed", Toast.LENGTH_SHORT).show();
                    }
                }

            }
        });

        viewPassBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    // Get the current input type of the EditText
                    int inputType = pass.getInputType();

                    // Toggle the input type between visible and hidden
                    if (inputType == 129) // 129=text-hidden-password-type
                    {
                        pass.setInputType(128); // 128=visible-password-type
                    }
                    else if(inputType == 144L || inputType == 128 ) //144=default-type
                    {
                        pass.setInputType(129);
                    }
                    // Move the cursor to the end of the text
                    pass.setSelection(pass.getText().length());
                }catch (Exception e)
                {
                    Log.e("My-Error","View Password Button Error="+e);
                }
            }
        });

        viewCPassBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    // Get the current input type of the EditText
                    int inputType = cpass.getInputType();

                    // Toggle the input type between visible and hidden
                    if (inputType == 129) // 129=text-hidden-password-type
                    {
                        cpass.setInputType(128); // 128=visible-password-type
                    }
                    else if(inputType == 144L || inputType == 128 ) //144=default-type
                    {
                        cpass.setInputType(129);
                    }
                    // Move the cursor to the end of the text
                    cpass.setSelection(cpass.getText().length());
                }catch (Exception e)
                {
                    Log.e("My-Error","View Password Button Error="+e);
                }
            }
        });

        db.close();
    }
//    public boolean isValidEmail(String v_email) {
//        String emailPattern = "[a-zA-Z0-9._-]+@[a-zA-Z]+\\.+[a-z]+";
//        return v_email.matches(emailPattern);
//    }

}