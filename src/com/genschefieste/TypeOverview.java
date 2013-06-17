package com.genschefieste;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;

public class TypeOverview extends BaseActivity {

    public int type;
    public Intent intent;
    public String typeTitle;
    public String[] string_resources;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.type_list);

        // Get type and date.
        Bundle extras = getIntent().getExtras();
        type = extras.getInt("type");

        // Get the list view.
        ListView list = (ListView) findViewById(R.id.list);

        // Set type title.
        switch (type) {
            case 1:
                string_resources = getResources().getStringArray(R.array.category_strings);
                typeTitle = getString(R.string.select_a_category);
                break;
            case 2:
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
