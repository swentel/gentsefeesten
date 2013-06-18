package com.genschefieste;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Simple list adapter.
 */
// TODO for some reason lint doesn't like something here, find out what.
public class SimpleListAdapter extends ArrayAdapter implements OnClickListener {
    private final Context context;
    private final String[] values;

    public SimpleListAdapter(Context context, String[] values) {
        super(context, R.layout.list_item, values);
        this.context = context;
        this.values = values;
    }

    public void onClick(View view) {

    }

    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_item, null);
        }

        // Change color of row.
        LinearLayout row = (LinearLayout) convertView.findViewById(R.id.single_row);
        if ((position % 2) == 0) {
            row.setBackgroundColor(Color.parseColor("#f6f6f6"));
        }
        else {
            row.setBackgroundColor(Color.parseColor("#ffffff"));
        }

        TextView textView = (TextView) convertView.findViewById(R.id.single_row_item);
        textView.setText(values[position]);

        return convertView;
    }
}