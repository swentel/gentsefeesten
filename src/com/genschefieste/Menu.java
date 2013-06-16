package com.genschefieste;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TableRow;

public class Menu extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu);

        // Add listeners on rows.
        TableRow goHome = (TableRow) findViewById(R.id.menu_home);
        goHome.setId(1);
        goHome.setOnClickListener(actionMenu);

        TableRow goFavorites = (TableRow) findViewById(R.id.menu_favorites);
        goFavorites.setId(3);
        goFavorites.setOnClickListener(actionMenu);

        TableRow manageUpdates = (TableRow) findViewById(R.id.menu_settings);
        manageUpdates.setId(3);
        manageUpdates.setOnClickListener(actionMenu);

        TableRow goAbout = (TableRow) findViewById(R.id.menu_about);
        goAbout.setId(4);
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
                    Intent manageUpdates = new Intent(getBaseContext(), ManageUpdate.class);
                    startActivity(manageUpdates);
                    break;
                case 4:
                    Intent about = new Intent(getBaseContext(), About.class);
                    startActivity(about);
                    break;
            }
        }
    };

}
