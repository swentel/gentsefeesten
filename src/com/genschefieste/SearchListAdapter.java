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
 * Favorite list adapter.
 */
public class SearchListAdapter extends BaseAdapter implements OnClickListener {
    private final Context context;
    private final List<Event> events;
    private int currentDate = 0;

    private static final int DAY = 0;
    private static final int EVENT = 1;
    private LayoutInflater mInflater;

    public SearchListAdapter(Context context, List<Event> events) {
        this.context = context;
        this.events = events;
        this.mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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

    @Override
    public int getViewTypeCount() {
        return 2;
    }
    @Override
    public int getItemViewType(int position) {
        return events.get(position).getExternalId() == 0 ? DAY : EVENT;
    }

    public static class ViewHolder {
        public TextView day;
        public TextView hour;
        public TextView title;
        public ImageView image;
        public int eventId;
        public LinearLayout row;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        int type = getItemViewType(position);
        Event event = events.get(position);

        if (convertView == null) {
            holder = new ViewHolder();
            switch (type) {
                case DAY:
                    convertView = mInflater.inflate(R.layout.day_item, null);
                    holder.day = (TextView)convertView.findViewById(R.id.day_title);
                    break;
                case EVENT:
                    convertView = mInflater.inflate(R.layout.search_list_item, null);
                    holder.hour = (TextView)convertView.findViewById(R.id.event_hour);
                    holder.title = (TextView)convertView.findViewById(R.id.event_title);
                    holder.image = (ImageView)convertView.findViewById(R.id.event_favorite);
                    holder.row = (LinearLayout) convertView.findViewById(R.id.event_row);
                    holder.eventId = event.getId();
                    break;
            }
            convertView.setTag(holder);
        }
        else {
            holder = (ViewHolder)convertView.getTag();
        }

        if (event != null) {
            if (event.getExternalId() == 0) {
                String dayText = BaseActivity.getDateFromTimestamp(event.getDate(), context);
                holder.day.setText(dayText);
            }
            else {
                // Hour.
                String hour = event.getStartHour();
                if (hour.length() == 0) {
                    hour = context.getString(R.string.event_whole_day);
                }
                holder.hour.setText(hour);

                // Title.
                String title = event.getTitle();
                holder.title.setText(title);

                // Favorite.
                if (event.getFavorite() == 0) {
                    holder.image.setImageResource(R.drawable.fav_off_small);
                } else {
                    holder.image.setImageResource(R.drawable.fav_on_small);
                }

                holder.eventId = event.getId();
                convertView.setOnTouchListener(eventTouch);
            }
        }

        return convertView;
    }

    View.OnTouchListener eventTouch = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent motionEvent) {
            ViewHolder holder = (ViewHolder)v.getTag();
            switch(motionEvent.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    holder.hour.setTextColor(Color.parseColor("#ffffff"));
                    holder.title.setTextColor(Color.parseColor("#ffffff"));
                    holder.row.setBackgroundColor(Color.parseColor("#ef4f3f"));
                    break;
                case MotionEvent.ACTION_CANCEL:
                    holder.hour.setTextColor(Color.parseColor("#ef4f3f"));
                    holder.title.setTextColor(Color.parseColor("#323232"));
                    holder.row.setBackgroundColor(Color.parseColor("#ffffff"));
                    break;
                case MotionEvent.ACTION_UP:
                    holder.hour.setTextColor(Color.parseColor("#ef4f3f"));
                    holder.title.setTextColor(Color.parseColor("#323232"));
                    holder.row.setBackgroundColor(Color.parseColor("#ffffff"));
                    int eventId = holder.eventId;
                    Intent intent = new Intent(context, EventDetail.class);
                    intent.putExtra("eventId", eventId);
                    context.startActivity(intent);
                    break;
            }
            return true;
        }
    };
}