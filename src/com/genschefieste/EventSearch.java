package com.genschefieste;

import android.app.SearchManager;
import android.content.Intent;
import android.database.DatabaseUtils;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

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
            String selectQuery = "SELECT * FROM " + DatabaseHandler.TABLE_EVENTS + " WHERE ";
            selectQuery += DatabaseHandler.KEY_TITLE + " LIKE " + escaped_query + " OR ";
            selectQuery += DatabaseHandler.KEY_LOC_NAME + " LIKE " + escaped_query + " OR ";
            selectQuery += DatabaseHandler.KEY_CAT_NAME + " LIKE " + escaped_query + " OR ";
            selectQuery += DatabaseHandler.KEY_DESCRIPTION + " LIKE " + escaped_query + " ";
            selectQuery += "ORDER BY " + DatabaseHandler.KEY_TITLE + " ASC ";
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
        }
    }
}
