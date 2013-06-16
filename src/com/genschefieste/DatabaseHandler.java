package com.genschefieste;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHandler extends SQLiteOpenHelper {

    // Database Version.
    private static final int DATABASE_VERSION = 1;

    // Database name.
    private static final String DATABASE_NAME = "GenscheFieste";

    // Events table name.
    private static final String TABLE_EVENTS = "events";

    // Events table column names.
    private static final String KEY_ID = "id";
    private static final String KEY_TITLE = "name";
    private static final String EXTERNAL_ID = "external_id";
    private static final String KEY_FREE = "price_free";
    private static final String KEY_PRICE = "price";
    private static final String KEY_PRICE_PS = "price_vvk";
    private static final String KEY_DESCRIPTION = "description";
    private static final String KEY_DATE = "date";
    private static final String KEY_DATE_PERIOD = "date_period";
    private static final String KEY_START_HOUR = "hour_start";
    private static final String KEY_DATE_SORT = "date_sort";
    private static final String KEY_CAT_NAME = "cat_name";
    private static final String KEY_CAT_ID = "cat_id";
    private static final String KEY_LOC_ID = "loc_id";
    private static final String KEY_LOC_NAME = "loc_name";
    private static final String KEY_LAT = "latitude";
    private static final String KEY_LONG = "longitude";
    private static final String KEY_DISCOUNT = "discount";
    private static final String KEY_FESTIVAL = "festival";
    private static final String KEY_FAVORITE = "favorite";

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
                "" + KEY_LOC_ID + " INTEGER," +
                "" + KEY_LOC_NAME + " TEXT," +
                "" + KEY_LAT + " TEXT," +
                "" + KEY_LONG + " TEXT," +
                "" + KEY_DISCOUNT + " TEXT," +
                "" + KEY_FESTIVAL + " INTEGER," +
                "" + KEY_FAVORITE + " INTEGER" +
                ")";
        db.execSQL(CREATE_EVENTS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // No upgrades.
    }

    // Insert or update event.
    public void insertEvent(Event event) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_TITLE, event.getTitle());
        values.put(EXTERNAL_ID, event.getExternalId());
        values.put(KEY_PRICE, event.getPrice());
        values.put(KEY_PRICE_PS, event.getPricePresale());
        values.put(KEY_DESCRIPTION, event.getDescription());
        values.put(KEY_DATE, event.getDate());
        values.put(KEY_DATE_PERIOD, event.getDatePeriod());
        values.put(KEY_START_HOUR, event.getStartHour());
        values.put(KEY_DATE_SORT, event.getDateSort());
        values.put(KEY_CAT_NAME, event.getCategory());
        values.put(KEY_CAT_ID, event.getCategoryId());
        values.put(KEY_LOC_ID, event.getLocationId());
        values.put(KEY_LOC_NAME, event.getLocation());
        values.put(KEY_LAT, event.getLatitude());
        values.put(KEY_LONG, event.getLongitude());
        values.put(KEY_DISCOUNT, event.getDiscount());
        values.put(KEY_FESTIVAL, event.getFestival());
        values.put(KEY_FAVORITE, event.getFavorite());

        db.insert(TABLE_EVENTS, null, values);
        db.close();
    }

    // Update favorite status for an event.
    public void saveFavorite(int favorite, int eventId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_FAVORITE, favorite);
        db.update(TABLE_EVENTS, values, KEY_ID + "=" + eventId, null);
        db.close();
    }

    // Get all events.
    public List<Event> getEvents(String query) {
        List<Event> eventList = new ArrayList<Event>();

        // Select All Query.
        String selectQuery = "SELECT * FROM " + TABLE_EVENTS;
        if (query.length() > 0) {
            selectQuery += " AND (" + KEY_TITLE + " LIKE '%" + query + "%'";
            selectQuery += " OR " + KEY_DESCRIPTION + " LIKE '%" + query + "%') ";
        }
        selectQuery += " ORDER BY random() limit 30";

        SQLiteDatabase db = this.getWritableDatabase();
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
                    cursor.getInt(13),
                    cursor.getString(14),
                    cursor.getString(15),
                    cursor.getString(16),
                    cursor.getString(17),
                    cursor.getInt(18),
                    cursor.getInt(19)
                );

                // Adding event to list.
                eventList.add(event);
            }
            while (cursor.moveToNext());
        }

        db.close();

        return eventList;
    }

    // Get favorites.
    public List<Event> getFavoriteEvents() {
        List<Event> eventList = new ArrayList<Event>();

        // Query
        // TODO add sort
        String selectQuery = "SELECT * FROM " + TABLE_EVENTS + " WHERE favorite = 1";
        selectQuery += " ORDER BY id ASC limit 30";

        SQLiteDatabase db = this.getWritableDatabase();
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
                        cursor.getInt(13),
                        cursor.getString(14),
                        cursor.getString(15),
                        cursor.getString(16),
                        cursor.getString(17),
                        cursor.getInt(18),
                        cursor.getInt(19)
                );

                // Adding event to list.
                eventList.add(event);
            }
            while (cursor.moveToNext());
        }

        db.close();

        return eventList;
    }

    // Get single event.
    public Event getEvent(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_EVENTS,
                new String[]{
                        KEY_ID,
                        KEY_TITLE,
                        EXTERNAL_ID,
                        KEY_FREE,
                        KEY_PRICE,
                        KEY_PRICE_PS,
                        KEY_DESCRIPTION,
                        KEY_DATE,
                        KEY_DATE_PERIOD,
                        KEY_START_HOUR,
                        KEY_DATE_SORT,
                        KEY_CAT_NAME,
                        KEY_CAT_ID,
                        KEY_LOC_ID,
                        KEY_LOC_NAME,
                        KEY_LAT,
                        KEY_LONG,
                        KEY_DISCOUNT,
                        KEY_FESTIVAL,
                        KEY_FAVORITE},
                KEY_ID + "=?", new String[]{String.valueOf(id)}, null, null, null, null);

        if (cursor != null) {
            cursor.moveToFirst();
        }

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
            cursor.getInt(13),
            cursor.getString(14),
            cursor.getString(15),
            cursor.getString(16),
            cursor.getString(17),
            cursor.getInt(18),
            cursor.getInt(19)
        );
    }
}
