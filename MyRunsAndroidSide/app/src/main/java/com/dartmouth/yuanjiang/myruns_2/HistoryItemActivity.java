package com.dartmouth.yuanjiang.myruns_2;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;

import java.text.DecimalFormat;

public class HistoryItemActivity extends Activity {
    private Entry entry;
    private long entry_id;
    private EditText mActivityType;
    private EditText mDateTime;
    private EditText mDuration;
    private EditText mDistance;
    private EditText mCalories;
    private EditText mHeartrate;
    private EditText mComment;
    private EntryDataSource data_source;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_item);
        Intent intent = getIntent();

        entry_id = intent.getLongExtra("_id", 0);

        data_source = EntryDataSource.getInstance(this);

        entry = data_source.getEntryByIndex(entry_id);

        mActivityType = (EditText) findViewById(R.id.activityType);
        mActivityType.setText(entry.getActivityType());

        mDateTime = (EditText) findViewById(R.id.dateTime);
        mDateTime.setText(entry.getDateTime());

        mDuration = (EditText) findViewById(R.id.duration);

        String str = "";
        double dur = entry.getDuration();
        if ((int) dur > 0) {
            str = String.valueOf((int) dur) + "mins ";
        }
        str += String.valueOf(new DecimalFormat("#.##").format((dur - Math
                .floor(dur)) * 60.0)) + " secs";
        mDuration.setText(str);

        mDistance = (EditText) findViewById(R.id.distance);

        String unit = PreferenceManager.getDefaultSharedPreferences(this)
                .getString("list_preference", "Miles");
        if (unit.equalsIgnoreCase("Miles")) {
            mDistance.setText(String.valueOf(new DecimalFormat("#.##")
                    .format(entry.getDistance()) + " " + unit));
        } else {
            mDistance.setText(String.valueOf(new DecimalFormat("#.##")
                    .format(entry.getDistance() * 1.60934)) + " " + unit);
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_history_item, menu);
        return true;
    }


    //Create the option menu to delete the current saved exercise entry.

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_delete:
                data_source.deleteEntry(entry);
                finish();
                break;

        }
        return true;
    }

}

