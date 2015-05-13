package com.lesliedahlberg.placemem;

/**
Data structure for mem:
Data from database gets converted to Mems and added to lists of Mems
Should include all fields that the DB includes
 */

public class Mem {
    int id;
    String photoUri;
    String voiceUri;
    String location;
    String latitude;
    String longitude;
    String date;
    Boolean playing;
    String title;
    String tripId;

    public Mem(int id, String photoUri, String voiceUri, String location, String latitude, String longitude, String date, String title, String tripId) {
        this.id = id;
        this.photoUri = photoUri;
        this.voiceUri = voiceUri;
        this.location = location;
        this.latitude = latitude;
        this.longitude = longitude;
        this.date = date;
        this.playing = false;
        this.title = title;
        this.tripId = tripId;
    }
}
