package com.genschefieste;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Point;
import android.os.Bundle;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;

import java.util.List;

public class MapBase extends MapActivity {

    Event event;
    MapView mapView;
    MapController mc;
    GeoPoint p;

    public void onCreate(Bundle savedInstanceState) {

        // Get event.
        Bundle extras = getIntent().getExtras();
        int eventId = extras.getInt("eventId");
        DatabaseHandler db = new DatabaseHandler(this);
        event = db.getEvent(eventId);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.map);

        mapView = (MapView) findViewById(R.id.mapview);
        mapView.setBuiltInZoomControls(true);

        mc = mapView.getController();
        String coordinates[] = { event.getLatitude(), event.getLongitude() };
        double lat = Double.parseDouble(coordinates[0]);
        double lng = Double.parseDouble(coordinates[1]);

        p = new GeoPoint((int) (lat * 1E6), (int) (lng * 1E6));

        mc.animateTo(p);
        mc.setZoom(17);

        // Add marker.
        MapOverlay mapOverlay = new MapOverlay();
        List<Overlay> listOfOverlays = mapView.getOverlays();
        listOfOverlays.clear();
        listOfOverlays.add(mapOverlay);

        mapView.invalidate();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    protected boolean isRouteDisplayed() {
        return false;
    }

    class MapOverlay extends com.google.android.maps.Overlay {
        public boolean draw(Canvas canvas, MapView mapView, boolean shadow, long when) {
            super.draw(canvas, mapView, shadow);

            // Translate the GeoPoint to screen pixels
            Point screenPts = new Point();
            mapView.getProjection().toPixels(p, screenPts);

            // Add the marker
            Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.drawable.menu_home);
            canvas.drawBitmap(bmp, screenPts.x, screenPts.y - 50, null);
            return true;
        }
    }

}
