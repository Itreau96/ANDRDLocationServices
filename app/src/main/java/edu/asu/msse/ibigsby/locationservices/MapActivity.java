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
@version 2.0 Nov 27, 2020
 */

package edu.asu.msse.ibigsby.locationservices;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.PointF;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.ArrayList;

// Activity used to display bearing and distance between two selected place descriptions
public class MapActivity extends AppCompatActivity {
    // Class variables
    ArrayList<String> placeNames = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Intent intent = getIntent();
        // Load list of place names
        placeNames = intent.getStringArrayListExtra("placeNames");
        // Set form fields
        setFormFields();
    }

    public void setFormFields()
    {
        // Setup from adapter
        Spinner fromSpinner = (Spinner) findViewById(R.id.from_spinner);
        ArrayAdapter<String> fromAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_dropdown_item, placeNames);
        fromAdapter.setDropDownViewResource( android.R.layout.simple_spinner_dropdown_item );
        fromSpinner.setAdapter(fromAdapter);

        // Setup to adapter
        Spinner toSpinner = (Spinner) findViewById(R.id.to_spinner);
        ArrayAdapter<String> toAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_dropdown_item, placeNames);
        toAdapter.setDropDownViewResource( android.R.layout.simple_spinner_dropdown_item );
        toSpinner.setAdapter(toAdapter);

        // Setup type spinner
        Spinner typeSpinner = (Spinner) findViewById(R.id.type_spinner);
        ArrayAdapter<CharSequence> typeAdapter = ArrayAdapter.createFromResource(this,
                R.array.map_options_array, android.R.layout.simple_spinner_item);
        typeSpinner.setAdapter(typeAdapter);
    }

    public void calculateClicked(View view)
    {
        // Retrieve fields by id
        Spinner fromSpinner = (Spinner) findViewById(R.id.from_spinner);
        Spinner toSpinner = (Spinner) findViewById(R.id.to_spinner);
        Spinner typeSpinner = (Spinner) findViewById(R.id.type_spinner);
        TextView calculation = (TextView) findViewById(R.id.calc_label);

        // Get two locations
        String firstPlace = fromSpinner.getSelectedItem().toString();
        String secondPlace = toSpinner.getSelectedItem().toString();

        // Calculate points for two locations
        PointF p1 = getLocationFromName(firstPlace);
        PointF p2 = getLocationFromName(secondPlace);
        // Get computation value
        String calculationType = typeSpinner.getSelectedItem().toString();
        if (calculationType.equals("Distance")) {
            calculation.setText("The circle distance between " + firstPlace + " and " +
                    secondPlace + " is: " + circleDistance(p1, p2));
        } else if (calculationType.equals("Bearing")) {
            calculation.setText("Bearing angle between " + firstPlace + " and " +
                    secondPlace + " is: " + calculateBearing(p1, p2));
        } else {
            createStatusDialog("Error",
                    "Please select a mapping category from the dropdown.");
        }
    }

    // Helper function used to retrieve location data from sqlite database model given place name
    private PointF getLocationFromName(String name) {
        PointF retPoint = new PointF();
        try{
            PlacesDB db = new PlacesDB((Context)this);
            SQLiteDatabase placeDB = db.openDB();
            Cursor cur = placeDB.rawQuery("select latitude, longitude from places where name =? ;", new String[]{name});
            while(cur.moveToNext()) {
                retPoint = new PointF(cur.getFloat(0), cur.getFloat(1));
            }
            db.close();
            placeDB.close();
        }catch(Exception ex){
            android.util.Log.w(this.getClass().getSimpleName(),
                    "unable to load place location");
        }
        return retPoint;
    }

    // Helper function used to calculate circle distance between two points
    public double circleDistance(PointF first, PointF second) {
        // Convert values to radians
        double x1 = Math.toRadians(first.x);
        double y1 = Math.toRadians(first.y);
        double x2 = Math.toRadians(second.x);
        double y2 = Math.toRadians(second.y);

        // Get angle from radians
        double angle = Math.acos(Math.sin(x1) * Math.sin(x2)
                + Math.cos(x1) * Math.cos(x2) * Math.cos(y1 - y2));

        // Convert to degrees
        angle = Math.toDegrees(angle);

        // Multiply by 60 nautical miles
        double distance = 60 * angle;

        return distance;
    }

    // Helper function used to calculate bearing between two points
    public double calculateBearing(PointF first, PointF second) {
        // Get delta values
        float x = Float.valueOf(first.y).compareTo(second.y);
        float y = Float.valueOf(second.y).compareTo(first.y);
        float deltaLong = Math.abs(x * y);
        // Calculate bx
        double bX = Math.cos(second.x) * Math.sin(deltaLong);
        // Calculate by
        double bY = Math.cos(first.x) * Math.sin(second.x) - Math.sin(first.x) * Math.cos(second.x) * Math.cos(deltaLong);

        // Return bearing angle
        return Math.atan2(bX, bY);
    }

    // Helper function used to generate generic message dialog
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