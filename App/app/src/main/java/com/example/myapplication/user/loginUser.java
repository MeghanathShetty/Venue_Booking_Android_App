package com.example.myapplication.user;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.R;
import com.example.myapplication.admin.adminPage;
import com.example.myapplication.database.userDBHandler;
import com.example.myapplication.database.venueDBHandler;
import com.example.myapplication.homePage;

public class loginUser extends AppCompatActivity {
    private EditText email;
    private EditText pass;
    private Button logBtn;

    private ImageButton viewPassBtn;

    private TextView gotoRegPage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_user);

        // IMPORTANT ( Create database )
        venueDBHandler v1_db=new venueDBHandler(loginUser.this);
        v1_db.showAllVenues();
        v1_db.close();

        email = findViewById(R.id.email);
        pass = findViewById(R.id.pass);
        logBtn=findViewById(R.id.loginBtn);
        viewPassBtn=findViewById(R.id.viewPassBtn);
        gotoRegPage=findViewById(R.id.gotoToRegPage);

        //retrieve email&pass from memory,if present
        SharedPreferences retrieveShared = getSharedPreferences("login_details", MODE_PRIVATE);
        String shared_email = retrieveShared.getString("user_email", "");
        String shared_pass = retrieveShared.getString("user_pass", "");

        //set the retrieved data
        email.setText(shared_email);
        pass.setText(shared_pass);

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


        logBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try {
                    String v_email = email.getText().toString();
                    String v_pass = pass.getText().toString();

                    if(v_email.equals("admin") && v_pass.equals("pass"))
                    {
                        Intent adminPage=new Intent(loginUser.this,com.example.myapplication.admin.adminPage.class);
                        startActivity(adminPage);
                    }
                    else
                    {
                        userDBHandler db = new userDBHandler(loginUser.this);
                        if(db.checkUser(v_email))
                        {
                            String s_pass=db.getPass(v_email);
                            if(v_pass.equals(s_pass))
                            {
                                Intent nxtPage=new Intent(loginUser.this, homePage.class);

                                //save user-email in shared preference,to be used later in other activities
                                SharedPreferences sharedPreferences = getSharedPreferences("login_details", MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putString("user_email", v_email);
                                editor.putString("user_pass",v_pass);
                                editor.apply();

//                                Toast.makeText(loginUser.this,"Login Successful",Toast.LENGTH_SHORT).show();
                                startActivity(nxtPage);
                                finish();
                            }
                            else {
                                pass.setError("Incorrect Password");
                            }
                        }
                        else
                        {
                            email.setError("Email does not exist");
                        }
                        db.close();
                    }

                }catch (Exception e)
                {
                    Log.e("My-Error","Login button Error="+e);
                }
                }
        });

        gotoRegPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent regPage=new Intent(loginUser.this,registerUser.class);
                startActivity(regPage);
            }
        });
    }
}