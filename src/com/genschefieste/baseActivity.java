package com.genschefieste;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

public class baseActivity extends Activity {

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
     * Button listener.
     */
    private final View.OnClickListener topBar = new View.OnClickListener() {
        public void onClick(View v) {
        switch (v.getId()) {
            case 1:
                Intent menu = new Intent(getBaseContext(), Menu.class);
                startActivity(menu);
                break;
            case 2:
                Intent favorites = new Intent(getBaseContext(), Favorites.class);
                startActivity(favorites);
                break;
        }
        }
    };

}
