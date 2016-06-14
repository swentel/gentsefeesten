package com.genschefieste;

import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

public class AroundMePre extends BaseActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.around_me_pre);
        addTopbarListeners = false;

        try {
            locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
        }
        catch (SecurityException ignored) {
            if (Build.VERSION.SDK_INT >= 23) {
                Toast.makeText(getApplicationContext(), getString(R.string.location_permission), Toast.LENGTH_LONG).show();
            }
        }

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {

            @Override
            public void run() {
                Intent GoAroundMe = new Intent(getBaseContext(), AroundMe.class);
                startActivity(GoAroundMe);
            }
        }, 500);

        super.onCreate(savedInstanceState);
    }

}
