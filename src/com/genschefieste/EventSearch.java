package com.genschefieste;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.List;

public class EventSearch extends Activity {

    public List<Event> events;
    public int eventId = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search);
        handleIntent(getIntent());
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
            // TODO add sorting
            DatabaseHandler db = new DatabaseHandler(this);
            String selectQuery = "SELECT * FROM " + DatabaseHandler.TABLE_EVENTS;
            if (query.length() > 0) {
                selectQuery += " AND (" + DatabaseHandler.KEY_TITLE + " LIKE '%" + query + "%'";
                selectQuery += " OR " + DatabaseHandler.KEY_DESCRIPTION + " LIKE '%" + query + "%') ";
            }
            selectQuery += " ORDER BY random() limit 30";
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
