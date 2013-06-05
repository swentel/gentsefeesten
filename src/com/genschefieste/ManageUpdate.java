package com.genschefieste;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

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
        setContentView(R.layout.manage_update);

        // Add listener on check image button.
        Button button = (Button) findViewById(R.id.check_updates);
        button.setId(1);
        button.setOnClickListener(actionUpdate);
    }

    /**
     * Button listener.
     */
    private final View.OnClickListener actionUpdate = new View.OnClickListener() {
        public void onClick(View v) {
            switch (v.getId()) {
                case 1:
                    /*if (!common.appIsOnline()) {
                        common.isOffline(Main.this);
                        return;
                    }*/
                    dialog = ProgressDialog.show(ManageUpdate.this, getString(R.string.updating), getString(R.string.please_wait), true);
                    new updateTask().execute();
                    break;
            }
        }
    };

    /**
     * update task.
     */
    class updateTask extends AsyncTask<Void, Void, String> {

        protected String doInBackground(Void... unused) {

            try {
                ManageUpdate.getJSONFromUrl();
                parseJson(getApplicationContext());
            }
            catch (IOException ignored) {

            }

            return sResponse;
        }

        @Override
        protected void onPostExecute(String sResponse) {
            // TODO rename this method.
            searchPostExecute(getApplicationContext(), dialog);
        }
    }

    public static void getJSONFromUrl() throws IOException {
        DefaultHttpClient httpClient = new DefaultHttpClient();
        HttpGet httpGet = new HttpGet(Event.eventUrl);

        try {
            HttpResponse httpResponse = httpClient.execute(httpGet);
            HttpEntity httpEntity = httpResponse.getEntity();
            is = httpEntity.getContent();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Parse and store events.
     *
     * @param context
     *  The application context.
     */
    public static void parseJson(Context context) {
        DatabaseHandler db = new DatabaseHandler(context);

        try {
            reader = new JsonReader(new InputStreamReader(is, "UTF-8"));
        } catch (UnsupportedEncodingException e1) {
            e1.printStackTrace();
        }

        try {
            reader.beginArray();
            while (reader.hasNext()) {
                Event event = new Event();

                reader.beginObject();

                while (reader.hasNext()) {
                    String name = reader.nextName();

                    // Ignore null values
                    if (reader.peek() == JsonToken.NULL) {
                        reader.skipValue();
                    }
                    else if (name.equals("title")) {
                        event.setTitle(reader.nextString());
                    }
                    else {
                        // Skip fields we don't want to parse
                        reader.skipValue();
                    }
                }

                reader.endObject();

                db.insertEvent(event);
            }
            reader.endArray();
        }
        catch (IOException ignored) {

        }
    }

    public static void searchPostExecute(Context context, Dialog dialog) {
        // Close dialog.
        if (dialog.isShowing()) {
            dialog.dismiss();
        }
    }

}