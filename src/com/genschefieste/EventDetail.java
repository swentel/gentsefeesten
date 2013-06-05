package com.genschefieste;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class EventDetail extends Activity {

    private int eventId;
    private Event event;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event_detail);

        // Get event.
        Bundle extras = getIntent().getExtras();
        eventId = extras.getInt("eventId");
        DatabaseHandler db = new DatabaseHandler(this);
        event = db.getEvent(eventId);

        // Set title.
        TextView title = (TextView) findViewById(R.id.event_title);
        title.setText(event.getTitle());

        // Set location.
        TextView location = (TextView) findViewById(R.id.event_location);
        location.setText(event.getLocation());

        // Set description.
        TextView description = (TextView) findViewById(R.id.event_description);
        description.setText(event.getDescription());
    }
}