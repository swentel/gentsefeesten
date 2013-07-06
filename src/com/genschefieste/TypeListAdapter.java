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
 * Type list adapter.
 */
public class TypeListAdapter extends ArrayAdapter implements OnClickListener {
    private final Context context;
    private final String[] values;
    private final int facetId;

    public TypeListAdapter(Context context, String[] values, int facetId) {
        super(context, R.layout.list_item, values);
        this.context = context;
        this.values = values;
        this.facetId = facetId;
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
                        row.setBackgroundColor(Color.parseColor("#ef4f3f"));
                        textView.setTextColor(Color.parseColor("#ffffff"));
                        break;
                    case MotionEvent.ACTION_CANCEL:
                        row.setBackgroundColor(Color.parseColor(backColor));
                        textView.setTextColor(Color.parseColor("#005371"));
                        break;
                    case MotionEvent.ACTION_UP:
                        row.setBackgroundColor(Color.parseColor(backColor));
                        textView.setTextColor(Color.parseColor("#005371"));
                        Intent intent = new Intent(context, DaysOverview.class);
                        intent.putExtra("facetId", facetId);
                        intent.putExtra("typeIndex", position);
                        context.startActivity(intent);
                        break;
                }
                return true;
            }
        });


        return convertView;
    }
}