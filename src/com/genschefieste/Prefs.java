package com.genschefieste;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

public class Prefs extends PreferenceActivity {

    // First run.
    public boolean firstrun = false;

    // Download variables.
    ProgressDialog dialog;
    public static int siteStatus = 200;
    public static InputStream dataFile = null;

    // Number of lines to read, this is just approximate, might change in the future,
    // but this allows us to create a progress bar.
    public int numberOfEvents = 810;

    // URI to the event services. In theory we could import 2 other services which describe
    // the locations and categories, however, we have made those available in the resources
    // so they are also easily translatable. Note that for the Android version we just
    // have a file with the values part of a query which allows us to import in around 30 seconds.
    public static String eventUrl = "";

    // The name of the data file.
    public static String fileName = "events.data";

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
                updateProgram();
                return true;
            }
        });

        // Check for initial run.
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            firstrun = extras.getBoolean("firstrun");
            if (firstrun) {
                updateProgram();
            }
        }
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
     * Updates program.
     */
    public void updateProgram() {
        // Make sure we are connected to the internet.
        if ((cm.getActiveNetworkInfo() != null) && cm.getActiveNetworkInfo().isAvailable() && cm.getActiveNetworkInfo().isConnected()) {
            dialog = new customProgressDialog(Prefs.this);
            dialog.setTitle(R.string.updating);
            dialog.setMessage(getString(R.string.updating_message));
            dialog.setIndeterminate(false);
            dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            dialog.setCancelable(false);
            dialog.show();
            new updateTask().execute();
        }
        else {
            Toast.makeText(getApplicationContext(), getString(R.string.update_offline), Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Download the program from the internet and save it locally.
     */
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
                FileOutputStream fos = openFileOutput(fileName, Context.MODE_PRIVATE);
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

                    dataFile = openFileInput(fileName);
                    InputStreamReader inputreader = new InputStreamReader(dataFile, "UTF-8");
                    BufferedReader buffreader = new BufferedReader(inputreader);

                    try {

                        int count = 0;
                        String line;

                        DatabaseHandler handler = new DatabaseHandler(getApplicationContext());
                        handler.truncateTable();
                        SQLiteDatabase db = handler.getWritableDatabase();

                        @SuppressWarnings("static-access")
						String query = "INSERT INTO " + handler.TABLE_EVENTS + "(" +
                                "" + handler.KEY_TITLE + "," +
                                "" + handler.EXTERNAL_ID + "," +
                                "" + handler.KEY_FREE + "," +
                                "" + handler.KEY_PRICE + "," +
                                "" + handler.KEY_PRICE_PS + "," +
                                "" + handler.KEY_DESCRIPTION + "," +
                                "" + handler.KEY_DATE + "," +
                                "" + handler.KEY_DATE_PERIOD + "," +
                                "" + handler.KEY_START_HOUR + "," +
                                "" + handler.KEY_DATE_SORT + "," +
                                "" + handler.KEY_CAT_NAME + "," +
                                "" + handler.KEY_CAT_ID + "," +
                                "" + handler.KEY_URL + "," +
                                "" + handler.KEY_LOC_ID + "," +
                                "" + handler.KEY_LOC_NAME + "," +
                                "" + handler.KEY_LAT + "," +
                                "" + handler.KEY_LONG + "," +
                                "" + handler.KEY_DISCOUNT + "," +
                                "" + handler.KEY_FESTIVAL + "" +
                                ") VALUES ";

                            do {
                                line = buffreader.readLine();

                                // Split on ::SPLIT::
                                line = line.replace("|NEWLINE|", "\n");
                                String [] values = line.split(":SPLIT:");

                                try {
                                    db.beginTransaction();
                                    for ( int i = 0; i <= values.length - 1; i++) {
                                        String insertQuery = query + values[i] + ";";
                                        db.execSQL(insertQuery);
                                    }
                                    db.setTransactionSuccessful();
                                }
                                catch (SQLException e) {} finally {
                                    db.endTransaction();
                                }

                                // Notify dialog.
                                count++;
                                int update = (count*100/numberOfEvents);
                                publishProgress(update);

                            } while (line != null);

                        } catch (IOException ignored) {
                    }
                    catch (Exception ignored) {}
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
        getApplicationContext().deleteFile(fileName);

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