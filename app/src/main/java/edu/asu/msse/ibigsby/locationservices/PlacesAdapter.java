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
@version 1.0 Nov 27, 2020
 */

package edu.asu.msse.ibigsby.locationservices;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;

// Custom adapter class used to display place listView data
public class PlacesAdapter extends ArrayAdapter<String> {

    // Class variables
    public ArrayList<String> placeNames;
    Context mContext;
    boolean editing = false;

    // Initialize with place data
    public PlacesAdapter(ArrayList<String> data, Context context) {
        super(context, R.layout.place_listview, data);
        this.placeNames = data;
        this.mContext=context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Generate new list item (if null)
        View listItem = convertView;
        if(listItem == null)
            listItem = LayoutInflater.from(mContext).inflate(R.layout.place_listview, parent, false);

        // Retrieve view fields
        ImageButton removeButton = (ImageButton) listItem.findViewById(R.id.delete_place);
        TextView placeLabel = (TextView) listItem.findViewById(R.id.place_label);

        // Set display text
        String title = getItem(position);
        placeLabel.setText(title);

        // If editing, display remove button
        if (editing) {
            removeButton.setVisibility(View.VISIBLE);
        } else {
            removeButton.setVisibility(View.GONE);
        }

        // Set custom click listener
        removeButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                PlacesAdapter.this.deleteClicked(position);
            }
        });

        return listItem;
    }

    // Custom onclick listener for removing item
    private void deleteClicked(int position) {
        // Ensure main activity executed click
        if (mContext instanceof LibViewActivity) {
            // If editing, remove item clicked and update data set
            ((LibViewActivity) mContext).deleteFromDB(placeNames.get(position));
            placeNames.remove(position);
            notifyDataSetChanged();
        }
    }
}
