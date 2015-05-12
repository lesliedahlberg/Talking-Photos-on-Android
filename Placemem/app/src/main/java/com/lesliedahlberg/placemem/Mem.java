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
    String transcript;

    public Mem(int id, String photoUri, String voiceUri, String location, String latitude, String longitude, String date, String transcript) {
        this.id = id;
        this.photoUri = photoUri;
        this.voiceUri = voiceUri;
        this.location = location;
        this.latitude = latitude;
        this.longitude = longitude;
        this.date = date;
        this.transcript = transcript;
    }
}
