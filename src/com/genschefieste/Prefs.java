package com.genschefieste;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.view.Menu;
import android.view.MenuItem;

public class Prefs extends PreferenceActivity {

    Intent intent;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);

        // TODO add manage updates intent + number of events.
    }

    /**
     * Create options menu.
     */
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        menu.add(Menu.NONE, 0, 0, getString(R.string.menu_home)).setIcon(R.drawable.menu_home);
        menu.add(Menu.NONE, 1, 0, getString(R.string.menu_bar_go_to_menu)).setIcon(R.drawable.menu_menu);
        menu.add(Menu.NONE, 2, 0, getString(R.string.menu_bar_go_to_favorites)).setIcon(R.drawable.menu_favorites);
        return true;
    }

    /**
     * Menu item selection.
     */
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 0:
                intent = new Intent(getBaseContext(), Main.class);
                startActivity(intent);
                return true;
            case 1:
                intent = new Intent(getBaseContext(), MenuList.class);
                startActivity(intent);
                return true;
            case 2:
                intent = new Intent(getBaseContext(), Favorites.class);
                startActivity(intent);
                return true;
        }
        return false;
    }
}