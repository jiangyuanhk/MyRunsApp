package com.dartmouth.yuanjiang.myruns_2;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Spinner;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class StartFragment extends Fragment {

    private Context mContext;
    private Spinner spinner_read;
    private Spinner spinner_activity;
    private Button button_start;
    private Button button_activity;

    private EntryDataSource data_source;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_start, container, false);
        final Intent startIntent = new Intent();
        mContext = getActivity();

        button_start = (Button)v.findViewById(R.id.start_button);
        button_activity = (Button)v.findViewById(R.id.sync_button);

        spinner_read = (Spinner)v.findViewById(R.id.input_type);
        spinner_activity = (Spinner)v.findViewById(R.id.activity_type);

        button_activity.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){}
        });

        button_start.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {

                int index = spinner_read.getSelectedItemPosition();

                startIntent.putExtra("activityType", spinner_activity.getSelectedItem().toString());
                startIntent.putExtra("inputType", spinner_read.getSelectedItem().toString());

                if(index == 0){
                    startIntent.setClass(getActivity(), ManualEntryActivity.class);
                } else {
                    startIntent.setClass(getActivity(), MapsActivity.class);
                    startIntent.putExtra("displayType", "new");
                    startIntent.putExtra("inputType", spinner_read.getSelectedItem().toString());
                }

                startActivity(startIntent);
            }
        });

        button_activity.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                data_source = EntryDataSource.getInstance(mContext);
                List<Entry>entries = data_source.getAllEntries();
                JSONArray array = new JSONArray();
                JSONObject jsonData = new JSONObject();
                String unit = PreferenceManager.getDefaultSharedPreferences(mContext)
                        .getString("unit_list_preference", "Miles");

                try{
                    jsonData.put(MainActivity.PROPERTY_REG_ID, MainActivity.regid);
                    jsonData.put("unit",unit);


                    for(Entry entry:entries){
                        JSONObject obj = new JSONObject();
                        //obj.put(MainActivity.PROPERTY_REG_ID, MainActivity.regid);
                        obj.put("id", entry.getId());
                        obj.put("inputType", entry.getInputType());
                        obj.put("activityType", entry.getActivityType());
                        obj.put("dateTime", entry.getDateTime());
                        obj.put("duration", entry.getDuration());
                        obj.put("calories", entry.getCalorie());
                        obj.put("heartrate", entry.getHeartRate());
                        obj.put("comment", entry.getComment());

                        if (!unit.equalsIgnoreCase("Miles")) {
                            obj.put("distance", entry.getDistance() / Globals.KMToMile);
                            obj.put("climb", entry.getClimb() / Globals.KMToMile);
                            obj.put("avgspeed", entry.getAvgspeed() / Globals.KMToMile);
                        } else {
                            obj.put("distance", entry.getDistance());
                            obj.put("climb", entry.getClimb());
                            obj.put("avgspeed", entry.getAvgspeed());
                        }
                        array.put(obj);
                    }

                    jsonData.put("array", array);
                }catch(Exception e){
                    e.printStackTrace();
                }

                String out_put = jsonData.toString();
                syncExercise(out_put);
            }
        });
        return v;
    }

    private void syncExercise(String data) {
        new AsyncTask<String, Void, String>() {

            @Override
            protected String doInBackground(String... arg0) {
                String url = getString(R.string.server_addr) + "/post.do";
                String res = "";
                Map<String, Object> params = new HashMap<String, Object>();
                params.put("data", arg0[0]);
                try {
                    res = ServerUtilities.post_t(url, params, "application/json");
                } catch (Exception ex) {
                    ex.printStackTrace();
                }

                return res;
            }

            @Override
            protected void onPostExecute(String res) {
                //mPostText.setText("");
                //refreshPostHistory();
            }

        }.execute(data);
    }

/*
	private void refreshPostHistory() {
		new AsyncTask<Void, Void, String>() {

			@Override
			protected String doInBackground(Void... arg0) {
				String url = getString(R.string.server_addr)
						+ "/get_history.do";
				String res = "";
				Map<String, String> params = new HashMap<String, String>();
				try {
					res = ServerUtilities.post(url, params);
				} catch (Exception ex) {
					ex.printStackTrace();
				}

				return res;
			}

			@Override
			protected void onPostExecute(String res) {
				if (!res.equals("")) {
					mHistoryText.setText(res);
				}
			}

		}.execute();
	}
*/
}
