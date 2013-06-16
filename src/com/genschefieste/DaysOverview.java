package com.genschefieste;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

public class DaysOverview extends BaseActivity {

    public int type;
    Intent intent;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.day_list);

        // Get next screen.
        Bundle extras = getIntent().getExtras();
        type = extras.getInt("next_screen");

        // Get the list view.
        ListView list = (ListView) findViewById(R.id.list);

        // Make every item clickable.
        list.setClickable(true);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                switch (type) {
                    // Goes to typeOverview for categories, festivals and locations.
                    case 1:
                    case 3:
                    case 4:
                        intent = new Intent(getBaseContext(), TypeOverview.class);
                        intent.putExtra("type", type);
                        break;
                    /*case 2:
                        intent = new Intent(getBaseContext(), Result.class);
                        intent.putExtra("type", type);
                        break;*/
                }

                startActivity(intent);
            }
        });

        // Fire the list adapter.
        String[] date_string_resources = getResources().getStringArray(R.array.dates_full);
        DayListAdapter adapter = new DayListAdapter(this, date_string_resources);
        list.setAdapter(adapter);

        super.onCreate(savedInstanceState);
    }
}
