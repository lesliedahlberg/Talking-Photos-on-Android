package com.lesliedahlberg.placemem;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;

/**
Class for interfacing with DB
Provides methods for pushing and pulling data from DB
Provides search methods for only pulling certain data
 */

public class DBInterface {

    DBHelper DBHelper;
    SQLiteDatabase readDb;
    SQLiteDatabase writeDb;


    public DBInterface(Context context) {
        DBHelper = new DBHelper(context);
        readDb = DBHelper.getReadableDatabase();
        writeDb = DBHelper.getWritableDatabase();
    }

    public String getTripName(String id) {
        //Mem data
        ArrayList<Trip> trips = new ArrayList<>();

        //DB Columns to get
        String[] projection = {
                DBContract.Trips._ID,
                DBContract.Trips.VIDEO_URI,
                DBContract.Trips.TITLE
        };

        //Sorting
        String sortOrder = DBContract.Trips._ID + " DESC";

        String selection;
        String[] selectionArgs;

        selection = DBContract.Trips._ID+"=?";
        selectionArgs = new String[]{id};


        //Cursor for storing all retrieved data
        Cursor cursor = readDb.query(DBContract.Trips.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);

        cursor.moveToFirst();

        return cursor.getString(cursor.getColumnIndexOrThrow(DBContract.Trips.TITLE));
    }

    //Get DB entry for trip rows
    public ArrayList<Trip> getTripRows() {

        //Mem data
        ArrayList<Trip> trips = new ArrayList<>();

        //DB Columns to get
        String[] projection = {
                DBContract.Trips._ID,
                DBContract.Trips.VIDEO_URI,
                DBContract.Trips.TITLE
        };

        //Sorting
        String sortOrder = DBContract.Trips._ID + " DESC";

        String selection;
        String[] selectionArgs;




        //Cursor for storing all retrieved data
        Cursor cursor = readDb.query(DBContract.Trips.TABLE_NAME, projection, null, null, null, null, sortOrder);

        while(cursor.moveToNext()) {
            trips.add(new Trip(cursor.getInt(cursor.getColumnIndexOrThrow(DBContract.Trips._ID)),
                    cursor.getString(cursor.getColumnIndexOrThrow(DBContract.Trips.TITLE)),
                    cursor.getString(cursor.getColumnIndexOrThrow(DBContract.Trips.VIDEO_URI))));
        }

        return trips;
    }



    //Get DB entry for row ID
    public ArrayList<Mem> getRows(String tripId) {

        //Mem data
        ArrayList<Mem> mems = new ArrayList<>();

        //DB Columns to get
        String[] projection = {
                DBContract.Mems._ID,
                DBContract.Mems.PHOTO_URI,
                DBContract.Mems.VOICE_URI,
                DBContract.Mems.PLACE_NAME,
                DBContract.Mems.LAT,
                DBContract.Mems.LONG,
                DBContract.Mems.DATE,
                DBContract.Mems.TITLE,
                DBContract.Mems.TRIP_ID
        };

        //Sorting
        String sortOrder = DBContract.Mems._ID + " DESC";

        String selection;
        String[] selectionArgs;

        selection = DBContract.Mems.TRIP_ID+"=?";
        selectionArgs = new String[1];
        selectionArgs[0] = tripId;


        //Cursor for storing all retrieved data
        Cursor cursor = readDb.query(DBContract.Mems.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);

        while(cursor.moveToNext()) {
            mems.add(new Mem(cursor.getInt(cursor.getColumnIndexOrThrow(DBContract.Mems._ID)),
                    cursor.getString(cursor.getColumnIndexOrThrow(DBContract.Mems.PHOTO_URI)),
                    cursor.getString(cursor.getColumnIndexOrThrow(DBContract.Mems.VOICE_URI)),
                    cursor.getString(cursor.getColumnIndexOrThrow(DBContract.Mems.PLACE_NAME)),
                    cursor.getString(cursor.getColumnIndexOrThrow(DBContract.Mems.LAT)),
                    cursor.getString(cursor.getColumnIndexOrThrow(DBContract.Mems.LONG)),
                    cursor.getString(cursor.getColumnIndexOrThrow(DBContract.Mems.DATE)),
                    cursor.getString(cursor.getColumnIndexOrThrow(DBContract.Mems.TITLE)),
                    cursor.getString(cursor.getColumnIndexOrThrow(DBContract.Mems.TRIP_ID))));
        }

        return mems;
    }

    //Remove row for ID
    public void removeRow(int id) {
        writeDb.delete(DBContract.Mems.TABLE_NAME, DBContract.Mems._ID+"=?", new String[]{String.valueOf(id)});
    }

    //Remove trip row for ID
    public void removeTripRow(int id) {
        writeDb.delete(DBContract.Trips.TABLE_NAME, DBContract.Trips._ID+"=?", new String[]{String.valueOf(id)});
        writeDb.delete(DBContract.Mems.TABLE_NAME, DBContract.Mems.TRIP_ID+"=?", new String[]{String.valueOf(id)});
    }

    //Add row for data
    public int addRow(String photoUri, String voiceUri, String location, double latitude, double longitude, String date, String title, String tripId) {

        //Feed data into content value pairs
        ContentValues contentValues = new ContentValues();
        contentValues.put(DBContract.Mems.PHOTO_URI, photoUri);
        contentValues.put(DBContract.Mems.VOICE_URI, voiceUri);
        contentValues.put(DBContract.Mems.PLACE_NAME, location);
        contentValues.put(DBContract.Mems.LAT, latitude);
        contentValues.put(DBContract.Mems.LONG, longitude);
        contentValues.put(DBContract.Mems.DATE, date);
        contentValues.put(DBContract.Mems.TITLE, title);
        contentValues.put(DBContract.Mems.TRIP_ID, tripId);

        //write to db and return row ID
        return (int) writeDb.insert(DBContract.Mems.TABLE_NAME, null, contentValues);
    }

    //Add trip row for data
    public int addTripRow(String title) {

        //Feed data into content value pairs
        ContentValues contentValues = new ContentValues();
        contentValues.put(DBContract.Trips.TITLE, title);
        contentValues.put(DBContract.Trips.VIDEO_URI, "");

        Log.v("LULU3", "TITLE IS: "+title);

        //write to db and return row ID
        return (int) writeDb.insert(DBContract.Trips.TABLE_NAME, null, contentValues);
    }


}


