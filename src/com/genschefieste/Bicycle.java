package com.genschefieste;

import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.widget.TextView;

public class Bicycle extends BaseActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.bicycle);

        BaseActivity.sendGaView("Bicycle info", getApplicationContext());

        String text = (String)getResources().getText(R.string.bicycle_info);
        TextView t = (TextView) findViewById(R.id.bicycle_info);
        t.setMovementMethod(LinkMovementMethod.getInstance());
        t.setText(Html.fromHtml(text));

        super.onCreate(savedInstanceState);
    }

}
