package dva217_grupp1.placemem;


public class Mem {
    int id;
    String photoUri;
    String voiceUri;
    String location;
    String latitude;
    String longitude;
    String date;

    public Mem(int id, String photoUri, String voiceUri, String location, String latitude, String longitude, String date) {
        this.id = id;
        this.photoUri = photoUri;
        this.voiceUri = voiceUri;
        this.location = location;
        this.latitude = latitude;
        this.longitude = longitude;
        this.date = date;
    }
}
