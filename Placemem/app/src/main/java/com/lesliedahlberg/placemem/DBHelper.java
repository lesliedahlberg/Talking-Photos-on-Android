package com.lesliedahlberg.placemem;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
DB Helper
Basic methods for creating the DB, deleting the DB and upgrading it
 */

public class DBHelper extends SQLiteOpenHelper {

    //DB version, changing it calls the onUpgrade method
    public static final int DATABASE_VERSION = 8;

    public static final String DATABASE_NAME = "Units.db";
    private static final String TEXT_TYPE = " TEXT";
    private static final String REAL_TYPE = " REAL";
    private static final String COMMA_SEP = ", ";

    //SQL script for creating tables
    private static final String SQL_CREATE_ENTRIES_MEMS =
            "CREATE TABLE " + DBContract.Mems.TABLE_NAME + " (" +
                    DBContract.Mems._ID + " INTEGER PRIMARY KEY, " +
                    DBContract.Mems.PHOTO_URI + TEXT_TYPE + COMMA_SEP +
                    DBContract.Mems.VOICE_URI + TEXT_TYPE + COMMA_SEP +
                    DBContract.Mems.TRANSCRIPT + TEXT_TYPE + COMMA_SEP +
                    DBContract.Mems.PLACE_NAME + TEXT_TYPE + COMMA_SEP +
                    DBContract.Mems.LAT + REAL_TYPE + COMMA_SEP +
                    DBContract.Mems.LONG + REAL_TYPE + COMMA_SEP +
                    DBContract.Mems.DATE + TEXT_TYPE +
                    " )";

    //SQL script for deleting tables
    private static final String SQL_DELETE_ENTRIES_MEMS =
            "DROP TABLE IF EXISTS " + DBContract.Mems.TABLE_NAME;

    //Constructor
    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    //Gets called the first time the app runs if no db exists
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES_MEMS);
    }

    //Upgrades the DB
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_ENTRIES_MEMS);
        onCreate(db);
    }
}
