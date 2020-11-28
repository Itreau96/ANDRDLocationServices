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
@version 1.0 Nov 21, 2020
 */

package edu.asu.msse.ibigsby.locationservices;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.SQLException;

// Sqlite helper class for opening place description database.
public class PlacesDB extends SQLiteOpenHelper {
    // Class variables
    private static final int DATABASE_VERSION = 1;
    private static String dbName = "place";
    private String dbPath;
    private SQLiteDatabase placesDB;
    private final Context context;

    public PlacesDB(Context context){
        super(context, dbName, null, DATABASE_VERSION);
        this.context = context;
        dbPath = context.getFilesDir().getPath() + "/";
    }

    public void createDB() throws IOException {
        this.getReadableDatabase();
        try {
            copyDB();
        } catch (IOException e) {
            android.util.Log.w(this.getClass().getSimpleName(),
                    "createDB Error copying database " + e.getMessage());
        }
    }

    // Attempt to open db provided. If the db doesn't exist in app data, copy from app bundle
    // and initialize.
    public SQLiteDatabase openDB() throws SQLException {
        String path = dbPath + dbName + ".db";
        if(checkDB()) {
            placesDB = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.OPEN_READWRITE);
        } else {
            try {
                this.copyDB();
                placesDB = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.OPEN_READWRITE);
            } catch(Exception ex) {
                android.util.Log.w(this.getClass().getSimpleName(),
                        "unable to copy and open db: " + ex.getMessage());
            }
        }
        return placesDB;
    }

    // Check that db is valid
    private boolean checkDB() {
        SQLiteDatabase testDB = null;
        boolean ret = false;
        try {
            String path = dbPath + dbName + ".db";
            File aFile = new File(path);
            if(aFile.exists()) {
                testDB = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.OPEN_READWRITE);
                if (testDB != null) {
                    ret = true;
                }
            }
        } catch(SQLiteException e) {
            android.util.Log.w("Error in checkDB: ", e.getMessage());
        }
        if(testDB != null){
            testDB.close();
        }
        return ret;
    }

    public void copyDB() throws IOException{
        try {
            if(!checkDB()) {
                InputStream inputDB =  context.getResources().openRawResource(R.raw.place);
                File aFile = new File(dbPath);
                if(!aFile.exists()) {
                    aFile.mkdirs();
                }
                String path = dbPath + dbName + ".db";
                OutputStream outDB = new FileOutputStream(path);
                byte[] fBuffer = new byte[1024];
                int length;
                while ((length = inputDB.read(fBuffer)) > 0) {
                    outDB.write(fBuffer, 0, length);
                }
                outDB.flush();
                outDB.close();
                inputDB.close();
            }
        } catch (IOException e) {
            android.util.Log.w("copyDB error: ", e.getMessage());
        }
    }

    @Override
    public synchronized void close() {
        if(placesDB != null)
            placesDB.close();
        super.close();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // No create logic necessary at present.
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // No upgrade logic necessary at present.
    }
}