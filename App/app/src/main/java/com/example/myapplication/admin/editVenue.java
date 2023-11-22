package com.example.myapplication.admin;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;

import com.example.myapplication.R;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.example.myapplication.R;
import com.example.myapplication.database.venueDBHandler;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

public class editVenue extends AppCompatActivity
{

    EditText name, desc, loc,img_path;
    Button submitBtn,deleteBtn;
    Spinner venueTypes;
    String v_type = "Select venue type";
    boolean valid_link=false;
    String venueName,venueType,venueDesc,venueLoc,imgPath;

    @SuppressLint("Range")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_venue);

        name = findViewById(R.id.name);
        desc = findViewById(R.id.description);
        loc = findViewById(R.id.location);
        submitBtn = findViewById(R.id.addVenueBtn);
        venueTypes = findViewById(R.id.type);
        img_path = findViewById(R.id.img_path);
        deleteBtn = findViewById(R.id.deleteVenueBtn);

        // Custom delete confirmation toast
        LayoutInflater inflater = getLayoutInflater();
        View customToastLayout = inflater.inflate(R.layout.custom_toast_delete, null);
        TextView c_txt1 = customToastLayout.findViewById(R.id.custom_txt1);
        Button yesBtn = customToastLayout.findViewById(R.id.yesBtn);
        Button noBtn = customToastLayout.findViewById(R.id.noBtn);

        // Create an AlertDialog and set the custom layout
        AlertDialog.Builder builder = new AlertDialog.Builder(editVenue.this);
        builder.setView(customToastLayout);
        AlertDialog customToastDialog = builder.create();

        Intent prvPage=getIntent();
        venueName=prvPage.getStringExtra("Venue_Name");

        // retreive venue details
        venueDBHandler v_db=new venueDBHandler(this);
        Cursor v_cur=v_db.retreiveVenues(venueName);

        if(v_cur.getCount()>0)
        {
            v_cur.moveToFirst();
            venueType = v_cur.getString(v_cur.getColumnIndex("type"));
            venueDesc = v_cur.getString(v_cur.getColumnIndex("description"));
            venueLoc = v_cur.getString(v_cur.getColumnIndex("location"));
            imgPath = v_cur.getString(v_cur.getColumnIndex("img_path"));
        }

        v_cur.close();
        v_db.close();

        name.setText(venueName);
        desc.setText(venueDesc);
        loc.setText(venueLoc);
        img_path.setText(imgPath);

        // Spinner initialization
        String[] venues = {"Select venue type", "sports", "entertainment", "education", "arts", "fitness"};
        ArrayAdapter<CharSequence> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, venues);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        venueTypes.setAdapter(adapter);
        int sel=0;
        if(venueType.equals("sports"))
            sel=1;
        else if(venueType.equals("entertainment"))
            sel=2;
        else if(venueType.equals("education"))
            sel=3;
        else if(venueType.equals("arts"))
            sel=4;
        else if(venueType.equals("fitness"))
            sel=5;
        venueTypes.setSelection(sel);

        venueTypes.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position != 0) {
                    v_type = parent.getSelectedItem().toString();
                }
                else if(position == 0){
                    v_type = "Select venue type";
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        venueDBHandler db = new venueDBHandler(this);

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String v_desc = desc.getText().toString();
                String v_loc = loc.getText().toString();
                String v_img_path = img_path.getText().toString();

                // Check if link is proper
                if(!v_img_path.isEmpty())
                {
                    ImageButton img = new ImageButton(editVenue.this);
                    Picasso.get().load(v_img_path).into(img, new Callback() {
                        @Override
                        public void onSuccess() {
                            // Image loaded successfully
                            valid_link=true;
                        }
                        @Override
                        public void onError(Exception e) {
                            // image did not load,so link invalid
                            valid_link=false;
                        }
                    });
                }

                if(v_desc.isEmpty())
                    desc.setError("Please add some details about the venue");
                else if(v_loc.isEmpty())
                    loc.setError("Please add a location");
                else if (v_type.equals("Select venue type"))
                {
                    Toast.makeText(editVenue.this, "Please select a venue type", Toast.LENGTH_LONG).show();
                }
                else if(!v_img_path.isEmpty() && !valid_link)
                {
                    img_path.setError("Paste a valid link for image or leave empty or please wait!");
                }
                 else
                {
                    boolean result = db.updateVenue(venueName,v_type,v_desc,v_loc,v_img_path);

                    if (result)
                        Toast.makeText(editVenue.this, "Venue updated Successfully", Toast.LENGTH_SHORT).show();
                    else
                        Toast.makeText(editVenue.this, "Venue Could not be updated", Toast.LENGTH_SHORT).show();
                }
            }
        });

        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                c_txt1.setText("Note : Venue will be permanently deleted.");
                // Display the Custom Toast Confirmation
                customToastDialog.show();
            }
        });

        yesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                venueDBHandler db1=new venueDBHandler(editVenue.this);
                boolean check=db1.deleteVenue(venueName);

                if(check)
                {
                    // Toast message success
                    Toast.makeText(editVenue.this,"Venue Deleted Successfully",Toast.LENGTH_LONG).show();
                    customToastDialog.dismiss();

                    // Move to all venues page
                    Intent i=new Intent(editVenue.this, adminPage.class);
                    startActivity(i);
                    finish();
                }
                else
                {
                    // Toast message not success
                    Toast.makeText(editVenue.this,"Deletion failed!!!",Toast.LENGTH_LONG).show();
                    customToastDialog.dismiss();

                }
                db1.close();
            }
        });

        noBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // remove Custom Toast Confirmation
                customToastDialog.dismiss();
            }
        });

        // CLose db
        db.close();

    }
}
