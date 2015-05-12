package com.lesliedahlberg.placemem;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
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
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.common.api.GoogleApiClient;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class AddMemActivity extends Activity {

    //Constants
    static final int REQUEST_TAKE_PHOTO = 1;
    static final int REQUEST_GET_LOCATION = 2;


    GoogleApiClient mGoogleApiClient;



    //UI elements
    ImageView uiPhotoView;
    RelativeLayout uiBackgroundView;
    TextView uiGpsCoordsField;
    TextView uiDateField;
    TextView uiLocationField;

    //Values
    String currentLocation;
    String currentDate;
    double currentLatitude;
    double currentLongitude;

    //URI
    Uri currentPhotoUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Inflate UI
        setContentView(R.layout.activity_add_mem);

        //Get UI references
        uiPhotoView = (ImageView) findViewById(R.id.photoView);
        uiBackgroundView = (RelativeLayout) findViewById(R.id.backgroundView);
        uiGpsCoordsField = (TextView) findViewById(R.id.gps);
        uiDateField = (TextView) findViewById(R.id.date);
        uiLocationField = (TextView) findViewById(R.id.location);

        //Get date
        currentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());


        //Get location data
        getLocationData();


        //Get photo
        takePhoto();
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
        //Write to DB
        new DBInterface(this).addRow(currentPhotoUri.toString(), "2", currentLocation, currentLatitude, currentLongitude, currentDate);
        //Set result OK
        setResult(RESULT_OK);
        //Exit
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
            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), currentPhotoUri);
            } catch (IOException e) {
                e.printStackTrace();
            }
            setUiBackgroundView(bitmap);
        }else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    //Set background to taken photo
    private void setUiBackgroundView (Bitmap bitmap) {
        uiBackgroundView.setBackgroundDrawable(new BitmapDrawable(getResources(), bitmap));
    }

    //Create file to store photo in (locally in private app storage)
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStorageDirectory();
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        //Save URI to file
        currentPhotoUri = Uri.fromFile(image);

        //return file
        return image;
    }

    //Set date and location to UI fields
    private void setUiFields() {
        //Set date
        uiDateField.setText(currentDate);
        //Set GPS coords
        uiGpsCoordsField.setText(currentLatitude + ", " + currentLongitude);
        //Set location
        uiLocationField.setText(currentLocation);
    }

    //Get location data
    private void getLocationData () {
        // Acquire a reference to the system Location Manager
        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        // Define a listener that responds to location updates
        LocationListener locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                // Called when a new location is found by the network location provider.
                currentLatitude = location.getLatitude();
                currentLongitude = location.getLongitude();

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

                setUiFields();

            }

            public void onStatusChanged(String provider, int status, Bundle extras) {}

            public void onProviderEnabled(String provider) {}

            public void onProviderDisabled(String provider) {}
        };

        // Register the listener with the Location Manager to receive location updates
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
    }


}
