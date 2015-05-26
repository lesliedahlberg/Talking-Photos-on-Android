package com.lesliedahlberg.placemem;

import android.app.Activity;
import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.VideoView;


import com.github.hiteshsondhi88.libffmpeg.ExecuteBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.FFmpeg;
import com.github.hiteshsondhi88.libffmpeg.LoadBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegCommandAlreadyRunningException;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegNotSupportedException;

import java.io.File;
import java.io.IOException;


public class ShareVideoActivity extends Activity {

    String tripId;
    DBInterface i;
    Mem mem;
    VideoView videoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_video);
        tripId = getIntent().getStringExtra("MEM_ID");
        i = new DBInterface(this);
        mem = i.getRow(Integer.valueOf(tripId));

        videoView = (VideoView) findViewById(R.id.videoView);

        String photoPath = getRealPathFromURI(Uri.parse(mem.photoUri));

        String audioPath = getRealPathFromURI(Uri.parse(mem.voiceUri));

        /*
        FFmpeg ffmpeg = FFmpeg.getInstance(this);
        try {
            ffmpeg.loadBinary(new LoadBinaryResponseHandler() {

                @Override
                public void onStart() {}

                @Override
                public void onFailure() {}

                @Override
                public void onSuccess() {}

                @Override
                public void onFinish() {}
            });
        } catch (FFmpegNotSupportedException e) {
            // Handle if FFmpeg is not supported by device
        }
        */

        FFmpeg ffmpeg = FFmpeg.getInstance(this);
        try {
            ffmpeg.execute("-loop 1 -i " + photoPath + " -i " + audioPath + " -c:v libx264 -pix_fmt yuv420p -c:a copy -shortest -s 1920x1080 /storage/emulated/0/video.mp4", new ExecuteBinaryResponseHandler() {


                @Override
                public void onStart() {
                }

                @Override
                public void onProgress(String message) {
                    Log.v("LOLO", message);
                }

                @Override
                public void onFailure(String message) {
                    Log.v("LOLO", message);
                }

                @Override
                public void onSuccess(String message) {
                    Log.v("LOLO", message);
                }

                @Override
                public void onFinish() {
                    Log.v("LOLO", "DONE! ");
                }
            });
        } catch (FFmpegCommandAlreadyRunningException e) {
            // Handle if FFmpeg is already running
            Log.v("LOLO", "LOLO RUNNING ALREADY ");
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_share_video, menu);
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


    private String getRealPathFromURI(Uri contentURI) {
        String result;
        Cursor cursor = getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) { // Source is Dropbox or other similar local file path
            result = contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            result = cursor.getString(idx);
            cursor.close();
        }
        return result;
    }

    public static final String ANDROID_RESOURCE = "android.resource://";
    public static final String FORESLASH = "/";

    public Uri resIdToUri(int resId) {
        return Uri.parse(ANDROID_RESOURCE + getPackageName()
                + FORESLASH + resId);
    }

}

