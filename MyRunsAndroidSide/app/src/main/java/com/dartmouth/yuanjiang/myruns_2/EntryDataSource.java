package com.dartmouth.yuanjiang.myruns_2;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by dartmouth on 1/30/15.
 */
public class EntryDataSource {

    // Database fields
    private SQLiteDatabase database;
    private MySQLiteHelper dbHelper;
    private static EntryDataSource datasource;

    private String[] allColumns = {
            MySQLiteHelper.COLUMN_ID,
            MySQLiteHelper.COLUMN_INPUTYPE,
            MySQLiteHelper.COLUMN_ACTIVITYTYPE,
            MySQLiteHelper.COLUMN_DATETIME,
            MySQLiteHelper.COLUMN_DURATION,
            MySQLiteHelper.COLUMN_DISTANCE,
            MySQLiteHelper.COLUMN_CALORIES,
            MySQLiteHelper.COLUMN_HEARTRATE,
            MySQLiteHelper.COLUMN_COMMENT,
            MySQLiteHelper.COLUMN_LATLNG,
            MySQLiteHelper.COLUMN_CURSPEED,
            MySQLiteHelper.COLUMN_AVGSPEED,
            MySQLiteHelper.COLUMN_CLIMB
    };

    private static final String TAG = "ENTRIES";

    private EntryDataSource(Context context) {
        dbHelper = new MySQLiteHelper(context);
    }

    public void open() throws SQLException {

        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public static EntryDataSource getInstance(Context context) {
        if (datasource == null) {
            datasource = new EntryDataSource(context);
            datasource.open();
        }

        return datasource;
    }

    public Cursor createCursor(String[] cols, String args) {
        return database.query(MySQLiteHelper.TABLE_ENTRIES, cols, args, null,
                null, null, null);

    }

    public Entry InsertEntry(Entry entry) {
        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.COLUMN_INPUTYPE, entry.getInputType());
        values.put(MySQLiteHelper.COLUMN_ACTIVITYTYPE, entry.getActivityType());
        values.put(MySQLiteHelper.COLUMN_DATETIME, entry.getDateTime());
        values.put(MySQLiteHelper.COLUMN_DURATION, entry.getDuration());
        values.put(MySQLiteHelper.COLUMN_DISTANCE, entry.getDistance());
        values.put(MySQLiteHelper.COLUMN_CALORIES, entry.getCalorie());
        values.put(MySQLiteHelper.COLUMN_HEARTRATE, entry.getHeartRate());
        values.put(MySQLiteHelper.COLUMN_COMMENT, entry.getComment());
        if (entry.getLatLngList() != null) {
            List<LatLng> arrayList = entry.getLatLngList();
            ByteBuffer buf = ByteBuffer.allocate(arrayList.size() * 16);
            for (int i = 0; i < arrayList.size(); i++) {
                buf.putDouble(arrayList.get(i).latitude);
                buf.putDouble(arrayList.get(i).longitude);
            }
            values.put(MySQLiteHelper.COLUMN_LATLNG, buf.array());
        }
        values.put(MySQLiteHelper.COLUMN_CURSPEED, entry.getCurspeed());
        values.put(MySQLiteHelper.COLUMN_AVGSPEED, entry.getAvgspeed());
        values.put(MySQLiteHelper.COLUMN_CLIMB, entry.getClimb());

        Log.d("ssaa", Double.toString(entry.getDuration()));

        long insertId = database.insert(MySQLiteHelper.TABLE_ENTRIES, null,
                values);

        Cursor cursor = database.query(MySQLiteHelper.TABLE_ENTRIES,
                allColumns, MySQLiteHelper.COLUMN_ID + " = " + insertId, null,
                null, null, null);
        cursor.moveToFirst();
        Entry newEntry = cursorToEntry(cursor);

        // Log the comment stored
        Log.d(TAG, "entry = " + cursorToEntry(cursor).toString()
                + " insert ID = " + insertId);

        cursor.close();
        entry.setId(insertId);

        return entry;
    }

    public void deleteEntry(Entry entry) {
        long id = entry.getId();
        Log.d(TAG, "delete entry = " + id);
        System.out.println("Entry deleted with id: " + id);
        database.delete(MySQLiteHelper.TABLE_ENTRIES, MySQLiteHelper.COLUMN_ID
                + " = " + id, null);
    }

    public List<Entry> getAllEntries() {
        List<Entry> entries = new ArrayList<Entry>();

        Cursor cursor = database.query(MySQLiteHelper.TABLE_ENTRIES,
                allColumns, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Entry entry = cursorToEntry(cursor);
            Log.d(TAG, "get entry = " + cursorToEntry(cursor).toString());
            entries.add(entry);
            cursor.moveToNext();
        }
        // Make sure to close the cursor
        cursor.close();
        return entries;
    }


    public Entry getEntryByIndex(long entry_id) {
        Entry entry = new Entry();

        Cursor cursor = database.query(MySQLiteHelper.TABLE_ENTRIES,
                allColumns, MySQLiteHelper.COLUMN_ID + " = " + entry_id, null, null,
                null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            entry = cursorToEntry(cursor);
            Log.d(TAG, "get entry = " + cursorToEntry(cursor).toString());
            cursor.moveToNext();
        }
        // Make sure to close the cursor
        cursor.close();
        return entry;
    }

    private Entry cursorToEntry(Cursor cursor) {
        Entry entry = new Entry();
        entry.setId(cursor.getLong(0));
        entry.setInputType(cursor.getString(1));
        entry.setActivityType(cursor.getString(2));
        entry.setDateTime(cursor.getString(3));
        entry.setDuration(cursor.getDouble(4));
        entry.setDistance(cursor.getDouble(5));
        entry.setCalorie(cursor.getInt(6));
        entry.setHeartRate(cursor.getInt(7));
        entry.setComment(cursor.getString(8));

        byte[] buf = cursor.getBlob(9);
        if (buf != null) {
            ByteBuffer byteBuffer = ByteBuffer.wrap(buf);
            entry.setLatLngList(new ArrayList<LatLng>());

            for (int i=0; i < buf.length / 16; i++)
                entry.getLatLngList().add(
                        new LatLng(byteBuffer.getDouble(),
                                byteBuffer.getDouble()));

        }
        entry.setCurspeed(cursor.getDouble(10));
        entry.setAvgspeed(cursor.getDouble(11));
        entry.setClimb(cursor.getDouble(12));

        Log.d("cccc", Double.toString(entry.getDuration()));
        return entry;
    }
}
