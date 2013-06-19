package com.genschefieste;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class Main extends BaseActivity {

    private List<Event> events;
    private int eventId = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.main);

        // Get the list view.
        ListView list = (ListView) findViewById(R.id.list);

        // Get events.
        events = getMainEvents();

        // Check on size of events. In case there are no events, show the messages
        // row so people can go to settings and change them or import the program.
        if (events.size() == 0) {
            TextView noEvents = (TextView) findViewById(R.id.no_events);
            noEvents.setOnClickListener(goToPreferences);
            ViewGroup.LayoutParams params = noEvents.getLayoutParams();
            params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            noEvents.setLayoutParams(params);
        }

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

    /**
     * goToPreferences button listener.
     */
    private final View.OnClickListener goToPreferences = new View.OnClickListener() {
        public void onClick(View v) {
        Intent preferences = new Intent(getBaseContext(), Prefs.class);
        startActivity(preferences);
        }
    };

    /**
     * Get events.
     */
    public List<Event> getMainEvents() {

        // Get settings for main query.
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        boolean filterOnLocation = pref.getBoolean("filter_loc", false);
        String prefLocationId = pref.getString("pref_loc", "230");
        boolean filterOnCategory = pref.getBoolean("filter_cat", false);
        String prefCategoryId = pref.getString("pref_cat", "15");
        boolean prefShowAllDay = pref.getBoolean("pref_all_day", false);

        // Create query.
        List<String> whereClauses = new ArrayList<String>();
        // Location and category.
        if (filterOnLocation || filterOnCategory) {
            if (filterOnLocation) {
                whereClauses.add(DatabaseHandler.KEY_LOC_ID + " = " + prefLocationId);
            }
            if (filterOnCategory) {
                whereClauses.add(DatabaseHandler.KEY_CAT_ID + " = " + prefCategoryId);
            }
        }
        // Start hour.
        if (!prefShowAllDay) {
            whereClauses.add(DatabaseHandler.KEY_START_HOUR + " != ''");
        }

        DatabaseHandler db = new DatabaseHandler(this);
        String selectQuery = "SELECT * FROM " + DatabaseHandler.TABLE_EVENTS;
        selectQuery += " te LEFT JOIN " + DatabaseHandler.TABLE_FAVORITES + " tf ON te." + DatabaseHandler.EXTERNAL_ID + " = tf." + DatabaseHandler.FAVORITES_KEY_ID + " ";
        // This will actually not always be correct since the order date
        // colum contains hours like 2500, but their timestamp is from a day earlier.
        Calendar c = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        long unixTimeStamp = c.getTimeInMillis() / 1000;

        SimpleDateFormat currentTime = new SimpleDateFormat("hhmm");
        String now = currentTime.format(new Date());
        selectQuery += " WHERE "+ DatabaseHandler.KEY_DATE +" >= " + unixTimeStamp + " AND " + DatabaseHandler.KEY_DATE_SORT + " >= " + now + " AND ";

        if (whereClauses.size() > 0) {
            StringBuilder builder = new StringBuilder();
            builder.append(whereClauses.remove(0));
            for( String s : whereClauses) {
                builder.append( " AND ");
                builder.append( s);
            }
            String statement = builder.toString();
            selectQuery += statement;
        }
        selectQuery += " ORDER BY "+ DatabaseHandler.KEY_DATE +" ASC, "+ DatabaseHandler.KEY_DATE_SORT +" ASC limit 30";
        events = db.getEvents(selectQuery);

        return events;
    }

}