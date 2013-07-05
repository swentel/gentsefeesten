package com.genschefieste;

import android.app.SearchManager;
import android.content.Intent;
import android.database.DatabaseUtils;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

public class EventSearch extends BaseActivity {

    public List<Event> events;
    public int eventId = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.search);
        handleIntent(getIntent());
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);

            // Get the list view.
            ListView list = (ListView) findViewById(R.id.list);

            // Get events.
            DatabaseHandler db = new DatabaseHandler(this);
            String escaped_query = DatabaseUtils.sqlEscapeString("%" + query + "%");
            String selectQuery = "SELECT * FROM " + DatabaseHandler.TABLE_EVENTS;
            selectQuery += " te LEFT JOIN " + DatabaseHandler.TABLE_FAVORITES + " tf ON te." + DatabaseHandler.EXTERNAL_ID + " = tf." + DatabaseHandler.FAVORITES_KEY_ID + " WHERE ";
            selectQuery += DatabaseHandler.KEY_TITLE + " LIKE " + escaped_query + " OR ";
            selectQuery += DatabaseHandler.KEY_LOC_NAME + " LIKE " + escaped_query + " OR ";
            selectQuery += DatabaseHandler.KEY_CAT_NAME + " LIKE " + escaped_query + " OR ";
            selectQuery += DatabaseHandler.KEY_DESCRIPTION + " LIKE " + escaped_query + " ";
            selectQuery += " ORDER BY " + DatabaseHandler.KEY_DATE + " ASC, " + DatabaseHandler.KEY_DATE_SORT + " ASC";
            // In case the search word is only two or less characters, limit it, so we don't
            // freeze the thread. It's also kind of useless anyway.
            if (query.length() <= 2) {
                selectQuery += " limit 100";
            }
            events = db.getEvents(selectQuery, true);

            // Check on size of events. In case there are no events, add
            // a listener on the empty list text and remove the list.
            // Also add a listener so one can easily restart a search.
            if (events.size() == 0) {
                list.setEmptyView(findViewById(R.id.empty));
                TextView noEvents = (TextView) findViewById(R.id.empty);
                noEvents.setOnClickListener(openSearch);
            }

            // Fire the list adapter.
            SearchListAdapter adapter = new SearchListAdapter(this, events);
            list.setAdapter(adapter);
        }
    }

    /**
     * openSearch button listener.
     */
    private final View.OnClickListener openSearch = new View.OnClickListener() {
        public void onClick(View v) {
            onSearchRequested();
        }
    };
}
