package com.lesliedahlberg.placemem;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;

/**
 Trips Recycler View Adapter
 Connects DB data to RecyclerView and populates it with CardViews
 */

public class TripsRecyclerViewAdapter extends RecyclerView.Adapter<TripsRecyclerViewAdapter.MemViewHolder> {

    DBInterface dbInterface;
    Context context;
    ArrayList<Trip> trips;

    public TripsRecyclerViewAdapter(DBInterface dbInterface, Context context) {
        this.dbInterface = dbInterface;
        this.context = context;
        update();

    }

    //Inflates one CardView from XML and gets ViewHolder with references
    @Override
    public MemViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        //Create view for CardView
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.trip_card_view, viewGroup, false);
        //Get MemViewHolder with references to all UI elements
        MemViewHolder memViewHolder = new MemViewHolder(view);
        //Return memViewHolder
        return memViewHolder;
    }



    //Updates data in ViewHolder
    @Override
    public void onBindViewHolder(final MemViewHolder memViewHolder, int i) {
        final Trip trip = trips.get(i);



        //Set values in UI elements
        memViewHolder.title.setText(trip.title);
        Log.v("LULU", "LULU: TITLE: "+trip.title+" ,ID: "+trip.id);

        //Database ID and position on RecyclerView
        final int id = trip.id;
        final int position = i;

        //OnClickListener

        memViewHolder.titleFrameLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent showTrip = new Intent(context, MemActivity.class);
                showTrip.putExtra(MemActivity.TRIP_ID, String.valueOf(id));
                Log.v("LILA", "LILA TRVA: " + String.valueOf(id));
                context.startActivity(showTrip);
            }
        });

        memViewHolder.shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*Intent shareIntent = new Intent();
                shareIntent.setAction(Intent.ACTION_SEND);
                shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse(mem.photoUri));
                shareIntent.setType("image/jpg");
                context.startActivity(Intent.createChooser(shareIntent, "Share this Mems image"));*/
            }
        });

        memViewHolder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


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

    }



    //Inner class that creates references for all UI elements
    public static class MemViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView title;
        ImageButton deleteButton;
        ImageButton shareButton;
        FrameLayout titleFrameLayout;

        public MemViewHolder(View itemView) {
            super(itemView);
            cardView = (CardView) itemView.findViewById(R.id.cardView);
            title = (TextView) itemView.findViewById(R.id.titleField);
            deleteButton = (ImageButton) itemView.findViewById(R.id.deleteButton);
            shareButton = (ImageButton) itemView.findViewById(R.id.shareButton);
            titleFrameLayout = (FrameLayout) itemView.findViewById(R.id.titleFrameLayout);
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
            return trips.size();
        }else {
            return 0;
        }

    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }



    //Update data from db and notify adapter
    public void update() {
        trips = dbInterface.getTripRows();
        notifyDataSetChanged();
    }


}
