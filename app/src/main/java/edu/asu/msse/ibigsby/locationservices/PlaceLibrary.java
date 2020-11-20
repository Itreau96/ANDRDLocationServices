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
@version 1.0 Oct 25, 2020
 */

package edu.asu.msse.ibigsby.locationservices;

import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONObject;

import java.math.BigDecimal;

public class PlaceLibrary {
    ArrayList<PlaceDescription> places;

    PlaceLibrary() {
        places = new ArrayList<PlaceDescription>();
    }

    PlaceLibrary(String jsonStr) {
        try {
            JSONArray jo = new JSONArray(jsonStr);
            for (int i = 0; i < jo.length(); i++) {
                JSONObject place = jo.getJSONObject(i);
                PlaceDescription desc = new PlaceDescription(place);
                places.add(desc);
            }
        } catch (Exception ex) {
            android.util.Log.w(this.getClass().getSimpleName(),
                    "error converting to/from json");
        }
    }


}
