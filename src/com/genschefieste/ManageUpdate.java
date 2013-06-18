package com.genschefieste;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;

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

public class ManageUpdate extends Activity {

    ProgressDialog dialog;
    public static InputStream is = null;
    public static JsonReader reader = null;
    public static String sResponse = "";


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.preferences);

        // TODO speed up this immensively.
        // Add listener on check image button.
        //Button button = (Button) findViewById(R.id.check_updates);
        //button.setId(1);
        //button.setOnClickListener(actionUpdate);
    }

    /**
     * Button listener.
     */
    private final View.OnClickListener actionUpdate = new View.OnClickListener() {
        public void onClick(View v) {
        switch (v.getId()) {
            case 1:
                /*
                // TODO check if we are online.
                if (!common.appIsOnline()) {
                    common.isOffline(Main.this);
                    return;
                }*/
                dialog = new customProgressDialog(ManageUpdate.this);
                dialog.setTitle(R.string.updating);
                dialog.setMessage(getString(R.string.please_wait));
                dialog.setIndeterminate(false);
                dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                dialog.show();
                new updateTask().execute();
                break;
        }
        }
    };

    /**
     * update task.
     */
    class updateTask extends AsyncTask<Context, Integer, String> {

        protected String doInBackground(Context... params) {

            try {
                ManageUpdate.getJSONFromUrl();
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
            closeDialog( dialog);
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

    public static void closeDialog(Dialog dialog) {
        // Close dialog.
        if (dialog.isShowing()) {
            dialog.dismiss();
        }
    }

}