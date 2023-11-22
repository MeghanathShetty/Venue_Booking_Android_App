package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.viewpager2.widget.ViewPager2;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.myapplication.Utils.emailHelper;
import com.example.myapplication.database.bookedDetailsDBHandler;
import com.example.myapplication.database.venueDBHandler;
import com.example.myapplication.receivers.StatusUpdateWorker;
import com.example.myapplication.user.loginUser;
import com.example.myapplication.user.updateUser;
import com.example.myapplication.user.userHistory;
import com.example.myapplication.user.userProfile;
import com.example.myapplication.user.userTransactions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class homePage extends AppCompatActivity {

    ImageView sportsImg,enterImg,eduImg,artsImg,fitnessImg;

    DrawerLayout drawerLayout;
    LinearLayout sliderDots;
    BottomNavigationView bottomNavigationView;
    int dotsCount;
    ImageView[] dots;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        sportsImg=findViewById(R.id.sportsImageView);
        enterImg=findViewById(R.id.entertainmentImageView);
        eduImg=findViewById(R.id.educationalImageView);
        artsImg=findViewById(R.id.artsImageView);
        fitnessImg=findViewById(R.id.fitnessImageView);
        sliderDots = findViewById(R.id.SliderDots);
        bottomNavigationView = findViewById(R.id.bottom_navigation);


        // drawable layout
        drawerLayout = findViewById(R.id.drawer_layout);

        // hamburger menu
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                // Handle navigation item clicks here
                int id = item.getItemId();
                if (id == R.id.edit) {
                    Intent i = new Intent(homePage.this, updateUser.class);
                    startActivity(i);
                }
                else if (id == R.id.userHistory) {
                    Intent i = new Intent(homePage.this, userHistory.class);
                    startActivity(i);
                } else if (id == R.id.logout) {
                    Intent ii = new Intent(homePage.this, loginUser.class);

                    SharedPreferences sharedPreferences = getSharedPreferences("login_details", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    // Remove the "user_email" and "user_pass" data
                    editor.remove("user_email");
                    editor.remove("user_pass");
                    editor.apply();

                    Toast.makeText(homePage.this, "Logout Successfull", Toast.LENGTH_SHORT).show();
                    startActivity(ii);
                    finish();

                } else if (id == R.id.about) {
                    Intent iii = new Intent(homePage.this, aboutUs.class);
                    startActivity(iii);
                }

                drawerLayout.openDrawer(GravityCompat.START);
                return true;
            }
        });



//        Image Slider ++++++++++++++++++++++++++++++++++++++++++++++++
        List<Integer> imageList = new ArrayList<>();
        imageList.add(R.drawable.slider_1);
        imageList.add(R.drawable.slider_2);
        imageList.add(R.drawable.slider_3);

        ViewPager2 viewPager = findViewById(R.id.viewPager);
        ImageSliderAdapter adapter = new ImageSliderAdapter(imageList);
        viewPager.setAdapter(adapter);

        //auto-change image
        final Handler handler = new Handler();
        final Runnable update = new Runnable() {
            public void run() {
                int currentPage = viewPager.getCurrentItem();
                int totalPages = imageList.size();
                if (currentPage == totalPages - 1) {
                    viewPager.setCurrentItem(0);
                } else {
                    viewPager.setCurrentItem(currentPage + 1);
                }
            }
        };
        int delay = 2000;
        int period = 3000;

        Timer swipeTimer = new Timer();
        swipeTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.post(update);
            }
        }, delay, period);
//        +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

        // Slider dots
        dotsCount =imageList.size();
        dots = new ImageView[dotsCount];

        for(int i = 0; i < dotsCount; i++){
            dots[i] = new ImageView(this);
            dots[i].setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.nonactive_dot));

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

            params.setMargins(8, 0, 8, 0);

            sliderDots.addView(dots[i], params);

        }

        dots[0].setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.active_dot));

        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);

                for (int i = 0; i < dotsCount; i++) {
                    dots[i].setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.nonactive_dot));
                }

                dots[position].setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.active_dot));
            }
        });


        Intent venueSelect_Page=new Intent(homePage.this, venueSelect.class);
        sportsImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    venueSelect_Page.putExtra("type","sports");
                    startActivity(venueSelect_Page);

            }
        });

        enterImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                venueSelect_Page.putExtra("type","entertainment");
                startActivity(venueSelect_Page);
            }
        });

        eduImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                venueSelect_Page.putExtra("type","education");
                startActivity(venueSelect_Page);
            }
        });

        artsImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                venueSelect_Page.putExtra("type","arts");
                startActivity(venueSelect_Page);
            }
        });

        fitnessImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                venueSelect_Page.putExtra("type","fitness");
                startActivity(venueSelect_Page);
            }
        });

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.navigation_transactions) {
                    Intent i=new Intent(homePage.this, userTransactions.class);
                    startActivity(i);
                    return true;
                }
                else if (item.getItemId() == R.id.navigation_profile) {
                    Intent i=new Intent(homePage.this, userProfile.class);
                    startActivity(i);
                    return true;
                }
                return false;
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                drawerLayout.closeDrawer(GravityCompat.START);
            } else {
                drawerLayout.openDrawer(GravityCompat.START);
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}