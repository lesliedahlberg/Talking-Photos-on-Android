package com.lesliedahlberg.placemem;

import android.content.Context;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.github.hiteshsondhi88.libffmpeg.ExecuteBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.FFmpeg;
import com.github.hiteshsondhi88.libffmpeg.LoadBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegCommandAlreadyRunningException;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegNotSupportedException;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by lesliedahlberg on 01/06/15.
 */
public class VideoEncoder {
    public static void encodeVideo(final Context context, String memId) {
        final Uri[] videoUri = new Uri[1];
        DBInterface dbInterface;
        final Mem mem;

        dbInterface = new DBInterface(context);
        mem = dbInterface.getRow(Integer.valueOf(memId));

        String photoPath = getRealPathFromURI(context, Uri.parse(mem.photoUri));
        String audioPath = getRealPathFromURI(context, Uri.parse(mem.voiceUri));

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

        FFmpeg ffmpeg = FFmpeg.getInstance(context);
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
            ffmpeg.execute("-loop 1 -r 1 -i " + photoPath + " -i " + audioPath + " -force_key_frames 00:00:00.000 -c:v libx264 -pix_fmt yuv420p -c:a copy -shortest -s " + resolution + " -preset ultrafast " + videoPath, new ExecuteBinaryResponseHandler() {


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

        Uri uri = Uri.parse("file://"+videoPath);
        new DBInterface(context).updateVideoUri(String.valueOf(mem.id), String.valueOf(uri));


    }

    public static String getRealPathFromURI(Context context, Uri contentURI) {
        String result;
        Cursor cursor = context.getContentResolver().query(contentURI, null, null, null, null);
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

}
