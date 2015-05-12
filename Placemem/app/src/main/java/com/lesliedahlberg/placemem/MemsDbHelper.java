package com.lesliedahlberg.placemem;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by lesliedahlberg on 08/05/15.
 */
public class MemsDbHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 8;
    public static final String DATABASE_NAME = "Units.db";
    private static final String TEXT_TYPE = " TEXT";
    private static final String REAL_TYPE = " REAL";
    private static final String COMMA_SEP = ", ";
    private static final String SQL_CREATE_ENTRIES_MEMS =
            "CREATE TABLE " + MemsContract.Mems.TABLE_NAME + " (" +
                    MemsContract.Mems._ID + " INTEGER PRIMARY KEY, " +
                    MemsContract.Mems.PHOTO_URI + TEXT_TYPE + COMMA_SEP +
                    MemsContract.Mems.VOICE_URI + TEXT_TYPE + COMMA_SEP +
                    MemsContract.Mems.TRANSCRIPT + TEXT_TYPE + COMMA_SEP +
                    MemsContract.Mems.PLACE_NAME + TEXT_TYPE + COMMA_SEP +
                    MemsContract.Mems.LAT + REAL_TYPE + COMMA_SEP +
                    MemsContract.Mems.LONG + REAL_TYPE + COMMA_SEP +
                    MemsContract.Mems.DATE + TEXT_TYPE +
                    " )";
    private static final String SQL_DELETE_ENTRIES_MEMS =
            "DROP TABLE IF EXISTS " + MemsContract.Mems.TABLE_NAME;

    public MemsDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES_MEMS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_ENTRIES_MEMS);
        onCreate(db);
    }
}
