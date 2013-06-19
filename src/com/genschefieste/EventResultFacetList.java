package com.genschefieste;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

public class EventResultFacetList extends BaseActivity implements View.OnClickListener {

    public int facetId;
    public int typeIndex;
    public int typeId;
    public int dateIndex;
    public List<Event> events;
    public int eventId = 0;
    public int timestamp;
    public String date_text;
    public String type_text;

    private RelativeLayout dayRow;
    private RelativeLayout typeRow;
    private ImageButton dayButton;
    private ImageButton listButton;
    private Context mContext;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.event_facet_list);

        mContext = this;

        // Get type and date.
        Bundle extras = getIntent().getExtras();
        assert extras != null;
        facetId = extras.getInt("facetId");
        typeIndex = extras.getInt("typeIndex");
        dateIndex = extras.getInt("dateIndex");

        // Day dialog changer.
        dayRow = (RelativeLayout) findViewById(R.id.day_change);
        dayRow.setOnClickListener(this);
        dayButton = (ImageButton) findViewById(R.id.day_row_button);
        dayButton.setOnClickListener(this);

        // Type dialog changer.
        if (facetId != 2) {
            typeRow = (RelativeLayout) findViewById(R.id.type_change);
            typeRow.setOnClickListener(this);
            listButton = (ImageButton) findViewById(R.id.type_row_button);
            listButton.setImageResource(R.drawable.list);
            listButton.setOnClickListener(this);
        }

        // Get the list view.
        ListView list = (ListView) findViewById(R.id.list);

        // Set day row text.
        timestamp = GenscheFieste.getTimestampFromIndex(dateIndex, this);
        date_text = GenscheFieste.getDateFromTimestamp(timestamp, this);
        TextView day_row = (TextView) findViewById(R.id.day_row);
        day_row.setText(date_text);

        // Set facet type row text.
        if (facetId == 2) {
            type_text = getString(R.string.event_free);
        }
        else {
            // Categories
            if (facetId == 1) {
                typeId = GenscheFieste.getCategoryIdFromIndex(typeIndex, this);
                type_text = GenscheFieste.getCategoryFromId(typeId, this);
            }
            // Locations.
            else if (facetId == 3) {
                typeId = GenscheFieste.getLocationIdFromIndex(typeIndex, this);
                type_text = GenscheFieste.getLocationFromId(typeId, this);
            }
        }
        TextView type_row = (TextView) findViewById(R.id.type_row);
        type_row.setText(type_text);

        // Get the events.
        DatabaseHandler db = new DatabaseHandler(this);
        String selectQuery = "SELECT * FROM " + DatabaseHandler.TABLE_EVENTS;
        selectQuery += " te LEFT JOIN " + DatabaseHandler.TABLE_FAVORITES + " tf ON te." + DatabaseHandler.EXTERNAL_ID + " = tf." + DatabaseHandler.FAVORITES_KEY_ID + " ";
        selectQuery += " WHERE " + DatabaseHandler.KEY_DATE + " = " + timestamp;
        // Categories.
        if (facetId == 1) {
            selectQuery += " AND " + DatabaseHandler.KEY_CAT_ID + " = " + typeId;
        }
        // Free events.
        else if (facetId == 2) {
            selectQuery += " AND " + DatabaseHandler.KEY_FREE + " = 1";
        }
        // Locations.
        else if (facetId == 3) {
            selectQuery += " AND " + DatabaseHandler.KEY_LOC_ID + " = " + typeId;
        }
        selectQuery += " ORDER BY " + DatabaseHandler.KEY_DATE_SORT + " ASC, " + DatabaseHandler.KEY_TITLE + " ASC";

        // Debugging.
        if (GenscheFieste.debugMode) {
            Log.d("DebugApp", "TypeIndex" + typeIndex + " typeId " + typeId + " - Q = " + selectQuery);
        }

        events = db.getEvents(selectQuery);

        // Make every item clickable.
        list.setClickable(true);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Intent intent = new Intent(getBaseContext(), EventDetail.class);
            eventId = events.get(position).getId();
            intent.putExtra("eventId", eventId);
            startActivity(intent);
            }
        });

        // Fire the list adapter.
        EventsListAdapter adapter = new EventsListAdapter(this, events);
        list.setAdapter(adapter);

        super.onCreate(savedInstanceState);
    }

    @Override
    public void onClick(View view) {
        if (view.equals(dayRow) || view.equals(dayButton)) {
            showSwitchDayDialog();
        }
        if (view.equals(typeRow) || view.equals(listButton)) {
            showSwitchTypeDialog();
        }
    }

    /**
     * Show switch day dialog.
     */
    private void showSwitchDayDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle(getString(R.string.change_day));

        final String[] choiceList = getResources().getStringArray(R.array.dates_full);
        int selected = dateIndex;

        builder.setSingleChoiceItems(choiceList, selected, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int newDateIndex) {
            dialog.dismiss();
            intent = new Intent(mContext, EventResultFacetList.class);
            intent.putExtra("facetId", facetId);
            intent.putExtra("typeIndex", typeIndex);
            intent.putExtra("dateIndex", newDateIndex);
            startActivity(intent);
            }
        });
        AlertDialog changeDay = builder.create();
        changeDay.show();
    }

    /**
     * Show switch type dialog.
     */
    private void showSwitchTypeDialog() {
        String[] choiceList;
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);

        int selected = typeIndex;

        if (facetId == 1) {
            builder.setTitle(getString(R.string.change_category));
            choiceList = getResources().getStringArray(R.array.category_strings);
        }
        else {
            builder.setTitle(getString(R.string.change_location));
            choiceList = getResources().getStringArray(R.array.location_strings);
        }

        builder.setSingleChoiceItems(choiceList, selected, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int newTypeIndex) {
            dialog.dismiss();
            intent = new Intent(mContext, EventResultFacetList.class);
            intent.putExtra("facetId", facetId);
            intent.putExtra("typeIndex", newTypeIndex);
            intent.putExtra("dateIndex", dateIndex);
            startActivity(intent);
            }
        });
        AlertDialog changeType = builder.create();
        changeType.show();
    }
}
