package com.example.myapplication.user;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.R;
import com.example.myapplication.database.userDBHandler;

public class registerUser extends AppCompatActivity {

    private EditText email;
    private EditText name;
    private EditText dept;
    private EditText phno;
    private EditText pass;
    private EditText cpass;
    private Button regBtn;
    private TextView gotoLoginBtn;
    private ImageButton viewPassBtn,viewCPassBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_user);

        email = findViewById(R.id.editTextTextEmailAddress2);
        name = findViewById(R.id.editTextTextName);
        phno = findViewById(R.id.editTextTextPhone);
        dept=findViewById(R.id.editTextTextDept);
        pass = findViewById(R.id.editTextTextPassword);
        cpass = findViewById(R.id.editTextTextConfirmPassword);
        regBtn = findViewById(R.id.RegisterButton);
        gotoLoginBtn=findViewById(R.id.gotoLoginBtn);
        viewPassBtn = findViewById(R.id.imageButtonTogglePassword);
        viewCPassBtn = findViewById(R.id.imageButtonTogglePassword2);

        userDBHandler db = new userDBHandler(this);

        regBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String v_email = email.getText().toString();
                String v_name = name.getText().toString();
                String v_dept=dept.getText().toString();
                String v_phno = phno.getText().toString();
                String v_pass = pass.getText().toString();
                String v_cpass = cpass.getText().toString();

                if (v_name.isEmpty()) {
                    name.setError("Name is required");
                } else if (v_email.isEmpty()) {
                    email.setError("Email is required");
                } else if (!isValidEmail(v_email)) {
                    email.setError("Please enter an valid email");
                }
                else if(v_dept.isEmpty())
                {
                    dept.setError("Department name is required");
                }
                else if (v_phno.isEmpty() || v_phno.length() != 10) {
                    phno.setError("Please enter an valid phone number");
                } else if (v_pass.isEmpty()) {
                    pass.setError("Password is required");
                    Toast.makeText(registerUser.this,"Password is required",Toast.LENGTH_SHORT).show();
                } else if (!v_pass.equals(v_cpass)) {
                    cpass.setError("Passwords do not match");
                    Toast.makeText(registerUser.this,"Passwords do not match",Toast.LENGTH_SHORT).show();
                } else if (db.checkUser(v_email))
                {
                    email.setError("Email already used");
                } else {
                    boolean result = db.addUser(v_email, v_name,v_dept, v_phno, v_pass);
                    if (result) {
                        Toast.makeText(registerUser.this, "Registration Successful", Toast.LENGTH_SHORT).show();
                        Intent loginPage=new Intent(registerUser.this,loginUser.class);
                        startActivity(loginPage);
                        finish();
                    } else {
                        Toast.makeText(registerUser.this, "Registration Failed", Toast.LENGTH_SHORT).show();
                    }
                }

            }
        });

        gotoLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent loginPage=new Intent(registerUser.this,loginUser.class);
                startActivity(loginPage);
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
    public boolean isValidEmail(String v_email) {
        String emailPattern = "[a-zA-Z0-9._-]+@[a-zA-Z]+\\.+[a-z]+";
        return v_email.matches(emailPattern);
    }

}