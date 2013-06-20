package com.genschefieste;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

public class Favorites extends BaseActivity {

    public List<Event> events;
    public int eventId = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.favorites);

        // Disable favorites button.
        disableFavoritesButton = true;

        // Get the list view.
        ListView list = (ListView) findViewById(R.id.list);

        // Get your favorites.
        DatabaseHandler db = new DatabaseHandler(this);
        String selectQuery = "SELECT * FROM " + DatabaseHandler.TABLE_EVENTS;
        selectQuery += " te INNER JOIN " + DatabaseHandler.TABLE_FAVORITES + " tf ON te." + DatabaseHandler.EXTERNAL_ID + " = tf." + DatabaseHandler.FAVORITES_KEY_ID + " ";
        selectQuery += " ORDER BY " + DatabaseHandler.KEY_DATE + " ASC, " + DatabaseHandler.KEY_DATE_SORT + " ASC";
        events = db.getEvents(selectQuery);

        // Check on size of events. In case there are no events, show the messages
        // row to inform the people how to add favorites.
        if (events.size() == 0) {
            TextView noEvents = (TextView) findViewById(R.id.no_events);
            ViewGroup.LayoutParams params = noEvents.getLayoutParams();
            params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            noEvents.setLayoutParams(params);
        }

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
        FavoritesListAdapter adapter = new FavoritesListAdapter(this, events);
        list.setAdapter(adapter);

        super.onCreate(savedInstanceState);
    }
}
