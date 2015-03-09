package com.dartmouth.yuanjiang.myruns_2;

import android.location.Location;

import com.google.android.gms.maps.model.LatLng;

import java.text.DecimalFormat;

/**
 * Created by dartmouth on 2/8/15.
 */
public class Globals {
    public static final double Th = 1000, Hun = 100;
    public static final double MFS = 60;
    public static final double KMToMile = 0.621371;
    public static final double MeterToFoot = 3.28084;
    public static final int BLOCK_QUEUE_CAPACITY =  64;

    public static final DecimalFormat df = new  DecimalFormat("#.##");
    public static LatLng fromLocationToLatLng(Location location){
        return new LatLng(location.getLatitude(), location.getLongitude());
    }
}
