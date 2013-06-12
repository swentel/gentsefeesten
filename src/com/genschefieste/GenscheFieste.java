package com.genschefieste;

import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;

import java.util.ArrayList;

public class GenscheFieste extends Application {

    // URI to the services.
    public static String eventUrl = "";
    public static String locationsUrl = "";
    public static String categoryUrl = "";

    // Public list of events which we can access from everywhere.
    public static ArrayList<Event> Events = new ArrayList<Event>();

    // Connectivity manager.
    private ConnectivityManager cm;

    /**
     * onCreate().
     */
    public void onCreate() {
        super.onCreate();
        cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
    }

    /**
     * Helper function to check if network is available.
     */
    public boolean netwerkIsAvailable() {
        if (cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isAvailable() && cm.getActiveNetworkInfo().isConnected()) {
            return true;
        }
        return false;
    }
}
