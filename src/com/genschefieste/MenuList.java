package com.genschefieste;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.View;
import android.widget.TableRow;
import android.widget.Toast;

public class MenuList extends BaseActivity {

    Intent goDaysOverview;

    // ID of the public toilet 'event'.
    // Note that this is the external id.
    public static int toiletsId = 2745;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.menu);

        // Do not add topbar listeners.
        addTopbarListeners = false;

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

        TableRow goAroundMe = (TableRow) findViewById(R.id.menu_around_me);
        goAroundMe.setId(6);
        goAroundMe.setOnClickListener(actionMenu);

        TableRow goFree = (TableRow) findViewById(R.id.menu_free);
        goFree.setId(7);
        goFree.setOnClickListener(actionMenu);


        TableRow goSearch = (TableRow) findViewById(R.id.menu_search);
        goSearch.setId(8);
        goSearch.setOnClickListener(actionMenu);

        /* Not yet implemented or needs to be revised.
        TableRow goParking = (TableRow) findViewById(R.id.menu_parking);
        goParking.setId(9);
        goParking.setOnClickListener(actionMenu);

        TableRow goTransport = (TableRow) findViewById(R.id.menu_transport);
        goTransport.setId(10);
        goTransport.setOnClickListener(actionMenu);

        TableRow goToilet = (TableRow) findViewById(R.id.menu_toilets);
        goToilet.setId(11);
        goToilet.setOnClickListener(actionMenu);
        */

        TableRow manageUpdates = (TableRow) findViewById(R.id.menu_settings);
        manageUpdates.setId(12);
        manageUpdates.setOnClickListener(actionMenu);

        TableRow goAbout = (TableRow) findViewById(R.id.menu_about);
        goAbout.setId(13);
        goAbout.setOnClickListener(actionMenu);

        super.onCreate(savedInstanceState);
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
                ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                if ((cm.getActiveNetworkInfo() != null) && cm.getActiveNetworkInfo().isAvailable() && cm.getActiveNetworkInfo().isConnected()) {
                    // We go to a different intermediate screen first, because the
                    // default is just to slow in reaction. We should probably solve this
                    // with an asynctask, but let's get this done first before store release.
                    Intent GoAroundMePre = new Intent(getBaseContext(), AroundMePre.class);
                    startActivity(GoAroundMePre);
                }
                else {
                    Toast.makeText(MenuList.this, getString(R.string.menu_around_me_not_connected), Toast.LENGTH_LONG).show();
                }
                break;
            case 7:
                // Goes to days, then the list all free events.
                goDaysOverview = new Intent(getBaseContext(), DaysOverview.class);
                goDaysOverview.putExtra("facetId", 2);
                startActivity(goDaysOverview);
                break;
            case 8:
                onSearchRequested();
                break;
            case 9:
                Toast.makeText(MenuList.this, "Parking coming soon", Toast.LENGTH_LONG).show();
                break;
            case 10:
                Toast.makeText(MenuList.this, "Public transport coming soon", Toast.LENGTH_LONG).show();
                break;
            /*case 11:
                DatabaseHandler db = new DatabaseHandler(MenuList.this);
                String selectQuery = "SELECT * FROM " + DatabaseHandler.TABLE_EVENTS;
                selectQuery += " te LEFT JOIN " + DatabaseHandler.TABLE_FAVORITES + " tf ON te." + DatabaseHandler.EXTERNAL_ID + " = tf." + DatabaseHandler.FAVORITES_KEY_ID + " ";
                selectQuery += " WHERE "+ DatabaseHandler.EXTERNAL_ID +" = " + toiletsId;
                List<Event> events = db.getEvents(selectQuery, false);
                if (events.size() > 0) {
                    Event event = events.get(0);
                    Intent goToilet = new Intent(getBaseContext(), EventDetail.class);
                    goToilet.putExtra("eventId", event.getId());
                    startActivity(goToilet);
                }
                break;*/
            case 12:
                Intent goSettings = new Intent(getBaseContext(), Prefs.class);
                startActivity(goSettings);
                break;
            case 13:
                Intent about = new Intent(getBaseContext(), About.class);
                startActivity(about);
                break;
        }
        }
    };

}
