package com.genschefieste;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

public class EventResultFacetList extends BaseActivity {

    public int facetId;
    public int typeIndex;
    public int typeId;
    public int dateIndex;
    public List<Event> events;
    public int eventId = 0;
    public int timestamp;
    public String date_text;
    public String type_text;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.event_facet_list);

        // Get type and date.
        Bundle extras = getIntent().getExtras();
        facetId = extras.getInt("facetId");
        typeIndex = extras.getInt("typeIndex");
        dateIndex = extras.getInt("dateIndex");

        // Get the list view.
        ListView list = (ListView) findViewById(R.id.list);

        // Set day row text.
        timestamp = GenscheFieste.getTimestampFromIndex(dateIndex, this);
        date_text = GenscheFieste.getDateFromTimestamp(timestamp, this);
        TextView day_row = (TextView) findViewById(R.id.day_row);
        day_row.setText(date_text);

        // Set facet type row text.
        if (facetId == 2) {
            type_text = getString(R.string.event_free);
        }
        else {
            // Categories
            if (facetId == 1) {
                typeId = GenscheFieste.getCategoryIdFromIndex(typeIndex, this);
                type_text = GenscheFieste.getCategoryFromId(typeId, this);
            }
            // Locations.
            else if (facetId == 3) {
                typeId = GenscheFieste.getLocationIdFromIndex(typeIndex, this);
                type_text = GenscheFieste.getLocationFromId(typeId, this);
            }
        }
        TextView type_row = (TextView) findViewById(R.id.type_row);
        type_row.setText(type_text);

        // Get the events.
        DatabaseHandler db = new DatabaseHandler(this);
        String selectQuery = "SELECT * FROM " + DatabaseHandler.TABLE_EVENTS;
        selectQuery += " WHERE " + DatabaseHandler.KEY_DATE + " = " + timestamp;
        // Categories.
        if (facetId == 1) {
            selectQuery += " AND " + DatabaseHandler.KEY_CAT_ID + " = " + typeId;
        }
        // Free events.
        else if (facetId == 2) {
            selectQuery += " AND " + DatabaseHandler.KEY_FREE + " = 1";
        }
        // Locations.
        else if (facetId == 3) {
            selectQuery += " AND " + DatabaseHandler.KEY_LOC_ID + " = " + typeId;
        }
        selectQuery += " ORDER BY " + DatabaseHandler.KEY_DATE_SORT + " ASC, " + DatabaseHandler.KEY_TITLE + " ASC";

        // Debugging.
        if (GenscheFieste.debugMode) {
            Log.d("DebugApp", "TypeIndex" + typeIndex + " typeId " + typeId + " - Q = " + selectQuery);
        }

        events = db.getEvents(selectQuery);

        // Make every item clickable.
        list.setClickable(true);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
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
