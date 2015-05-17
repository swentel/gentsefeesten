package com.genschefieste;

import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.widget.TextView;

public class GentInfo extends BaseActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.gent_info);

        String text = (String)getResources().getText(R.string.gent_info);
        TextView t = (TextView) findViewById(R.id.gent_info);
        t.setMovementMethod(LinkMovementMethod.getInstance());
        t.setText(Html.fromHtml(text));

        super.onCreate(savedInstanceState);

        BaseActivity.sendGaView("Gent info", getApplicationContext());
    }

}
