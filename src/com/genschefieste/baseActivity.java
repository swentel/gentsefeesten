package com.genschefieste;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;

public class BaseActivity extends Activity {

    Intent intent;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Add listener on menu button.
        ImageButton go_to_menu = (ImageButton) findViewById(R.id.menu_bar_go_to_menu);
        go_to_menu.setId(1);
        go_to_menu.setOnClickListener(topBar);

        // Add listener on favorites button.
        ImageButton go_to_favorites = (ImageButton) findViewById(R.id.menu_bar_go_to_favorites);
        go_to_favorites.setId(2);
        go_to_favorites.setOnClickListener(topBar);
    }

    /**
     * topBar button listener.
     */
    private final View.OnClickListener topBar = new View.OnClickListener() {
        public void onClick(View v) {
        switch (v.getId()) {
            case 1:
                Intent menu = new Intent(getBaseContext(), MenuList.class);
                startActivity(menu);
                break;
            case 2:
                Intent favorites = new Intent(getBaseContext(), Favorites.class);
                startActivity(favorites);
                break;
        }
        }
    };

    /**
     * Create options menu.
     */
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        menu.add(Menu.NONE, 0, 0, getString(R.string.menu_home)).setIcon(R.drawable.menu_home);
        menu.add(Menu.NONE, 1, 0, getString(R.string.menu_bar_go_to_menu)).setIcon(R.drawable.menu_menu);
        menu.add(Menu.NONE, 2, 0, getString(R.string.menu_bar_go_to_favorites)).setIcon(R.drawable.menu_favorites);
        menu.add(Menu.NONE, 3, 0, getString(R.string.menu_settings)).setIcon(R.drawable.menu_settings);
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
