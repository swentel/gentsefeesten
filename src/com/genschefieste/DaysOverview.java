package com.genschefieste;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ListView;

public class DaysOverview extends BaseActivity {

    Intent intent;
    public int facetId;
    public int typeIndex;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.day_list);

        // Get facet id and type index.
        Bundle extras = getIntent().getExtras();
        assert extras != null;
        facetId = extras.getInt("facetId");
        typeIndex = extras.getInt("typeIndex");

        // Get the list view.
        ListView list = (ListView) findViewById(R.id.list);

        // Fire the list adapter.
        String[] date_string_resources = getResources().getStringArray(R.array.dates_full);
        DatesListAdapter adapter = new DatesListAdapter(this, date_string_resources, facetId, typeIndex);
        list.setAdapter(adapter);

        super.onCreate(savedInstanceState);
    }
}
