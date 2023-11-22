package com.example.myapplication;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;
import com.example.myapplication.admin.addVenue;
import com.example.myapplication.admin.booked_venues;
import com.example.myapplication.database.venueDBHandler;
import com.example.myapplication.user.loginUser;
import com.example.myapplication.user.registerUser;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // IMPORTANT ( Create database )
        venueDBHandler v1_db=new venueDBHandler(this);
        v1_db.showAllVenues();
        v1_db.close();

//
//        Intent bookedVenuePage=new Intent(MainActivity.this, booked_venues.class);
//        Intent adminPage=new Intent(MainActivity.this, com.example.myapplication.admin.adminPage.class);
//        Intent viewAllVenues=new Intent(MainActivity.this, com.example.myapplication.admin.viewAllVenues.class);
//        Intent homePage=new Intent(MainActivity.this, homePage.class);
//        Intent registerUser_page=new Intent(MainActivity.this, registerUser.class);
//        Intent loginUser_page=new Intent(MainActivity.this, loginUser.class);
//        Intent venueSelect_page=new Intent(MainActivity.this, venueSelect.class);
//        Intent addVenue_page=new Intent(MainActivity.this, addVenue.class);
//        startActivity(loginUser_page);

        ImageView logoImageView = findViewById(R.id.logoImageView);
        TextView textView = findViewById(R.id.slogan);

        // Create fade-in animations
        ObjectAnimator logoFadeIn = ObjectAnimator.ofFloat(logoImageView, "alpha", 0f, 1f);
        ObjectAnimator textFadeIn = ObjectAnimator.ofFloat(textView, "alpha", 0f, 1f);

        // Set animation duration (in milliseconds)
        int fadeDuration = 2500;
        logoFadeIn.setDuration(fadeDuration);
        textFadeIn.setDuration(fadeDuration);

        // Create a scale-up animation for the logo
        ObjectAnimator logoScaleX = ObjectAnimator.ofFloat(logoImageView, "scaleX", 0.5f, 1f);
        ObjectAnimator logoScaleY = ObjectAnimator.ofFloat(logoImageView, "scaleY", 0.5f, 1f);
        logoScaleX.setDuration(1500);
        logoScaleY.setDuration(1500);

        // Create a translation animation for the text
        ObjectAnimator textTranslationY = ObjectAnimator.ofFloat(textView, "translationY", 100f, 0f);
        textTranslationY.setDuration(1500);
        textTranslationY.setInterpolator(new DecelerateInterpolator());

        // Combine animations into a set
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(logoFadeIn, textFadeIn, logoScaleX, logoScaleY, textTranslationY);

        // Start the animations
        animatorSet.start();

        // open login page after some amount of time
        new Handler().postDelayed(() -> {

            //retrieve email&pass from memory,if present
            SharedPreferences retrieveShared = getSharedPreferences("login_details", MODE_PRIVATE);
            String shared_email = retrieveShared.getString("user_email", "");
//            String shared_pass = retrieveShared.getString("user_pass", "");

            if(shared_email.isEmpty() || shared_email.equals(""))
            {
                Intent yourIntent = new Intent(MainActivity.this, loginUser.class);
                startActivity(yourIntent);
                finish();
            }
            else
            {
                Intent yourIntent = new Intent(MainActivity.this, homePage.class);
                startActivity(yourIntent);
                finish();
            }

        }, 2400);
    }
}