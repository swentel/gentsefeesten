package com.genschefieste;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import java.util.List;

public class Main extends BaseActivity {

    private List<Event> events;
    private int eventId = 0;
    private String prefLocationId;
    private String prefCategoryId;
    private boolean prefShowAllDay;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.main);

        // Get the list view.
        ListView list = (ListView) findViewById(R.id.list);

        // Default settings are also set in preferences.xml
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        prefLocationId = pref.getString("pref_loc", "230");
        prefCategoryId = pref.getString("pref_cat", "15");
        prefShowAllDay = pref.getBoolean("pref_all_day", false);

        // Get main events.
        DatabaseHandler db = new DatabaseHandler(this);
        String selectQuery = "SELECT * FROM " + DatabaseHandler.TABLE_EVENTS;
        // Location and category.
        // TODO this has to be multiple.
        selectQuery += " WHERE " + DatabaseHandler.KEY_LOC_ID + " = " + prefLocationId;
        selectQuery += " AND " + DatabaseHandler.KEY_CAT_ID + " = " + prefCategoryId;
        // Start hour.
        if (!prefShowAllDay) {
            selectQuery += " AND " + DatabaseHandler.KEY_START_HOUR + " != ''";
        }
        selectQuery += " ORDER BY "+ DatabaseHandler.KEY_DATE +" ASC, "+ DatabaseHandler.KEY_DATE_SORT +" ASC limit 30";
        events = db.getEvents(selectQuery);

        // Make every item clickable.
        list.setClickable(true);
        list.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Intent intent = new Intent(getBaseContext(), EventDetail.class);
            eventId = events.get(position).getId();
            intent.putExtra("eventId", eventId);
            startActivity(intent);
            }
        });

        // Fire the list adapter.
        EventsListAdapter adapter = new EventsListAdapter(this, events);
        list.setAdapter(adapter);

        super.onCreate(savedInstanceState);
    }

}