package com.lesliedahlberg.placemem;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by lesliedahlberg on 14/05/15.
 */
public class LoadBitmap {
    //Load bitmap
    public static Bitmap decodeSampledBitmapFromResource(Context context, Uri resource, int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;

        InputStream inputStream = null;
        try {
            inputStream = context.getContentResolver().openInputStream(resource);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        BitmapFactory.decodeStream(inputStream, null, options);

        try {
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;


        inputStream = null;
        try {
            inputStream = context.getContentResolver().openInputStream(resource);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }



        Bitmap bitmap = BitmapFactory.decodeStream(inputStream, null, options);

        try {
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }



        return bitmap;
    }

    //Compute size of bitmap
    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }
}
