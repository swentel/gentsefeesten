package com.genschefieste;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;

import com.google.analytics.tracking.android.GAServiceManager;
import com.google.analytics.tracking.android.GoogleAnalytics;
import com.google.analytics.tracking.android.Logger;
import com.google.analytics.tracking.android.Tracker;

public class BaseActivity extends Activity implements LocationListener {

    // Variables for this activity.
    public boolean showHomebutton = true;
    public boolean addTopbarListeners = true;
    Intent intent;

    // Version. This is stored in shared preferences so we can trigger an updated
    // and remove favorites.
    public static int version = 1;

    // Location variables.
    public static double longitude = -1;
    public static double latitude = -1;

    // Location manager.
    public static LocationManager locationManager;
    public static boolean geoListening = false;

    // Google analytics.
    private static GoogleAnalytics mGa;
    private static Tracker mTracker;
    private static final String GA_PROPERTY_ID = "UA-1986666-3";
    private static final int GA_DISPATCH_PERIOD = 30;
    private static final Logger.LogLevel GA_LOG_VERBOSITY = Logger.LogLevel.INFO;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Start location listening.
        if (latitude == -1 && longitude == -1) {
            geoListening = true;
            startLocationListening();
        }

        if (addTopbarListeners) {
            // Add listener on menu button.
            ImageButton go_to_menu = (ImageButton) findViewById(R.id.menu_bar_go_to_menu);
            go_to_menu.setId(1);
            go_to_menu.setOnClickListener(topBar);

            // Add listener on favorites button.
            ImageButton go_to_favorites = (ImageButton) findViewById(R.id.menu_bar_go_to_favorites);
            go_to_favorites.setId(2);
            go_to_favorites.setOnClickListener(topBar);
        }

        initializeGa();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        geoListening = true;
    }

    @Override
    protected void onStop() {
        super.onStop();
        geoListening = false;
        locationManager.removeUpdates(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (latitude == -1 && longitude == -1) {
            geoListening = true;
            startLocationListening();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        locationManager.removeUpdates(this);
    }

    @Override
    public void onLocationChanged(Location location) {
        longitude = location.getLongitude();
        latitude = location.getLatitude();
        if (geoListening) {
            locationManager.removeUpdates(this);
            geoListening = false;
        }
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {
    }

    @Override
    public void onProviderEnabled(String s) {
    }

    @Override
    public void onProviderDisabled(String s) {
    }

    /**
     * Start location listening
     */
    public void startLocationListening() {
        geoListening = true;
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
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
        if (showHomebutton) {
            menu.add(Menu.NONE, 0, 0, getString(R.string.menu_home)).setIcon(R.drawable.menu_home);
        }
        menu.add(Menu.NONE, 1, 0, getString(R.string.menu_bar_go_to_menu)).setIcon(R.drawable.menu_menu);
        menu.add(Menu.NONE, 2, 0, getString(R.string.menu_bar_go_to_favorites)).setIcon(R.drawable.menu_favorites);
        menu.add(Menu.NONE, 3, 0, getString(R.string.menu_search)).setIcon(R.drawable.menu_search);
        menu.add(Menu.NONE, 4, 0, getString(R.string.menu_settings)).setIcon(R.drawable.menu_settings);
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
        int[] date_int_resources = context.getResources().getIntArray(R.array.dates_int);
        String[] date_string_resources = context.getResources().getStringArray(R.array.dates_full);

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
    private void initializeGa() {
        mGa = GoogleAnalytics.getInstance(this);
        mTracker = mGa.getTracker(GA_PROPERTY_ID);

        // Set dispatch period.
        GAServiceManager.getInstance().setLocalDispatchPeriod(GA_DISPATCH_PERIOD);

        // Set Logger verbosity.
        mGa.getLogger().setLogLevel(GA_LOG_VERBOSITY);
    }

    /*
    * Returns the Google Analytics tracker.
    */
    public static Tracker getGaTracker() {
        return mTracker;
    }

    /**
     * Send a screen view.
     */
    public static void sendGaView(String action, Context context) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        Boolean sendGa = pref.getBoolean("send_ga", true);
        if (sendGa) {
            //BaseActivity.getGaTracker().set(Fields.SCREEN_NAME, action);
            //BaseActivity.getGaTracker().send(MapBuilder.createAppView().build());
        }
    }
}
