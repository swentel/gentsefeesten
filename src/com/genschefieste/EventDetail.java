package com.genschefieste;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.widget.Toast;

public class EventDetail extends Activity {

    private int position;
    private int id;
    private Event event;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event_detail);

        Bundle extras = getIntent().getExtras();
        position = extras.getInt("index");
        id = position + 1;
        DatabaseHandler db = new DatabaseHandler(this);
        event = db.getevent(id);

        Context context = getApplicationContext();
        CharSequence text = event.getTitle();
        int duration = Toast.LENGTH_LONG;

        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }
}