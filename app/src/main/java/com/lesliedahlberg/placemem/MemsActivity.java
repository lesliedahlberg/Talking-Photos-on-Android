package com.lesliedahlberg.placemem;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;


public class MemsActivity extends Activity {

    //Recycler elements
    RecyclerView recyclerView;
    MemsRecyclerViewAdapter memsRecyclerViewAdapter;

    //DB
    DBInterface dbInterface;

    //Constants
    public static final int NEW_MEM = 1;
    public static final String TRIP_ID = "trip_id";
    public static final String PHOTO_URI = "photo_uri";

    //State
    String tripId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Get trip ID from intent
        tripId = getIntent().getStringExtra(TRIP_ID);

        //Inflate UI
        setContentView(R.layout.activity_mems);

        //Get DB
        dbInterface = new DBInterface(this);

        //Inflate Recycler View
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(llm);

        //Connect DB & Recycler View
        memsRecyclerViewAdapter = new MemsRecyclerViewAdapter(dbInterface, this, tripId);
        recyclerView.setAdapter(memsRecyclerViewAdapter);

        //Set activity title from Trip
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
            memsRecyclerViewAdapter.update();
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
    public void launchNewMemActivity() {
        Intent intent = new Intent(this, AddMemActivity.class);
        intent.putExtra(TRIP_ID, tripId);
        startActivityForResult(intent, NEW_MEM);
    }

}
