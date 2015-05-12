package com.lesliedahlberg.placemem;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;


public class MainActivity extends Activity {

    RecyclerView recyclerView;
    RecyclerViewAdapter recyclerViewAdapter;

    //Constants and codes
    public static final int NEW_MEM = 1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Inflate UI
        setContentView(R.layout.activity_main);

        //Connect data from DB to RecyclerView
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(llm);
        DBInterface dbInterface = new DBInterface(this);
        recyclerViewAdapter = new RecyclerViewAdapter(dbInterface, this);
        recyclerView.setAdapter(recyclerViewAdapter);

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
            recyclerViewAdapter.notifyDataSetChanged();
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
        startActivityForResult(intent, NEW_MEM);
    }

}
