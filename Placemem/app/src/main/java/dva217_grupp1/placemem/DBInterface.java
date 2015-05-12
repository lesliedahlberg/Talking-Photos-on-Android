package dva217_grupp1.placemem;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;


public class DBInterface {

    DBHelper memsDbDBHelper;
    SQLiteDatabase readDb;
    SQLiteDatabase writeDb;

    public DBInterface(Context context) {
        memsDbDBHelper = new DBHelper(context);
        readDb = memsDbDBHelper.getReadableDatabase();
        writeDb = memsDbDBHelper.getWritableDatabase();
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
                DBContract.Mems.DATE
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
                cursor.getString(cursor.getColumnIndexOrThrow(DBContract.Mems.DATE)));

        return mem;
    }

    public void removeRow(int id) {
        writeDb.delete(DBContract.Mems.TABLE_NAME, DBContract.Mems._ID+"=?", new String[]{String.valueOf(id)});
    }

    public int addRow(String photoUri, String voiceUri, String location, double latitude, double longitude, String date) {

        ContentValues contentValues = new ContentValues();
        contentValues.put(DBContract.Mems.PHOTO_URI, photoUri);
        contentValues.put(DBContract.Mems.VOICE_URI, voiceUri);
        contentValues.put(DBContract.Mems.PLACE_NAME, location);
        contentValues.put(DBContract.Mems.LAT, latitude);
        contentValues.put(DBContract.Mems.LONG, longitude);
        contentValues.put(DBContract.Mems.DATE, date);

        return (int) writeDb.insert(DBContract.Mems.TABLE_NAME, null, contentValues);
    }
}
