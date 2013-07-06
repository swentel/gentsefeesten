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

import java.util.List;

/**
 * ATM list adapter.
 */
public class AtmListAdapter extends ArrayAdapter implements OnClickListener {
    private final Context context;
    private final List<Atm> atms;

    public AtmListAdapter(Context context, List<Atm> atms) {
        super(context, R.layout.atm_item, atms);
        this.context = context;
        this.atms = atms;
    }

    public void onClick(View view) {

    }

    public View getView(final int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.atm_item, null);
        }

        // Change color of row.
        assert convertView != null;
        LinearLayout row = (LinearLayout) convertView.findViewById(R.id.single_row);
        String backColor = ((position % 2) == 0) ? "#f6f6f6" : "#ffffff";
        row.setBackgroundColor(Color.parseColor(backColor));

        Atm atm = atms.get(position);
        TextView title = (TextView) convertView.findViewById(R.id.atm_title);
        title.setText(atm.getName());
        TextView address = (TextView) convertView.findViewById(R.id.atm_address);
        address.setText(atm.getAddress());

        return convertView;
    }
}