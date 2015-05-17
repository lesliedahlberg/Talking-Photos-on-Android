package com.lesliedahlberg.placemem;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
    String tripId;
    ArrayList<Mem> mems;

    public RecyclerViewAdapter(DBInterface dbInterface, Context context, String tripId) {
        this.dbInterface = dbInterface;
        this.context = context;
        this.tripId = tripId;

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
    public void onBindViewHolder(final MemViewHolder memViewHolder, int i) {
        final Mem mem = mems.get(i);

        //Photo Uri
        Uri photoUri = Uri.parse(mem.photoUri);

        final int THUMBSIZE = 1024;
        Bitmap bitmap = LoadBitmap.decodeSampledBitmapFromResource(context, photoUri, THUMBSIZE, THUMBSIZE);

        //Set values in UI elements
        memViewHolder.photoView.setImageBitmap(bitmap);
        memViewHolder.location.setText(mem.location);
        memViewHolder.date.setText(mem.date);
        memViewHolder.title.setText(mem.title);

        //Database ID and position on RecyclerView
        final int id = mem.id;
        final int position = i;
        final double latitude = Double.parseDouble(mem.latitude);
        final double longitude = Double.parseDouble(mem.longitude);


        //OnClickListener
        memViewHolder.playPauseButton.setOnClickListener(new View.OnClickListener() {

            MediaPlayer mPlayer;

            //Plays back audio recording
            private void startPlaying()
            {
                mPlayer = new MediaPlayer();
                try{
                    mPlayer.setDataSource(mems.get(position).voiceUri.toString()); //currentAudioUri.getPath()
                    mPlayer.prepare();
                    mPlayer.start();
                }catch(IOException e) {
                    e.printStackTrace();
                }

                Toast toast = Toast.makeText(context, "Playing", Toast.LENGTH_SHORT);
                toast.show();
            }


            //Stops audio playback
            private void stopPlaying()
            {
                mPlayer.release();
                mPlayer = null;

                Toast toast = Toast.makeText(context, "Paused", Toast.LENGTH_SHORT);
                toast.show();
            }

            @Override
            public void onClick(View v) {
                if (mems.get(position).playing == true) {
                    stopPlaying();
                    memViewHolder.playPauseButton.setImageResource(R.drawable.ic_play);
                    mems.get(position).playing = false;
                }else {
                    startPlaying();
                    memViewHolder.playPauseButton.setImageResource(R.drawable.ic_stop);
                    mems.get(position).playing = true;
                }
            }
        });

        memViewHolder.shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent shareIntent = new Intent();
                shareIntent.setAction(Intent.ACTION_SEND);
                shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse(mem.photoUri));
                shareIntent.setType("image/jpg");
                context.startActivity(Intent.createChooser(shareIntent, "Share this Mems image"));
            }
        });

        memViewHolder.showOnMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Creates an Intent that will load a map of San Francisco
                Uri gmmIntentUri = Uri.parse("geo:0,0?q=" + latitude + "," + longitude);
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                context.startActivity(mapIntent);
            }


        });

        memViewHolder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);

                builder.setTitle("Delete memory");
                builder.setMessage("Are you sure?");

                builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        // Remove and close the dialog
                        removeItemFromList(position, id);
                        dialog.dismiss();
                    }

                });

                builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Do nothing
                        dialog.dismiss();
                    }
                });
                AlertDialog alert = builder.create();
                alert.show();
            }
        });

    }



    //Inner class that creates references for all UI elements
    public static class MemViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        ImageView photoView;
        TextView location;
        TextView date;
        TextView title;
        ImageButton showOnMap;
        ImageButton playPauseButton;
        ImageButton deleteButton;
        ImageButton shareButton;

        public MemViewHolder(View itemView) {
            super(itemView);
            cardView = (CardView) itemView.findViewById(R.id.cardView);
            photoView = (ImageView) itemView.findViewById(R.id.photoView);
            location = (TextView) itemView.findViewById(R.id.location);
            date = (TextView) itemView.findViewById(R.id.date);
            title = (TextView) itemView.findViewById(R.id.titleField);
            showOnMap = (ImageButton) itemView.findViewById(R.id.showOnMapButton);
            playPauseButton = (ImageButton) itemView.findViewById(R.id.playPauseButton);
            deleteButton = (ImageButton) itemView.findViewById(R.id.deleteButton);
            shareButton = (ImageButton) itemView.findViewById(R.id.shareButton);
        }
    }



    //Removes recyclerView item and deletes it from DB
    private void removeItemFromList (int position, int id) {
        dbInterface.removeRow(id);
        update();
    }

    @Override
    public int getItemCount() {
        if (mems != null){
            Log.v("LILA", "LILA size: "+mems.size());
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
    public void setTripId(String tripId){
        this.tripId = tripId;
        update();
    }

    //Update data from db and notify adapter
    public void update() {
        Log.v("LILA", "LILA updated and tripId is: "+this.tripId);
        mems = dbInterface.getRows(tripId);
        notifyDataSetChanged();
    }


}
