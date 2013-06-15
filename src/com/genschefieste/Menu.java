package com.genschefieste;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableRow;

public class Menu extends Activity {

    String search_free = "";
    ProgressDialog dialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu);

        // Add listeners on rows.
        TableRow goHome = (TableRow) findViewById(R.id.menu_home);
        goHome.setId(1);
        goHome.setOnClickListener(actionMenu);

        TableRow manageUpdates = (TableRow) findViewById(R.id.menu_settings);
        manageUpdates.setId(2);
        manageUpdates.setOnClickListener(actionMenu);

        // Add listener on search button.
        Button search = (Button) findViewById(R.id.search_button);
        search.setOnClickListener(onSearch);
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
                Intent manageUpdates = new Intent(getBaseContext(), ManageUpdate.class);
                startActivity(manageUpdates);
                break;
        }
        }
    };

    /**
     * search OnClickListener.
     */
    private final View.OnClickListener onSearch = new View.OnClickListener() {
        public void onClick(View v) {

            // Get the input from the form.
            EditText search_free_edit = (EditText) findViewById(R.id.search);
            search_free = search_free_edit.getText().toString();

            dialog = ProgressDialog.show(Menu.this, getString(R.string.searching), getString(R.string.please_wait), true);
            new searchTask().execute();
        }
    };

    /**
     * Search task.
     */
    class searchTask extends AsyncTask<Void, Void, String> {

        protected String doInBackground(Void... unused) {
            String sResponse = "";

            return sResponse;
        }

        protected void onPostExecute(String sResponse) {
            /*common.searchPostExecute(getApplicationContext(), sResponse, dialog);
            if (Common.response_success == Common.SUCCESS) {
                // Parse restaurants.
                Common.ParseRestaurants(RestaurantForm.this);
                // Start new intent.
                Intent intent = new Intent(RestaurantForm.this, EventResults.class);
                startActivity(intent);
            }*/
        }
    }
}
