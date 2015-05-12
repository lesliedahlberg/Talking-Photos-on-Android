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

    public int getSize (String filter) {

        //DB Columns to get
        String[] projection = {
                DBContract.Mems._ID,
                DBContract.Mems.PHOTO_URI,
                DBContract.Mems.VOICE_URI,
                DBContract.Mems.PLACE_NAME,
                DBContract.Mems.LAT,
                DBContract.Mems.LONG,
                DBContract.Mems.DATE
        };

        //Sorting
        String sortOrder = DBContract.Mems._ID + " DESC";

        String selection;
        String[] selectionArgs;

        if (filter.isEmpty()) {
            selection = null;
            selectionArgs = null;
        }else {
            selection = DBContract.Mems.PLACE_NAME+"=?";
            selectionArgs = new String[1];
            selectionArgs[0] = filter;
        }


        //Cursor for storing all retrieved data
        Cursor cursor = readDb.query(DBContract.Mems.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);



        return cursor.getCount();
    }

    //Get DB entry for row ID
    public ArrayList<Mem> getRows(String filter) {

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
                DBContract.Mems.DATE
        };

        //Sorting
        String sortOrder = DBContract.Mems._ID + " DESC";

        String selection;
        String[] selectionArgs;

        if (filter.isEmpty()) {
            selection = null;
            selectionArgs = null;
        }else {
            selection = DBContract.Mems.PLACE_NAME+"=?";
            selectionArgs = new String[1];
            selectionArgs[0] = filter;
        }


        //Cursor for storing all retrieved data
        Cursor cursor = readDb.query(DBContract.Mems.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);

        while(cursor.moveToNext()) {
            mems.add(new Mem(cursor.getInt(cursor.getColumnIndexOrThrow(DBContract.Mems._ID)),
                    cursor.getString(cursor.getColumnIndexOrThrow(DBContract.Mems.PHOTO_URI)),
                    cursor.getString(cursor.getColumnIndexOrThrow(DBContract.Mems.VOICE_URI)),
                    cursor.getString(cursor.getColumnIndexOrThrow(DBContract.Mems.PLACE_NAME)),
                    cursor.getString(cursor.getColumnIndexOrThrow(DBContract.Mems.LAT)),
                    cursor.getString(cursor.getColumnIndexOrThrow(DBContract.Mems.LONG)),
                    cursor.getString(cursor.getColumnIndexOrThrow(DBContract.Mems.DATE))));
        }

        return mems;
    }

    //Get DB entry for row ID
    public Mem getRow(int position, String filter) {

        //Mem data
        Mem mem;

        //DB Columns to get
        String[] projection = {
                DBContract.Mems._ID,
                DBContract.Mems.PHOTO_URI,
                DBContract.Mems.VOICE_URI,
                DBContract.Mems.PLACE_NAME,
                DBContract.Mems.LAT,
                DBContract.Mems.LONG,
                DBContract.Mems.DATE
        };

        //Sorting
        String sortOrder = DBContract.Mems._ID + " DESC";

        String selection;
        String[] selectionArgs;

        if (filter.isEmpty()) {
            selection = null;
            selectionArgs = null;
        }else {
            selection = DBContract.Mems.PLACE_NAME+"=?";
            selectionArgs = new String[1];
            selectionArgs[0] = filter;
        }


        //Cursor for storing all retrieved data
        Cursor cursor = readDb.query(DBContract.Mems.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);


        //Get first row in cursor (only 1 exists)
        cursor.moveToPosition(position);

        //Load mem with data
        mem = new Mem(cursor.getInt(cursor.getColumnIndexOrThrow(DBContract.Mems._ID)),
                cursor.getString(cursor.getColumnIndexOrThrow(DBContract.Mems.PHOTO_URI)),
                cursor.getString(cursor.getColumnIndexOrThrow(DBContract.Mems.VOICE_URI)),
                cursor.getString(cursor.getColumnIndexOrThrow(DBContract.Mems.PLACE_NAME)),
                cursor.getString(cursor.getColumnIndexOrThrow(DBContract.Mems.LAT)),
                cursor.getString(cursor.getColumnIndexOrThrow(DBContract.Mems.LONG)),
                cursor.getString(cursor.getColumnIndexOrThrow(DBContract.Mems.DATE)));


        Log.v("LESLIE", "READ DB");

        return mem;
    }

    //Remove row for ID
    public void removeRow(int id) {
        writeDb.delete(DBContract.Mems.TABLE_NAME, DBContract.Mems._ID+"=?", new String[]{String.valueOf(id)});
    }

    //Add row for data
    public int addRow(String photoUri, String voiceUri, String location, double latitude, double longitude, String date) {

        //Feed data into content value pairs
        ContentValues contentValues = new ContentValues();
        contentValues.put(DBContract.Mems.PHOTO_URI, photoUri);
        contentValues.put(DBContract.Mems.VOICE_URI, voiceUri);
        contentValues.put(DBContract.Mems.PLACE_NAME, location);
        contentValues.put(DBContract.Mems.LAT, latitude);
        contentValues.put(DBContract.Mems.LONG, longitude);
        contentValues.put(DBContract.Mems.DATE, date);

        //write to db and return row ID
        return (int) writeDb.insert(DBContract.Mems.TABLE_NAME, null, contentValues);
    }



}


