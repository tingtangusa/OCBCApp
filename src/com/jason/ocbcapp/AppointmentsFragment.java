package com.jason.ocbcapp;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.Toast;
import android.app.DatePickerDialog;
import android.app.Dialog;

public class AppointmentsFragment extends Fragment {

    ArrayList<CharSequence> dateList;
    ArrayAdapter<CharSequence> dateAdapter;
    SimpleDateFormat df = new SimpleDateFormat("EEE, dd MMM, yyyy");
    

    public AppointmentsFragment() {

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Setup date spinner
        Spinner dateSpinner = (Spinner) getView()
                .findViewById(R.id.dateSpinner);
        dateList = new ArrayList<CharSequence>();
        Date currentDate = new Date();
        dateList.add(df.format(currentDate));
        dateAdapter = new ArrayAdapter<CharSequence>(getView().getContext(),
                android.R.layout.simple_spinner_dropdown_item, dateList);
        dateSpinner.setAdapter(dateAdapter);

        if (dateList.size() < 2) {
            dateSpinner.setClickable(false);
            dateSpinner.setOnTouchListener(new View.OnTouchListener() {

                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    // TODO Auto-generated method stub
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        DialogFragment newFragment = new AppointmentsFragment.DatePickerFragment();
                        newFragment.show(getFragmentManager(), "datePicker");
                        return true;
                    } else
                        return false;
                }
            });

        }
        
        
        // Setup submit button
        Button submitButton = (Button) getView().findViewById(R.id.submitButton);
        submitButton.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                Dialog d = new Dialog(getActivity());
                d.setContentView(R.layout.activity_circle_screen);
                d.show();
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View apptView = inflater.inflate(R.layout.fragment_appt, container,
                false);

        return apptView;
    }

    public class DatePickerFragment extends DialogFragment implements
            DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        @Override
        public void onDateSet(DatePicker view, int year, int month, int day) {
            // Do something with the date chosen by the user

            SimpleDateFormat parseDF = new SimpleDateFormat("dd MM yyyy");
            try {
                Date chosenDate = parseDF.parse(String.format("%d %d %d", day, month + 1, year));
                //Toast.makeText(getActivity(), "date = " + chosenDate.toString() + "\n" + String.format("%d %d %d", day, month + 1, year), Toast.LENGTH_LONG).show();
                dateAdapter.clear();
                dateAdapter.add(df.format(chosenDate));
            } catch (ParseException e) {
                // TODO Auto-generated catch block
                Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG)
                        .show();
                e.printStackTrace();
            }
        }
    }
}