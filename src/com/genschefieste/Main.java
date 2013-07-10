package com.genschefieste;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class Main extends BaseActivity {

    private List<Event> events;
    //unused private int eventId = 0;
    SharedPreferences pref = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.main);

        pref = PreferenceManager.getDefaultSharedPreferences(this);

        // Hide home button.
        showHomebutton = false;

        // Get the list view.
        ListView list = (ListView) findViewById(R.id.list);

        // Get events.
        events = getMainEvents();

        // Check on size of events. In case there are no events, show the messages
        // row so people can go to settings and change them or import the program.
        // In case the Gentse Feesten is past july 29, show a different message.
        if (events.size() == 0) {
            long unixTime = (System.currentTimeMillis() / 1000L);
            TextView noEvents = (TextView) findViewById(R.id.no_events);
            ViewGroup.LayoutParams params = noEvents.getLayoutParams();
            params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            noEvents.setLayoutParams(params);
            if (unixTime > 1375142400) {
                noEvents.setText(getString(R.string.no_events_after_july_29));
            }
            else {
                noEvents.setOnClickListener(goToPreferences);
                noEvents.setText(getString(R.string.no_events_found_on_home));
            }
        }

        // Fire the list adapter.
        EventsListAdapter adapter = new EventsListAdapter(this, events);
        list.setAdapter(adapter);

        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (pref.getBoolean("firstrun", true)) {
            pref.edit().putBoolean("firstrun", false).commit();
            // Go to prefs activity and start download.
            Intent intent = new Intent(getBaseContext(), Prefs.class);
            intent.putExtra("firstrun", true);
            startActivity(intent);
        }
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
        String limit = pref.getString("pref_front_limit", "40");
        boolean filterOnLocation = pref.getBoolean("filter_loc", false);
        String prefLocationId = pref.getString("pref_loc", "230");
        boolean filterOnCategory = pref.getBoolean("filter_cat", false);
        String prefCategoryId = pref.getString("pref_cat", "15");
        boolean ongoing = pref.getBoolean("pref_front_ongoing", false);

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

        DatabaseHandler db = new DatabaseHandler(this);
        String selectQuery = "SELECT * FROM " + DatabaseHandler.TABLE_EVENTS;
        selectQuery += " te LEFT JOIN " + DatabaseHandler.TABLE_FAVORITES + " tf ON te." + DatabaseHandler.EXTERNAL_ID + " = tf." + DatabaseHandler.FAVORITES_KEY_ID + " ";

        // Current time.
        int addTime = ongoing ? 5400 : 7200;
        long unixTime = (System.currentTimeMillis() / 1000L) + addTime;
        whereClauses.add(DatabaseHandler.KEY_DATE_SORT + " > " + unixTime);

        if (whereClauses.size() > 0) {
            selectQuery += " WHERE ";
            StringBuilder builder = new StringBuilder();
            builder.append(whereClauses.remove(0));
            for( String s : whereClauses) {
                builder.append( " AND ");
                builder.append( s);
            }
            String statement = builder.toString();
            selectQuery += statement;
        }

        selectQuery += " ORDER BY "+ DatabaseHandler.KEY_DATE_SORT +" ASC, " + DatabaseHandler.KEY_TITLE + " ASC limit " + limit;
        events = db.getEvents(selectQuery, false);

        return events;
    }

}