package com.genschefieste;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TableRow;

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

        // TODO add parking
        // TODO add toilets - we can always go search for toilets ;)

        TableRow manageUpdates = (TableRow) findViewById(R.id.menu_settings);
        manageUpdates.setId(8);
        manageUpdates.setOnClickListener(actionMenu);

        TableRow goAbout = (TableRow) findViewById(R.id.menu_about);
        goAbout.setId(9);
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
                Intent goSettings = new Intent(getBaseContext(), Prefs.class);
                startActivity(goSettings);
                break;
            case 9:
                Intent about = new Intent(getBaseContext(), About.class);
                startActivity(about);
                break;
        }
        }
    };

}
