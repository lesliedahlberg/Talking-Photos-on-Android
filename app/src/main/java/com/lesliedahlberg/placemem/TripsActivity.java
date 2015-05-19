package com.lesliedahlberg.placemem;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;


public class TripsActivity extends Activity {

    //Recycler View Elements
    RecyclerView tripsRecyclerView;
    TripsRecyclerViewAdapter recyclerViewAdapter;

    //Constants
    public static final int NEW_TRIP = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Inflate UI
        setContentView(R.layout.activity_trips);

        //Get DB
        DBInterface dbInterface = new DBInterface(this);

        //Inflate Recycler View
        tripsRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        tripsRecyclerView.setLayoutManager(llm);

        //Connect DB & View
        recyclerViewAdapter = new TripsRecyclerViewAdapter(dbInterface, this);
        tripsRecyclerView.setAdapter(recyclerViewAdapter);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_trips, menu);
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
        }else if (id == R.id.action_new_trip) {
            launchNewTripActivity();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();

        //Update View
        recyclerViewAdapter.update();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //Update recyclerView
        if (requestCode == NEW_TRIP && resultCode == RESULT_OK) {
            recyclerViewAdapter.update();
        }

    }

    //Launch new trip activity
    private void launchNewTripActivity() {
        Intent intent = new Intent(this, AddTripActivity.class);
        startActivityForResult(intent, NEW_TRIP);
    }
}
