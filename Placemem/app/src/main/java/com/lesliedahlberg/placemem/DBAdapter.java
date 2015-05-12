package com.lesliedahlberg.placemem;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

/**
 * Created by lesliedahlberg on 08/05/15.
 */
public class DBAdapter {

    MemsDbHelper memsDbHelper;
    SQLiteDatabase readDb;
    SQLiteDatabase writeDb;


    public DBAdapter(Context context) {
        memsDbHelper = new MemsDbHelper(context);
        readDb = memsDbHelper.getReadableDatabase();
        writeDb = memsDbHelper.getWritableDatabase();
    }

    public ArrayList<Mem> getAllData() {
        ArrayList<Mem> mems = new ArrayList<>();

        String[] projection = {
                MemsContract.Mems._ID,
                MemsContract.Mems.PHOTO_URI,
                MemsContract.Mems.VOICE_URI,
                MemsContract.Mems.PLACE_NAME,
                MemsContract.Mems.LAT,
                MemsContract.Mems.LONG,
                MemsContract.Mems.DATE,
                MemsContract.Mems.TRANSCRIPT
        };

        String sortOrder = MemsContract.Mems._ID + " DESC";
        Cursor cursor = readDb.query(MemsContract.Mems.TABLE_NAME, projection, null, null, null, null, sortOrder);

        while(cursor.moveToNext()) {
            Mem mem = new Mem(cursor.getInt(cursor.getColumnIndexOrThrow(MemsContract.Mems._ID)),
                    cursor.getString(cursor.getColumnIndexOrThrow(MemsContract.Mems.PHOTO_URI)),
                    cursor.getString(cursor.getColumnIndexOrThrow(MemsContract.Mems.VOICE_URI)),
                    cursor.getString(cursor.getColumnIndexOrThrow(MemsContract.Mems.PLACE_NAME)),
                    cursor.getString(cursor.getColumnIndexOrThrow(MemsContract.Mems.LAT)),
                    cursor.getString(cursor.getColumnIndexOrThrow(MemsContract.Mems.LONG)),
                    cursor.getString(cursor.getColumnIndexOrThrow(MemsContract.Mems.DATE)),
                    cursor.getString(cursor.getColumnIndexOrThrow(MemsContract.Mems.TRANSCRIPT)));
            mems.add(mem);
        }

        return mems;
    }

    public Mem getRow(int position) {
        Mem mem;

        String[] projection = {
                MemsContract.Mems._ID,
                MemsContract.Mems.PHOTO_URI,
                MemsContract.Mems.VOICE_URI,
                MemsContract.Mems.PLACE_NAME,
                MemsContract.Mems.LAT,
                MemsContract.Mems.LONG,
                MemsContract.Mems.DATE,
                MemsContract.Mems.TRANSCRIPT
        };

        String sortOrder = MemsContract.Mems._ID + " DESC";
        Cursor cursor = readDb.query(MemsContract.Mems.TABLE_NAME, projection, null, null, null, null, sortOrder);

        cursor.moveToPosition(position);
        mem = new Mem(cursor.getInt(cursor.getColumnIndexOrThrow(MemsContract.Mems._ID)),
                cursor.getString(cursor.getColumnIndexOrThrow(MemsContract.Mems.PHOTO_URI)),
                cursor.getString(cursor.getColumnIndexOrThrow(MemsContract.Mems.VOICE_URI)),
                cursor.getString(cursor.getColumnIndexOrThrow(MemsContract.Mems.PLACE_NAME)),
                cursor.getString(cursor.getColumnIndexOrThrow(MemsContract.Mems.LAT)),
                cursor.getString(cursor.getColumnIndexOrThrow(MemsContract.Mems.LONG)),
                cursor.getString(cursor.getColumnIndexOrThrow(MemsContract.Mems.DATE)),
                cursor.getString(cursor.getColumnIndexOrThrow(MemsContract.Mems.TRANSCRIPT)));

        return mem;
    }

    public void removeRow(int id) {
        writeDb.delete(MemsContract.Mems.TABLE_NAME, MemsContract.Mems._ID+"=?", new String[]{String.valueOf(id)});
    }

    public int addRow(String photoUri, String voiceUri, String location, double latitude, double longitude, String date, String transcript) {

        ContentValues contentValues = new ContentValues();
        contentValues.put(MemsContract.Mems.PHOTO_URI, photoUri);
        contentValues.put(MemsContract.Mems.VOICE_URI, voiceUri);
        contentValues.put(MemsContract.Mems.PLACE_NAME, location);
        contentValues.put(MemsContract.Mems.LAT, latitude);
        contentValues.put(MemsContract.Mems.LONG, longitude);
        contentValues.put(MemsContract.Mems.DATE, date);
        contentValues.put(MemsContract.Mems.TRANSCRIPT, transcript);

        return (int) writeDb.insert(MemsContract.Mems.TABLE_NAME, null, contentValues);
    }



}


