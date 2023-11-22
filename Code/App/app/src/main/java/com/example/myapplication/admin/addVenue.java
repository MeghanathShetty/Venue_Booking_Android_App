package com.example.myapplication.admin;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;
import com.example.myapplication.R;
import com.example.myapplication.database.venueDBHandler;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

public class addVenue extends AppCompatActivity {

    EditText name, desc, loc,img_path;
    Button submitBtn;
    Spinner venueTypes;
    String v_type = "Select venue type";
    boolean valid_link=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_venue);

        name = findViewById(R.id.name);
        desc = findViewById(R.id.description);
        loc = findViewById(R.id.location);
        submitBtn = findViewById(R.id.addVenueBtn);
        venueTypes = findViewById(R.id.type);
        img_path = findViewById(R.id.img_path);

        // Spinner initialization
        String[] venues = {"Select venue type", "sports", "entertainment", "education", "arts", "fitness"};
        ArrayAdapter<CharSequence> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, venues);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        venueTypes.setAdapter(adapter);

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


        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String v_name = name.getText().toString();
                String v_desc = desc.getText().toString();
                String v_loc = loc.getText().toString();
                String v_img_path = img_path.getText().toString();

                venueDBHandler db = new venueDBHandler(addVenue.this);

                // Check if link is proper
                if(!v_img_path.isEmpty())
                {
                    ImageButton img = new ImageButton(addVenue.this);
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

                if(v_name.isEmpty())
                    name.setError("Venue name required");
                else if(v_desc.isEmpty())
                    desc.setError("Please add some description");
                else if(v_loc.isEmpty())
                    loc.setError("Please add a location");
                else if (v_type.equals("Select venue type"))
                {
                    Toast.makeText(addVenue.this, "Please select a venue type", Toast.LENGTH_LONG).show();
                }
                else if (!v_img_path.isEmpty() && !valid_link)
                {
                    img_path.setError("Paste a valid link for image or leave empty or please wait!");
                }
                else {
                    if (db.checkVenue(v_name)) {
                        name.setError("Venue with the same name already exists");
//                        Toast.makeText(addVenue.this, "Venue with the same name already exists", Toast.LENGTH_LONG).show();
                    } else {
                        boolean result = db.addVenue(v_name, v_type, v_desc, v_loc,v_img_path);

                        if (result)
                        {
                            Toast.makeText(addVenue.this, "Venue added Successfully", Toast.LENGTH_SHORT).show();

                            //save in shared memory,refering that the database is created ( admin loggin )
                            SharedPreferences sharedPreferences = getSharedPreferences("admin_login", MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString("logged_in", "yes");
                            editor.apply();
                        }
                        else
                            Toast.makeText(addVenue.this, "Venue Could not be added", Toast.LENGTH_SHORT).show();
                    }
                }
                db.close();
            }
        });
    }
}
