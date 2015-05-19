package com.lesliedahlberg.placemem;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterViewFlipper;


public class TripSlideshowActivity extends Activity {

    TripSlideshowViewFlipperAdapter adapter;
    String tripId;
    DBInterface dbInterface;

    AdapterViewFlipper flipper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_slideshow);
        dbInterface = new DBInterface(this);
        flipper = (AdapterViewFlipper) findViewById(R.id.adapterViewFlipper);
        tripId = getIntent().getStringExtra(MemsActivity.TRIP_ID);
        adapter = new TripSlideshowViewFlipperAdapter(this, dbInterface, tripId);
        flipper.setAdapter(adapter);
        flipper.setAutoStart(true);
        flipper.setFlipInterval(2500);
        setTitle(dbInterface.getTripName(tripId));

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_trip_slideshow, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
