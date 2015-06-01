package com.lesliedahlberg.placemem;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import java.io.File;
import java.lang.reflect.Array;
import java.util.ArrayList;

/**
Class for interfacing with DB
Provides methods for pushing and pulling data from DB
Provides search methods for only pulling certain data
 */

public class DBInterface {

    //Environment variables
    Context context;

    //DB variables
    DBHelper DBHelper;
    SQLiteDatabase readDb;
    SQLiteDatabase writeDb;

    //Constructor
    public DBInterface(Context context) {
        this.context = context;
        DBHelper = new DBHelper(context);
        readDb = DBHelper.getReadableDatabase();
        writeDb = DBHelper.getWritableDatabase();
    }


    //Return trip title by trip ID
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

        //Where?
        String selection;
        String[] selectionArgs;
        selection = DBContract.Trips._ID+"=?";
        selectionArgs = new String[]{id};

        //Cursor for storing all retrieved data
        Cursor cursor = readDb.query(DBContract.Trips.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);

        //Get first item
        cursor.moveToFirst();

        //Return title
        return cursor.getString(cursor.getColumnIndexOrThrow(DBContract.Trips.TITLE));
    }

    //Get first mem in trip by trip ID
    public Mem getTripSomeMem(String id) {

        //Mem data
        Mem mem;

        //DB Columns to get
        String[] projection = {
                DBContract.Mems._ID,
                DBContract.Mems.PHOTO_URI,
                DBContract.Mems.VOICE_URI,
                DBContract.Mems.PLACE_NAME,
                DBContract.Mems.VIDEO_URI,
                DBContract.Mems.LAT,
                DBContract.Mems.LONG,
                DBContract.Mems.DATE,
                DBContract.Mems.TITLE,
                DBContract.Mems.TRIP_ID
        };

        //Where?
        String selection;
        String[] selectionArgs;
        selection = DBContract.Mems.TRIP_ID+"=?";
        selectionArgs = new String[1];
        selectionArgs[0] = String.valueOf(id);

        //Cursor for storing all retrieved data
        Cursor cursor = readDb.query(DBContract.Mems.TABLE_NAME, projection, selection, selectionArgs, null, null, null);

        if (cursor.getCount() > 0){
            cursor.moveToFirst();
            mem = new Mem(cursor.getInt(cursor.getColumnIndexOrThrow(DBContract.Mems._ID)),
                    cursor.getString(cursor.getColumnIndexOrThrow(DBContract.Mems.PHOTO_URI)),
                    cursor.getString(cursor.getColumnIndexOrThrow(DBContract.Mems.VOICE_URI)),
                    cursor.getString(cursor.getColumnIndexOrThrow(DBContract.Mems.VIDEO_URI)),
                    cursor.getString(cursor.getColumnIndexOrThrow(DBContract.Mems.PLACE_NAME)),
                    cursor.getString(cursor.getColumnIndexOrThrow(DBContract.Mems.LAT)),
                    cursor.getString(cursor.getColumnIndexOrThrow(DBContract.Mems.LONG)),
                    cursor.getString(cursor.getColumnIndexOrThrow(DBContract.Mems.DATE)),
                    cursor.getString(cursor.getColumnIndexOrThrow(DBContract.Mems.TITLE)),
                    cursor.getString(cursor.getColumnIndexOrThrow(DBContract.Mems.TRIP_ID)));
            return mem;
        }else {
            //Return null if trip empty
            return null;
        }

    }

    //Get Trips
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

        //Cursor for storing all retrieved data
        Cursor cursor = readDb.query(DBContract.Trips.TABLE_NAME, projection, null, null, null, null, sortOrder);

        //Load cursor to Trips
        while(cursor.moveToNext()) {
            trips.add(new Trip(cursor.getInt(cursor.getColumnIndexOrThrow(DBContract.Trips._ID)),
                    cursor.getString(cursor.getColumnIndexOrThrow(DBContract.Trips.TITLE)),
                    cursor.getString(cursor.getColumnIndexOrThrow(DBContract.Trips.VIDEO_URI))));
        }

        //Return trips
        return trips;
    }

    //Get mem by mem ID
    public Mem getRow(int id) {

        //Mem data
        Mem mem;

        //DB Columns to get
        String[] projection = {
                DBContract.Mems._ID,
                DBContract.Mems.PHOTO_URI,
                DBContract.Mems.VOICE_URI,
                DBContract.Mems.VIDEO_URI,
                DBContract.Mems.PLACE_NAME,
                DBContract.Mems.LAT,
                DBContract.Mems.LONG,
                DBContract.Mems.DATE,
                DBContract.Mems.TITLE,
                DBContract.Mems.TRIP_ID
        };

        String selection;
        String[] selectionArgs;

        selection = DBContract.Mems._ID+"=?";
        selectionArgs = new String[1];
        selectionArgs[0] = String.valueOf(id);


        //Cursor for storing all retrieved data
        Cursor cursor = readDb.query(DBContract.Mems.TABLE_NAME, projection, selection, selectionArgs, null, null, null);

        //Load data to Mem
        cursor.moveToFirst();
        mem = new Mem(cursor.getInt(cursor.getColumnIndexOrThrow(DBContract.Mems._ID)),
                    cursor.getString(cursor.getColumnIndexOrThrow(DBContract.Mems.PHOTO_URI)),
                    cursor.getString(cursor.getColumnIndexOrThrow(DBContract.Mems.VOICE_URI)),
                    cursor.getString(cursor.getColumnIndexOrThrow(DBContract.Mems.VIDEO_URI)),
                    cursor.getString(cursor.getColumnIndexOrThrow(DBContract.Mems.PLACE_NAME)),
                    cursor.getString(cursor.getColumnIndexOrThrow(DBContract.Mems.LAT)),
                    cursor.getString(cursor.getColumnIndexOrThrow(DBContract.Mems.LONG)),
                    cursor.getString(cursor.getColumnIndexOrThrow(DBContract.Mems.DATE)),
                    cursor.getString(cursor.getColumnIndexOrThrow(DBContract.Mems.TITLE)),
                    cursor.getString(cursor.getColumnIndexOrThrow(DBContract.Mems.TRIP_ID)));


        //Return Mem
        return mem;
    }

    //Get Mem count in Trip
    public int getMemCountInTrip(String tripId){
        return (int) DatabaseUtils.longForQuery(readDb, "SELECT COUNT(*) FROM "+DBContract.Mems.TABLE_NAME+" WHERE "+DBContract.Mems.TRIP_ID+"="+tripId, null);
    }


    //Get Mems in Trip by Trip ID
    public ArrayList<Mem> getRows(String tripId) {

        //Mem data
        ArrayList<Mem> mems = new ArrayList<>();

        //DB Columns to get
        String[] projection = {
                DBContract.Mems._ID,
                DBContract.Mems.PHOTO_URI,
                DBContract.Mems.VOICE_URI,
                DBContract.Mems.PLACE_NAME,
                DBContract.Mems.VIDEO_URI,
                DBContract.Mems.LAT,
                DBContract.Mems.LONG,
                DBContract.Mems.DATE,
                DBContract.Mems.TITLE,
                DBContract.Mems.TRIP_ID
        };

        //Sorting
        String sortOrder = DBContract.Mems._ID + " DESC";


        //Where
        String selection;
        String[] selectionArgs;
        selection = DBContract.Mems.TRIP_ID+"=?";
        selectionArgs = new String[1];
        selectionArgs[0] = tripId;

        //Cursor for storing all retrieved data
        Cursor cursor = readDb.query(DBContract.Mems.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);

        //Load data to mems
        while(cursor.moveToNext()) {
            mems.add(new Mem(cursor.getInt(cursor.getColumnIndexOrThrow(DBContract.Mems._ID)),
                    cursor.getString(cursor.getColumnIndexOrThrow(DBContract.Mems.PHOTO_URI)),
                    cursor.getString(cursor.getColumnIndexOrThrow(DBContract.Mems.VOICE_URI)),
                    cursor.getString(cursor.getColumnIndexOrThrow(DBContract.Mems.VIDEO_URI)),
                    cursor.getString(cursor.getColumnIndexOrThrow(DBContract.Mems.PLACE_NAME)),
                    cursor.getString(cursor.getColumnIndexOrThrow(DBContract.Mems.LAT)),
                    cursor.getString(cursor.getColumnIndexOrThrow(DBContract.Mems.LONG)),
                    cursor.getString(cursor.getColumnIndexOrThrow(DBContract.Mems.DATE)),
                    cursor.getString(cursor.getColumnIndexOrThrow(DBContract.Mems.TITLE)),
                    cursor.getString(cursor.getColumnIndexOrThrow(DBContract.Mems.TRIP_ID))));
        }

        //Return mems
        return mems;
    }

    //Remove Mem by Mem ID
    public void removeRow(int id) {
        Mem rowToDelete = getRow(id);

        //Remove files
        new File(VideoEncoder.getRealPathFromURI(context, Uri.parse(rowToDelete.photoUri))).delete();
        new File(VideoEncoder.getRealPathFromURI(context, Uri.parse(rowToDelete.voiceUri))).delete();
        new File(VideoEncoder.getRealPathFromURI(context, Uri.parse(rowToDelete.videoUri))).delete();

        //Remove DB entry
        writeDb.delete(DBContract.Mems.TABLE_NAME, DBContract.Mems._ID+"=?", new String[]{String.valueOf(id)});
    }

    //Remove Trip by Trip ID and remove all Mem in Trip
    public void removeTripRow(int id) {
        //Remove trip DB entry
        writeDb.delete(DBContract.Trips.TABLE_NAME, DBContract.Trips._ID + "=?", new String[]{String.valueOf(id)});

        //Remove Mems in Trip
        ArrayList<Mem> mems = getRows(String.valueOf(id));
        for (Mem mem : mems){
            removeRow(mem.id);
        }
    }

    //Add Mem
    public int addRow(String photoUri, String voiceUri, String videoUri, String location, double latitude, double longitude, String date, String title, String tripId) {

        //Feed data into content value pairs
        ContentValues contentValues = new ContentValues();
        contentValues.put(DBContract.Mems.PHOTO_URI, photoUri);
        contentValues.put(DBContract.Mems.VOICE_URI, voiceUri);
        contentValues.put(DBContract.Mems.VIDEO_URI, videoUri);
        contentValues.put(DBContract.Mems.PLACE_NAME, location);
        contentValues.put(DBContract.Mems.LAT, latitude);
        contentValues.put(DBContract.Mems.LONG, longitude);
        contentValues.put(DBContract.Mems.DATE, date);
        contentValues.put(DBContract.Mems.TITLE, title);
        contentValues.put(DBContract.Mems.TRIP_ID, tripId);

        //write to db and return row ID
        return (int) writeDb.insert(DBContract.Mems.TABLE_NAME, null, contentValues);
    }

    public int updateVideoUri(String memId, String videoUri) {

        ContentValues contentValues = new ContentValues();
        contentValues.put(DBContract.Mems.VIDEO_URI, videoUri);

        String selection = DBContract.Mems._ID+"=?";
        String[] selectionArgs = new String[1];
        selectionArgs[0] = memId;

        return (int) writeDb.update(DBContract.Mems.TABLE_NAME, contentValues, selection, selectionArgs);
    }

    //Add Trip
    public int addTripRow(String title) {

        //Feed data into content value pairs
        ContentValues contentValues = new ContentValues();
        contentValues.put(DBContract.Trips.TITLE, title);
        contentValues.put(DBContract.Trips.VIDEO_URI, "");

        //write to db and return row ID
        return (int) writeDb.insert(DBContract.Trips.TABLE_NAME, null, contentValues);
    }


}


