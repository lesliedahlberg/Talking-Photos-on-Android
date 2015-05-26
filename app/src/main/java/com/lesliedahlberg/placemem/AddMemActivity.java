package com.lesliedahlberg.placemem;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.content.pm.PackageManager;
import android.widget.Toast;


import com.github.hiteshsondhi88.libffmpeg.ExecuteBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.FFmpeg;
import com.github.hiteshsondhi88.libffmpeg.LoadBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegCommandAlreadyRunningException;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegNotSupportedException;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class AddMemActivity extends Activity {

    //CONSTANTS
    static final int REQUEST_TAKE_PHOTO = 1;
    static final String PHOTO_TAKEN = "photo_taken";
    static final String PHOTO_URI = "photo_uri";
    static final String AUDIO_URI = "audio_uri";

    //UI
    ImageView uiPhotoView;
    TextView uiTitleField;
    ImageButton uiAudioRecordButton;
    ImageButton uiAudioPlayButton;

    //Values
    String currentTitle;
    String currentDate;
    String currentLocation;
    Double currentLatitude;
    Double currentLongitude;

    //URIs
    Uri currentPhotoUri;
    Uri currentAudioUri;

    //State variables
    Boolean photoTaken = false;
    Boolean hasGps = false;
    Boolean recording = false;
    Boolean playing = false;

    //Trip variables
    String tripId;

    //Recorder resource
    MediaRecorder mRecorder = null;
    MediaPlayer mPlayer = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Load saved instance state
        if (savedInstanceState != null) {
            onRestoreInstanceState(savedInstanceState);
        }

        //Get Trip ID from intent
        tripId = getIntent().getStringExtra(MemsActivity.TRIP_ID);

        //Inflate UI
        setContentView(R.layout.activity_add_mem);

        //UI Elements
        uiPhotoView = (ImageView) findViewById(R.id.photoView);
        uiTitleField = (TextView) findViewById(R.id.titleField);
        uiAudioRecordButton = (ImageButton) findViewById(R.id.audio_record_button);
        uiAudioPlayButton = (ImageButton) findViewById(R.id.audio_play_button);

        //Date
        currentDate = new SimpleDateFormat("dd. MM. yyyy", Locale.getDefault()).format(new Date());
        
        //Check for GPS
        PackageManager packageManager = this.getPackageManager();
        hasGps = packageManager.hasSystemFeature(PackageManager.FEATURE_LOCATION_GPS);

        //Location
        getLocationData();

        //Create audio file
        /* !! ADD IF CLAUSE SO IT DOES NOT CREATE THE AUDIO FILE MORE THAN ONCE !! */
        createAndSetAudioFilePath();

        //Show photo after taken
        if (currentPhotoUri != null) {
            if (!currentPhotoUri.toString().isEmpty()) {
                showPhoto();
            }
        }

        //Take photo
        if (photoTaken == false) {
            takePhoto();
            photoTaken = true;
        }

        //LISTENERS
        uiAudioRecordButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v)
            {
                if(recording)
                {
                    stopRecording();
                    recording = false;
                    uiAudioRecordButton.setImageResource(R.drawable.ic_action_microphone_white);
                }
                else
                {
                    if(playing)
                        stopPlaying();

                    startRecording();
                    recording = true;
                    uiAudioRecordButton.setImageResource(R.drawable.ic_action_microphone_red);
                }
            }
        });

        uiAudioPlayButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v)
            {
                if(playing)
                {
                    stopPlaying();
                    playing = false;
                    uiAudioPlayButton.setImageResource(R.drawable.ic_play);
                }
                else
                {
                    if(recording)
                        stopRecording();

                    startPlaying();
                    playing = true;
                    uiAudioPlayButton.setImageResource(R.drawable.ic_stop);
                }
            }
        });

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        //Save state variables and Uris
        outState.putBoolean(PHOTO_TAKEN, photoTaken);
        outState.putString(PHOTO_URI, currentPhotoUri.toString());
        outState.putString(AUDIO_URI, currentAudioUri.toString());
        outState.putString(MemsActivity.TRIP_ID, tripId);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        //Restore state variables and Uris
        if (savedInstanceState != null) {
            photoTaken = savedInstanceState.getBoolean(PHOTO_TAKEN);

            String mPhotoUri = savedInstanceState.getString(PHOTO_URI);
            String mAudioUri = savedInstanceState.getString(AUDIO_URI);

            if (mPhotoUri != null){
                currentPhotoUri = Uri.parse(mPhotoUri);
            }
            if (mAudioUri != null){
                currentAudioUri = Uri.parse(mAudioUri);
            }

            tripId = savedInstanceState.getString(MemsActivity.TRIP_ID);

        }
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

    //Save & exit
    public void save (View view) {

        //Get title string
        currentTitle = uiTitleField.getText().toString();

        if (currentLongitude == null) {
            currentLongitude = 0.0;
        }
        if (currentLatitude == null) {
            currentLatitude = 0.0;
        }

        //Write to DB
        int id = new DBInterface(this).addRow(currentPhotoUri.toString(), currentAudioUri.toString(), "", currentLocation, currentLatitude, currentLongitude, currentDate, currentTitle, tripId);

        //Set result OK
        setResult(RESULT_OK);

        //Exit
        finish();

    }

    public void discard (View view) {

        //Delete files
        new File(String.valueOf(currentPhotoUri)).delete();
        new File(String.valueOf(currentAudioUri)).delete();

        //Cancel intent
        setResult(RESULT_CANCELED);

        //Exit
        finish();
    }

    //Take photo
    public void takePhoto() {
        dispatchTakePictureIntent();
    }

    //Plays back audio recording
    private void startPlaying()
    {
        mPlayer = new MediaPlayer();
        try{
            mPlayer.setDataSource(currentAudioUri.getPath());
            mPlayer.prepare();
            mPlayer.start();
        }catch(IOException e) {
            e.printStackTrace();
        }

        Toast toast = Toast.makeText(this, "Playing", Toast.LENGTH_SHORT);
        toast.show();
    }


    //Stops audio playback
    private void stopPlaying()
    {
        mPlayer.release();
        mPlayer = null;

        Toast toast = Toast.makeText(this, "Paused", Toast.LENGTH_SHORT);
        toast.show();
    }


    //Starts recording audio to file specified by currentAudioUri
    private void startRecording()
    {
        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4); // Might have to change to some other format //AAC_ADTS
        mRecorder.setOutputFile(currentAudioUri.getPath()); //audioUri
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.HE_AAC);

        try
        {
            mRecorder.prepare();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        mRecorder.start();

        Toast toast = Toast.makeText(this, "Recording", Toast.LENGTH_SHORT);
        toast.show();
    }


    //Stops recording audio
    private void stopRecording()
    {
        mRecorder.stop();
        mRecorder.release(); //Release resources
        mRecorder = null; //null reference

        Toast toast = Toast.makeText(this, "Stopped Recording", Toast.LENGTH_SHORT);
        toast.show();
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
        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
            //Photo taken
            showPhoto();
        }else if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_CANCELED) {
            //Photo canceled
            discard(null);
        }else{
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    //Create Uri for audio file
    private void createAndSetAudioFilePath()
    {
        File audioFile = null;
        try {
            audioFile = createAudioFile();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        currentAudioUri = Uri.fromFile(audioFile);
    }

    //Display photo in UI
    private void showPhoto() {
        if (!currentPhotoUri.toString().isEmpty()) {
            final int THUMBSIZE = 512;
            Bitmap bitmap = BitmapLoader.decodeSampledBitmapFromResource(this, currentPhotoUri, THUMBSIZE, THUMBSIZE);
            uiPhotoView.setImageBitmap(bitmap);
        }
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

    //Create file to store audio in (locally in private app storage)
    private File createAudioFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = timeStamp + "_"; // AAC_
        File storageDir = Environment.getExternalStorageDirectory();
        File audio = File.createTempFile(
                imageFileName,  /* prefix */
                ".aac",         /* suffix */ //.acc
                storageDir      /* directory */
        );

        //Save URI to file
        currentAudioUri = Uri.fromFile(audio);

        //return file
        return audio;
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
                    currentLatitude = 0.0;
                    currentLongitude = 0.0;
                }

                //Get city name from GPS
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
        if(hasGps){
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
        }
    }




}
