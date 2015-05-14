package com.lesliedahlberg.placemem;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.content.pm.PackageManager;


import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class AddMemActivity extends Activity {

    //Constants
    static final int REQUEST_TAKE_PHOTO = 1;
    static final String PHOTO_TAKEN = "photoTaken";
    static final String PHOTO_URI = "photoUri";

    //UI elements
    ImageView uiPhotoView;
    TextView uiTitleField;
    TextView uiGpsCoordsField;
    TextView uiDateField;
    TextView uiLocationField;

    //Values
    String currentLocation;
    String currentDate;
    double currentLatitude;
    double currentLongitude;
    String currentTitle;

    Boolean photoTaken;
    boolean hasGps;

    String tripId;

    //URI
    Uri currentPhotoUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        tripId = getIntent().getStringExtra(MemActivity.TRIP_ID);

        if (savedInstanceState != null) {
            photoTaken = savedInstanceState.getBoolean(PHOTO_TAKEN);
            currentPhotoUri = Uri.parse(savedInstanceState.getString(PHOTO_URI));
            tripId = savedInstanceState.getString(MemActivity.TRIP_ID);
        }else {
            photoTaken = false;
        }


        //Inflate UI
        setContentView(R.layout.activity_add_mem);

        //Get UI references
        uiPhotoView = (ImageView) findViewById(R.id.photoView);
        uiTitleField = (TextView) findViewById(R.id.titleField);

        //Get date
        currentDate = new SimpleDateFormat("dd. MM. yyyy", Locale.getDefault()).format(new Date());
        
        //For checking if device has gps
        PackageManager packageManager = this.getPackageManager();
        hasGps = packageManager.hasSystemFeature(PackageManager.FEATURE_LOCATION_GPS);

        //Get location data
        getLocationData();

        //Get photo
        if (photoTaken == false) {
            takePhoto();
            photoTaken = true;
        }






    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(PHOTO_TAKEN, photoTaken);
        outState.putString(PHOTO_URI, currentPhotoUri.toString());
        outState.putString(MemActivity.TRIP_ID, tripId);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState != null) {
            photoTaken = savedInstanceState.getBoolean(PHOTO_TAKEN);
            currentPhotoUri = Uri.parse(savedInstanceState.getString(PHOTO_URI));
            tripId = savedInstanceState.getString(MemActivity.TRIP_ID);
            showPhoto();
        }else {
            photoTaken = false;
        }
        photoTaken = savedInstanceState.getBoolean(PHOTO_TAKEN);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add_mem, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    //Save mem and exit activity
    public void save (View view) {
        currentTitle = uiTitleField.getText().toString();
        //Write to DB
        new DBInterface(this).addRow(currentPhotoUri.toString(), "2", currentLocation, currentLatitude, currentLongitude, currentDate, currentTitle, tripId);
        //Set result OK
        setResult(RESULT_OK);
        //Exit
        finish();

    }

    public void discard (View view) {
        setResult(RESULT_CANCELED);
        finish();
    }

    //Take photo
    public void takePhoto() {
        dispatchTakePictureIntent();
    }


    //Send intent to take photo
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //On photo taken
        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
            showPhoto();
        }else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void showPhoto() {
        if (!currentPhotoUri.toString().isEmpty()) {
            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), currentPhotoUri);
            } catch (IOException e) {
                e.printStackTrace();
            }
            setUiBackgroundView(bitmap);
        }
    }

    //Set background to taken photo
    private void setUiBackgroundView (Bitmap bitmap) {
        uiPhotoView.setImageBitmap(bitmap);
    }

    //Create file to store photo in (locally in private app storage)
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        /////////File storageDir = Environment.getExternalStorageDirectory();
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        //Save URI to file
        currentPhotoUri = Uri.fromFile(image);

        //Add photo to gallery
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        mediaScanIntent.setData(currentPhotoUri);
        this.sendBroadcast(mediaScanIntent);


        //return file
        return image;
    }



    //Get location data
    private void getLocationData () {
        // Acquire a reference to the system Location Manager
        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        // Define a listener that responds to location updates
        LocationListener locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                // Called when a new location is found by the network location provider.
                if(hasGps) {
                    currentLatitude = location.getLatitude();
                    currentLongitude = location.getLongitude();
                }
                else{
                    currentLatitude = 54.127537;
                    currentLongitude = 18.627353;
                }

                Geocoder gcd = new Geocoder(getApplicationContext(), Locale.getDefault());
                List<Address> addresses = null;
                try {
                    addresses = gcd.getFromLocation(currentLatitude, currentLongitude, 1);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (addresses.size() > 0){
                    currentLocation = addresses.get(0).getLocality();
                }

            }

            public void onStatusChanged(String provider, int status, Bundle extras) {}

            public void onProviderEnabled(String provider) {}

            public void onProviderDisabled(String provider) {}
        };

        // Register the listener with the Location Manager to receive location updates
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
    }


}
