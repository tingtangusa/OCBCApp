package com.jason.ocbcapp;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Scanner;

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
import android.view.ViewParent;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;

public class AppointmentsFragment extends Fragment {

    private static final String APP_TAG = MainActivity.APP_TAG;

    ArrayList<CharSequence> dateList;
    static ArrayAdapter<CharSequence> dateAdapter;
    static SimpleDateFormat df = new SimpleDateFormat("EEE, dd MMM, yyyy");
    static Date chosenDate = new Date();

    Spinner branchesSpinner = null;
    ArrayList<Integer> checkedStates = null;
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
        Spinner dateSpinner = (Spinner) vi.findViewById(R.id.dateSpinner);
        dateList = new ArrayList<CharSequence>();
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
        checkedStates = new ArrayList<Integer>();

        // Setup submit button
        Button submitButton = (Button) vi.findViewById(R.id.submitButton);
        submitButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // setup variables to send
                String dateString = "2013-03-14T03:45:53.447Z";
                String userToken = getTokenFromPrefs();
                String branchId = "" + branchesSpinner.getSelectedItemPosition();
                JSONObject jobj = new JSONObject();
                JSONArray jsonStates = new JSONArray();
                for (int i = 0; i < checkboxes.size(); i++) {
                    if (checkboxes.get(i).isChecked()) {
                        checkedStates.add(i - 1);
                        jsonStates.put(i - 1);
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

            SimpleDateFormat parseDF = new SimpleDateFormat("dd MM yyyy");
            try {
                chosenDate = parseDF.parse(String.format("%d %d %d", day,
                        month + 1, year));
                // Toast.makeText(getActivity(), "date = " +
                // chosenDate.toString() + "\n" + String.format("%d %d %d", day,
                // month + 1, year), Toast.LENGTH_LONG).show();
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
                Log.d(APP_TAG,
                        "response code: " + urlConnection.getResponseCode());
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