package com.lesliedahlberg.placemem;

import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.Bitmap;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import java.util.ArrayList;

/**
 * Created by lesliedahlberg on 19/05/15.
 */
public class TripSlideshowViewFlipperAdapter extends BaseAdapter {

    Context context;
    DBInterface dbInterface;

    String tripId;

    ArrayList<Mem> mems;

    public TripSlideshowViewFlipperAdapter(Context context, DBInterface dbInterface, String tripId) {
        this.context = context;
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
        return 0;
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
        return null;
    }

    //Update data from db and notify adapter
    public void update() {
        mems = dbInterface.getRows(tripId);
        notifyDataSetChanged();
    }

}
