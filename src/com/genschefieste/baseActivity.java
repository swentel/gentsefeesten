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
        ImageButton menu = (ImageButton) findViewById(R.id.go_to_menu);
        menu.setId(1);
        menu.setOnClickListener(actionMain);
    }

    /**
     * Button listener.
     */
    private final View.OnClickListener actionMain = new View.OnClickListener() {
        public void onClick(View v) {
            switch (v.getId()) {
                case 1:
                    Intent menu = new Intent(getBaseContext(), Menu.class);
                    startActivity(menu);
                    break;
            }
        }
    };

}
