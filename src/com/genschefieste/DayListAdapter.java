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
 * Day list adapter.
 */
public class DayListAdapter extends ArrayAdapter implements OnClickListener {
    private final Context context;
    private final String[] days;

    public DayListAdapter(Context context, String[] days) {
        super(context, R.layout.day_list_item, days);
        this.context = context;
        this.days = days;
    }

    public void onClick(View view) {

    }

    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.day_list_item, null);
        }

        if ((position%2) == 0) {
            LinearLayout row = (LinearLayout) convertView.findViewById(R.id.day_row);
            row.setBackgroundColor(Color.parseColor("#f5f5f5"));
        }

        TextView textView = (TextView) convertView.findViewById(R.id.day);
        textView.setText(days[position]);

        return convertView;
    }
}