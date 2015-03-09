package com.dartmouth.yuanjiang.myruns_2;

import com.google.android.gms.maps.model.LatLng;
import java.util.List;

/**
 * Created by dartmouth on 1/30/15.
 */
public class Entry {
    private long id;
    private String mInputType;
    private String mActivityType;
    private String mDateTime;
    private double mDuration;
    private double mDistance;
    private int mCalory;
    private int mHeartrate;
    private String mComment = "";
    private double mCurspeed;
    private double mAvgspeed;
    private double mClimb;
    private List<LatLng> mLatLngList;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getActivityType() {
        return mActivityType;
    }

    public void setActivityType(String activityType) {
        this.mActivityType = activityType;
    }

    public String getInputType() {
        return mInputType;
    }

    public void setInputType(String inputType) {
        this.mInputType = inputType;
    }

    public String getDateTime() {
        return mDateTime;
    }

    public void setDateTime(String dateTime) {
        this.mDateTime = dateTime;
    }

    public String getComment() {
        return mComment;
    }

    public void setComment(String comment) {
        this.mComment = comment;
    }

    public double getDuration() {
        return mDuration;
    }

    public void setDuration(double duration) {
        this.mDuration = duration;
    }

    public double getDistance() {
        return mDistance;
    }

    public void setDistance(double distance) {
        this.mDistance = distance;
    }

    public int getCalorie() {
        return mCalory;
    }

    public void setCalorie(int calory) {
        this.mCalory = calory;
    }

    public int getHeartRate() {
        return mHeartrate;
    }

    public void setHeartRate(int heartrate) {
        this.mHeartrate = heartrate;
    }

    public void setLatLngList(List<LatLng> latLngList) {
        this.mLatLngList = latLngList;
    }

    public List<LatLng> getLatLngList() {
        return mLatLngList;
    }

    public double getCurspeed() {
        return mCurspeed;
    }

    public void setCurspeed(double curspeed) {
        this.mCurspeed = curspeed;
    }

    public double getAvgspeed() {
        return mAvgspeed;
    }

    public void setAvgspeed(double avgspeed) {
        this.mAvgspeed = avgspeed;
    }

    public double getClimb() {
        return mClimb;
    }

    public void setClimb(double climb) {
        this.mClimb = climb;
    }

}
