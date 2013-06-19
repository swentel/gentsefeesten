package com.genschefieste;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

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

    // Insert or update event.
    public void insertEvent(Event event) {
        SQLiteDatabase db = this.getWritableDatabase();

        DatabaseUtils.InsertHelper ih = new DatabaseUtils.InsertHelper(db, "events");
        final int INDEX_KEY_TITLE = ih.getColumnIndex(KEY_TITLE);
        final int INDEX_EXTERNAL_ID = ih.getColumnIndex(EXTERNAL_ID);
        final int INDEX_KEY_FREE = ih.getColumnIndex(KEY_FREE);
        final int INDEX_KEY_PRICE = ih.getColumnIndex(KEY_PRICE);
        final int INDEX_KEY_PRICE_PS = ih.getColumnIndex(KEY_PRICE_PS);
        final int INDEX_KEY_DESCRIPTION = ih.getColumnIndex(KEY_DESCRIPTION);
        final int INDEX_KEY_DATE = ih.getColumnIndex(KEY_DATE);
        final int INDEX_KEY_DATE_PERIOD = ih.getColumnIndex(KEY_DATE_PERIOD);
        final int INDEX_KEY_START_HOUR = ih.getColumnIndex(KEY_START_HOUR);
        final int INDEX_KEY_DATE_SORT = ih.getColumnIndex(KEY_DATE_SORT);
        final int INDEX_KEY_CAT_NAME = ih.getColumnIndex(KEY_CAT_NAME);
        final int INDEX_KEY_CAT_ID = ih.getColumnIndex(KEY_CAT_ID);
        final int INDEX_KEY_LOC_ID = ih.getColumnIndex(KEY_LOC_ID);
        final int INDEX_KEY_LOC_NAME = ih.getColumnIndex(KEY_LOC_NAME);
        final int INDEX_KEY_LAT = ih.getColumnIndex(KEY_LAT);
        final int INDEX_KEY_LONG = ih.getColumnIndex(KEY_LONG);
        final int INDEX_KEY_DISCOUNT = ih.getColumnIndex(KEY_DISCOUNT);
        final int INDEX_KEY_FESTIVAL = ih.getColumnIndex(KEY_FESTIVAL);

        ih.prepareForInsert();

        ih.bind(INDEX_KEY_TITLE, event.getTitle());
        ih.bind(INDEX_EXTERNAL_ID, event.getExternalId());
        ih.bind(INDEX_KEY_FREE, event.getFree());
        ih.bind(INDEX_KEY_PRICE, event.getPrice());
        ih.bind(INDEX_KEY_PRICE_PS, event.getPricePresale());
        ih.bind(INDEX_KEY_DESCRIPTION, event.getDescription());
        ih.bind(INDEX_KEY_DATE, event.getDate());
        ih.bind(INDEX_KEY_DATE_PERIOD, event.getDatePeriod());
        ih.bind(INDEX_KEY_START_HOUR, event.getStartHour());
        ih.bind(INDEX_KEY_DATE_SORT, event.getDateSort());
        ih.bind(INDEX_KEY_CAT_NAME, event.getCategory());
        ih.bind(INDEX_KEY_CAT_ID, event.getCategoryId());
        ih.bind(INDEX_KEY_LOC_ID, event.getLocationId());
        ih.bind(INDEX_KEY_LOC_NAME, event.getLocation());
        ih.bind(INDEX_KEY_LAT, event.getLatitude());
        ih.bind(INDEX_KEY_LONG, event.getLongitude());
        ih.bind(INDEX_KEY_DISCOUNT, event.getDiscount());
        ih.bind(INDEX_KEY_FESTIVAL, event.getFestival());

        ih.execute();

        db.close();
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

        if (GenscheFieste.debugMode) {
            Log.d("DebugApp", "Query: " + selectQuery);
        }

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
