package com.lesliedahlberg.placemem;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * Created by lesliedahlberg on 08/05/15.
 */
public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.MemViewHolder> {

    DBAdapter adapter;
    Context context;

    public RecyclerViewAdapter(DBAdapter adapter, Context context) {
        this.adapter = adapter;
        this.context = context;
    }

    @Override
    public MemViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_view, viewGroup, false);
        MemViewHolder memViewHolder = new MemViewHolder(view);
        return memViewHolder;
    }

    @Override
    public void onBindViewHolder(MemViewHolder memViewHolder, int i) {
        Mem mem = adapter.getRow(i);
        Uri photoUri = Uri.parse(mem.photoUri);

        final int THUMBSIZE = 1024;
        InputStream inputStream = null;
        try {
            inputStream = context.getContentResolver().openInputStream(photoUri);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        Bitmap bitmap = ThumbnailUtils.extractThumbnail(BitmapFactory.decodeStream(inputStream),
                THUMBSIZE, THUMBSIZE);

        memViewHolder.photoView.setImageBitmap(bitmap);
        //memViewHolder.photoUri.setText(mem.photoUri);
        //memViewHolder.voiceUri.setText(mem.voiceUri);
        memViewHolder.location.setText(mem.location);
        memViewHolder.latitude.setText(mem.latitude);
        memViewHolder.longitude.setText(mem.longitude);
        memViewHolder.date.setText(mem.date);
        memViewHolder.transcript.setText(mem.transcript);
        final int id = mem.id;
        final int position = i;
        memViewHolder.photoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeItemFromList(position, id);
            }


        });
    }

    public void removeItemFromList(int position, int id) {
        adapter.removeRow(id);
        notifyItemRemoved(position);
    }

    @Override
    public int getItemCount() {
        return adapter.getAllData().size();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

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

}
