package com.genschefieste;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

/**
 * Event list 'Around me' adapter.
 */
public class AroundMeListAdapter extends BaseAdapter implements OnClickListener {
    private final Context context;
    private final List<Event> events;

    public AroundMeListAdapter(Context context, List<Event> events) {
        this.context = context;
        this.events = events;
    }

    public int getCount() {
        return events.size();
    }

    public Event getItem(int position) {
        return events.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public void onClick(View view) {

    }

    public View getView(final int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.event_around_me_item, null);
        }

        final Event event = events.get(position);
        if (event != null) {

            // Change color of row.
            assert convertView != null;
            final LinearLayout row = (LinearLayout) convertView.findViewById(R.id.event_row);
            String backColor = ((position % 2) == 0) ? "#f6f6f6" : "#ffffff";
            row.setBackgroundColor(Color.parseColor(backColor));

            // Distance.
            final TextView td = (TextView) convertView.findViewById(R.id.distance);
            float distance = Float.valueOf(event.getLocation()).floatValue();
            td.setText((Math.round(distance * 100) / 100) + "m");

            // Title.
            final TextView tt = (TextView) convertView.findViewById(R.id.event_title);
            String title = event.getTitle();
            tt.setText(title);

            // Hour.
            final TextView th = (TextView) convertView.findViewById(R.id.event_hour);
            String hour = event.getStartHour();
            if (hour.length() == 0) {
                hour = context.getString(R.string.event_whole_day);
            }
            th.setText(hour);

            // Set on touch listener.
            row.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    String backColor = ((position % 2) == 0) ? "#f6f6f6" :  "#ffffff";
                    switch(motionEvent.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            row.setBackgroundColor(Color.parseColor("#ef4f3f"));
                            th.setTextColor(Color.parseColor("#ffffff"));
                            td.setTextColor(Color.parseColor("#ffffff"));
                            tt.setTextColor(Color.parseColor("#ffffff"));
                            break;
                        case MotionEvent.ACTION_CANCEL:
                            row.setBackgroundColor(Color.parseColor(backColor));
                            th.setTextColor(Color.parseColor("#f14f44"));
                            td.setTextColor(Color.parseColor("#323232"));
                            tt.setTextColor(Color.parseColor("#323232"));
                            break;
                        case MotionEvent.ACTION_UP:
                            row.setBackgroundColor(Color.parseColor(backColor));
                            th.setTextColor(Color.parseColor("#f14f44"));
                            td.setTextColor(Color.parseColor("#323232"));
                            tt.setTextColor(Color.parseColor("#323232"));
                            Intent intent = new Intent(context, EventDetail.class);
                            intent.putExtra("eventId", event.getId());
                            context.startActivity(intent);
                            break;
                    }
                    return true;
                }
            });


        }

        return convertView;
    }
}