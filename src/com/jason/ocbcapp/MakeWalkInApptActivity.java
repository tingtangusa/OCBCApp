package com.jason.ocbcapp;

import java.util.ArrayList;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.support.v4.app.NavUtils;
import android.text.AndroidCharacter;

public class MakeWalkInApptActivity extends Activity {

    private ArrayList<CheckBox> checkboxes;
    public static final String intentExtraName = "servicesSelected";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_walk_in_appt);
        // Show the Up button in the action bar.
        setupActionBar();
        setupSubmitButton();
    }

    /**
     * Handle the on click action for the submit {@link android.widget.Button}
     */
    private void setupSubmitButton() {
        Button submitButton = (Button) findViewById(R.id.submitButton);
        View.OnClickListener submitListener = new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if (isFilledUp()) {
                    ArrayList<String> servicesSelected = getSelectedServices();
                    startQrActivity(servicesSelected);
                } else {
                    showSelectServicesDialog();
                }
            }
        };
        submitButton.setOnClickListener(submitListener);
    }

    protected void showSelectServicesDialog() {
        AlertDialog selectServicesDialog = createSelectSevicesDialog();
        selectServicesDialog.show();
    }

    /**
     * @return {@link android.app.AlertDialog} selectServicesDialog
     */
    private AlertDialog createSelectSevicesDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        // chaining methods jquery style
        builder.setTitle("Services not selected").setMessage(
                "Please select at least one service.").setPositiveButton("OK",
                new OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        AlertDialog selectServicesDialog = builder.create();
        return selectServicesDialog;
    }

    /**
     * Passes the services that the user has selected to the
     * {@link ShowQrActivity} activity and starts it.
     * 
     * @param servicesSelected
     */
    protected void startQrActivity(ArrayList<String> servicesSelected) {
        Intent intent = new Intent(this, ShowQrActivity.class);
        intent.putStringArrayListExtra(intentExtraName, servicesSelected);
        startActivity(intent);
    }

    protected ArrayList<String> getSelectedServices() {
        ArrayList<CheckBox> checkboxes = getCheckBoxes();
        ArrayList<String> services = new ArrayList<String>();
        for (CheckBox checkbox : checkboxes) {
            if (checkbox.isChecked()) {
                services.add((String) checkbox.getTag());
            }
        }
        return services;
    }

    /**
     * Checks if the form is filled up.
     * @return true if form is filled.
     */
    protected boolean isFilledUp() {
        ArrayList<CheckBox> checkboxes = getCheckBoxes();
        for (CheckBox checkbox : checkboxes) {
            if (checkbox.isChecked()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Retrieves the {@link android.widget.CheckBox} objects from the layout and
     * assigns the strings associated with it. These strings are required to
     * communicate with other components in the queue system.
     * 
     * @return ArrayList of checkboxes used in this activity.
     */
    private ArrayList<CheckBox> getCheckBoxes() {
        if (checkboxes == null) {
            CheckBox accCb = (CheckBox) findViewById(R.id.accCheckBox);
            accCb.setTag("account_opening");
            CheckBox loanCb = (CheckBox) findViewById(R.id.loanCheckBox);
            loanCb.setTag("loan");
            CheckBox ccCb = (CheckBox) findViewById(R.id.ccCheckBox);
            ccCb.setTag("credit_card");
            CheckBox othersCb = (CheckBox) findViewById(R.id.othersCheckBox);
            othersCb.setTag("others");
            checkboxes = new ArrayList<CheckBox>();
            checkboxes.add(accCb);
            checkboxes.add(loanCb);
            checkboxes.add(ccCb);
            checkboxes.add(othersCb);
        }
        return checkboxes;
    }

    /**
     * Set up the {@link android.app.ActionBar}.
     */
    private void setupActionBar() {

        getActionBar().setDisplayHomeAsUpEnabled(true);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.make_walk_in_appt, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case android.R.id.home:
            // This ID represents the Home or Up button. In the case of this
            // activity, the Up button is shown. Use NavUtils to allow users
            // to navigate up one level in the application structure. For
            // more details, see the Navigation pattern on Android Design:
            //
            // http://developer.android.com/design/patterns/navigation.html#up-vs-back
            //
            NavUtils.navigateUpFromSameTask(this);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
