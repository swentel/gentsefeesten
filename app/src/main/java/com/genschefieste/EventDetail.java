package com.genschefieste;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

public class EventDetail extends BaseActivity {

    // Event and shared preference.
    private Event event;
    SharedPreferences pref = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.event_detail);

        pref = PreferenceManager.getDefaultSharedPreferences(EventDetail.this);

        // Get event.
        Bundle extras = getIntent().getExtras();
        assert extras != null;
        int eventId = extras.getInt("eventId");
        DatabaseHandler db = new DatabaseHandler(this);
        event = db.getEvent(eventId);

        // Set title.
        TextView title = findViewById(R.id.event_title);
        title.setText(event.getTitle());
        this.setTitle(event.getTitle());

        // Set location.
        TextView location = findViewById(R.id.event_location);
        location.setText(event.getLocation());

        // Set date.
        int date_int = event.getDate();
        String date_text = getDateFromTimestamp(date_int, getApplicationContext());
        if (date_text.length() > 0) {
            // Period.
            if (event.getDatePeriod().length() > 0) {
                date_text += "\n" + event.getDatePeriod();
            }
            TextView date = findViewById(R.id.event_date);
            date.setText(date_text);
        }

        // Set price.
        String price_text;
        String price_entry = event.getPrice();
        if (event.getFree() == 1 || price_entry.length() == 0) {
            price_text = getString(R.string.event_free);
        }
        else {
            price_text = event.getPrice();
            if (event.getPricePresale().length() != 0) {
                price_text += "\n" + getString(R.string.event_pre_sale) + ": € " + event.getPricePresale();
            }
        }
        TextView price = findViewById(R.id.event_price);
        price.setText(price_text);

        // Category.
        TextView category = findViewById(R.id.event_category);
        category.setText(event.getCategory());

        // Set description and URL.
        String descriptionText = "";
        TextView description = findViewById(R.id.event_description);
        if (event.getDescription().length() > 0) {
            descriptionText += event.getDescription() + "\n";
        }
        if (event.getUrl().length() > 0) {
            descriptionText += "\n" + event.getUrl();
        }
        description.setText(descriptionText);

        // Set favorite image and onclickListener.
        ImageView i = findViewById(R.id.favorite);
        if (event.getFavorite() == 0) {
            i.setImageResource(R.drawable.fav_off);
        } else {
            i.setImageResource(R.drawable.fav_on);
        }
        i.setOnClickListener(actionFavorite);


        ImageButton goOnlineButton = findViewById(R.id.external_link);
        //ImageButton map = findViewById(R.id.map);
        goOnlineButton.setOnClickListener(actionOnline);
        //map.setOnClickListener(actionMap);

        // Add listener on share button.
        //ImageButton menu = findViewById(R.id.share);
        //menu.setOnClickListener(actionShare);

        // Media.
        /*String media = event.getMedia();
        ImageView eventImage = findViewById(R.id.event_image);
        boolean loadImages = pref.getBoolean("pref_load_images", false);
        if (loadImages && hasConnection() && media.length() > 0) {
            Glide.with(this)
                    .load(media)
                    .into(eventImage);
        }
        else {
            eventImage.setVisibility(ImageView.GONE);
        }*/

        super.onCreate(savedInstanceState);
    }

    /**
     * Map listener.
     */
    /*private final View.OnClickListener actionMap = new View.OnClickListener() {
        public void onClick(View v) {
        Intent goMap = new Intent(getBaseContext(), MapBase.class);
        goMap.putExtra("eventId", event.getId());
        goMap.putExtra("latitude", latitude);
        goMap.putExtra("longitude", longitude);
        startActivity(goMap);
        }
    };*/

    /**
     * Go online listener.
     */
    private final View.OnClickListener actionOnline = new View.OnClickListener() {
        public void onClick(View v) {
        Uri url = Uri.parse("http://gentsefeesten.be/event/" + event.getExternalId());
        if (event.getUrl().length() > 0) {
            url = Uri.parse(event.getUrl());
        }
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, url);
        startActivity(browserIntent);
        }
    };

    /**
     * Share listener.
     */
    /*private final View.OnClickListener actionShare = new View.OnClickListener() {
        public void onClick(View v) {
            String share_message = pref.getString("share_message", "");

            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
            intent.putExtra(Intent.EXTRA_SUBJECT, event.getTitle());
            intent.putExtra(Intent.EXTRA_TEXT, share_message.replace("!replace", event.getTitle()));
            startActivity(Intent.createChooser(intent, "Share"));
        }
    };*/

    /**
     * Favorite listener.
     */
    private final View.OnClickListener actionFavorite = new View.OnClickListener() {
        public void onClick(View v) {
        // Get favorite.
        int favorite = event.getFavorite();

        // Switch image.
        ImageView i = findViewById(R.id.favorite);
        int setFavorite;
        if (favorite == 0) {
            setFavorite = 1;
            i.setImageResource(R.drawable.fav_on);
        } else {
            setFavorite = 0;
            i.setImageResource(R.drawable.fav_off);
        }

        // Update in database.
        DatabaseHandler db = new DatabaseHandler(getApplicationContext());
        db.saveFavorite(setFavorite, event.getExternalId());

        // Update event in memory as well.
        event.setFavorite(setFavorite);
        }
    };
}