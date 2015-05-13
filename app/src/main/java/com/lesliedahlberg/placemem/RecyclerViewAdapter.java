package com.lesliedahlberg.placemem;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

/**
Recycler View Adapter
Connects DB data to RecyclerView and populates it with CardViews
 */

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.MemViewHolder> {

    DBInterface dbInterface;
    Context context;
    String searchFilter;
    ArrayList<Mem> mems;
    private int cardViewWidth;

    public RecyclerViewAdapter(DBInterface dbInterface, Context context) {
        this.dbInterface = dbInterface;
        this.context = context;
        searchFilter = "";
        update();

    }

    //Inflates one CardView from XML and gets ViewHolder with references
    @Override
    public MemViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        //Create view for CardView
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_view, viewGroup, false);
        //Get MemViewHolder with references to all UI elements
        MemViewHolder memViewHolder = new MemViewHolder(view);
        //Return memViewHolder
        return memViewHolder;
    }



    //Updates data in ViewHolder
    @Override
    public void onBindViewHolder(MemViewHolder memViewHolder, int i) {
        Mem mem = mems.get(i);

        //Photo Uri
        Uri photoUri = Uri.parse(mem.photoUri);

        final int THUMBSIZE = 1024;
        Bitmap bitmap = decodeSampledBitmapFromResource(photoUri, THUMBSIZE, THUMBSIZE);

        //Set values in UI elements
        memViewHolder.photoView.setImageBitmap(bitmap);
        memViewHolder.location.setText(mem.location);
        memViewHolder.date.setText(mem.date);

        //Database ID and position on RecyclerView
        final int id = mem.id;
        final int position = i;

        //OnClickListener for deleting
        memViewHolder.photoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeItemFromList(position, id);
            }


        });
    }

    //Inner class that creates references for all UI elements
    public static class MemViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        ImageView photoView;
        TextView location;
        TextView latitude;
        TextView longitude;
        TextView date;

        public MemViewHolder(View itemView) {
            super(itemView);
            cardView = (CardView) itemView.findViewById(R.id.cardView);
            photoView = (ImageView) itemView.findViewById(R.id.photoView);
            location = (TextView) itemView.findViewById(R.id.location);
            //latitude = (TextView) itemView.findViewById(R.id.latitude);
            //longitude = (TextView) itemView.findViewById(R.id.longitude);
            date = (TextView) itemView.findViewById(R.id.date);
        }
    }

    //Removes recyclerView item and deletes it from DB
    private void removeItemFromList(int position, int id) {
        dbInterface.removeRow(id);
        update();
    }

    @Override
    public int getItemCount() {
        if (mems != null){
            return mems.size();
        }else {
            return 0;
        }

    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    //Set search keyword
    public void setSearchFilter (String searchFilter){
        this.searchFilter = searchFilter;
        update();
    }

    //Remove search keyword, return to default
    public void removeSearchFilter (){
        searchFilter = "";
        update();
    }

    //Update data from db and notify adapter
    public void update() {
        mems = dbInterface.getRows(searchFilter);
        notifyDataSetChanged();
    }

    //Load bitmap
    public Bitmap decodeSampledBitmapFromResource(Uri resource, int reqWidth, int reqHeight) {

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
