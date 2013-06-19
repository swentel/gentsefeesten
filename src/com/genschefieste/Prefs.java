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
import android.widget.Toast;

import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

public class Prefs extends PreferenceActivity {

    ProgressDialog dialog;
    public static InputStream is = null;
    public static JsonReader reader = null;
    public static String sResponse = "";

    // Connectivity manager.
    private ConnectivityManager cm;

    @Override
    public void onCreate(Bundle savedInstanceState) {
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
            // Make sure we are online.
            if ((cm.getActiveNetworkInfo() != null) && cm.getActiveNetworkInfo().isAvailable() && cm.getActiveNetworkInfo().isConnected()) {
                dialog = new customProgressDialog(Prefs.this);
                dialog.setTitle(R.string.updating);
                dialog.setMessage(getString(R.string.please_wait));
                dialog.setIndeterminate(false);
                dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                dialog.show();
                new updateTask().execute();
            } else {
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

    /**
     * Update task.
     */
    class updateTask extends AsyncTask<Context, Integer, String> {

        protected String doInBackground(Context... params) {

            try {
                Prefs.getJSONFromUrl();
                DatabaseHandler db = new DatabaseHandler(getApplicationContext());

                try {
                    reader = new JsonReader(new InputStreamReader(is, "UTF-8"));
                }
                catch (UnsupportedEncodingException ignored) {}

                try {

                    int total = GenscheFieste.numberOfEvents;
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

                        // TODO update.
                        db.insertEvent(event);
                    }
                    reader.endArray();
                }
                catch (IOException ignored) {
                }
            }
            catch (IOException ignored) {}

            return sResponse;
        }

        @Override
        public void onProgressUpdate(Integer... values){
            super.onProgressUpdate(values);
            dialog.setProgress(values[0]);
        }

        @Override
        protected void onPostExecute(String sResponse) {
            closeDialog(dialog);
        }
    }

    public static void getJSONFromUrl() throws IOException {
        DefaultHttpClient httpClient = new DefaultHttpClient();
        HttpGet httpGet = new HttpGet(GenscheFieste.eventUrl);

        try {
            HttpResponse httpResponse = httpClient.execute(httpGet);
            HttpEntity httpEntity = httpResponse.getEntity();
            is = httpEntity.getContent();
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
    }

    public void closeDialog(Dialog dialog) {
        // Close dialog and go to home so we get a fresh start.
        if (dialog.isShowing()) {
            dialog.dismiss();
            Toast.makeText(Prefs.this, getString(R.string.updating_done), Toast.LENGTH_LONG).show();
            Intent goHome = new Intent(getBaseContext(), Main.class);
            startActivity(goHome);
        }
    }
}