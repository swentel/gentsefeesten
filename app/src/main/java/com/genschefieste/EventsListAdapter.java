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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

/**
 * Event list adapter.
 */
public class EventsListAdapter extends BaseAdapter implements OnClickListener {
    private final Context context;
    private final List<Event> events;

    public EventsListAdapter(Context context, List<Event> events) {
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
            convertView = inflater.inflate(R.layout.event_list_item, null);
        }

        final Event event = events.get(position);
        if (event != null) {

            // Change color of row.
            assert convertView != null;
            String color = ((position % 2) == 0) ? "#f8f7f1" :  "#ffffff";
            final LinearLayout row = (LinearLayout) convertView.findViewById(R.id.event_row);
            row.setBackgroundColor(Color.parseColor(color));

            // Hour.
            final TextView th = (TextView) convertView.findViewById(R.id.event_hour);
            String hour = event.getStartHour();
            if (hour.length() == 0) {
                hour = context.getString(R.string.event_whole_day);
            }
            th.setText(hour);

            // Title.
            final TextView tt = (TextView) convertView.findViewById(R.id.event_title);
            String title = event.getTitle();
            tt.setText(title);

            // Favorite.
            ImageView i = (ImageView) convertView.findViewById(R.id.event_favorite);
            if (event.getFavorite() == 0) {
                i.setImageResource(R.drawable.fav_off_small);
                i.setScaleType(ImageView.ScaleType.CENTER);
            }
            else {
                i.setImageResource(R.drawable.fav_on_small);
                i.setScaleType(ImageView.ScaleType.CENTER);
            }

            // Set on touch listener.
            row.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    String backColor = ((position % 2) == 0) ? "#f8f7f1" :  "#ffffff";
                    switch(motionEvent.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            row.setBackgroundColor(Color.parseColor("#cda300"));
                            th.setTextColor(Color.parseColor("#333333"));
                            tt.setTextColor(Color.parseColor("#333333"));
                            break;
                        case MotionEvent.ACTION_CANCEL:
                            row.setBackgroundColor(Color.parseColor(backColor));
                            th.setTextColor(Color.parseColor("#cda300"));
                            tt.setTextColor(Color.parseColor("#333333"));
                            break;
                        case MotionEvent.ACTION_UP:
                            row.setBackgroundColor(Color.parseColor(backColor));
                            th.setTextColor(Color.parseColor("#cda300"));
                            tt.setTextColor(Color.parseColor("#333333"));
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