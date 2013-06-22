package com.genschefieste;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.PointF;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHandler extends SQLiteOpenHelper {

    // Database Version.
    private static final int DATABASE_VERSION = 1;

    // Database name.
    private static final String DATABASE_NAME = "GenscheFieste";

    // Events table name.
    public static final String TABLE_EVENTS = "events";

    // Events table column names.
    public static final String KEY_ID = "id";
    public static final String KEY_TITLE = "name";
    public static final String EXTERNAL_ID = "external_id";
    public static final String KEY_FREE = "price_free";
    public static final String KEY_PRICE = "price";
    public static final String KEY_PRICE_PS = "price_vvk";
    public static final String KEY_DESCRIPTION = "description";
    public static final String KEY_DATE = "date";
    public static final String KEY_DATE_PERIOD = "date_period";
    public static final String KEY_START_HOUR = "hour_start";
    public static final String KEY_DATE_SORT = "date_sort";
    public static final String KEY_CAT_NAME = "cat_name";
    public static final String KEY_CAT_ID = "cat_id";
    public static final String KEY_URL = "url";
    public static final String KEY_LOC_ID = "loc_id";
    public static final String KEY_LOC_NAME = "loc_name";
    public static final String KEY_LAT = "latitude";
    public static final String KEY_LONG = "longitude";
    public static final String KEY_DISCOUNT = "discount";
    public static final String KEY_FESTIVAL = "festival";

    // Favorites table name.
    public static final String TABLE_FAVORITES = "favorites";

    // Favorites table column names.
    public static final String FAVORITES_KEY_ID = "fav_external_id";


    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_EVENTS_TABLE = "CREATE TABLE " + TABLE_EVENTS + "(" +
                "" + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                "" + KEY_TITLE + " TEXT," +
                "" + EXTERNAL_ID + " INTEGER," +
                "" + KEY_FREE + " INTEGER," +
                "" + KEY_PRICE + " TEXT," +
                "" + KEY_PRICE_PS + " TEXT," +
                "" + KEY_DESCRIPTION + " TEXT," +
                "" + KEY_DATE + " INTEGER," +
                "" + KEY_DATE_PERIOD + " TEXT," +
                "" + KEY_START_HOUR + " TEXT," +
                "" + KEY_DATE_SORT + " INTEGER," +
                "" + KEY_CAT_NAME + " TEXT," +
                "" + KEY_CAT_ID + " INTEGER," +
                "" + KEY_URL + " TEXT," +
                "" + KEY_LOC_ID + " INTEGER," +
                "" + KEY_LOC_NAME + " TEXT," +
                "" + KEY_LAT + " TEXT," +
                "" + KEY_LONG + " TEXT," +
                "" + KEY_DISCOUNT + " TEXT," +
                "" + KEY_FESTIVAL + " INTEGER" +
                ")";
        db.execSQL(CREATE_EVENTS_TABLE);

        String CREATE_FAVORITES_TABLE = "CREATE TABLE " + TABLE_FAVORITES + "(" +
                "" + FAVORITES_KEY_ID + " INTEGER" +
                ")";
        db.execSQL(CREATE_FAVORITES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // No upgrades.
    }

    // Set favorite status for an event.
    public void saveFavorite(int favorite, int eventId) {
        SQLiteDatabase db = this.getWritableDatabase();

        // Always delete just to make sure.
        assert db != null;
        db.delete(TABLE_FAVORITES, FAVORITES_KEY_ID + " = ?",
                new String[] { "" + eventId });

        // Insert if favorite is 1.
        if (favorite == 1) {
            ContentValues values = new ContentValues();
            values.put(FAVORITES_KEY_ID, eventId);
            db.insert(TABLE_FAVORITES, null, values);
        }

        db.close();
    }

    // Truncate the table, this only happens for update.
    public void truncateTable() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_EVENTS, null, null);
        db.close();
    }

    // Get events.
    public List<Event> getEvents(String selectQuery) {
        List<Event> eventList = new ArrayList<Event>();

        SQLiteDatabase db = this.getWritableDatabase();
        assert db != null;
        Cursor cursor = db.rawQuery(selectQuery, null);

        // Loop through all rows and add to list.
        if (cursor.moveToFirst()) {
            do {
                Event event = new Event(
                    cursor.getInt(0),
                    cursor.getString(1),
                    cursor.getInt(2),
                    cursor.getInt(3),
                    cursor.getString(4),
                    cursor.getString(5),
                    cursor.getString(6),
                    cursor.getInt(7),
                    cursor.getString(8),
                    cursor.getString(9),
                    cursor.getInt(10),
                    cursor.getString(11),
                    cursor.getInt(12),
                    cursor.getString(13),
                    cursor.getInt(14),
                    cursor.getString(15),
                    cursor.getString(16),
                    cursor.getString(17),
                    cursor.getString(18),
                    cursor.getInt(19),
                    cursor.getInt(20)
                );

                // Adding event to list.
                eventList.add(event);
            }
            while (cursor.moveToNext());
        }

        db.close();

        return eventList;
    }

    // Get events.
    public List<Event> getEventsAroundMe(String selectQuery, PointF center, double radius) {
        List<Event> eventList = new ArrayList<Event>();

        SQLiteDatabase db = this.getWritableDatabase();
        assert db != null;
        Cursor cursor = db.rawQuery(selectQuery, null);

        // Loop through all rows and add to list.
        if (cursor.moveToFirst()) {
            do {
                Event event = new Event(
                    cursor.getInt(0),
                    cursor.getString(1),
                    cursor.getInt(2),
                    cursor.getInt(3),
                    cursor.getString(4),
                    cursor.getString(5),
                    cursor.getString(6),
                    cursor.getInt(7),
                    cursor.getString(8),
                    cursor.getString(9),
                    cursor.getInt(10),
                    cursor.getString(11),
                    cursor.getInt(12),
                    cursor.getString(13),
                    cursor.getInt(14),
                    cursor.getString(15),
                    cursor.getString(16),
                    cursor.getString(17),
                    cursor.getString(18),
                    cursor.getInt(19),
                    cursor.getInt(20)
                );

                // Adding event to list in case it's in our circle.
                float x = Float.parseFloat(event.getLatitude());
                float y = Float.parseFloat(event.getLongitude());
                PointF pointForCheck = new PointF(x, y);

                double distance = getDistanceBetweenTwoPoints(pointForCheck, center);
                if (distance <= radius) {
                    // Put the distance in the location property.
                    event.setLocation(String.valueOf(distance));
                    eventList.add(event);
                }
            }
            while (cursor.moveToNext());
        }

        db.close();

        return eventList;
    }

    /**
     * Get distance between two points.
     */
    public static double getDistanceBetweenTwoPoints(PointF p1, PointF p2) {
        double R = 6371000; // m
        double dLat = Math.toRadians(p2.x - p1.x);
        double dLon = Math.toRadians(p2.y - p1.y);
        double lat1 = Math.toRadians(p1.x);
        double lat2 = Math.toRadians(p2.x);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) + Math.sin(dLon / 2)
                * Math.sin(dLon / 2) * Math.cos(lat1) * Math.cos(lat2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double d = R * c;

        return d;
    }

    // Get number of events.
    public int getEventCount() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor dataCount = db.rawQuery("select * from " + TABLE_EVENTS, null);
        int count = dataCount.getCount();
        db.close();
        return count;
    }

    // Get single event.
    public Event getEvent(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        assert db != null;
        String selectQuery = "SELECT * FROM " + TABLE_EVENTS;
        selectQuery += " te LEFT JOIN " + DatabaseHandler.TABLE_FAVORITES + " tf ON te." + DatabaseHandler.EXTERNAL_ID + " = tf." + DatabaseHandler.FAVORITES_KEY_ID + " ";
        selectQuery += " WHERE " + KEY_ID + " = " + id;
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor != null) {
            cursor.moveToFirst();
        }

        assert cursor != null;
        return new Event(
            cursor.getInt(0),
            cursor.getString(1),
            cursor.getInt(2),
            cursor.getInt(3),
            cursor.getString(4),
            cursor.getString(5),
            cursor.getString(6),
            cursor.getInt(7),
            cursor.getString(8),
            cursor.getString(9),
            cursor.getInt(10),
            cursor.getString(11),
            cursor.getInt(12),
            cursor.getString(13),
            cursor.getInt(14),
            cursor.getString(15),
            cursor.getString(16),
            cursor.getString(17),
            cursor.getString(18),
            cursor.getInt(19),
            cursor.getInt(20)
        );
    }
}
