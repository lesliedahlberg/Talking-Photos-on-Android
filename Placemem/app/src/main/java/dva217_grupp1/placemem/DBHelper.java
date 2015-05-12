package dva217_grupp1.placemem;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by lesliedahlberg on 12/05/15.
 */
public class DBHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 0;
    public static final String DATABASE_NAME = "Database.db";
    private static final String TEXT_TYPE = " TEXT";
    private static final String REAL_TYPE = " REAL";
    private static final String COMMA_SEP = ", ";

    private static final String SQL_CREATE_ENTRIES_MEMS =
            "CREATE TABLE " + DBContract.Mems.TABLE_NAME + " (" +
                    DBContract.Mems._ID + " INTEGER PRIMARY KEY, " +
                    DBContract.Mems.PHOTO_URI + TEXT_TYPE + COMMA_SEP +
                    DBContract.Mems.VOICE_URI + TEXT_TYPE + COMMA_SEP +
                    DBContract.Mems.PLACE_NAME + TEXT_TYPE + COMMA_SEP +
                    DBContract.Mems.LAT + REAL_TYPE + COMMA_SEP +
                    DBContract.Mems.LONG + REAL_TYPE + COMMA_SEP +
                    DBContract.Mems.DATE + TEXT_TYPE +
                    " )";

    private static final String SQL_DELETE_ENTRIES_MEMS =
            "DROP TABLE IF EXISTS " + DBContract.Mems.TABLE_NAME;
    
    public DBHelper(Context context) {
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
