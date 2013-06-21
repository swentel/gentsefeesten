package com.genschefieste;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

public class Festivals extends BaseActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.festival_list);

        // Get the list view.
        ListView list = (ListView) findViewById(R.id.list);

        // Make every item clickable.
        list.setClickable(true);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //intent = new Intent(Festivals.this, FestivalList.class);
                //intent.putExtra("festivalIndex", position);
                //startActivity(intent);
                // Note ignore 'expo obsession collective in the next overview'
                Toast.makeText(Festivals.this, "Coming soon", Toast.LENGTH_LONG).show();
            }
        });

        // Fire the list adapter.
        String[] festival_string_resources = getResources().getStringArray(R.array.festival_strings);
        SimpleListAdapter adapter = new SimpleListAdapter(this, festival_string_resources);
        list.setAdapter(adapter);

        super.onCreate(savedInstanceState);
    }
}
