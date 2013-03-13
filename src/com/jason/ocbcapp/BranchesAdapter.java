package com.jason.ocbcapp;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

// A base adapter with modified elements to put buttons in list elements
public class BranchesAdapter extends BaseAdapter {

    private static final String APP_TAG = MainActivity.APP_TAG;
    
    private Activity activity;
    private ArrayList<String> data;
    private static LayoutInflater inflater = null;
    
    public BranchesAdapter(Activity activity, ArrayList<String> data) {
        this.activity = activity;
        this.data = data;
        inflater = activity.getLayoutInflater();
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View vi = convertView;
        if (convertView == null)
            vi = inflater.inflate(R.layout.branches_row, null);

        TextView name = (TextView) vi.findViewById(R.id.branchName);
        Button btn = (Button) vi.findViewById(R.id.waitingTimeBtn);
        ProgressBar pb = (ProgressBar) vi.findViewById(R.id.progressBar);
        pb.setVisibility(View.INVISIBLE);

        name.setText(data.get(position));
        btn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Button btnClicked = (Button) v;
                Log.d(APP_TAG, "executing task");
                RequestTask task = new RequestTask();
                task.execute(new Pair<String, Button>("http://cutebalrog.com:8080/OCBC-QM-Server-web/webresources/Branch/GetBranch/"
                        + position, btnClicked));
                View view = (View) v.getParent();
                ProgressBar pb = (ProgressBar) view.findViewById(R.id.progressBar);
                pb.setVisibility(View.VISIBLE);
                Log.d(APP_TAG, "Visibility: " + pb.getVisibility());
                pb.bringToFront();
            }
        });
        return vi;
    }
    class RequestTask extends AsyncTask<Pair<String,Button>, String, String> {
        
        private Button btnClicked = null;
        @Override
        protected String doInBackground(Pair<String,Button>... pair) {
            String responseString = null;
            InputStream responseStream = null;
            HttpURLConnection urlConnection = null;
            try {
                URL url = new URL(pair[0].first);
                btnClicked = pair[0].second;
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.addRequestProperty("Content-type",
                        "application/json");
                responseStream = new BufferedInputStream(
                        urlConnection.getInputStream());
                Log.d(APP_TAG,
                        "response code: " + urlConnection.getResponseCode());
                responseString = readStream(responseStream);
            } catch (Exception e) {
                Log.e(APP_TAG, e.getMessage());
            }
            return responseString;
        }

        private String readStream(InputStream inputStream) {
            // TODO Auto-generated method stub
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
            Log.d(APP_TAG, "finished request task, showing waiting time");
            try {
                JSONObject jObj = new JSONObject(result);
                String waitingTime = jObj.getString("waitingTime");
                btnClicked.setText(waitingTime + "mins");
                View parentView = (View) btnClicked.getParent();
                ProgressBar pb = (ProgressBar) parentView.findViewById(R.id.progressBar);
                pb.setVisibility(View.INVISIBLE);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

}
