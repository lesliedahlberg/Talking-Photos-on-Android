package com.lesliedahlberg.placemem;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.FileNotFoundException;
import java.io.InputStream;

/**
Recycler View Adapter
Connects DB data to RecyclerView and populates it with CardViews
 */

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.MemViewHolder> {

    DBInterface dbInterface;
    Context context;
    String searchFilter;

    public RecyclerViewAdapter(DBInterface dbInterface, Context context) {
        this.dbInterface = dbInterface;
        this.context = context;
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
        //Get DB data for row
        Mem mem = dbInterface.getRow(i, searchFilter);

        //Photo Uri
        Uri photoUri = Uri.parse(mem.photoUri);

        //Resize photo to thumbnail
        final int THUMBSIZE = 1024;
        InputStream inputStream = null;
        try {
            inputStream = context.getContentResolver().openInputStream(photoUri);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        Bitmap bitmap = ThumbnailUtils.extractThumbnail(BitmapFactory.decodeStream(inputStream),
                THUMBSIZE, THUMBSIZE);

        //Set values in UI elements
        memViewHolder.photoView.setImageBitmap(bitmap);
        memViewHolder.location.setText(mem.location);
        memViewHolder.latitude.setText(mem.latitude);
        memViewHolder.longitude.setText(mem.longitude);
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
        //TextView voiceUri;
        TextView location;
        TextView latitude;
        TextView longitude;
        TextView date;
        TextView transcript;

        public MemViewHolder(View itemView) {
            super(itemView);
            cardView = (CardView) itemView.findViewById(R.id.cardView);
            photoView = (ImageView) itemView.findViewById(R.id.photoView);
            //photoUri = (TextView) itemView.findViewById(R.id.photoUri);
            //voiceUri = (TextView) itemView.findViewById(R.id.voiceUri);
            location = (TextView) itemView.findViewById(R.id.location);
            latitude = (TextView) itemView.findViewById(R.id.latitude);
            longitude = (TextView) itemView.findViewById(R.id.longitude);
            date = (TextView) itemView.findViewById(R.id.date);
            transcript = (TextView) itemView.findViewById(R.id.transcript);
        }
    }

    //Removes recyclerView item and deletes it from DB
    private void removeItemFromList(int position, int id) {
        dbInterface.removeRow(id);
        notifyItemRemoved(position);
    }

    @Override
    public int getItemCount() {
        return 0;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    public void setSearchFilter (String searchFilter){
        this.searchFilter = searchFilter;
        notifyDataSetChanged();
    }

    public void removeSearchFilter (){
        searchFilter = null;
        notifyDataSetChanged();
    }

}
