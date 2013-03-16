package com.jason.ocbcapp;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class GetQrActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_qr);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.get_qr, menu);
        return true;
    }

}
