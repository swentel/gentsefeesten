package com.genschefieste;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class BackActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Used as an activity to set as parent. This makes the back button behave nicer.
        // The chance that this one is used is close to 0.
        Intent main = new Intent(getBaseContext(), Main.class);
        startActivity(main);
        this.finish();
    }

}
