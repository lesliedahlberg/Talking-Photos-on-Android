package com.lesliedahlberg.placemem;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SearchView;


public class MemActivity extends Activity {

    RecyclerView recyclerView;
    RecyclerViewAdapter recyclerViewAdapter;
    DBInterface dbInterface;

    //Constants and codes
    public static final int NEW_MEM = 1;
    public static final String TRIP_ID = "trip_id";
    public static final String PHOTO_URI = "photo_uri";

    //Values
    String tripId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        tripId = getIntent().getStringExtra(TRIP_ID);


        //Inflate UI
        setContentView(R.layout.activity_main);

        //Connect data from DB to RecyclerView
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(llm);
        dbInterface = new DBInterface(this);
        recyclerViewAdapter = new RecyclerViewAdapter(dbInterface, this, tripId);
        recyclerView.setAdapter(recyclerViewAdapter);

        setTitle(dbInterface.getTripName(tripId));
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState != null) {
            tripId = savedInstanceState.getString(TRIP_ID);
        }
        setTitle(dbInterface.getTripName(tripId));
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(TRIP_ID, tripId);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //Update recyclerView
        if (requestCode == NEW_MEM && resultCode == RESULT_OK) {
            recyclerViewAdapter.update();
        }

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
        }else if (id == R.id.action_new_mem) {
            launchNewMemActivity();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    //Launch new mem activity
    private void launchNewMemActivity() {
        Intent intent = new Intent(this, AddMemActivity.class);
        intent.putExtra(TRIP_ID, tripId);
        startActivityForResult(intent, NEW_MEM);
    }

}
