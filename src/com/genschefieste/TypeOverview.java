package com.genschefieste;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

public class TypeOverview extends BaseActivity {

    public int facetId;
    public Intent intent;
    public String typeTitle;
    public String[] string_resources;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.type_list);

        // Get type and date.
        Bundle extras = getIntent().getExtras();
        assert extras != null;
        facetId = extras.getInt("facetId");

        // Get the list view.
        ListView list = (ListView) findViewById(R.id.list);
        // Make every item clickable.
        list.setClickable(true);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            intent = new Intent(getBaseContext(), DaysOverview.class);
            intent.putExtra("facetId", facetId);
            intent.putExtra("typeIndex", position);
            startActivity(intent);
            }
        });

        // Set facet type title.
        switch (facetId) {
            case 1:
                string_resources = getResources().getStringArray(R.array.category_strings);
                typeTitle = getString(R.string.select_a_category);
                break;
            case 3:
                string_resources = getResources().getStringArray(R.array.location_strings);
                typeTitle = getString(R.string.select_a_location);
                break;
        }
        TextView textView = (TextView) findViewById(R.id.type_title);
        textView.setText(typeTitle);

        // Fire the list adapter.
        SimpleListAdapter adapter = new SimpleListAdapter(this, string_resources);
        list.setAdapter(adapter);

        super.onCreate(savedInstanceState);
    }
}
