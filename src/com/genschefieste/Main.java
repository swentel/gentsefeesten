package com.genschefieste;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.ListView;

import java.util.List;

public class Main extends Activity {

    public List<Event> events;
    public int eventId = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        // Get the list view.
        ListView list = (ListView) findViewById(R.id.list);

        // Get all events.
        DatabaseHandler db = new DatabaseHandler(this);
        events = db.getAllEvents();

        // Make every item clickable.
        list.setClickable(true);
        list.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getBaseContext(), EventDetail.class);
                eventId = events.get(position).getID();
                intent.putExtra("eventId", eventId);
                startActivity(intent);
            }
        });

        // Fire the list adapter.
        EventsListAdapter adapter = new EventsListAdapter(this, events);
        list.setAdapter(adapter);

        // Add listener on menu button.
        ImageButton menu = (ImageButton) findViewById(R.id.go_to_menu);
        menu.setId(1);
        menu.setOnClickListener(actionMain);
    }

    /**
     * Button listener.
     */
    private final View.OnClickListener actionMain = new View.OnClickListener() {
        public void onClick(View v) {
        switch (v.getId()) {
            case 1:
                Intent manageUpdate = new Intent(getBaseContext(), ManageUpdate.class);
                startActivity(manageUpdate);
                break;
        }
        }
    };

}