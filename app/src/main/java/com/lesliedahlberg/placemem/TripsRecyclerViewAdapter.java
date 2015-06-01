package com.lesliedahlberg.placemem;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.Image;
import android.net.Uri;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 Trips Recycler View Adapter
 Connects DB data to RecyclerView and populates it with CardViews
 */

public class TripsRecyclerViewAdapter extends RecyclerView.Adapter<TripsRecyclerViewAdapter.MemViewHolder> {

    //DB
    DBInterface dbInterface;

    //Context
    Context context;
    TripsActivity parent;

    //Data
    ArrayList<Trip> trips;

    public TripsRecyclerViewAdapter(DBInterface dbInterface, Context context) {
        this.dbInterface = dbInterface;
        this.context = context;
        this.parent = (TripsActivity) context;
        update();

    }

    //Inflates one CardView from XML and gets ViewHolder with references
    @Override
    public MemViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        //Items loaded from DB
        if (i == 0) {
            //Create view for CardView
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.trip_card_view, viewGroup, false);
            //Get MemViewHolder with references to all UI elements
            MemViewHolder memViewHolder = new MemViewHolder(view);
            //Return memViewHolder
            return memViewHolder;
        }else if (i == 1){ //Load extras
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.trip_card_view_add_trip, viewGroup, false);
            MemViewHolder memViewHolder = new MemViewHolder(view);
            return memViewHolder;
        }
        return null;

    }



    //Updates data in ViewHolder
    @Override
    public void onBindViewHolder(final MemViewHolder memViewHolder, int i) {

        if (getItemViewType(i) == 0) {
            //Get Data
            final Trip trip = trips.get(i);

            //Set values in UI elements
            memViewHolder.title.setText(trip.title);
            String numberOfPhotos = String.valueOf(dbInterface.getMemCountInTrip(String.valueOf(trip.id))) + " mems";
            memViewHolder.numberOfPhotos.setText(numberOfPhotos);

            //Set Bitmap
            Mem someMem = dbInterface.getTripSomeMem(String.valueOf(trip.id));
            if (someMem != null) {
                final int THUMBSIZE = 1024;
                Bitmap bitmap = BitmapLoader.decodeSampledBitmapFromResource(context, Uri.parse(someMem.photoUri), THUMBSIZE, THUMBSIZE);
                memViewHolder.tripBackgroundImage.setImageBitmap(bitmap);
            }else {
                memViewHolder.tripBackgroundImage.setImageResource(R.mipmap.ic_launcher);
            }



            //Database ID and position on RecyclerView -- Finals for inner classes
            final int id = trip.id;
            final int position = i;

            //Share menu
            final ActionMode.Callback shareTypeCallback = new ActionMode.Callback() {
                @Override
                public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                    MenuInflater inflater = mode.getMenuInflater();
                    inflater.inflate(R.menu.menu_select_sharing_mode_trip, menu);
                    return true;
                }

                @Override
                public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                    return false;
                }

                @Override
                public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                    Intent shareIntent;
                    ArrayList<Mem> mems;
                    switch (item.getItemId()) {
                        case R.id.shareImages:
                            //Share all photos
                            ArrayList<Uri> imageUris = new ArrayList();

                            mems = dbInterface.getRows(String.valueOf(trip.id));

                            for (Mem mem : mems){
                                imageUris.add(Uri.parse(mem.photoUri));
                            }

                            shareIntent = new Intent();
                            shareIntent.setAction(Intent.ACTION_SEND_MULTIPLE);
                            shareIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, imageUris);
                            shareIntent.setType("image/jpg");
                            context.startActivity(Intent.createChooser(shareIntent, "Share Images"));
                            mode.finish(); // Action picked, so close the CAB
                            return true;
                        case R.id.shareVideo:
                            //Share all photos
                            /*ArrayList<Uri> audioUris = new ArrayList();

                            mems = dbInterface.getRows(String.valueOf(trip.id));

                            for (Mem mem : mems){
                                audioUris.add(Uri.parse(mem.voiceUri));
                            }

                            shareIntent = new Intent();
                            shareIntent.setAction(Intent.ACTION_SEND_MULTIPLE);
                            shareIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, audioUris);
                            shareIntent.setType("audio/*");
                            context.startActivity(Intent.createChooser(shareIntent, "Share Audio"));*/
                            mode.finish(); // Action picked, so close the CAB
                            return true;
                        default:
                            return false;
                    }
                }

                @Override
                public void onDestroyActionMode(ActionMode mode) {

                }
            };

            //OnClickListener
            memViewHolder.titleFrameLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Start MemsAcitivity
                    Intent showTrip = new Intent(context, MemsActivity.class);
                    showTrip.putExtra(MemsActivity.TRIP_ID, String.valueOf(id));
                    context.startActivity(showTrip);
                }
            });

            memViewHolder.slideshowButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, TripSlideshowActivity.class);
                    intent.putExtra(MemsActivity.TRIP_ID, String.valueOf(id));
                    context.startActivity(intent);
                }
            });

            memViewHolder.shareButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    parent.startActionMode(shareTypeCallback);
                }
            });

            memViewHolder.shareVideoButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Intent intent = new Intent(context, ...);
                    //intent.putExtra(MemsActivity.TRIP_ID, String.valueOf(id));
                }
            });

            memViewHolder.deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    //Delete trip
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);

                    builder.setTitle("Delete trip");
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
        }else if (getItemViewType(i) == 1){
            memViewHolder.titleFrameLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    parent.launchNewTripActivity();
                }
            });
        }
    }



    //Inner class that creates references for all UI elements
    public static class MemViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView title;
        ImageButton deleteButton;
        ImageButton shareButton;
        FrameLayout titleFrameLayout;
        ImageView tripBackgroundImage;
        TextView numberOfPhotos;
        ImageButton slideshowButton;
        ImageButton shareVideoButton;

        //Extras
        Button newTripButton;

        public MemViewHolder(View itemView) {
            super(itemView);
                cardView = (CardView) itemView.findViewById(R.id.cardView);
                title = (TextView) itemView.findViewById(R.id.titleField);
                deleteButton = (ImageButton) itemView.findViewById(R.id.deleteButton);
                shareButton = (ImageButton) itemView.findViewById(R.id.shareButton);
                titleFrameLayout = (FrameLayout) itemView.findViewById(R.id.titleFrameLayout);
                tripBackgroundImage = (ImageView) itemView.findViewById(R.id.tripBackgroundImage);
                numberOfPhotos = (TextView) itemView.findViewById(R.id.numberOfPhotos);
                slideshowButton = (ImageButton) itemView.findViewById(R.id.slideshowButton);
                shareVideoButton = (ImageButton) itemView.findViewById(R.id.shareVideoButton);

        }
    }



    //Removes recyclerView item and deletes it from DB
    private void removeItemFromList (int position, int id) {
        dbInterface.removeTripRow(id);
        update();
    }

    @Override
    public int getItemCount() {
        if (trips != null){
            return trips.size()+1;
        }else {
            return 1;
        }

    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public int getItemViewType(int position) {
        if (position < getItemCount()-1){
            return 0;
        }else {
            return 1;
        }
    }

    //Update data from db and notify adapter
    public void update() {
        trips = dbInterface.getTripRows();
        notifyDataSetChanged();
    }




}
