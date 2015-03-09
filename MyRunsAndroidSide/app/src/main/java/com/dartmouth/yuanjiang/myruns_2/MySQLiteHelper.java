package com.dartmouth.yuanjiang.myruns_2;

/**
 * Created by dartmouth on 1/30/15.
 */

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class MySQLiteHelper extends SQLiteOpenHelper {

    public static final String TABLE_ENTRIES = "entry2";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_INPUTYPE = "input_type";
    public static final String COLUMN_ACTIVITYTYPE = "activityType";
    public static final String COLUMN_DATETIME = "datetime";
    public static final String COLUMN_DURATION = "duration";
    public static final String COLUMN_DISTANCE = "distance";
    public static final String COLUMN_CALORIES = "calories";
    public static final String COLUMN_HEARTRATE = "heartrate";
    public static final String COLUMN_COMMENT = "comment";
    public static final String COLUMN_LATLNG = "latlng";
    public static final String COLUMN_CURSPEED = "curspeed";
    public static final String COLUMN_AVGSPEED = "avgspeed";
    public static final String COLUMN_CLIMB = "climb";

    private static final String DATABASE_NAME = "entry2.db";
    private static final int DATABASE_VERSION = 1;

    // Database creation sql statement
    private static final String DATABASE_CREATE = "create table "
            + TABLE_ENTRIES + "("
            + COLUMN_ID + " integer primary key autoincrement, "
            + COLUMN_INPUTYPE + " text, "
            + COLUMN_ACTIVITYTYPE + " text, "
            + COLUMN_DATETIME + " text, "
            + COLUMN_DURATION + " real, "
            + COLUMN_DISTANCE + " real, "
            + COLUMN_CALORIES + " real, "
            + COLUMN_HEARTRATE + " real, "
            + COLUMN_COMMENT + " text, "
            + COLUMN_LATLNG + " blob, "
            + COLUMN_CURSPEED + " real, "
            + COLUMN_AVGSPEED + " real, "
            + COLUMN_CLIMB + " real );";

    public MySQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(DATABASE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(MySQLiteHelper.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ENTRIES);
        onCreate(db);
    }

}