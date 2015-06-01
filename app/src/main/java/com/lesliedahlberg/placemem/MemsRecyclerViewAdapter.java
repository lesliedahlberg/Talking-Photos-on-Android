package com.lesliedahlberg.placemem;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.Image;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
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
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.hiteshsondhi88.libffmpeg.ExecuteBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.FFmpeg;
import com.github.hiteshsondhi88.libffmpeg.LoadBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegCommandAlreadyRunningException;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegNotSupportedException;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
Recycler View Adapter
Connects DB data to RecyclerView and populates it with CardViews
 */

public class MemsRecyclerViewAdapter extends RecyclerView.Adapter<MemsRecyclerViewAdapter.MemViewHolder> {

    DBInterface dbInterface;
    Context context;
    MemsActivity parent;
    String tripId;
    ArrayList<Mem> mems;


    public MemsRecyclerViewAdapter(DBInterface dbInterface, Context context, String tripId) {
        this.dbInterface = dbInterface;
        this.context = context;
        this.tripId = tripId;
        this.parent = (MemsActivity) context;

        update();

    }

    //Inflates one CardView from XML and gets ViewHolder with references
    @Override
    public MemViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view;
        MemViewHolder memViewHolder;
        switch (i){
            case 0:
                //Create view for CardView
                view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_view, viewGroup, false);
                //Get MemViewHolder with references to all UI elements
                memViewHolder = new MemViewHolder(view);
                //Return memViewHolder
                return memViewHolder;
            case 1:
                //Create view for CardView
                view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_view_add_mem, viewGroup, false);
                //Get MemViewHolder with references to all UI elements
                memViewHolder = new MemViewHolder(view);
                //Return memViewHolder
                return memViewHolder;
        }

        return null;
    }



    //Updates data in ViewHolder
    @Override
    public void onBindViewHolder(final MemViewHolder memViewHolder, int i) {
        switch (getItemViewType(i)){
            case 0:
                final Mem mem = mems.get(i);

                /*
                //Photo Uri
                final Uri photoUri = Uri.parse(mem.photoUri);

                final int THUMBSIZE = 512;
                Bitmap bitmap = BitmapLoader.decodeSampledBitmapFromResource(context, photoUri, THUMBSIZE, THUMBSIZE);

                //Set values in UI elements
                memViewHolder.photoView.setImageBitmap(bitmap);*/

                final Uri photoUri = Uri.parse(mem.photoUri);
                new BitmapLoaderTask(photoUri, mem, memViewHolder.photoView).execute();


                memViewHolder.location.setText(mem.location);
                memViewHolder.date.setText(mem.date);
                memViewHolder.title.setText(mem.title);

                //Database ID and position on RecyclerView
                final int id = mem.id;
                final int position = i;
                final double latitude = Double.parseDouble(mem.latitude);
                final double longitude = Double.parseDouble(mem.longitude);



                //Share menu
                final ActionMode.Callback shareTypeCallback = new ActionMode.Callback() {
                    @Override
                    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                        MenuInflater inflater = mode.getMenuInflater();
                        inflater.inflate(R.menu.menu_select_sharing_mode, menu);
                        return true;
                    }

                    @Override
                    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                        return false;
                    }

                    @Override
                    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                        Intent shareIntent;
                        switch (item.getItemId()) {
                            case R.id.shareImage:
                                shareIntent = new Intent();
                                shareIntent.setAction(Intent.ACTION_SEND);
                                shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse(mem.photoUri));
                                shareIntent.setType("image/jpeg");
                                context.startActivity(Intent.createChooser(shareIntent, "Share Image"));
                                mode.finish(); // Action picked, so close the CAB
                                return true;
                            case R.id.shareVideo:
                                if (!mem.videoUri.isEmpty()){
                                    shareIntent = new Intent(
                                            Intent.ACTION_SEND);
                                    shareIntent.setType("video/*");
                                    shareIntent.putExtra(Intent.EXTRA_STREAM, mem.videoUri);
                                    context.startActivity(Intent.createChooser(shareIntent,
                                            "Share video"));
                                }else {
                                    Intent intent = new Intent(context, ShareVideoActivity.class);
                                    intent.putExtra("MEM_ID", String.valueOf(id));
                                    context.startActivity(intent);
                                }

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
                        parent.startActionMode(shareTypeCallback);
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

                memViewHolder.photoView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        parent.viewPhoto(MemsActivity.PHOTO_URI, mem.photoUri);


                    }
                });
                break;
            case 1:
                memViewHolder.titleFrameLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        parent.launchNewMemActivity();
                    }
                });
                break;
        }



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
        FrameLayout titleFrameLayout;
        ImageButton shareVideoButton;

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
            titleFrameLayout = (FrameLayout) itemView.findViewById(R.id.titleFrameLayout);
            shareVideoButton = (ImageButton) itemView.findViewById(R.id.shareVideoButton);
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
            return mems.size()+1;
        }else {
            return 1;
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
        mems = dbInterface.getRows(tripId);
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        if (position < getItemCount()-1){
            return 0;
        }else {
            return 1;
        }
    }

    class BitmapLoaderTask extends AsyncTask<Void, Void, Bitmap> {
        private final WeakReference<ImageView> imageViewReference;
        final int THUMBSIZE = 1024;
        Uri uri;
        Mem mem;

        public BitmapLoaderTask(Uri uri, Mem mem, ImageView view) {
            this.uri = uri;
            this.mem = mem;
            imageViewReference = new WeakReference<ImageView>(view);
        }

        @Override
        protected void onPreExecute() {
            if (mem.isSetThumbnail()){
                if (imageViewReference != null) {
                    final ImageView imageView = imageViewReference.get();
                    if (imageView != null) {
                        imageView.setImageBitmap(mem.getThumbnail());
                    }
                }
            }else{
                if (imageViewReference != null) {
                    final ImageView imageView = imageViewReference.get();
                    if (imageView != null) {
                        imageView.setImageResource(R.mipmap.ic_launcher);
                    }
                }
            }

        }

        @Override
        protected Bitmap doInBackground(Void... params) {
            if (mem.isSetThumbnail()) {
                return null;
            }else {
                return BitmapLoader.decodeSampledBitmapFromResource(context, uri, THUMBSIZE, THUMBSIZE);
            }
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if (mem.isSetThumbnail()) {

            }else {
                if (imageViewReference != null && bitmap != null) {
                    final ImageView imageView = imageViewReference.get();
                    if (imageView != null) {
                        imageView.setImageBitmap(bitmap);
                    }
                }
                mem.setThumbnail(bitmap);
            }

        }
    }


}
