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

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Vector;
import java.math.BigDecimal;

public class PlaceDescription {
    private String name;
    private String description;
    private String category;
    private String addrTitle;
    private String addrStreet;
    private float elevation;
    private float latitude;
    private float longitude;

    PlaceDescription() {
        name = "";
        description = "";
        category = "";
        addrTitle = "";
        addrStreet = "";
        elevation = 0;
        latitude = 0;
        longitude = 0;
    }

    PlaceDescription(String name, String description, String category,
             String addrTitle, String addrStreet, float elevation,
             float latitude, float longitude) {
        this.name = name;
        this.description = description;
        this.category = category;
        this.addrTitle = addrTitle;
        this.addrStreet = addrStreet;
        this.elevation = elevation;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    PlaceDescription(String jsonStr) {
        try {
            JSONObject jo = new JSONObject(jsonStr);
            name = jo.getString("name");
            description = jo.getString("description");
            category = jo.getString("category");
            addrTitle = jo.getString("addrTitle");
            addrStreet = jo.getString("addrStreet");
            elevation = BigDecimal.valueOf(jo.getDouble("elevation")).floatValue();
            latitude = BigDecimal.valueOf(jo.getDouble("latitude")).floatValue();
            longitude = BigDecimal.valueOf(jo.getDouble("longitude")).floatValue();
        } catch (Exception ex) {
            android.util.Log.w(this.getClass().getSimpleName(),
                    "error converting to/from json");
        }
    }

    public String getJson() {
        String ret = "";
        try {
            JSONObject jo = new JSONObject();
            jo.put("name", name);
            jo.put("description", description);
            jo.put("category", category);
            jo.put("addrTitle", addrTitle);
            jo.put("addrStreet", addrStreet);
            jo.put("elevation", elevation);
            jo.put("latitude", latitude);
            jo.put("longitude", longitude);
        } catch (Exception ex) {
            android.util.Log.w(this.getClass().getSimpleName(),
                    "error converting to/from json");
        }
        return ret;
    }
}
