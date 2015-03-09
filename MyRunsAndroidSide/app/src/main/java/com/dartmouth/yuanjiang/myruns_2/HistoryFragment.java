package com.dartmouth.yuanjiang.myruns_2;
import android.app.ListFragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.text.DecimalFormat;


public class HistoryFragment extends ListFragment {
    private EntryDataSource data_source;
    private IntentFilter mMessageIntentFilter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_history, container, false);
        mMessageIntentFilter = new IntentFilter();
        mMessageIntentFilter.addAction("GCM_NOTIFY");
        data_source = EntryDataSource.getInstance(getActivity());

        return view;
    }

    private BroadcastReceiver mMessageUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            long id = intent.getLongExtra("id", -1);

                try{
                    Thread.sleep(100);
                }catch(Exception e){
                    e.printStackTrace();
                }
                refresh();

        }
    };

    private class ActivityEntriesCursorAdapter extends CursorAdapter {

        private LayoutInflater mInflater;

        public ActivityEntriesCursorAdapter(Context context, Cursor c) {
            super(context, c, FLAG_REGISTER_CONTENT_OBSERVER);
            mInflater = LayoutInflater.from(context);
        }

        // Override the BindView function to set our data which means,
        // take the data from the cursor and put it into views.
        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            TextView tv1 = (TextView) view.findViewById(android.R.id.text1);
            TextView tv2 = (TextView) view.findViewById(android.R.id.text2);

            String part1 = cursor.getString(1);
            String part2 = cursor.getString(2);
            tv1.setText(part1 + ", " + part2);

            String unit = PreferenceManager.getDefaultSharedPreferences(
                    getActivity()).getString("list_preference", "Miles");
            if (unit.equalsIgnoreCase("Kilometers")) {
                part1 = String.valueOf(new DecimalFormat("#.##").format(cursor
                        .getDouble(4) * 1.60934)) + " " + unit;
            } else {
                part1 = String.valueOf(new DecimalFormat("#.##").format(cursor
                        .getDouble(4))) + " " + unit;
            }

            part2 = "";
            double duration = cursor.getDouble(3);
            if ((int) duration > 0) {
                part2 = String.valueOf((int) duration) + "mins ";
                Log.d("bbbb", Double.toString(duration));
            }
            part2 += String.valueOf(new DecimalFormat("#.##")
                    .format((duration - Math.floor(duration)) * 60.0))
                    + " secs";

            tv2.setText(part1 + ", " + part2);
        }

        // When the view will be created for first time,
        // we need to tell the adapters, how each item will look
        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            return mInflater.inflate(android.R.layout.two_line_list_item, null);
        }
    }

    public void refresh(){
        Cursor cursor = data_source.createCursor(new String[] {
                MySQLiteHelper.COLUMN_ID,
                MySQLiteHelper.COLUMN_ACTIVITYTYPE,
                MySQLiteHelper.COLUMN_DATETIME,
                MySQLiteHelper.COLUMN_DURATION,
                MySQLiteHelper.COLUMN_DISTANCE
        }, null);

        ActivityEntriesCursorAdapter mAdapter = new ActivityEntriesCursorAdapter(
                getActivity(), cursor);
        setListAdapter(mAdapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().registerReceiver(mMessageUpdateReceiver, mMessageIntentFilter);
        refresh();
    }


    @Override
    public void onPause() {

        getActivity().unregisterReceiver(mMessageUpdateReceiver);
        super.onPause();
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        Entry entry = data_source.getEntryByIndex(id);
        Intent intent = new Intent();
        intent.putExtra("_id", id);

        if(entry.getLatLngList() != null){
            intent.setClass(getActivity(), MapsActivity.class);
            intent.putExtra("displayType", "history");
        } else {
            intent.setClass(getActivity(), HistoryItemActivity.class);
        }
        startActivity(intent);
    }

}

