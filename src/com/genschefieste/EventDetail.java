package com.genschefieste;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

public class EventDetail extends BaseActivity {

    private int eventId;
    private Event event;
    private int favorite;
    private int setFavorite;

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

        // Set date.
        // @todo set 'Whole day' in case there's no start hour.
        int date_int = event.getDate();
        String date_text = GenscheFieste.createDate(date_int, getApplicationContext());
        if (date_text.length() > 0) {
            // Period.
            if (event.getDatePeriod().length() > 0) {
                date_text += "\n" + event.getDatePeriod();
            }
            TextView date = (TextView) findViewById(R.id.event_date);
            date.setText(date_text);
        }

        // Set price.
        String price_text = getString(R.string.event_free);
        String price_entry = event.getPrice();
        if (event.getFree() == 1 || price_entry.length() == 0) {
            price_text = getString(R.string.event_free);
        }
        else {
            price_text = "€" + event.getPrice();
            if (event.getPricePresale().length() != 0) {
                price_text += "\n" + getString(R.string.event_pre_sale) + ": € " + event.getPricePresale();
            }
        }
        TextView price = (TextView) findViewById(R.id.event_price);
        price.setText(price_text);

        // Set description.
        // TODO check html/utf-8 stuff in descriptions
        TextView description = (TextView) findViewById(R.id.event_description);
        description.setText(event.getDescription());

        // Set favorite image.
        ImageView i = (ImageView) findViewById(R.id.favorite);
        if (event.getFavorite() == 1) {
            i.setImageResource(R.drawable.fav_on);
        } else {
            i.setImageResource(R.drawable.fav_off);
        }

        // Add listener on share button.
        ImageButton menu = (ImageButton) findViewById(R.id.share);
        menu.setOnClickListener(actionShare);

        // Add listener on favorite button.
        ImageButton favorite = (ImageButton) findViewById(R.id.favorite);
        favorite.setOnClickListener(actionFavorite);

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
        intent.putExtra(Intent.EXTRA_TEXT, R.string.share_pre_message + " " + event.getTitle());
        startActivity(Intent.createChooser(intent, "Share"));
        }
    };

    /**
     * Favorite listener.
     */
    private final View.OnClickListener actionFavorite = new View.OnClickListener() {
        public void onClick(View v) {
            // Get favorite.
            favorite = event.getFavorite();

            // Switch image.
            ImageView i = (ImageView) findViewById(R.id.favorite);
            if (favorite == 0) {
                setFavorite = 1;
                i.setImageResource(R.drawable.fav_on);
            } else {
                setFavorite = 0;
                i.setImageResource(R.drawable.fav_off);
            }

            // Update in database.
            DatabaseHandler db = new DatabaseHandler(getApplicationContext());
            db.saveFavorite(setFavorite, event.getId());

            // Update event in memory as well.
            event.setFavorite(setFavorite);
        }
    };
}