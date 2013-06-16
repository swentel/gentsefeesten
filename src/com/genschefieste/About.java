package com.genschefieste;

import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.widget.TextView;

public class About extends BaseActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.about);

        String text = (String)getResources().getText(R.string.about_info);
        TextView t = (TextView) findViewById(R.id.about_info);
        t.setMovementMethod(LinkMovementMethod.getInstance());
        t.setText(Html.fromHtml(text));

        super.onCreate(savedInstanceState);
    }
}
