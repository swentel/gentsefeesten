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
        TableRow goHome = (TableRow) findViewById(R.id.go_home);
        goHome.setId(1);
        goHome.setOnClickListener(actionMenu);

        TableRow manageUpdates = (TableRow) findViewById(R.id.go_to_manage_updates);
        manageUpdates.setId(2);
        manageUpdates.setOnClickListener(actionMenu);
    }

    /**
     * onClickListener.
     */
    private final View.OnClickListener actionMenu = new View.OnClickListener() {
        public void onClick(View v) {
        switch (v.getId()) {
            case 1:
                Intent goHome = new Intent(getBaseContext(), Main.class);
                startActivity(goHome);
                break;
            case 2:
                Intent manageUpdates = new Intent(getBaseContext(), ManageUpdate.class);
                startActivity(manageUpdates);
                break;
        }
        }
    };

}
