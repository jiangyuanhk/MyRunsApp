package com.dartmouth.yuanjiang.myruns_2;


import android.app.Activity;
import android.os.Bundle;
import android.view.View;

public class MapActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
    }

    public void onSaveClicked(View v){
        finish();
    }

    public void onCancelClicked(View v){
        finish();
    }
}
