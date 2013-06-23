package com.genschefieste;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class AroundMePre extends BaseActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.around_me_pre);
        addTopbarListeners = false;

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {

            @Override
            public void run() {
                Intent GoAroundMe = new Intent(getBaseContext(), AroundMe.class);
                startActivity(GoAroundMe);
            }
        }, 500);

        super.onCreate(savedInstanceState);
    }

}
