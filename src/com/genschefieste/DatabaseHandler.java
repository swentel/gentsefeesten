package com.genschefieste;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
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
    private static final String TABLE_EVENTS = "events";

    // Events table columns names.
    private static final String KEY_ID = "id";
    private static final String EXTERNAL_ID = "external_id";
    private static final String KEY_TITLE = "name";
    private static final String KEY_PRICE = "price";
    private static final String KEY_DATE = "date";
    private static final String KEY_START_HOUR = "start_hour";
    private static final String KEY_FAVORITE = "favorite";

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE_EVENTS + "(" +
                "" + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                "" + EXTERNAL_ID + " INTEGER," +
                "" + KEY_TITLE + " TEXT," +
                "" + KEY_PRICE + " TEXT, " +
                "" + KEY_DATE + " TEXT," +
                "" + KEY_START_HOUR + " TEXT," +
                "" + KEY_FAVORITE + " INTEGER" +
                ")";
        db.execSQL(CREATE_CONTACTS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // No upgrades.
    }

    // Insert or update event.
    public void insertEvent(Event event) {
        SQLiteDatabase db = this.getWritableDatabase();
        //db.execSQL("INSERT INTO " + TABLE_EVENTS + " (name, price, date, start_hour, favorite) VALUES " +
        //        "(\""+ event.getTitle() +"\", \"Gratis\", \"Zondag 17 juli\", \"14u\", 0)");

        ContentValues values = new ContentValues();
        values.put(KEY_TITLE, event.getTitle());
        values.put(KEY_PRICE, "GRATIS");
        values.put(KEY_DATE, "Zondag 17 juli");
        values.put(KEY_START_HOUR, "14u");
        values.put(KEY_FAVORITE, 0);

        db.insert(TABLE_EVENTS, null, values);
        Log.d("Inserted event", event.getTitle());
        db.close();
    }

    // Get all events.
    public List<Event> getAllEvents() {
        List<Event> eventList = new ArrayList<Event>();

        // Select All Query.
        String selectQuery = "SELECT  * FROM " + TABLE_EVENTS + " ORDER BY RANDOM() LIMIT 0,7";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // Loop through all rows and add to list.
        if (cursor.moveToFirst()) {
            do {
                Event event = new Event();
                event.setID(Integer.parseInt(cursor.getString(0)));
                event.setTitle(cursor.getString(2));
                event.setPrice(cursor.getString(3));
                event.setDate(cursor.getString(4));
                event.setStartHour(cursor.getString(5));
                event.setFavorite(Integer.parseInt(cursor.getString(6)));
                // Adding event to list.
                eventList.add(event);
            }
            while (cursor.moveToNext());
        }

        return eventList;
    }

    // Get single event.
    public Event getevent(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_EVENTS, new String[]{KEY_ID, KEY_TITLE, KEY_PRICE, KEY_DATE, KEY_START_HOUR, KEY_FAVORITE}, KEY_ID + "=?", new String[]{String.valueOf(id)}, null, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
        }

        return new Event(Integer.parseInt(cursor.getString(0)), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4), Integer.parseInt(cursor.getString(0)));
    }
}
