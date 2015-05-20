package com.lesliedahlberg.placemem;

import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Handler;

/**
 * Created by lesliedahlberg on 19/05/15.
 */
public class TripSlideshowViewFlipperAdapter extends BaseAdapter {

    Context context;
    TripSlideshowActivity parent;
    DBInterface dbInterface;

    String tripId;

    ArrayList<Mem> mems;

    MediaPlayer mPlayer;

    int position;

    Handler handler;

    public TripSlideshowViewFlipperAdapter(Context context, DBInterface dbInterface, String tripId) {
        this.context = context;
        this.parent = (TripSlideshowActivity) context;
        this.dbInterface = dbInterface;
        this.tripId = tripId;

        update();
    }

    @Override
    public int getCount() {
        return mems.size();
    }

    @Override
    public Object getItem(int position) {
        return mems.get(position);
    }

    @Override
    public long getItemId(int position) {
        Mem item = (Mem) getItem(position);
        return item.id;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        ImageView imageView;
        Mem mem = (Mem) getItem(position);
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.slideshow_view, null);
            imageView = (ImageView) view.findViewById(R.id.imageView);
            final int THUMBSIZE = 1024;
            Bitmap bitmap = BitmapLoader.decodeSampledBitmapFromResource(context, Uri.parse(mem.photoUri), THUMBSIZE, THUMBSIZE);
            imageView.setImageBitmap(bitmap);
        }

        return view;
    }

    //Update data from db and notify adapter
    public void update() {
        mems = dbInterface.getRows(tripId);
        notifyDataSetChanged();
    }

    private void next() {
        stopPlaying();
        if (position < getCount() - 1) {
            position++;
        }else {
            position = 0;
        }
        parent.next();
        startPlaying(mems.get(position).voiceUri);
    }

    public void start() {
        position = 0;

        startPlaying(mems.get(0).voiceUri);
    }

    public void stop() {
        stopPlaying();
    }

    //Plays back audio recording
    private void startPlaying(String voiceUri)
    {

        try{
            mPlayer = new MediaPlayer();
            mPlayer.setDataSource(voiceUri);
            mPlayer.prepare();
            mPlayer.start();
        }catch(IOException e) {
            e.printStackTrace();
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    parent.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            next();
                        }
                    });

                }
            }, 2500);
        }
        mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                next();
            }
        });
    }


    //Stops audio playback
    private void stopPlaying()
    {
        if (mPlayer != null) {
            mPlayer.release();
        }

    }

}
