package com.genschefieste;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TableRow;

import java.util.List;

public class MenuList extends Activity {

    Intent goDaysOverview;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu);

        // Add listeners on rows.
        TableRow goHome = (TableRow) findViewById(R.id.menu_home);
        goHome.setId(1);
        goHome.setOnClickListener(actionMenu);

        TableRow goFavorites = (TableRow) findViewById(R.id.menu_favorites);
        goFavorites.setId(2);
        goFavorites.setOnClickListener(actionMenu);

        TableRow goCategories = (TableRow) findViewById(R.id.menu_categories);
        goCategories.setId(3);
        goCategories.setOnClickListener(actionMenu);

        TableRow goFree = (TableRow) findViewById(R.id.menu_free);
        goFree.setId(4);
        goFree.setOnClickListener(actionMenu);

        TableRow goLocations = (TableRow) findViewById(R.id.menu_locations);
        goLocations.setId(5);
        goLocations.setOnClickListener(actionMenu);

        TableRow goSearch = (TableRow) findViewById(R.id.menu_search);
        goSearch.setId(6);
        goSearch.setOnClickListener(actionMenu);

        TableRow goToilet = (TableRow) findViewById(R.id.menu_toilets);
        goToilet.setId(8);
        goToilet.setOnClickListener(actionMenu);

        TableRow manageUpdates = (TableRow) findViewById(R.id.menu_settings);
        manageUpdates.setId(9);
        manageUpdates.setOnClickListener(actionMenu);

        TableRow goAbout = (TableRow) findViewById(R.id.menu_about);
        goAbout.setId(10);
        goAbout.setOnClickListener(actionMenu);
    }

    /**
     * Rows onClickListener.
     */
    private final View.OnClickListener actionMenu = new View.OnClickListener() {
        public void onClick(View v) {
        switch (v.getId()) {
            case 1:
                Intent goHome = new Intent(getBaseContext(), Main.class);
                startActivity(goHome);
                break;
            case 2:
                Intent goFavorites = new Intent(getBaseContext(), Favorites.class);
                startActivity(goFavorites);
                break;
            case 3:
                // Goes to days first, then typeOverview passing categories.
                goDaysOverview = new Intent(getBaseContext(), DaysOverview.class);
                goDaysOverview.putExtra("facetId", 1);
                startActivity(goDaysOverview);
                break;
            case 4:
                // Goes to days first, then results.
                goDaysOverview = new Intent(getBaseContext(), DaysOverview.class);
                goDaysOverview.putExtra("facetId", 2);
                startActivity(goDaysOverview);
                break;
            case 5:
                // Goes to days first, then typeOverview passing locations.
                goDaysOverview = new Intent(getBaseContext(), DaysOverview.class);
                goDaysOverview.putExtra("facetId", 3);
                startActivity(goDaysOverview);
                break;
            case 6:
                onSearchRequested();
                break;
            case 8:
                DatabaseHandler db = new DatabaseHandler(MenuList.this);
                String selectQuery = "SELECT * FROM " + DatabaseHandler.TABLE_EVENTS;
                selectQuery += " te LEFT JOIN " + DatabaseHandler.TABLE_FAVORITES + " tf ON te." + DatabaseHandler.EXTERNAL_ID + " = tf." + DatabaseHandler.FAVORITES_KEY_ID + " ";
                selectQuery += " WHERE "+ DatabaseHandler.EXTERNAL_ID +" = " + GenscheFieste.toiletsId;
                List<Event> events = db.getEvents(selectQuery);
                if (events.size() > 0) {
                    Event event = events.get(0);
                    Intent goToilet = new Intent(getBaseContext(), EventDetail.class);
                    goToilet.putExtra("eventId", event.getId());
                    startActivity(goToilet);
                }
                break;
            case 9:
                Intent goSettings = new Intent(getBaseContext(), Prefs.class);
                startActivity(goSettings);
                break;
            case 10:
                Intent about = new Intent(getBaseContext(), About.class);
                startActivity(about);
                break;
        }
        }
    };

}
