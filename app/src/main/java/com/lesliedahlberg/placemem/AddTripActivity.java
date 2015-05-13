package com.lesliedahlberg.placemem;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;


public class AddTripActivity extends Activity {

    TextView uiTitleField;
    String currentTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_trip);

        uiTitleField = (TextView) findViewById(R.id.titleField);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add_trip, menu);
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

    //Save mem and exit activity
    public void save (View view) {
        currentTitle = uiTitleField.getText().toString();
        Log.v("LULU2", "TITLE IS: "+currentTitle);
        //Write to DB
        new DBInterface(this).addTripRow(currentTitle);
        //Set result OK
        setResult(RESULT_OK);
        //Exit
        finish();

    }

    public void discard (View view) {
        setResult(RESULT_CANCELED);
        finish();
    }


}
