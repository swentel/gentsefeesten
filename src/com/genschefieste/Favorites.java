package com.genschefieste;

import android.os.Bundle;
import android.widget.ListView;

import java.util.List;

public class Favorites extends BaseActivity {

    public List<Event> events;
    public int eventId = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.favorites);

        BaseActivity.sendGaView("Favorites", getApplicationContext());

        // Get the list view.
        ListView list = (ListView) findViewById(R.id.list);

        // Get your favorites.
        DatabaseHandler db = new DatabaseHandler(this);
        String selectQuery = "SELECT * FROM " + DatabaseHandler.TABLE_EVENTS;
        // Inner join on favorites.
        selectQuery += " te INNER JOIN " + DatabaseHandler.TABLE_FAVORITES + " tf ON te." + DatabaseHandler.EXTERNAL_ID + " = tf." + DatabaseHandler.FAVORITES_KEY_ID + " ";
        selectQuery += " ORDER BY " + DatabaseHandler.KEY_DATE + " ASC, " + DatabaseHandler.KEY_DATE_SORT + " ASC";
        events = db.getEvents(selectQuery, true);

        // Check on size of events. In case there are no events, show the empty
        // view to inform the people how to add favorites.
        if (events.size() == 0) {
            list.setEmptyView(findViewById(R.id.empty));
        }

        // Fire the list adapter.
        FavoritesListAdapter adapter = new FavoritesListAdapter(this, events);
        list.setAdapter(adapter);

        super.onCreate(savedInstanceState);
    }
}
