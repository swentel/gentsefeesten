package com.genschefieste;

import android.content.Intent;
import android.graphics.PointF;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class AroundMe extends BaseActivity {

    public List<Event> events;
    public int eventId = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.around_me);

        // Get the list view.
        ListView list = (ListView) findViewById(R.id.list);

        // Get the events.
        events = getEventsAroundMe();

        // Check on size of events. In case there are no events, show the empty
        // view to inform the people what might have gone wrong.
        if (events.size() == 0) {
            list.setEmptyView(findViewById(R.id.empty));
            TextView noEvents = (TextView) findViewById(R.id.empty);
            noEvents.setOnClickListener(refreshActivity);
        }

        // Make every item clickable.
        list.setClickable(true);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getBaseContext(), EventDetail.class);
                eventId = events.get(position).getId();
                intent.putExtra("eventId", eventId);
                startActivity(intent);
            }
        });

        // Fire the list adapter.
        AroundMeListAdapter adapter = new AroundMeListAdapter(this, events);
        list.setAdapter(adapter);

        super.onCreate(savedInstanceState);
    }

    /**
     * refreshActivity button listener.
     */
    private final View.OnClickListener refreshActivity = new View.OnClickListener() {
        public void onClick(View v) {
            Toast.makeText(AroundMe.this, getString(R.string.around_me_refresh), Toast.LENGTH_LONG).show();
            finish();
            startActivity(getIntent());
        }
    };

    /**
     * Get events around me.
     */
    public List<Event> getEventsAroundMe() {
        DatabaseHandler db = new DatabaseHandler(this);

        // Location part. The probem with sqlite is that it doesn't support geolocation
        // queries because functions like ACOS() simply don't exist like in MySQL.
        // So, we have to perform the heavy lifting ourselves.

        // Start the query.
        String selectQuery = "SELECT * FROM " + DatabaseHandler.TABLE_EVENTS + " te LEFT JOIN " + DatabaseHandler.TABLE_FAVORITES + " tf ON te." + DatabaseHandler.EXTERNAL_ID + " = tf." + DatabaseHandler.FAVORITES_KEY_ID + " ";

        // We take one half an hour less, so we can include events
        // which are happening right now as well.
        long unixTime = (System.currentTimeMillis() / 1000L) + 5400;
        selectQuery += "WHERE " + DatabaseHandler.KEY_DATE_SORT + " >= " + unixTime;

        // Calculate the points.
        int radius = 5000;
        float x = (float) latitude;
        float y = (float) longitude;
        PointF center = new PointF(x, y);
        final double mult = 1; // mult = 1.1; is more reliable
        PointF p1 = calculateDerivedPosition(center, mult * radius, 0);
        PointF p2 = calculateDerivedPosition(center, mult * radius, 90);
        PointF p3 = calculateDerivedPosition(center, mult * radius, 180);
        PointF p4 = calculateDerivedPosition(center, mult * radius, 270);

        selectQuery += " AND ("
            + DatabaseHandler.KEY_LAT + " > " + String.valueOf(p3.x) + " AND "
            + DatabaseHandler.KEY_LAT + " < " + String.valueOf(p1.x) + " AND "
            + DatabaseHandler.KEY_LONG + " < " + String.valueOf(p2.y) + " AND "
            + DatabaseHandler.KEY_LONG + " > " + String.valueOf(p4.y) + ")";

        // Sort by date sort.
        selectQuery += " order by " + DatabaseHandler.KEY_DATE_SORT + " ASC";

        // Add limit
        selectQuery += " LIMIT 50";
        events = db.getEventsAroundMe(selectQuery, center, radius);

        // Now order them by distance.
        Collections.sort(events, new DistanceDateComparator());

        return events;
    }

    /**
     * Sort on date, then distance.
     */
    public class DistanceDateComparator implements Comparator<Event> {
        public int compare(Event e1, Event e2) {

            if (e1.getDateSort() < e2.getDateSort()) {
                return 1;
            }
            Float distance1 = Float.valueOf(e1.getLocation());
            Float distance2 = Float.valueOf(e2.getLocation());
            return distance1.compareTo(distance2);
        }
    }

    /**
     * Calculates the end-point from a given source at a given range (meters)
     * and bearing (degrees). This methods uses simple geometry equations to
     * calculate the end-point.
     *
     * @param point
     *            Point of origin
     * @param range
     *            Range in meters
     * @param bearing
     *            Bearing in degrees
     * @return End-point from the source given the desired range and bearing.
     */
    public static PointF calculateDerivedPosition(PointF point, double range, double bearing) {
        double EarthRadius = 6371000; // m

        double latA = Math.toRadians(point.x);
        double lonA = Math.toRadians(point.y);
        double angularDistance = range / EarthRadius;
        double trueCourse = Math.toRadians(bearing);

        double lat = Math.asin(
                Math.sin(latA) * Math.cos(angularDistance) +
                        Math.cos(latA) * Math.sin(angularDistance)
                                * Math.cos(trueCourse));

        double dlon = Math.atan2(
                Math.sin(trueCourse) * Math.sin(angularDistance)
                        * Math.cos(latA),
                Math.cos(angularDistance) - Math.sin(latA) * Math.sin(lat));

        double lon = ((lonA + dlon + Math.PI) % (Math.PI * 2)) - Math.PI;

        lat = Math.toDegrees(lat);
        lon = Math.toDegrees(lon);

        PointF newPoint = new PointF((float) lat, (float) lon);

        return newPoint;
    }
}
