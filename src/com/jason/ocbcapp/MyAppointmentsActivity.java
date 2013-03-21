package com.jason.ocbcapp;

import java.util.ArrayList;

import android.os.Bundle;
import android.app.ActionBar;
import android.app.ListActivity;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;

import com.jason.ocbcapp.Appointment;

public class MyAppointmentsActivity extends ListActivity {

    private String APP_TAG = MainActivity.APP_TAG;

    ArrayList<Appointment> appointments = null;
    ArrayList<String> appointmentBranches =  new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_appointments);

        // Show the up button at the icon
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        initializeAppointmentsList();
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, android.R.id.text1, appointmentBranches);
        this.setListAdapter(adapter);
    }

    private void initializeAppointmentsList() {
        AppointmentsDataSource dataSource = new AppointmentsDataSource(getApplicationContext());
        dataSource.open();
        appointments = new ArrayList<Appointment>(dataSource.getAllAppointments());
        Log.d(APP_TAG, "appointments = " + appointments.toString());
        dataSource.close();
        String[] branches = getResources().getStringArray(R.array.branches);
        Log.d(APP_TAG, "branches" + branches[0]);
        for (Appointment appointment : appointments) {
            Log.d(APP_TAG, appointment.toString());
            String branch = branches[appointment.getBranchId()];
            appointmentBranches.add(branch);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.my_appointments, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case android.R.id.home:
            // app icon clicked; go home
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }
    

}
