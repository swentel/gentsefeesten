package com.genschefieste;

import android.app.Application;
import android.content.Context;

public class GenscheFieste extends Application {

    // Set to true to get debug statements. Filter on 'DebugApp'.
    public static boolean debugMode = false;

    // URI to the event services. In theory we could import 2 other services which describe
    // the locations and categories, however, we have made those available in the resources
    // so they are also easily translatable.
    public static String eventUrl = "";

    // Parking URI.
    public static String parkingUrl = "http://datatank.gent.be/Mobiliteitsbedrijf/Parkings11.json";

    // Number of events. Note that this doesn't have to be the exact number, it's just
    // go give a nicer indication of the progress dialog.
    public static int numberOfEvents = 4045;

    // ID of the public toilet 'event'.
    // Note that this is the external id.
    public static int toiletsId = 2745;

    /**
     * onCreate().
     */
    public void onCreate() {
        super.onCreate();
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
}
