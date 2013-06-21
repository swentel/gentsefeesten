package com.genschefieste;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

public class Prefs extends PreferenceActivity {

    ProgressDialog dialog;
    public static int siteStatus = 200;
    public static InputStream jsonfile = null;
    public static JsonReader reader = null;
    public static String sResponse = "";

    // Number of events. Note that this doesn't have to be the exact number, it's just
    // go give a nicer indication of the progress dialog.
    public static int numberOfEvents = 4050;

    // URI to the event services. In theory we could import 2 other services which describe
    // the locations and categories, however, we have made those available in the resources
    // so they are also easily translatable.
    public static String eventUrl = "";

    // Connectivity manager.
    private ConnectivityManager cm;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        // Keep screen alive here for the updates.
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);

        // Get connectivity manager.
        cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        Preference button = findPreference("get_updates");
        assert button != null;

        // Set summary.
        String summary = getString(R.string.updating_info);
        DatabaseHandler db = new DatabaseHandler(this);
        int total = db.getEventCount();
        summary += " " + total;
        button.setSummary(summary);

        // Add listener.
        button.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference arg0) {
                // Make sure we are connected to the internet.
                if ((cm.getActiveNetworkInfo() != null) && cm.getActiveNetworkInfo().isAvailable() && cm.getActiveNetworkInfo().isConnected()) {

                    dialog = new customProgressDialog(Prefs.this);
                    dialog.setTitle(R.string.updating);
                    dialog.setMessage(getString(R.string.tour_step_1_info));
                    dialog.setIndeterminate(false);
                    dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                    dialog.show();
                    new updateTask().execute();
                }
                else {
                    Toast.makeText(getApplicationContext(), getString(R.string.update_offline), Toast.LENGTH_LONG).show();
                }

                return true;
            }
        });
    }

    /**
     * Create options menu.
     */
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        menu.add(Menu.NONE, 0, 0, getString(R.string.menu_home)).setIcon(R.drawable.menu_home);
        menu.add(Menu.NONE, 1, 0, getString(R.string.menu_bar_go_to_menu)).setIcon(R.drawable.menu_menu);
        menu.add(Menu.NONE, 2, 0, getString(R.string.menu_bar_go_to_favorites)).setIcon(R.drawable.menu_favorites);
        return true;
    }

    /**
     * Menu item selection.
     */
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 0:
                Intent intent = new Intent(getBaseContext(), Main.class);
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

    public int downloadProgram() throws IOException {
        siteStatus = 0;

        try {
            HttpClient httpClient = new DefaultHttpClient();
            HttpGet request = new HttpGet(eventUrl);
            HttpResponse response = httpClient.execute(request);

            int status = response.getStatusLine().getStatusCode();
            siteStatus = status;
            if (status == HttpStatus.SC_OK) {
                // Read in the data.
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                response.getEntity().writeTo(outputStream);

                // Write data to local file.
                FileOutputStream fos = openFileOutput("events.json", Context.MODE_PRIVATE);
                fos.write(outputStream.toByteArray());
                fos.flush();
                fos.close();
            }

        }
        catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        catch (ClientProtocolException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        return siteStatus;
    }

    /**
     * Update task.
     */
    class updateTask extends AsyncTask<Context, Integer, String> {

        protected String doInBackground(Context... params) {

            try {

                try {
                    siteStatus = downloadProgram();
                }
                catch (IOException ignored) {}

                if (siteStatus == 200) {

                    DatabaseHandler db = new DatabaseHandler(getApplicationContext());

                    try {
                        jsonfile = openFileInput("events.json");
                        reader = new JsonReader(new InputStreamReader(jsonfile, "UTF-8"));
                    }
                    catch (UnsupportedEncodingException ignored) {}

                    try {

                        db.truncateTable();
                        int total = numberOfEvents;
                        int count = 0;

                        reader.beginArray();
                        while (reader.hasNext()) {
                            Event event = new Event();

                            reader.beginObject();

                            while (reader.hasNext()) {
                                String name = reader.nextName();

                                // Ignore null values.
                                if (reader.peek() == JsonToken.NULL) {
                                    reader.skipValue();
                                }
                                else if (name.equals("title")) {
                                    event.setTitle(reader.nextString());
                                }
                                else if (name.equals("id")) {
                                    event.setExternalId(reader.nextInt());
                                }
                                else if (name.equals("gratis")) {
                                    event.setFree(reader.nextInt());
                                }
                                else if (name.equals("prijs")) {
                                    event.setPrice(reader.nextString());
                                }
                                else if (name.equals("prijs_vvk")) {
                                    event.setPricePresale(reader.nextString());
                                }
                                else if (name.equals("omsch")) {
                                    event.setDescription(reader.nextString());
                                }
                                else if (name.equals("datum")) {
                                    // Add 2 hours so the timestamp is actually stored
                                    // on the next day, since our timestamps are
                                    // on GMT and we don't want to bother start
                                    // converting this at runtime.
                                    event.setDate(reader.nextInt() + 7200);
                                }
                                else if (name.equals("periode")) {
                                    event.setDatePeriod(reader.nextString());
                                }
                                else if (name.equals("start")) {
                                    event.setStartHour(reader.nextString());
                                }
                                else if (name.equals("sort")) {
                                    event.setDateSort(reader.nextInt());
                                }
                                else if (name.equals("cat")) {
                                    event.setCategory(reader.nextString());
                                }
                                else if (name.equals("cat_id")) {
                                    event.setCategoryId(reader.nextInt());
                                }
                                else if (name.equals("url")) {
                                    event.setUrl(reader.nextString());
                                }
                                else if (name.equals("loc_id")) {
                                    event.setLocationId(reader.nextInt());
                                }
                                else if (name.equals("loc")) {
                                    event.setLocation(reader.nextString());
                                }
                                else if (name.equals("lat")) {
                                    event.setLatitude(reader.nextString());
                                }
                                else if (name.equals("lon")) {
                                    event.setLongitude(reader.nextString());
                                }
                                else if (name.equals("korting")) {
                                    event.setDiscount(reader.nextString());
                                }
                                else if (name.equals("festival")) {
                                    event.setFestival(reader.nextInt());
                                }
                                else {
                                    // Skip fields we don't want to parse
                                    reader.skipValue();
                                }
                            }

                            reader.endObject();

                            count++;
                            int update = (count*100/total);
                            publishProgress(update);

                            // Save to database.
                            db.insertEvent(event);
                        }
                        reader.endArray();
                    }
                    catch (IOException ignored) {}
                }
                else {
                    return "servicedown";
                }
            }
            catch (IOException ignored) {}

            return "alldone";
        }

        @Override
        public void onProgressUpdate(Integer... values){
            super.onProgressUpdate(values);
            dialog.setProgress(values[0]);

            // Set tour, this is wicked cool.
            switch (values[0]) {
                case 2:
                    dialog.setMessage(getString(R.string.tour_step_2_info));
                    break;
                case 6:
                    dialog.setTitle(getString(R.string.tour_step_features_title));
                    dialog.setMessage(getString(R.string.tour_step_3_info));
                    break;
                case 12:
                    dialog.setMessage(getString(R.string.tour_step_4_info));
                    break;
                case 18:
                    dialog.setMessage(getString(R.string.tour_step_5_info));
                    break;
                case 24:
                    dialog.setMessage(getString(R.string.tour_step_6_info));
                    break;
                case 30:
                    dialog.setMessage(getString(R.string.tour_step_7_info));
                    break;
                case 36:
                    dialog.setTitle(getString(R.string.tour_step_intermezzo_title));
                    dialog.setMessage(getString(R.string.tour_step_8_info));
                    break;
                case 42:
                    dialog.setMessage(getString(R.string.tour_step_9_info));
                    break;
                case 48:
                    dialog.setTitle(getString(R.string.tour_step_features_title));
                    dialog.setMessage(getString(R.string.tour_step_10_info));
                    break;
                case 54:
                    dialog.setMessage(getString(R.string.tour_step_11_info));
                    break;
                case 60:
                    dialog.setMessage(getString(R.string.tour_step_12_info));
                    break;
                case 66:
                    dialog.setTitle(getString(R.string.tour_step_intermezzo_title));
                    dialog.setMessage(getString(R.string.tour_step_13_info));
                    break;
                case 72:
                    dialog.setMessage(getString(R.string.tour_step_14_info));
                    break;
                case 78:
                    dialog.setMessage(getString(R.string.tour_step_15_info));
                    break;
                case 84:
                    dialog.setTitle(getString(R.string.tour_step_features_title));
                    dialog.setMessage(getString(R.string.tour_step_16_info));
                    break;
                case 90:
                    dialog.setMessage(getString(R.string.tour_step_17_info));
                    break;
                case 96:
                    dialog.setTitle(getString(R.string.tour_step_end_title));
                    dialog.setMessage(getString(R.string.tour_step_18_info));
                    break;
            }
        }

        @Override
        protected void onPostExecute(String sResponse) {
            if (sResponse == "servicedown") {
                serviceDown(dialog);
            }
            else {
                closeParseDialog(dialog);
            }
        }
    }

    /**
     * Close the dialog and inform re: the service which is down.
     */
    public void serviceDown(Dialog dialog) {
        // Close dialog.
        if (dialog.isShowing()) {
            dialog.dismiss();
        }

        // Friendly message.
        Toast.makeText(Prefs.this, getString(R.string.service_offline), Toast.LENGTH_LONG).show();
    }

    /**
     * Close the dialog and remove the file.
     */
    public void closeParseDialog(Dialog dialog) {

        // Remove the file.
        getApplicationContext().deleteFile("events.json");

        // Close dialog
        if (dialog.isShowing()) {
            dialog.dismiss();
        }

        // Friendly message.
        Toast.makeText(Prefs.this, getString(R.string.updating_done), Toast.LENGTH_LONG).show();

        // Redirect to home.
        Intent goHome = new Intent(getBaseContext(), Main.class);
        startActivity(goHome);
    }
}