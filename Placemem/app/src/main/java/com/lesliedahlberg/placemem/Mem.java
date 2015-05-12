package com.lesliedahlberg.placemem;

/**
 * Created by lesliedahlberg on 08/05/15.
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
