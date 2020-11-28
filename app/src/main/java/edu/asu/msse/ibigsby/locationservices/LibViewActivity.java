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
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;

/*
    Activity used to display and manage PlaceDescription objects. Users can delete
    place descriptions from this view. Selecting a PlaceDescription will navigate to
    the PlaceActivity.
 */
public class LibViewActivity extends AppCompatActivity {

    // Private class variables
    private PlacesAdapter adapter;
    private boolean editing = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.libview_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // First, check if place description was edited or new
        Intent intent = getIntent();
        if (intent.hasExtra("place")) {
            Bundle bundle = intent.getExtras();
            PlaceDescription place = (PlaceDescription) bundle.getSerializable("place");
            boolean edit = intent.getBooleanExtra("edit", false);
            if (edit) {
                editPlaceInDB(place);
            } else {
                addPlaceToDB(place);
            }
        }

        // Initialize the adapter
        adapter = new PlacesAdapter(getPlaceNamesFromDB(), this);
        ListView listView = (ListView) findViewById(R.id.place_list);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {
                // If not editing, start new detail activity with place data
                if (!adapter.editing) {
                    Intent intent = new Intent(LibViewActivity.this, PlaceActivity.class);
                    PlaceDescription place = getPlaceFromNameDB(adapter.placeNames.get(position));
                    intent.putExtra("place", place);
                    intent.putExtra("edit", true);
                    startActivity(intent);
                }
            }
        });
        listView.setAdapter(adapter);

        // Set add place activity listener
        FloatingActionButton fab = findViewById(R.id.add_place_btn);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Start a new activity with an empty intent
                startActivity(new Intent(LibViewActivity.this, PlaceActivity.class));
            }
        });
    }

    // Adds a new place to the sqlite database model.
    public void addPlaceToDB(PlaceDescription place) {
        try {
            // Open database
            PlacesDB db = new PlacesDB(this);
            SQLiteDatabase placeDB = db.openDB();
            ContentValues hm = new ContentValues();
            // Populate all fields except for id
            hm.put("name", place.name);
            hm.put("description", place.description);
            hm.put("category", place.category);
            hm.put("address_street", place.addrStreet);
            hm.put("address_title", place.addrTitle);
            hm.put("elevation", place.elevation);
            hm.put("latitude", place.latitude);
            hm.put("longitude", place.longitude);
            // Insert and close
            placeDB.insert("places", null, hm);
            placeDB.close();
            db.close();
        } catch (Exception ex) {
            android.util.Log.w(this.getClass().getSimpleName(),"Exception adding place information: "+
                    ex.getMessage());
        }
    }

    // Deletes place from sqlite database model.
    public void deleteFromDB(String name) {
        try {
            // Open database
            PlacesDB db = new PlacesDB((Context) this);
            SQLiteDatabase placeDB = db.openDB();
            // Delete place
            placeDB.execSQL("delete from places where name=?;", new String[]{name});
            // Close database
            placeDB.close();
            db.close();
        }catch(Exception e){
            android.util.Log.w(this.getClass().getSimpleName()," error deleting place");
        }
    }

    // Edits place using the placeDescription data provided (place_id remains unchanged).
    private void editPlaceInDB(PlaceDescription place) {
        try{
            // Open database
            PlacesDB db = new PlacesDB(this);
            SQLiteDatabase placeDB = db.openDB();
            // Populate place values to edit
            ContentValues hm = new ContentValues();
            hm.put("name", place.name);
            hm.put("description", place.description);
            hm.put("category", place.category);
            hm.put("address_street", place.addrStreet);
            hm.put("address_title", place.addrTitle);
            hm.put("elevation", place.elevation);
            hm.put("latitude", place.latitude);
            hm.put("longitude", place.longitude);
            // Attempt to update entry and close
            placeDB.update("places", hm,  "place_id=" + place.id, null);
            placeDB.close();
            db.close();
        } catch (Exception ex){
            android.util.Log.w(this.getClass().getSimpleName(),
                    "Exception adding place description: "+ ex.getMessage());
        }
    }

    // Return placeDescription from database given place name as identifier
    public PlaceDescription getPlaceFromNameDB(String name) {
        PlaceDescription place = new PlaceDescription();
        try {
            // Open database
            PlacesDB db = new PlacesDB(this);
            SQLiteDatabase placeDB = db.openDB();
            // Query place names
            Cursor cur = placeDB.rawQuery("select name,description,category," +
                    "address_title,address_street,elevation,latitude,longitude," +
                    "place_id from places where name =?;", new String[]{name});
            // Loop through returned place descriptions (only return latest entry if more than 1)
            while (cur.moveToNext()) {
                try {
                    // Populate place values from cursor
                    place.name = cur.getString(0);
                    place.description = cur.getString(1);
                    place.category = cur.getString(2);
                    place.addrTitle = cur.getString(3);
                    place.addrStreet = cur.getString(4);
                    place.elevation = cur.getLong(5);
                    place.latitude = cur.getLong(6);
                    place.longitude = cur.getLong(7);
                    place.id = cur.getInt(8);
                } catch (Exception ex) {
                    android.util.Log.w(this.getClass().getSimpleName(),
                            "exception stepping through cursor" + ex.getMessage());
                }
            }
            // Close database
            cur.close();
            placeDB.close();
            db.close();
        }catch(Exception ex){
            android.util.Log.w(this.getClass().getSimpleName(),
                    "unable to load place from name");
        }
        return place;
    }

    // Return list of place names from database
    public ArrayList<String> getPlaceNamesFromDB() {
        // Init return list
        ArrayList<String> placeNames = new ArrayList<>();
        try {
            // Open database
            PlacesDB db = new PlacesDB(this);
            SQLiteDatabase placeDB = db.openDB();
            // Execute query
            Cursor cur = placeDB.rawQuery("select name from places;", new String[]{});
            // Add place names to return list
            while(cur.moveToNext()){
                try {
                    placeNames.add(cur.getString(0));
                } catch(Exception ex) {
                    android.util.Log.w(this.getClass().getSimpleName(),
                            "exception stepping through cursor" + ex.getMessage());
                }
            }
            // Close database
            cur.close();
            db.close();
            placeDB.close();
        }catch(Exception ex){
            android.util.Log.w(this.getClass().getSimpleName(),"unable to load place names");
        }
        return placeNames;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.libview_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            // If map item clicked, transition to map activity
            case R.id.action_map:
                Intent intent = new Intent(LibViewActivity.this, MapActivity.class);
                intent.putExtra("placeNames", adapter.placeNames);
                startActivity(intent);
                break;
            // If delete item clicked, set current state to list editing mode
            case R.id.deleting_item:
                editing = !item.isChecked();
                item.setChecked(editing);
                if (editing) {
                    item.setTitle("Done");
                } else {
                    item.setTitle("Edit");
                }
                adapter.editing = editing;
                adapter.notifyDataSetChanged();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}