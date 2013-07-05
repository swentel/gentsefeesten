package com.genschefieste;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

public class DaysOverview extends BaseActivity {

    Intent intent;
    public int facetId;
    public int typeIndex;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.day_list);

        // Get type.
        Bundle extras = getIntent().getExtras();
        assert extras != null;
        facetId = extras.getInt("facetId");
        typeIndex = extras.getInt("typeIndex");

        // Get the list view.
        ListView list = (ListView) findViewById(R.id.list);

        // Make every item clickable.
        list.setClickable(true);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            switch (facetId) {
                // Goes to typeOverview for categories, festivals and locations.
                case 1:
                case 3:
                case 4:
                    intent = new Intent(getBaseContext(), EventResultFacetList.class);
                    intent.putExtra("dateIndex", position);
                    intent.putExtra("facetId", facetId);
                    intent.putExtra("typeIndex", typeIndex);
                    break;
                case 2:
                    intent = new Intent(getBaseContext(), EventResultFacetList.class);
                    intent.putExtra("dateIndex", position);
                    intent.putExtra("facetId", facetId);
                    intent.putExtra("typeIndex", typeIndex);
                    break;
            }

            startActivity(intent);
            }
        });

        // Fire the list adapter.
        String[] date_string_resources = getResources().getStringArray(R.array.dates_full);
        SimpleListAdapter adapter = new SimpleListAdapter(this, date_string_resources);
        list.setAdapter(adapter);

        super.onCreate(savedInstanceState);
    }
}
