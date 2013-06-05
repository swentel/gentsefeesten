package com.genschefieste;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.widget.Toast;

public class EventDetail extends Activity {

    private int eventId;
    private Event event;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event_detail);

        Bundle extras = getIntent().getExtras();
        eventId = extras.getInt("eventId");
        DatabaseHandler db = new DatabaseHandler(this);
        event = db.getEvent(eventId);

        Context context = getApplicationContext();
        CharSequence text = event.getTitle();
        int duration = Toast.LENGTH_LONG;

        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }
}