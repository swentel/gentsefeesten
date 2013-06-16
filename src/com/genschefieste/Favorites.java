package com.genschefieste;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.List;

public class Favorites extends baseActivity {

    public List<Event> events;
    public int eventId = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.favorites);

        // Get the list view.
        ListView list = (ListView) findViewById(R.id.list);

        // Get your favorites.
        DatabaseHandler db = new DatabaseHandler(this);
        events = db.getFavoriteEvents();

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
