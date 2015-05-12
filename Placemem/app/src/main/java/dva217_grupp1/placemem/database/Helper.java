package dva217_grupp1.placemem.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by lesliedahlberg on 12/05/15.
 */
public class Helper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 0;
    public static final String DATABASE_NAME = "Database.db";
    private static final String TEXT_TYPE = " TEXT";
    private static final String REAL_TYPE = " REAL";
    private static final String COMMA_SEP = ", ";

    private static final String SQL_CREATE_ENTRIES_MEMS =
            "CREATE TABLE " + Contract.Mems.TABLE_NAME + " (" +
                    Contract.Mems._ID + " INTEGER PRIMARY KEY, " +
                    Contract.Mems.PHOTO_URI + TEXT_TYPE + COMMA_SEP +
                    Contract.Mems.VOICE_URI + TEXT_TYPE + COMMA_SEP +
                    Contract.Mems.PLACE_NAME + TEXT_TYPE + COMMA_SEP +
                    Contract.Mems.LAT + REAL_TYPE + COMMA_SEP +
                    Contract.Mems.LONG + REAL_TYPE + COMMA_SEP +
                    Contract.Mems.DATE + TEXT_TYPE +
                    " )";

    private static final String SQL_DELETE_ENTRIES_MEMS =
            "DROP TABLE IF EXISTS " + Contract.Mems.TABLE_NAME;
    
    public Helper(Context context) {
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
