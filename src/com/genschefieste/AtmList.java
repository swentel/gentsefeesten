package com.genschefieste;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.List;

public class AtmList extends BaseActivity {

    List<Atm> atms;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.atm_list);

        // Set listener on map button.
        RelativeLayout goToMapRow = (RelativeLayout) findViewById(R.id.map_button);
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if ((cm.getActiveNetworkInfo() != null) && cm.getActiveNetworkInfo().isAvailable() && cm.getActiveNetworkInfo().isConnected()) {
            // Add listener on goToMap button.
            goToMapRow.setOnClickListener(goToMap);
        }
        else {
            View sep = findViewById(R.id.sep);
            sep.setVisibility(View.GONE);
            goToMapRow.setVisibility(RelativeLayout.GONE);
        }

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

        // Fire AtmListAdapter.
        ListView list = (ListView) findViewById(R.id.list);
        AtmListAdapter adapter = new AtmListAdapter(this, atms);
        list.setAdapter(adapter);

        super.onCreate(savedInstanceState);
    }

    /**
     * GoToMap onClickListener.
     */
    private final View.OnClickListener goToMap = new View.OnClickListener() {
        public void onClick(View v) {
            Intent goMap = new Intent(getBaseContext(), AtmMap.class);
            goMap.putExtra("latitude", latitude);
            goMap.putExtra("longitude", longitude);
            startActivity(goMap);
        }
    };

}