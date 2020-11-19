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
@version 1.0 Oct 18, 2020
 */

package edu.asu.msse.ibigsby.locationservices;

import android.app.AlertDialog;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {

    private PlaceDescription location;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    public void createLocation(View view)
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
        if (locationName.getText().toString().trim().equals("") ||
            locationDesc.getText().toString().trim().equals("") ||
            locationCat.getText().toString().trim().equals("") ||
            locAddrTitle.getText().toString().trim().equals("") ||
            locAddrStreet.getText().toString().trim().equals("") ||
            locElevation.getText().toString().trim().equals("") ||
            locLatitude.getText().toString().trim().equals("") ||
            locLongitude.getText().toString().trim().equals(""))
        {
            createStatusDialog(getResources().getString(R.string.create_title_fail),
                    getResources().getString(R.string.create_msg_fail));
        }
        else
        {
            String name = locationName.getText().toString().trim();
            String description = locationDesc.getText().toString().trim();
            String category = locationCat.getText().toString().trim();
            String addrTitle = locAddrTitle.getText().toString().trim();
            String addrStreet = locAddrStreet.getText().toString().trim();
            float elevation = Float.parseFloat(locElevation.getText().toString().trim());
            float latitude = Float.parseFloat(locLatitude.getText().toString().trim());
            float longitude = Float.parseFloat(locLongitude.getText().toString().trim());

            location = new PlaceDescription(name, description, category, addrTitle, addrStreet, elevation, latitude, longitude);

            createStatusDialog(getResources().getString(R.string.create_title_success),
                    getResources().getString(R.string.create_msg_success));
        }
    }

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}