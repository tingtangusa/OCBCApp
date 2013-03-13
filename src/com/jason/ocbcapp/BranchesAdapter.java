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
        ViewHolder holder = null;
        
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.branches_row, null);
            
            holder = new ViewHolder();
            holder.name = (TextView) convertView.findViewById(R.id.branchName);
            holder.btn = (Button) convertView.findViewById(R.id.waitingTimeBtn);
            holder.pb = (ProgressBar) convertView.findViewById(R.id.progressBar);
            
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        

        holder.pb.setVisibility(View.INVISIBLE);
        holder.name.setText(data.get(position));
        holder.btn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Button btnClicked = (Button) v;
                View view = (View) v.getParent();
                ProgressBar pb = (ProgressBar) view.findViewById(R.id.progressBar);
                Log.d(APP_TAG, "executing task");
                RequestTask task = new RequestTask();
                task.execute(new Triple<String, Button, ProgressBar>("http://cutebalrog.com:8080/OCBC-QM-Server-web/webresources/Branch/GetBranch/"
                        + position, btnClicked, pb));
                pb.setVisibility(View.VISIBLE);
                Log.d(APP_TAG, "Visibility: " + pb.getVisibility());
                pb.bringToFront();
            }
        });
        return convertView;
    }
    class RequestTask extends AsyncTask<Triple<String,Button,ProgressBar>, String, String> {
        
        private Button btnClicked = null;
        private ProgressBar pb = null;
        @Override
        protected String doInBackground(Triple<String,Button,ProgressBar>... triple) {
            String responseString = null;
            InputStream responseStream = null;
            HttpURLConnection urlConnection = null;
            try {
                URL url = new URL(triple[0].first);
                btnClicked = triple[0].second;
                pb = triple[0].third;
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
                pb.setVisibility(View.INVISIBLE);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
    
    static class ViewHolder {
        TextView name;
        Button btn;
        ProgressBar pb;
    }

    static class Triple<F,S,T> {
        public final F first;
        public final S second;
        public final T third;
        
        public Triple(F first, S second, T third) {
            this.first = first;
            this.second = second;
            this.third = third;
        }
    }
}
