package com.genschefieste;

import android.os.Bundle;
import android.webkit.WebView;

public class Parking extends BaseActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.parking);

        WebView webView = (WebView) findViewById(R.id.parkingView);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl("http://parkingent.be");

        super.onCreate(savedInstanceState);
    }
}
