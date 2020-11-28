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

import java.io.Serializable;

public class PlaceDescription implements Serializable {
    public String name;
    public String description;
    public String category;
    public String addrTitle;
    public String addrStreet;
    public float elevation;
    public float latitude;
    public float longitude;
    public int id;

    // Empty initializer
    PlaceDescription() {
        name = "";
        description = "";
        category = "";
        addrTitle = "";
        addrStreet = "";
        elevation = 0;
        latitude = 0;
        longitude = 0;
        id = -1;
    }

    // Initializer with required values
    PlaceDescription(String name, String description, String category,
             String addrTitle, String addrStreet, float elevation,
             float latitude, float longitude, int id) {
        this.name = name;
        this.description = description;
        this.category = category;
        this.addrTitle = addrTitle;
        this.addrStreet = addrStreet;
        this.elevation = elevation;
        this.latitude = latitude;
        this.longitude = longitude;
        this.id = id;
    }
}
