package com.genschefieste;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

@SuppressLint("Registered")
public class BaseActivity extends AppCompatActivity {

    // Variables for this activity.
    public boolean showHomebutton = true;
    Intent intent;

    // Version. This is stored in shared preferences so we can trigger an updated
    // and remove favorites.
    public static int version = 4;

    // Location variables.
    public static double longitude = -1;
    public static double latitude = -1;

    // Google analytics.
    /*private static GoogleAnalytics mGa;
    private static Tracker mTracker;
    private static final String GA_PROPERTY_ID = "UA-1986666-3";
    private static final int GA_DISPATCH_PERIOD = 30;
    private static final Logger.LogLevel GA_LOG_VERBOSITY = Logger.LogLevel.INFO;*/

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //initializeGa();
    }

    /**
     * Create options menu.
     */
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        if (showHomebutton) {
            menu.add(Menu.NONE, 0, 0, getString(R.string.menu_home)).setIcon(android.R.drawable.ic_menu_today);
        }
        menu.add(Menu.NONE, 1, 0, getString(R.string.menu_bar_go_to_menu)).setIcon(android.R.drawable.ic_menu_help);
        menu.add(Menu.NONE, 2, 0, getString(R.string.menu_bar_go_to_favorites)).setIcon(android.R.drawable.ic_menu_my_calendar);
        menu.add(Menu.NONE, 3, 0, getString(R.string.menu_search)).setIcon(android.R.drawable.ic_menu_search);
        menu.add(Menu.NONE, 4, 0, getString(R.string.menu_settings)).setIcon(android.R.drawable.ic_menu_edit);
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
            case 3:
                onSearchRequested();
                return true;
            case 4:
                intent = new Intent(getBaseContext(), Prefs.class);
                startActivity(intent);
                return true;
        }
        return false;
    }

    /**
     * Returns the text string for a date based on a timestamp.
     */
    public static String getDateFromTimestamp(int timestamp, Context context) {

        String date_text = "";
        int index = -1;
        int[] date_int_resources = context.getResources().getIntArray(R.array.dates_int_all);
        String[] date_string_resources = context.getResources().getStringArray(R.array.dates_full_all);

        for (int i = 0; i < date_int_resources.length; i++) {
            if (date_int_resources[i] == timestamp) {
                index = i;
            }
        }

        if (index != -1) {
            date_text = date_string_resources[index];
        }

        return date_text;
    }

    /**
     * Returns the text string for a date based on a timestamp.
     */
    public static String getCategoryFromId(int id, Context context) {

        String category_text = "";
        int index = -1;
        int[] category_int_resources = context.getResources().getIntArray(R.array.category_ids);
        String[] category_string_resources = context.getResources().getStringArray(R.array.category_strings);

        for (int i = 0; i < category_int_resources.length; i++) {
            if (category_int_resources[i] == id) {
                index = i;
            }
        }

        if (index != -1) {
            category_text = category_string_resources[index];
        }

        return category_text;
    }

    /**
     * Returns the text string for a location based on an id.
     */
    public static String getLocationFromId(int id, Context context) {

        String location_text = "";
        int index = -1;
        int[] location_int_resources = context.getResources().getIntArray(R.array.location_ids);
        String[] location_string_resources = context.getResources().getStringArray(R.array.location_strings);

        for (int i = 0; i < location_int_resources.length; i++) {
            if (location_int_resources[i] == id) {
                index = i;
            }
        }

        if (index != -1) {
            location_text = location_string_resources[index];
        }

        return location_text;
    }

    /**
     * Returns the timestamp of a date based on index.
     */
    public static int getTimestampFromIndex(int dateIndex, Context context) {
        int[] date_int_resources = context.getResources().getIntArray(R.array.dates_int);
        return date_int_resources[dateIndex];
    }

    /**
     * Returns the id of a category based on index.
     */
    public static int getCategoryIdFromIndex(int index, Context context) {
        int[] category_int_resources = context.getResources().getIntArray(R.array.category_ids);
        return category_int_resources[index];
    }

    /**
     * Returns the id of a location based on index.
     */
    public static int getLocationIdFromIndex(int index, Context context) {
        int[] location_int_resources = context.getResources().getIntArray(R.array.location_ids);
        return location_int_resources[index];
    }

    /**
     * Initialize Google Analytics tracker.
     */
    /*private void initializeGa() {
        mGa = GoogleAnalytics.getInstance(this);
        mTracker = mGa.getTracker(GA_PROPERTY_ID);

        // Set dispatch period.
        GAServiceManager.getInstance().setLocalDispatchPeriod(GA_DISPATCH_PERIOD);

        // Set Logger verbosity.
        mGa.getLogger().setLogLevel(GA_LOG_VERBOSITY);
    }*/

    /*
    * Returns the Google Analytics tracker.
    */
    /*public static Tracker getGaTracker() {
        return mTracker;
    }*/

    /**
     * Send a screen view.
     */
    /*public static void sendGaView(String action, Context context) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        Boolean sendGa = pref.getBoolean("send_ga", true);
        if (sendGa) {
            BaseActivity.getGaTracker().set(Fields.SCREEN_NAME, action);
            BaseActivity.getGaTracker().send(MapBuilder.createAppView().build());
        }
    }*/
}
