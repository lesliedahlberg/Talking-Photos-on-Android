package com.lesliedahlberg.placemem;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.VideoView;


import com.github.hiteshsondhi88.libffmpeg.ExecuteBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.FFmpeg;
import com.github.hiteshsondhi88.libffmpeg.LoadBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegCommandAlreadyRunningException;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegNotSupportedException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;


public class ShareVideoActivity extends Activity {

    String tripId;
    DBInterface i;
    Mem mem;
    Uri currentVideoUri;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_video);
        tripId = getIntent().getStringExtra("MEM_ID");
        i = new DBInterface(this);
        mem = i.getRow(Integer.valueOf(tripId));

        context = this;

        if (!mem.videoUri.isEmpty()){
            Button shareButton = (Button) findViewById(R.id.button);
            shareButton.setVisibility(View.VISIBLE);
            TextView textView = (TextView) findViewById(R.id.textView);
            textView.setVisibility(View.GONE);
            currentVideoUri = Uri.parse(mem.videoUri);

        }else {
            String photoPath = getRealPathFromURI(Uri.parse(mem.photoUri));

            String audioPath = getRealPathFromURI(Uri.parse(mem.voiceUri));

            //String videoPath = getRealPathFromURI(currentVideoUri);
            String time = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            final String videoPath = Environment.getExternalStorageDirectory()+"/memory_"+time+".mp4";




            final BitmapFactory.Options options = new BitmapFactory.Options();

            options.inJustDecodeBounds = true;

            InputStream inputStream = null;
            try {
                inputStream = context.getContentResolver().openInputStream(Uri.parse(mem.photoUri));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            BitmapFactory.decodeStream(inputStream, null, options);

            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            String resolution = "1920x1080";

            if (options.outWidth < options.outHeight) {
                resolution = "1080x1920";
            }


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


            //FFmpeg ffmpeg = FFmpeg.getInstance(this);
            try {
                ///storage/emulated/0/video.mp4
                ffmpeg.execute("-loop 1 -r 1 -i " + photoPath + " -i " + audioPath + " -force_key_frames 00:00:00.000 -c:v libx264 -pix_fmt yuv420p -c:a copy -shortest -s " + resolution + " -preset ultrafast "+videoPath, new ExecuteBinaryResponseHandler() {


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

                        Button shareButton = (Button) findViewById(R.id.button);
                        shareButton.setVisibility(View.VISIBLE);
                        TextView textView = (TextView) findViewById(R.id.textView);
                        textView.setVisibility(View.GONE);
                        shareVideo(mem.title, videoPath);




                        /*Intent shareIntent;
                        shareIntent = new Intent();
                        shareIntent.setAction(Intent.ACTION_SEND);
                        shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse(videoPath));
                        shareIntent.setType("video/*");
                        startActivity(Intent.createChooser(shareIntent, "Share video"));*/

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

    public void shareVideo(final String title, String path) {
        MediaScannerConnection.scanFile(this, new String[]{path},
                null, new MediaScannerConnection.OnScanCompletedListener() {
                    public void onScanCompleted(String path, Uri uri) {
                        currentVideoUri = uri;
                        new DBInterface(context).updateVideoUri(String.valueOf(mem.id), String.valueOf(currentVideoUri));
                        startShareIntent(mem.title, uri);
                    }
                });
    }

    private void startShareIntent(String title, Uri uri) {
        Intent shareIntent = new Intent(
                android.content.Intent.ACTION_SEND);
        shareIntent.setType("video/*");
        shareIntent.putExtra(
                android.content.Intent.EXTRA_SUBJECT, title);
        shareIntent.putExtra(
                android.content.Intent.EXTRA_TITLE, title);
        shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
        shareIntent
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        context.startActivity(Intent.createChooser(shareIntent,
                "Share video"));
    }

    public void shareButtonAction(View view) {
        if (!currentVideoUri.toString().isEmpty()) {
            startShareIntent(mem.title, currentVideoUri);
        }
    }


}

