package com.genschefieste;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TableRow;
import android.widget.Toast;

import java.util.List;

public class MenuList extends Activity {

    Intent goDaysOverview;

    // ID of the public toilet 'event'.
    // Note that this is the external id.
    public static int toiletsId = 2745;

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

        TableRow goLocations = (TableRow) findViewById(R.id.menu_locations);
        goLocations.setId(4);
        goLocations.setOnClickListener(actionMenu);

        /*
        TableRow goFestival = (TableRow) findViewById(R.id.menu_festival);
        goFestival.setId(5);
        goFestival.setOnClickListener(actionMenu);*/

        TableRow goFree = (TableRow) findViewById(R.id.menu_free);
        goFree.setId(6);
        goFree.setOnClickListener(actionMenu);

        TableRow goSearch = (TableRow) findViewById(R.id.menu_search);
        goSearch.setId(7);
        goSearch.setOnClickListener(actionMenu);

        /* Not yet implemented
        TableRow goParking = (TableRow) findViewById(R.id.menu_parking);
        goParking.setId(8);
        goParking.setOnClickListener(actionMenu);

        TableRow goTransport = (TableRow) findViewById(R.id.menu_transport);
        goTransport.setId(9);
        goTransport.setOnClickListener(actionMenu);
        */

        TableRow goToilet = (TableRow) findViewById(R.id.menu_toilets);
        goToilet.setId(10);
        goToilet.setOnClickListener(actionMenu);

        TableRow manageUpdates = (TableRow) findViewById(R.id.menu_settings);
        manageUpdates.setId(11);
        manageUpdates.setOnClickListener(actionMenu);

        TableRow goAbout = (TableRow) findViewById(R.id.menu_about);
        goAbout.setId(12);
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
                // Goes to days, then typeOverview passing categories.
                goDaysOverview = new Intent(getBaseContext(), DaysOverview.class);
                goDaysOverview.putExtra("facetId", 1);
                startActivity(goDaysOverview);
                break;
            case 4:
                // Goes to days, then typeOverview passing locations.
                goDaysOverview = new Intent(getBaseContext(), DaysOverview.class);
                goDaysOverview.putExtra("facetId", 3);
                startActivity(goDaysOverview);
                break;
            case 5:
                Intent goFestivals = new Intent(getBaseContext(), Festivals.class);
                startActivity(goFestivals);
                break;
            case 6:
                // Goes to days, then the list all free events.
                goDaysOverview = new Intent(getBaseContext(), DaysOverview.class);
                goDaysOverview.putExtra("facetId", 2);
                startActivity(goDaysOverview);
                break;
            case 7:
                onSearchRequested();
                break;
            case 8:
                Toast.makeText(MenuList.this, "Parking coming soon", Toast.LENGTH_LONG).show();
                break;
            case 9:
                Toast.makeText(MenuList.this, "Public transport coming soon", Toast.LENGTH_LONG).show();
                break;
            case 10:
                DatabaseHandler db = new DatabaseHandler(MenuList.this);
                String selectQuery = "SELECT * FROM " + DatabaseHandler.TABLE_EVENTS;
                selectQuery += " te LEFT JOIN " + DatabaseHandler.TABLE_FAVORITES + " tf ON te." + DatabaseHandler.EXTERNAL_ID + " = tf." + DatabaseHandler.FAVORITES_KEY_ID + " ";
                selectQuery += " WHERE "+ DatabaseHandler.EXTERNAL_ID +" = " + toiletsId;
                List<Event> events = db.getEvents(selectQuery);
                if (events.size() > 0) {
                    Event event = events.get(0);
                    Intent goToilet = new Intent(getBaseContext(), EventDetail.class);
                    goToilet.putExtra("eventId", event.getId());
                    startActivity(goToilet);
                }
                break;
            case 11:
                Intent goSettings = new Intent(getBaseContext(), Prefs.class);
                startActivity(goSettings);
                break;
            case 12:
                Intent about = new Intent(getBaseContext(), About.class);
                startActivity(about);
                break;
        }
        }
    };

}
