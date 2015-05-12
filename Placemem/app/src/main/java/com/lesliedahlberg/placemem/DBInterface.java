package com.lesliedahlberg.placemem;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

/**
 * Created by lesliedahlberg on 08/05/15.
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

    public ArrayList<Mem> getAllData() {
        ArrayList<Mem> mems = new ArrayList<>();

        String[] projection = {
                DBContract.Mems._ID,
                DBContract.Mems.PHOTO_URI,
                DBContract.Mems.VOICE_URI,
                DBContract.Mems.PLACE_NAME,
                DBContract.Mems.LAT,
                DBContract.Mems.LONG,
                DBContract.Mems.DATE,
                DBContract.Mems.TRANSCRIPT
        };

        String sortOrder = DBContract.Mems._ID + " DESC";
        Cursor cursor = readDb.query(DBContract.Mems.TABLE_NAME, projection, null, null, null, null, sortOrder);

        while(cursor.moveToNext()) {
            Mem mem = new Mem(cursor.getInt(cursor.getColumnIndexOrThrow(DBContract.Mems._ID)),
                    cursor.getString(cursor.getColumnIndexOrThrow(DBContract.Mems.PHOTO_URI)),
                    cursor.getString(cursor.getColumnIndexOrThrow(DBContract.Mems.VOICE_URI)),
                    cursor.getString(cursor.getColumnIndexOrThrow(DBContract.Mems.PLACE_NAME)),
                    cursor.getString(cursor.getColumnIndexOrThrow(DBContract.Mems.LAT)),
                    cursor.getString(cursor.getColumnIndexOrThrow(DBContract.Mems.LONG)),
                    cursor.getString(cursor.getColumnIndexOrThrow(DBContract.Mems.DATE)),
                    cursor.getString(cursor.getColumnIndexOrThrow(DBContract.Mems.TRANSCRIPT)));
            mems.add(mem);
        }

        return mems;
    }

    public Mem getRow(int position) {
        Mem mem;

        String[] projection = {
                DBContract.Mems._ID,
                DBContract.Mems.PHOTO_URI,
                DBContract.Mems.VOICE_URI,
                DBContract.Mems.PLACE_NAME,
                DBContract.Mems.LAT,
                DBContract.Mems.LONG,
                DBContract.Mems.DATE,
                DBContract.Mems.TRANSCRIPT
        };

        String sortOrder = DBContract.Mems._ID + " DESC";
        Cursor cursor = readDb.query(DBContract.Mems.TABLE_NAME, projection, null, null, null, null, sortOrder);

        cursor.moveToPosition(position);
        mem = new Mem(cursor.getInt(cursor.getColumnIndexOrThrow(DBContract.Mems._ID)),
                cursor.getString(cursor.getColumnIndexOrThrow(DBContract.Mems.PHOTO_URI)),
                cursor.getString(cursor.getColumnIndexOrThrow(DBContract.Mems.VOICE_URI)),
                cursor.getString(cursor.getColumnIndexOrThrow(DBContract.Mems.PLACE_NAME)),
                cursor.getString(cursor.getColumnIndexOrThrow(DBContract.Mems.LAT)),
                cursor.getString(cursor.getColumnIndexOrThrow(DBContract.Mems.LONG)),
                cursor.getString(cursor.getColumnIndexOrThrow(DBContract.Mems.DATE)),
                cursor.getString(cursor.getColumnIndexOrThrow(DBContract.Mems.TRANSCRIPT)));

        return mem;
    }

    public void removeRow(int id) {
        writeDb.delete(DBContract.Mems.TABLE_NAME, DBContract.Mems._ID+"=?", new String[]{String.valueOf(id)});
    }

    public int addRow(String photoUri, String voiceUri, String location, double latitude, double longitude, String date, String transcript) {

        ContentValues contentValues = new ContentValues();
        contentValues.put(DBContract.Mems.PHOTO_URI, photoUri);
        contentValues.put(DBContract.Mems.VOICE_URI, voiceUri);
        contentValues.put(DBContract.Mems.PLACE_NAME, location);
        contentValues.put(DBContract.Mems.LAT, latitude);
        contentValues.put(DBContract.Mems.LONG, longitude);
        contentValues.put(DBContract.Mems.DATE, date);
        contentValues.put(DBContract.Mems.TRANSCRIPT, transcript);

        return (int) writeDb.insert(DBContract.Mems.TABLE_NAME, null, contentValues);
    }



}


