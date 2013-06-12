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

    // Events table columns names.
    private static final String KEY_ID = "id";
    private static final String EXTERNAL_ID = "external_id";
    private static final String KEY_TITLE = "name";
    private static final String KEY_DESCRIPTION = "description"; // set
    private static final String KEY_CAT_NAME = "cat_name"; // set
    private static final String KEY_CAT_ID = "cat_id"; // set
    private static final String KEY_URL = "url"; // set
    private static final String KEY_LOC_ID = "loc_id"; // set
    private static final String KEY_LOC_NAME = "loc_name";
    private static final String KEY_LAT = "lat"; // set
    private static final String KEY_LONG = "long"; // set
    private static final String KEY_PRICE = "price";
    private static final String KEY_DATE = "date";
    private static final String KEY_DATE_PERIOD = "date_period"; // set
    private static final String KEY_DATE_SORT = "date_sort"; // set
    private static final String KEY_DISCOUNT = "discount"; // set
    private static final String KEY_START_HOUR = "hour_start";
    private static final String KEY_FAVORITE = "favorite";

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_EVENTS_TABLE = "CREATE TABLE " + TABLE_EVENTS + "(" +
                "" + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                "" + EXTERNAL_ID + " INTEGER," +
                "" + KEY_TITLE + " TEXT," +
                "" + KEY_DESCRIPTION + " TEXT," +
                "" + KEY_LOC_NAME + " TEXT," +
                "" + KEY_PRICE + " TEXT, " +
                "" + KEY_DATE + " INTEGER," +
                "" + KEY_START_HOUR + " TEXT," +
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
        values.put(KEY_DESCRIPTION, event.getDescription());
        values.put(KEY_LOC_NAME, event.getLocation());
        values.put(KEY_PRICE, "GRATIS");
        values.put(KEY_DATE, event.getDate());
        values.put(KEY_START_HOUR, event.getStartHour());
        values.put(KEY_FAVORITE, 0);

        db.insert(TABLE_EVENTS, null, values);
        db.close();
    }

    // Get all events.
    public List<Event> getEvents() {
        List<Event> eventList = new ArrayList<Event>();

        // Select All Query.
        String selectQuery = "SELECT * FROM " + TABLE_EVENTS + " WHERE date = 1374271200 ORDER BY id ASC limit 30";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // Loop through all rows and add to list.
        if (cursor.moveToFirst()) {
            do {
                Event event = new Event();
                event.setID(cursor.getInt(0));
                event.setTitle(cursor.getString(2));
                event.setDescription(cursor.getString(3));
                event.setLocation(cursor.getString(4));
                event.setPrice(cursor.getString(5));
                event.setDate(cursor.getInt(6));
                event.setStartHour(cursor.getString(7));
                event.setFavorite(Integer.parseInt(cursor.getString(8)));
                // Adding event to list.
                eventList.add(event);
            }
            while (cursor.moveToNext());
        }

        return eventList;
    }

    // Get single event.
    public Event getEvent(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_EVENTS, new String[]{KEY_ID, KEY_TITLE, KEY_DESCRIPTION, KEY_PRICE, KEY_DATE, KEY_LOC_NAME , KEY_START_HOUR, KEY_FAVORITE}, KEY_ID + "=?", new String[]{String.valueOf(id)}, null, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
        }

        return new Event(cursor.getInt(0), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getInt(4), cursor.getString(5) ,cursor.getString(6), cursor.getInt(7));
    }
}
