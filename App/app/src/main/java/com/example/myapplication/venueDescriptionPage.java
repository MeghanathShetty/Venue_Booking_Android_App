package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.myapplication.database.venueDBHandler;
import com.example.myapplication.user.bookVenue;
import com.squareup.picasso.Picasso;

public class venueDescriptionPage extends AppCompatActivity {

    TextView name,loc,desc,type;
    Button bookPageBtn;
    ImageView img;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_venue_description_page);

        name=findViewById(R.id.venueName);
        loc=findViewById(R.id.venueLocation);
        desc=findViewById(R.id.descriptionTxt);
        type=findViewById(R.id.venueType);
        bookPageBtn=findViewById(R.id.btnBook);
        img=findViewById(R.id.imageView);

        // get venue name from previous page
        Intent venueSelect_page=getIntent();
        String v_name=venueSelect_page.getStringExtra("venue_name");

        // retreive image path
        venueDBHandler v_db=new venueDBHandler(this);
        Cursor v_cur=v_db.retreiveVenues(v_name);
        v_cur.moveToFirst();
        @SuppressLint("Range") String v_loc = v_cur.getString(v_cur.getColumnIndex("location"));
        @SuppressLint("Range") String v_type = v_cur.getString(v_cur.getColumnIndex("type"));
        @SuppressLint("Range") String v_imgPath = v_cur.getString(v_cur.getColumnIndex("img_path"));
        @SuppressLint("Range") String v_desc = v_cur.getString(v_cur.getColumnIndex("description"));
        v_cur.close();
        v_db.close();

        // set the retreived details to textviews etc
        name.setText(v_name);
        loc.setText(v_loc);
        desc.setText(v_desc);
        type.setText(v_type);
        Picasso.get().load(v_imgPath).into(img);

        bookPageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent nxtPage=new Intent(venueDescriptionPage.this, bookVenue.class);
                nxtPage.putExtra("venue_name",v_name);
                startActivity(nxtPage);
            }
        });

    }
}