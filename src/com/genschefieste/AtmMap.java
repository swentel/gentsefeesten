package com.genschefieste;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.OverlayItem;

import java.util.ArrayList;
import java.util.List;

/**
 * ATM location on a map.
 *
 * @author Jeppe Knockaert, Leen De Baets, Nicolas Dierck, Benjamin Mestdagh
 * (c) 2013, OKFN. All rights reserved.
 */

public class AtmMap extends MapActivity {

    List<Atm> atms;
    MapView mapView;
    MapController mc;
    public LinearLayout _bubbleLayout;
    public int position;
    int minLatitude = Integer.MAX_VALUE;
    int maxLatitude = Integer.MIN_VALUE;
    int minLongitude = Integer.MAX_VALUE;
    int maxLongitude = Integer.MIN_VALUE;
    public double latitude;
    public double longitude;

    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.atm_map);

        Bundle extras = getIntent().getExtras();
        latitude = extras.getDouble("latitude");
        longitude = extras.getDouble("longitude");
        //latitude = 51.058228;
        //longitude = 3.735352;

        atms = new ArrayList<Atm>();
        String[] atmList = this.getResources().getStringArray(R.array.atms);
        for (int i = 0; i < atmList.length ; i++) {
            String[] parts = atmList[i].split(";");
            Atm atm = new Atm();
            atm.setName(parts[0]);
            atm.setAddress(parts[1]);
            atm.setLatitude(Float.parseFloat(parts[2]));
            atm.setLongitude(Float.parseFloat(parts[3]));
            atms.add(atm);
        }

        mapView = (MapView) findViewById(R.id.mapview);
        mapView.setBuiltInZoomControls(true);
        mapView.getOverlays().clear();

        Drawable currentLocationIcon = this.getResources().getDrawable(R.drawable.menu_favorites);
        MyItemizedOverlay itemizedOverlay = new MyItemizedOverlay(currentLocationIcon);
        addOverlayItems(latitude, longitude, itemizedOverlay, 0);
        mapView.getOverlays().add(itemizedOverlay);

        // Also take current position in account for zooming factor.
        maxLatitude = Math.max((int) (latitude * 1E6), maxLatitude);
        minLatitude = Math.min((int) (latitude * 1E6), minLatitude);
        maxLongitude = Math.max((int) (longitude * 1E6), maxLongitude);
        minLongitude = Math.min((int) (longitude * 1E6), minLongitude);

        int index = 0;
        for (Atm a : atms) {
            Drawable atmIcon = this.getResources().getDrawable(R.drawable.atm);
            itemizedOverlay = new MyItemizedOverlay(atmIcon);
            addOverlayItems(a.getLatitude(), a.getLongitude(), itemizedOverlay, index);
            index++;
            mapView.getOverlays().add(itemizedOverlay);

            maxLatitude = Math.max((int) (a.getLatitude() * 1E6), maxLatitude);
            minLatitude = Math.min((int) (a.getLatitude() * 1E6), minLatitude);
            maxLongitude = Math.max((int) (a.getLongitude() * 1E6), maxLongitude);
            minLongitude = Math.min((int) (a.getLongitude() * 1E6), minLongitude);
        }

        mc = mapView.getController();

        double fitFactor = 1.5;
        mc.zoomToSpan((int) (Math.abs(maxLatitude - minLatitude) * fitFactor), (int) (Math.abs(maxLongitude - minLongitude) * fitFactor));
        mc.animateTo(new GeoPoint((maxLatitude + minLatitude) / 2, (maxLongitude + minLongitude) / 2));

        // Listener on back button.
        TextView BackToEvent = (TextView) findViewById(R.id.back_to_list);
        BackToEvent.setOnClickListener(backToList);
    }

    /**
     * OnClickListener on "Back" button.
     */
    private final View.OnClickListener backToList = new View.OnClickListener() {
        public void onClick(View v) {
            finish();
        }
    };

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

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_S) {
            mapView.setSatellite(!mapView.isSatellite());
            return (true);
        }
        else if (keyCode == KeyEvent.KEYCODE_Z) {
            mapView.displayZoomControls(true);
            return (true);
        }

        return (super.onKeyDown(keyCode, event));
    }

    private void addOverlayItems(double lat, double lng, MyItemizedOverlay itemizedOverlay, int index) {
        GeoPoint point = new GeoPoint((int) (lat * 1E6), (int) (lng * 1E6));
        OverlayItem overlayItem = new OverlayItem(point, "", Integer.toString(index));
        itemizedOverlay.addOverlayItem(overlayItem);
    }

    View.OnClickListener detailOnclick = new View.OnClickListener() {
        public void onClick(View v) {
            Atm atm = atms.get(position);
            String mapUrl = "http://maps.google.com/maps?saddr=" + latitude + "," + longitude + "&daddr=" + atm.getLatitude() + "," + atm.getLongitude();
            Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(mapUrl));
            startActivity(intent);
        }
    };

    View.OnClickListener closeBubble = new View.OnClickListener() {
        public void onClick(View v) {
            mapView.removeView(_bubbleLayout);
        }
    };

    private class MyItemizedOverlay extends ItemizedOverlay<OverlayItem> {

        private List<OverlayItem> mOverlays = new ArrayList<OverlayItem>();

        public MyItemizedOverlay(Drawable defaultMarker) {
            super(boundCenterBottom(defaultMarker));
        }

        @Override
        protected OverlayItem createItem(int i) {
            return mOverlays.get(i);
        }

        @Override
        public int size() {
            return mOverlays.size();
        }

        public void addOverlayItem(OverlayItem overlayItem) {
            mOverlays.add(overlayItem);
            populate();
        }

        @Override
        public boolean onTap(int index) {
            OverlayItem item = mOverlays.get(index);
            position = Integer.parseInt(item.getSnippet());
            Atm atm = atms.get(position);

            // If a bubble is currently displayed then clear it.
            if (_bubbleLayout != null) {
                mapView.removeView(_bubbleLayout);
            }

            // Get instance of the Bubble Layout.
            LayoutInflater inflater = (LayoutInflater) mapView.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            _bubbleLayout = (LinearLayout) inflater.inflate(R.layout.bubble, mapView, false);

            // Configure its layout parameters
            double latD = atm.getLatitude();
            double lngD = atm.getLongitude();
            int lat = (int) (latD * 1E6);
            int lng = (int) (lngD * 1E6);
            GeoPoint p = new GeoPoint(lat, lng);
            MapView.LayoutParams params = new MapView.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, p, MapView.LayoutParams.BOTTOM_CENTER);

            _bubbleLayout.setLayoutParams(params);

            // Add title of atm.
            TextView name = (TextView) _bubbleLayout.findViewById(R.id.atm_title);
            name.setText(atm.getName());

            // Add title of atm.
            TextView address = (TextView) _bubbleLayout.findViewById(R.id.atm_address);
            address.setText(atm.getAddress());

            // Link.
            TextView directions = (TextView) _bubbleLayout.findViewById(R.id.directions);
            directions.setText(R.string.info_directions);
            directions.setOnClickListener(detailOnclick);

            // Close button.
            TextView closeButton = (TextView) _bubbleLayout.findViewById(R.id.close_bubble);
            closeButton.setOnClickListener(closeBubble);

            // Add the view to the Map.
            mapView.addView(_bubbleLayout);

            // Animate the map to center on the location.
            mapView.getController().animateTo(p);
            return true;
        }

    }
}