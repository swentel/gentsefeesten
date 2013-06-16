package com.genschefieste;

import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;

public class GenscheFieste extends Application {

    // URI to the services. In theory we could import 2 other services which describe
    // the locations and categories, however, we have made those available in the resources
    // so they are also easily translatable.
    public static String eventUrl = "";

    // Number of events. Note that this doesn't have to be the exact number, it's just
    // go give a nicer indiction of the progress dialog.
    public static int numberOfEvents = 4045;

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
