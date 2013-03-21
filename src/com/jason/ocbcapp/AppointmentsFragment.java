package com.jason.ocbcapp;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Scanner;
import java.util.TimeZone;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;

public class AppointmentsFragment extends Fragment {

    private static final String APP_TAG = MainActivity.APP_TAG;

    private enum Services { ACCOUNT_OPENING, CREDIT_CARD, LOAN, OTHERS };

    ArrayList<CharSequence> dateList;
    static ArrayAdapter<CharSequence> dateAdapter;
    static SimpleDateFormat df = new SimpleDateFormat("EEE, dd MMM, yyyy");
    static Calendar chosenDate = Calendar.getInstance();

    Spinner branchesSpinner = null;
    Spinner dateSpinner = null;
    Spinner timeSpinner = null;

    ArrayList<String> selectedServices = null;
    ArrayList<CheckBox> checkboxes = null;

    Dialog loadingDialog = null;
    AlertDialog successDialog = null;

    public AppointmentsFragment() {

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        View vi = getView();
        // Setup date spinner
        dateSpinner = (Spinner) vi.findViewById(R.id.dateSpinner);
        timeSpinner = (Spinner) vi.findViewById(R.id.timeSpinner);

        dateList = new ArrayList<CharSequence>();

        // set the picker's default date to currentDate
        Date currentDate = new Date();
        dateList.add(df.format(currentDate));
        dateAdapter = new ArrayAdapter<CharSequence>(vi.getContext(),
                android.R.layout.simple_spinner_dropdown_item, dateList);
        dateSpinner.setAdapter(dateAdapter);

        if (dateList.size() < 2) {
            dateSpinner.setClickable(false);
            dateSpinner.setOnTouchListener(new View.OnTouchListener() {

                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    // TODO Auto-generated method stub
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        DialogFragment newFragment = new DatePickerFragment();
                        newFragment.show(getFragmentManager(), "datePicker");
                        return true;
                    } else
                        return false;
                }
            });

        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        
        builder.setMessage(R.string.label_queue_text).setTitle("Appointment Booked");
        builder.setPositiveButton("Got it!", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        successDialog = builder.create();
        
        branchesSpinner = (Spinner) vi.findViewById(R.id.branchesSpinner);

        CheckBox accCheckBox = (CheckBox) vi.findViewById(R.id.accCheckBox);
        CheckBox ccCheckBox = (CheckBox) vi.findViewById(R.id.ccCheckBox);
        CheckBox loanCheckBox = (CheckBox) vi.findViewById(R.id.loanCheckBox);
        CheckBox othersCheckBox = (CheckBox) vi
                .findViewById(R.id.othersCheckBox);

        checkboxes = new ArrayList<CheckBox>();
        checkboxes.add(accCheckBox);
        checkboxes.add(ccCheckBox);
        checkboxes.add(loanCheckBox);
        checkboxes.add(othersCheckBox);
        selectedServices = new ArrayList<String>();

        // Setup submit button
        Button submitButton = (Button) vi.findViewById(R.id.submitButton);
        submitButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // setup variables to send
                String dateString = "" + getSelectedTimestamp();
                String userToken = getTokenFromPrefs();
                String branchId = "" + branchesSpinner.getSelectedItemPosition();
                JSONObject jobj = new JSONObject();
                JSONArray jsonStates = new JSONArray();
                for (int i = 0; i < checkboxes.size(); i++) {
                    if (checkboxes.get(i).isChecked()) {
                        Services service = Services.values()[i];
                        String serviceStr = getServiceString(service);
                        selectedServices.add(serviceStr);
                        jsonStates.put(serviceStr);
                    }
                }
                try {
                    jobj.accumulate("appDate", dateString);
                    jobj.accumulate("branchId", branchId);
                    jobj.accumulate("tokenOfCustomer", userToken);
                    jobj.accumulate("serviceType", jsonStates);
                    Log.d(APP_TAG, "json object: " + jobj.toString());
                    UploadRequestTask task = new UploadRequestTask();
                    String url = "http://cutebalrog.com:8080/OCBC-QM-Server-web/webresources/Queue/addOnlineQueue";
                    task.execute(new Pair<String, JSONObject>(url, jobj));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                // show loading spinner
                loadingDialog = new Dialog(getActivity());
                loadingDialog.setContentView(R.layout.dialog_loading_screen);
                loadingDialog.show();
            }

            /*
             * Retrieves the userToken string from the phone's shared preference
             */
            private String getTokenFromPrefs() {
                SharedPreferences settings = getActivity().getSharedPreferences(MainActivity.PREFS_NAME, 0);
                String userToken = settings.getString("userToken", "");
                Log.d(APP_TAG, userToken);
                return userToken;
            }
        });
    }

    protected String getServiceString(Services service) {
        switch (service) {
        case ACCOUNT_OPENING:
            return "account_opening";
        case CREDIT_CARD:
            return "credit_card";
        case LOAN:
            return "loan";
        case OTHERS:
            return "others";
        default:
            return "ERROR!";
        }
    }

    protected long getSelectedTimestamp() {
        int[] timeSlots = getResources().getIntArray(R.array.start_time_slots);
        int selectedTime = timeSlots[timeSpinner.getSelectedItemPosition()];
        // set hour of day and leave mins, sec and ms blank
        chosenDate.setTimeZone(TimeZone.getTimeZone("GMT"));
        chosenDate.set(Calendar.HOUR_OF_DAY, selectedTime);
        chosenDate.set(Calendar.MINUTE, 0);
        chosenDate.set(Calendar.SECOND, 0);
        chosenDate.set(Calendar.MILLISECOND, 0);
        Log.d(APP_TAG, "cal datetime: " + chosenDate.toString());
        Log.d(APP_TAG, "cal getTimeInMillis datetime: " + chosenDate.getTimeInMillis());
        return chosenDate.getTimeInMillis();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View apptView = inflater.inflate(R.layout.fragment_appt, container,
                false);

        return apptView;
    }

    public static class DatePickerFragment extends DialogFragment implements
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

            chosenDate = new GregorianCalendar(year, month, day);

            Log.d(APP_TAG, "Chosen date: " + chosenDate.getTime().toString());
            dateAdapter.clear();
            dateAdapter.add(df.format(chosenDate.getTime()));

        }
    }

    class UploadRequestTask extends AsyncTask<Pair<String, JSONObject>, String, String> {

        @Override
        protected String doInBackground(Pair<String, JSONObject>... pair) {
            String responseString = null;
            InputStream responseStream = null;
            HttpURLConnection urlConnection = null;
            OutputStreamWriter writer = null;
            try {
                URL url = new URL(pair[0].first);
                JSONObject data = pair[0].second;
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setDoOutput(true);
                urlConnection.setRequestProperty("Accept", "application/json");
                urlConnection.addRequestProperty("Content-type", "application/json");
                urlConnection.setRequestMethod("POST");
                urlConnection.connect();
                
                writer = new OutputStreamWriter(urlConnection.getOutputStream());
                writer.write(data.toString());
                writer.close();
                responseStream = new BufferedInputStream( urlConnection.getInputStream());
                Log.d(APP_TAG, "response code: " + urlConnection.getResponseCode());
                responseString = readStream(responseStream);
            } catch (Exception e) {
                Log.e(APP_TAG, e.getMessage());
            }
            return responseString;
        }

        private String readStream(InputStream inputStream) {
            StringBuilder buf = new StringBuilder();
            Scanner sc = null;
            try {

                sc = new Scanner(inputStream);
                while (sc.hasNext()) {
                    buf.append(sc.next());
                }

            } catch (Exception e) {
                Log.e(APP_TAG, e.getMessage());
            }
            return buf.toString();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            // Extract waiting time from json object
            Log.d(APP_TAG, "finished post request task");
            Log.d(APP_TAG, "result: " + result);
            loadingDialog.dismiss();
            successDialog.setMessage(getString(R.string.label_queue_text) + " " + result);
            successDialog.show();
        }
    }

}