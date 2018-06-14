package com.genschefieste;

import android.content.Intent;
import android.os.Bundle;
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

        // Get facet id.
        Bundle extras = getIntent().getExtras();
        assert extras != null;
        facetId = extras.getInt("facetId");

        // Get the list view.
        ListView list = findViewById(R.id.list);

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
        TextView textView = findViewById(R.id.type_title);
        textView.setText(typeTitle);

        // Fire the list adapter.
        TypeListAdapter adapter = new TypeListAdapter(this, string_resources, facetId);
        list.setAdapter(adapter);

        super.onCreate(savedInstanceState);
    }
}
