package dva217_grupp1.placemem.database;

import android.provider.BaseColumns;

/**
 * Created by lesliedahlberg on 12/05/15.
 */
public class Contract {
    public static abstract class Mems implements BaseColumns {
        public static final String TABLE_NAME = "mems";
        public static final String PHOTO_URI = "photo_uri";
        public static final String VOICE_URI = "voice_uri";
        public static final String LAT = "lat";
        public static final String LONG = "long";
        public static final String PLACE_NAME = "place_name";
        public static final String DATE = "date";
    }
}
