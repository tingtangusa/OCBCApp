package com.jason.ocbcapp;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.Menu;

public class MainActivity extends Activity {

	//Name of the preference SharedPreferences that we are using for the app
    public static final String PREFS_NAME = "OCBCPrefsFile";
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
        // Restore preferences
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);

        // start SetupActivity if user has not setup the app
        boolean hasSetup = settings.getBoolean("hasSetup", false);
        Log.i("OCBCApp", "hasSetup = " + hasSetup);
        startSetup(hasSetup);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

    private void startSetup(boolean hasSetup) {
        if (!hasSetup) {
            Log.i("OCBCApp", "Starting setup");
            Intent intent = new Intent(this, SetupActivity.class);
            startActivity(intent);
        }
    }
    
}
