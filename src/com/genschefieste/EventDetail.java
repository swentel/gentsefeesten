package com.genschefieste;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

public class EventDetail extends baseActivity {

    private int eventId;
    private Event event;

    @Override
    public void onCreate(Bundle savedInstanceState) {
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

        // Add listener on share button.
        ImageButton menu = (ImageButton) findViewById(R.id.share);
        menu.setOnClickListener(actionShare);

        super.onCreate(savedInstanceState);
    }

    /**
     * Share listener.
     */
    private final View.OnClickListener actionShare = new View.OnClickListener() {
        public void onClick(View v) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        intent.putExtra(Intent.EXTRA_SUBJECT, event.getTitle());
        intent.putExtra(Intent.EXTRA_TEXT, "I'm going to " +  event.getTitle());
        startActivity(Intent.createChooser(intent, "Share"));
        }
    };
}