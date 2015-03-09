package com.dartmouth.yuanjiang.myruns_2;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import java.util.Calendar;

public class ManualEntryActivity extends ListActivity {

    private static final int MY_ID_DATE = 0;
    private static final int MY_ID_TIME = 1;
    private static final int MY_ID_DURATION = 2;
    private static final int MY_ID_DISTANCE = 3;
    private static final int MY_ID_CALORIES = 4;
    private static final int MY_ID_HEARTRATE = 5;
    private static final int MY_ID_COMMENT = 6;
    private Calendar mDateAndTime = Calendar.getInstance();

    private Entry entry;
    private EntryDataSource datasource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manual_entry);

        Intent intent = getIntent();
        datasource = EntryDataSource.getInstance(this);

        entry = new Entry();
        entry.setInputType(intent.getStringExtra("inputType"));
        entry.setActivityType(intent.getStringExtra("activityType"));
        //entry.setInputType("Manual Entry");
        entry.setDateTime(DateFormat.format("kk:mm:ss MMM dd yyyy",
                mDateAndTime.getTimeInMillis()).toString());

    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        int dialogId = 0;

        // show different dialog based on their id
        switch (position){
            case MY_ID_DATE:
                dialogId = MyDialogFragment.PROFILE_DIALOG_ID_DATE;
                break;
            case MY_ID_TIME:
                dialogId = MyDialogFragment.PROFILE_DIALOG_ID_TIME;
                break;
            case MY_ID_DURATION:
                dialogId = MyDialogFragment.PROFILE_DIALOG_ID_DURATION;
                break;
            case MY_ID_DISTANCE:
                dialogId = MyDialogFragment.PROFILE_DIALOG_ID_DISTANCE;
                break;
            case MY_ID_CALORIES:
                dialogId = MyDialogFragment.PROFILE_DIALOG_ID_CALORIES;
                break;
            case MY_ID_HEARTRATE:
                dialogId = MyDialogFragment.PROFILE_DIALOG_ID_HEARTRATE;
                break;
            case MY_ID_COMMENT:
                dialogId = MyDialogFragment.PROFILE_DIALOG_ID_COMMENT;
                break;
            default:
                dialogId = MyDialogFragment.PROFILE_DIALOG_ID_ERROR;
        }
        displayDialog(dialogId);
    }

    public void onDurationSet(double durationInMinutes){
        entry.setDuration(durationInMinutes);
    }

    public void displayDialog(int id){
        MyDialogFragment newFragment = MyDialogFragment.newInstance(id);
        newFragment.show(getFragmentManager(), getString(R.string.dialog_fragment_tag));
    }

    public void onDateSet(int year, int monthOfYear, int dayOfMonth) {
        // save date into sqlite
        mDateAndTime.set(Calendar.YEAR, year);
        mDateAndTime.set(Calendar.MONTH, monthOfYear);
        mDateAndTime.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        entry.setDateTime(DateFormat.format("kk:mm:ss MMM dd yyyy",
                mDateAndTime.getTimeInMillis()).toString());
    }

    public void onTimeSet(int hourOfDay, int minute) {
        // save time into sqlite
        mDateAndTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
        mDateAndTime.set(Calendar.MINUTE, minute);
        entry.setDateTime(DateFormat.format("kk:mm:ss MMM dd yyyy",
                mDateAndTime.getTimeInMillis()).toString());
    }

    public void onDurationSet(int durationInMinutes) {
        // save duration into sqlite
        entry.setDuration(durationInMinutes);
    }

    public void onDistanceSet(double distance) {

        String unit = PreferenceManager.getDefaultSharedPreferences(this)
                .getString("list_preference", "Miles");
        if (unit.equalsIgnoreCase("Miles")) {
            entry.setDistance(distance);
        } else {
            entry.setDistance(distance * Globals.KMToMile);
        }
    }

    public void onCaloriesSet(int calories) {

        entry.setCalorie(calories);
    }

    public void onHeartrateSet(int heartrate) {

        entry.setHeartRate(heartrate);
    }

    public void onCommentSet(String comment) {

        entry.setComment(comment);
    }

    // "Save" button is clicked
    public void onSaveClicked(View v) {
        // Pop up a toast
        Entry tmp_entry = datasource.InsertEntry(entry);
        Toast.makeText(this, "Entry #" + tmp_entry.getId() + " saved", Toast.LENGTH_SHORT).show();
        // Close the activity
        finish();
    }

    // "Cancel" button is clicked
    public void onCancelClicked(View v) {
        // Pop up a toast, discard the input and close the activity directly
        Toast.makeText(this, "Entry discarded", Toast.LENGTH_SHORT).show();
        finish();
    }
}
