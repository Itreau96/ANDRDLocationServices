/*
    Copyright 2020 Itreau Bigsby

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

@author   Itreau Bigsby    mailto:ibigsby@asu.edu
@version 3.0 Nov 27, 2020
 */

package edu.asu.msse.ibigsby.locationservices;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;

import android.widget.Button;
import android.widget.EditText;

// Activity used to display and edit place data
public class PlaceActivity extends AppCompatActivity {
    // Class variables
    PlaceDescription currentPlace;
    boolean edit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Intent intent = getIntent();
        // Load intent data if available
        if (intent.hasExtra("place")) {
            // If place provided, set form fields
            Bundle bundle = intent.getExtras();
            currentPlace = (PlaceDescription) bundle.getSerializable("place");

            // If editing existing place, change display to present this difference
            edit = intent.getBooleanExtra("edit", false);
            Button buttonLabel = (Button) findViewById(R.id.doneButton);
            if (edit) {
                buttonLabel.setText("Done");
            } else {
                buttonLabel.setText("Create");
            }
        } else {
            currentPlace = new PlaceDescription();
        }

        setFormFields(currentPlace);
    }

    public void setFormFields(PlaceDescription place)
    {
        // Retrieve form fields by id
        EditText locationName = (EditText) findViewById(R.id.nameInput);
        EditText locationDesc = (EditText) findViewById(R.id.descInput);
        EditText locationCat = (EditText) findViewById(R.id.categoryInput);
        EditText locAddrTitle = (EditText) findViewById(R.id.addrTitleInput);
        EditText locAddrStreet = (EditText) findViewById(R.id.addrStreetInput);
        EditText locElevation = (EditText) findViewById(R.id.elevationInput);
        EditText locLatitude = (EditText) findViewById(R.id.latitudeInput);
        EditText locLongitude = (EditText) findViewById(R.id.longitudeInput);

        // Set values to match place description provided
        locationName.setText(place.name);
        locationDesc.setText(place.description);
        locationCat.setText(place.category);
        locAddrTitle.setText(place.addrTitle);
        locAddrStreet.setText(place.addrStreet);
        locElevation.setText(Float.toString(place.elevation));
        locLatitude.setText(Float.toString(place.latitude));
        locLongitude.setText(Float.toString(place.longitude));
    }

    public void doneClicked(View view)
    {
        // Retrieve fields by id
        EditText locationName = (EditText) findViewById(R.id.nameInput);
        EditText locationDesc = (EditText) findViewById(R.id.descInput);
        EditText locationCat = (EditText) findViewById(R.id.categoryInput);
        EditText locAddrTitle = (EditText) findViewById(R.id.addrTitleInput);
        EditText locAddrStreet = (EditText) findViewById(R.id.addrStreetInput);
        EditText locElevation = (EditText) findViewById(R.id.elevationInput);
        EditText locLatitude = (EditText) findViewById(R.id.latitudeInput);
        EditText locLongitude = (EditText) findViewById(R.id.longitudeInput);

        // Validate input
        if (!validEntry())
        {
            createStatusDialog(getResources().getString(R.string.create_title_fail),
                    getResources().getString(R.string.create_msg_fail));
        }
        else
        {
            // Retrieve values from fields
            String name = locationName.getText().toString().trim();
            String description = locationDesc.getText().toString().trim();
            String category = locationCat.getText().toString().trim();
            String addrTitle = locAddrTitle.getText().toString().trim();
            String addrStreet = locAddrStreet.getText().toString().trim();
            float elevation = Float.parseFloat(locElevation.getText().toString().trim());
            float latitude = Float.parseFloat(locLatitude.getText().toString().trim());
            float longitude = Float.parseFloat(locLongitude.getText().toString().trim());

            // Generate place
            currentPlace = new PlaceDescription(name, description, category, addrTitle, addrStreet,
                    elevation, latitude, longitude, currentPlace.id);

            // Add intent data and start activity
            Intent intent = new Intent(PlaceActivity.this, LibViewActivity.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable("place", currentPlace);
            intent.putExtras(bundle);
            intent.putExtra("edit", edit);
            startActivity(intent);
        }
    }

    private boolean validEntry() {
        // Retrieve fields by id
        EditText locationName = (EditText) findViewById(R.id.nameInput);

        // Validate required null fields
        if (locationName.getText().toString().trim().equals(""))
        {
            return false;
        }

        // Query database to determine if name already exists in database
        String placeName = locationName.getText().toString();
        if (!placeName.equals(currentPlace.name)) {
            try {
                PlacesDB db = new PlacesDB((Context) this);
                SQLiteDatabase pDB = db.openDB();
                Cursor cur = pDB.rawQuery("select name from places where name=? ;", new String[]{placeName});
                if (cur.getCount() > 0) {
                    return false;
                }
            } catch (Exception ex) {
                android.util.Log.w(this.getClass().getSimpleName(), "Unable to load places from database.");
            }
        }
        return true;
    }

    // Helper function for generic status message
    public void createStatusDialog(String title, String msg)
    {
        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(msg)
                // A null listener allows the button to dismiss the dialog and take no further action.
                .setNeutralButton(android.R.string.ok, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }
}