package com.genschefieste;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Dates list adapter.
 */
public class DatesListAdapter extends ArrayAdapter<String> implements OnClickListener {
    private final Context context;
    private final String[] values;
    private final int facetId;
    private final int typeIndex;

    public DatesListAdapter(Context context, String[] values, int facetId, int typeIndex) {
        super(context, R.layout.list_item, values);
        this.context = context;
        this.values = values;
        this.facetId = facetId;
        this.typeIndex = typeIndex;
    }

    public void onClick(View view) {

    }

    public View getView(final int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_item, null);
        }

        // Change color of row.
        assert convertView != null;
        final LinearLayout row = (LinearLayout) convertView.findViewById(R.id.single_row);
        String backColor = ((position % 2) == 0) ? "#f6f6f6" : "#ffffff";
        row.setBackgroundColor(Color.parseColor(backColor));

        final TextView textView = (TextView) convertView.findViewById(R.id.single_row_item);
        textView.setText(values[position]);

        // Set on touch listener.
        row.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                String backColor = ((position % 2) == 0) ? "#f6f6f6" :  "#ffffff";
                switch(motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        row.setBackgroundColor(Color.parseColor("#323232"));
                        textView.setTextColor(Color.parseColor("#ffffff"));
                        break;
                    case MotionEvent.ACTION_CANCEL:
                        row.setBackgroundColor(Color.parseColor(backColor));
                        textView.setTextColor(context.getResources().getColor(R.color.global_textcolor));
                        break;
                    case MotionEvent.ACTION_UP:
                        row.setBackgroundColor(Color.parseColor(backColor));
                        textView.setTextColor(context.getResources().getColor(R.color.global_textcolor));
                        Intent intent = new Intent(context, EventResultFacetList.class);
                        intent.putExtra("dateIndex", position);
                        intent.putExtra("facetId", facetId);
                        intent.putExtra("typeIndex", typeIndex);
                        context.startActivity(intent);
                        break;
                }
                return true;
            }
        });


        return convertView;
    }
}