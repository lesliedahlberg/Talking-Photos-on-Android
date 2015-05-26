package com.lesliedahlberg.placemem;

import android.provider.BaseColumns;

/**
DB Contract
Defines tables and table columns
 */
public class DBContract {

    public static abstract class Mems implements BaseColumns {
        public static final String TABLE_NAME = "mems";
        public static final String PHOTO_URI = "photo_uri";
        public static final String VOICE_URI = "voice_uri";
        public static final String VIDEO_URI = "video_uri";
        public static final String TRANSCRIPT = "transcript";
        public static final String LAT = "lat";
        public static final String LONG = "long";
        public static final String PLACE_NAME = "place_name";
        public static final String DATE = "date";
        public static final String TITLE = "title";
        public static final String TRIP_ID = "trip_id";
    }

    public static abstract class Trips implements BaseColumns {
        public static final String TABLE_NAME = "trips";
        public static final String TITLE = "title";
        public static final String VIDEO_URI = "video_uri";
    }

}
