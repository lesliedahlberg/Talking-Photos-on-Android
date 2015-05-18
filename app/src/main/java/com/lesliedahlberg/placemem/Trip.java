package com.lesliedahlberg.placemem;

import android.util.Log;

/**
 Data structure for Trip:
 Data from database gets converted to Trips and added to lists of Trips
 Should include all fields that the DB includes
 */

public class Trip {
    int id;
    String title;
    String video_uri;

    public Trip(int id, String title, String video_uri) {
        this.id = id;
        this.title = title;
        this.video_uri = video_uri;
    }
}
