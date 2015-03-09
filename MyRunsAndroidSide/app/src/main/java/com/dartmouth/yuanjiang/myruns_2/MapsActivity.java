package com.dartmouth.yuanjiang.myruns_2;

import android.app.FragmentManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.location.Location;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MapsActivity extends FragmentActivity implements ServiceConnection {
    private GoogleMap myMap;
    private Intent serviceIntent;
    private IntentFilter mIntentFilter;
    private IntentFilter mLabelIntentFilter;
    public List<Location> locationList;
    private List<LatLng> latLngList;
    private Context mContext;
    private Polyline polyline;
    private Marker startMarker;
    private Marker endMarker;
    private EntryDataSource datasource;
    private Entry entry;
    private String mDisplaytype;
    private boolean mIsBound;

    private int lastIndex;
    private float zoomLevel = 17.0f;
    private boolean automatic;
    private boolean history;
    private boolean conti;
    private long startTime;
    private double sumDistance;
    private double sumAltitude;

    private int stand_count;
    private int walk_count;
    private int run_count;
    LocationBroadcastReceiver locationBroadcastReceiver;
    SensorBroadcastReceiver sensorBroadcastReceiver;

    private TextView text_activity;
    private TextView text_avg_speed;
    private TextView text_cur_speed;
    private TextView text_climb;
    private TextView text_calories;
    private TextView text_distance;

    private String LOGTAG = "MapDisplayActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(LOGTAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        mContext = this;
        latLngList = new ArrayList<LatLng>();

        text_activity = (TextView)findViewById(R.id.activityText);
        text_avg_speed = (TextView)findViewById(R.id.avgspeedText);
        text_cur_speed = (TextView)findViewById(R.id.curspeedText);
        text_climb = (TextView)findViewById(R.id.climbText);
        text_calories = (TextView)findViewById(R.id.caloriesText);
        text_distance = (TextView)findViewById(R.id.distanceText);

        Intent intent = getIntent();
        datasource = EntryDataSource.getInstance(this);
        entry = new Entry();
        entry.setInputType(intent.getStringExtra("inputType"));
        entry.setActivityType(intent.getStringExtra("activityType"));
        if(intent.getStringExtra("inputType") != null &&
                intent.getStringExtra("inputType").equalsIgnoreCase("automatic")){
            automatic = true;
        }
        entry.setDateTime(DateFormat.format("kk:mm:ss MMM dd yyyy",
                Calendar.getInstance().getTimeInMillis()).toString());

        serviceIntent = new Intent(mContext, TrackingService.class);
        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(TrackingService.LOCATION_CHANGED);
        mLabelIntentFilter = new IntentFilter();
        mLabelIntentFilter.addAction(TrackingService.LABEL_AVAILABLE);

        locationBroadcastReceiver = new LocationBroadcastReceiver();
        if(automatic){
            sensorBroadcastReceiver = new SensorBroadcastReceiver();
        }

        FragmentManager myFragmentManager = getFragmentManager();
        MapFragment myMapFragment
                = (MapFragment)myFragmentManager.findFragmentById(R.id.map);
        myMap = myMapFragment.getMap();
        myMap.setMyLocationEnabled(true);
        myMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        Button btnSave = (Button)findViewById(R.id.btnSave);
        Button btnCancel = (Button)findViewById(R.id.btnCancel);


        mDisplaytype = intent.getStringExtra("displayType");
        if (mDisplaytype != null ){
            if (mDisplaytype.equalsIgnoreCase("new")) {
                startService(serviceIntent);
            } else if (mDisplaytype.equalsIgnoreCase("history")) {
                btnSave.setVisibility(View.INVISIBLE);
                btnCancel.setVisibility(View.INVISIBLE);
                history = true;
                long entry_id = intent.getLongExtra("_id", 0);
                entry = datasource.getEntryByIndex(entry_id);
            }
        }
        if(!history){
            doBindService();
        }

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        Log.i(LOGTAG, "onSaveInstanceState");
        super.onSaveInstanceState(outState);

        outState.putParcelableArrayList("latLngList", (ArrayList<LatLng>)latLngList);

        outState.putInt("lastIndex", lastIndex);
        outState.putLong("startTime", startTime);
        outState.putDouble("sumDistance", sumDistance);
        outState.putDouble("sumAltitude", sumAltitude);
        outState.putFloat("zoomLevel", myMap.getCameraPosition().zoom);
        outState.putBoolean("automatic", automatic);
        outState.putInt("standingCnt", stand_count);
        outState.putInt("walkingCnt", walk_count);
        outState.putInt("runningCnt", run_count);
        outState.putString("curActivity", entry.getActivityType());
    }

    @Override
    protected void onRestoreInstanceState(Bundle mSaveInstance) {
        Log.i(LOGTAG, "onRestoreInstanceState");
        if (mSaveInstance != null) {
            latLngList = mSaveInstance.getParcelableArrayList("latLngList");
            lastIndex = mSaveInstance.getInt("lastIndex");
            startTime = mSaveInstance.getLong("startTime");
            sumDistance = mSaveInstance.getDouble("sumDistance");
            sumAltitude = mSaveInstance.getDouble("sumAltitude");
            zoomLevel = mSaveInstance.getFloat("zoomLevel");
            automatic = mSaveInstance.getBoolean("automatic");
            stand_count = mSaveInstance.getInt("standingCnt");
            walk_count = mSaveInstance.getInt("walkingCnt");
            run_count = mSaveInstance.getInt("runningCnt");
            entry.setActivityType(mSaveInstance.getString("curActivity"));
        }
        super.onRestoreInstanceState(mSaveInstance);
    }

    /**
     * Bind this Activity to TrackingService
     */
    private void doBindService() {

        if (!mIsBound) {
            bindService(serviceIntent, this, Context.BIND_AUTO_CREATE);
        }
        mIsBound = true;

        Log.i(LOGTAG, "Binding.");
    }

    private void doUnBindService() {
        Log.i(LOGTAG, "doUnBindService");
        if(mIsBound){
            unbindService(this);
        }
        mIsBound = false;
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service)  {
        Log.i(LOGTAG, "onServiceConnected");
        locationList = ((TrackingService.MyBinder)service).getLocationList();
        if(startTime == 0){
            startTime = System.currentTimeMillis();
        }
    }

    public void updateStats(int cur_index) {
        for (int i = lastIndex; i <= cur_index; i++) {
            Location location = locationList.get(i);
            if (location.hasSpeed()) {
                entry.setCurspeed(location.getSpeed() / Globals.Th
                        * Globals.KMToMile * Globals.MFS * Globals.MFS);
            }
            double duration = (System.currentTimeMillis() - startTime)
                                / (Globals.Th * Globals.MFS);
            entry.setDuration(duration);
            if (i > 0) {
                sumDistance += location.distanceTo(locationList.get(i - 1))
                                / Globals.Th * Globals.KMToMile;
            }
            entry.setDistance(sumDistance );
            entry.setAvgspeed(sumDistance  / (duration/Globals.MFS));
            if(i > 0 && location.hasAltitude() && locationList.get(i - 1).hasAltitude()
                    &&(location.getAltitude() - locationList.get(i - 1).getAltitude()) > 0){
                sumAltitude += (location.getAltitude() - locationList.get(i - 1).getAltitude()) /
                                            Globals.Th * Globals.KMToMile;
            }
            entry.setClimb(sumAltitude);
            entry.setCalorie((int) (sumDistance * Globals.Hun + sumAltitude * Globals.Th));
            entry.setLatLngList(latLngList);
        }
        lastIndex = cur_index + 1;
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        Log.i(LOGTAG, "onServiceDisconnected");
        mContext.stopService(serviceIntent);
    }

    // "Save" button is clicked
    public void onSaveClicked(View v) {
        Log.i(LOGTAG, "onSaveClicked");
        // Pop up a toast
        if (automatic) {
            String activity ="";
            if (stand_count > walk_count) {
                if (stand_count >= run_count) {
                    activity = "Standing";
                } else{
                    activity = "Running";
                }
            }else{
                if(stand_count >= run_count){
                    activity = "Walking";
                }else if(walk_count < run_count){
                    activity = "Running";
                }else{
                    activity = "Walking";
                }
            }
            entry.setActivityType(activity);
        }

        Entry tmp_entry = datasource.InsertEntry(entry);
        Toast.makeText(this, "Entry #" + tmp_entry.getId() + " saved", Toast.LENGTH_SHORT).show();

        doUnBindService();
        mContext.stopService(serviceIntent);
        finish();
    }

    // "Cancel" button is clicked
    public void onCancelClicked(View v) {
        Log.i(LOGTAG, "onCancelClicked");
        Toast.makeText(this, "Entry discarded", Toast.LENGTH_SHORT).show();
        doUnBindService();
        mContext.stopService(serviceIntent);
        finish();
    }

    @Override
    public void onBackPressed() {
        Log.i(LOGTAG, "onBackPressed");
        if(!history){

            Toast.makeText(this, "onBackPressed", Toast.LENGTH_SHORT).show();
            //this.unregisterReceiver(locationBroadcastReceiver);
            doUnBindService();
            mContext.stopService(serviceIntent);
        }

        super.onBackPressed();
    }

    @Override
    protected void onResume() {
        Log.i(LOGTAG, "onResume");
        if(!history){
            this.registerReceiver(locationBroadcastReceiver, mIntentFilter);
            if(automatic){
                this.registerReceiver(sensorBroadcastReceiver, mLabelIntentFilter);
            }
            conti = true;
        }else{
            displayMap(entry.getLatLngList());
        }

        super.onResume();
    }

    @Override
    protected void onPause() {
        Log.i(LOGTAG, "onPause");
        if(!history){
            doUnBindService();
            this.unregisterReceiver(locationBroadcastReceiver);
            if(automatic){
                this.unregisterReceiver(sensorBroadcastReceiver);
            }
        }
        super.onPause();
    }

    public void displayMap(List<LatLng> latLngList){
        Log.i(LOGTAG, "displayMap");
        int i = latLngList.size();
        Log.i(LOGTAG, "i = "+i);
        if(i == 1 || conti || history){
            conti = false;
            myMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLngList.get(0), zoomLevel));
        }

        PolylineOptions polylineOptions = new PolylineOptions().addAll(latLngList);
        polylineOptions.color(Color.BLACK);
        polylineOptions.width(10);

        if(polyline != null){
            polyline.remove();
            polyline = null;
        }

        polyline = myMap.addPolyline(polylineOptions);

        if(startMarker == null){
            startMarker = myMap.addMarker(new MarkerOptions().position(latLngList.get(0)).icon(BitmapDescriptorFactory.defaultMarker(
                    BitmapDescriptorFactory.HUE_GREEN)).title("Start"));
        }

        if(endMarker != null){
            endMarker.remove();
        }

        if (i > 1) {
            endMarker = myMap.addMarker(new MarkerOptions().position(latLngList.get(i-1)).icon(BitmapDescriptorFactory.defaultMarker(
                    BitmapDescriptorFactory.HUE_RED)).title("End"));
        }

        String unit = PreferenceManager.getDefaultSharedPreferences(this)
                .getString("list_preference", "Miles");

        if (unit.equalsIgnoreCase("Miles")) {
            text_avg_speed.setText("Avg speed: " + String.valueOf(Globals.df.format(entry.getAvgspeed())) + " m/h");
            text_cur_speed.setText("Cur speed: " + String.valueOf(Globals.df.format(entry.getCurspeed())) + " m/h");
            text_climb.setText("Climb: " + String.valueOf(Globals.df.format(entry.getClimb())) + " " + unit);
            text_distance.setText("Distance: " + String.valueOf(Globals.df.format(entry.getDistance())) + " " + unit);
        } else {
            text_avg_speed.setText("Avg speed: " + String.valueOf(Globals.df.format(entry.getAvgspeed() / Globals.KMToMile)) + " km/h");
            text_cur_speed.setText("Cur speed: " + String.valueOf(Globals.df.format(entry.getCurspeed() / Globals.KMToMile)) + " km/h");
            text_climb.setText("Climb: " + String.valueOf(Globals.df.format(entry.getClimb() / Globals.KMToMile)) + " " + unit);
            text_distance.setText("Distance: " + String.valueOf(Globals.df.format(entry.getDistance() / Globals.KMToMile)) + " " + unit);
        }

        text_activity.setText("Type: " + entry.getActivityType());
        text_calories.setText("Calorie: " + String.valueOf(entry.getCalorie()));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (history) {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.menu_history_item, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(!history) return true;
        switch (item.getItemId()) {
            case R.id.action_delete:
                datasource.deleteEntry(entry);
                finish();
                break;
        }
        return true;
    }

    public class LocationBroadcastReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context receiveContext, Intent receiveIntent) {
            // TODO Auto-generated method stub
            String action = receiveIntent.getAction();
            if (action.equalsIgnoreCase(TrackingService.LOCATION_CHANGED) ){
                Log.i(LOGTAG, "Receive Location Broadcast");
                boolean updated = false;
                for(int i=lastIndex;i<locationList.size();i++){
                    latLngList.add(new LatLng(locationList.get(i).getLatitude(), locationList.get(i).getLongitude()));
                    updated = true;
                }
                if(updated){
                    updateStats(latLngList.size()-1);
                    displayMap(latLngList);
                }
            }
        }
    }

    public class SensorBroadcastReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context receiveContext, Intent receiveIntent) {
            // TODO Auto-generated method stub
            String action = receiveIntent.getAction();
            if (action.equalsIgnoreCase(TrackingService.LABEL_AVAILABLE) ){
                Log.i(LOGTAG, "Receive Sensor Broadcast ");
                double label = receiveIntent.getDoubleExtra("label_result", .0);
                if (label == 0.0) {
                    stand_count++;
                    entry.setActivityType("Standing");
                } else if(label == 1.0){
                    walk_count++;
                    entry.setActivityType("Walking");
                } else if(label == 2.0){
                    run_count++;
                    entry.setActivityType("Running");
                }
            }
        }
    }
}
