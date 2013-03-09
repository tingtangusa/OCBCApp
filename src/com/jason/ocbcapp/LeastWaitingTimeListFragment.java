package com.jason.ocbcapp;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Scanner;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

import com.handmark.pulltorefresh.extras.listfragment.PullToRefreshListFragment;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

public class LeastWaitingTimeListFragment extends PullToRefreshListFragment {

    static final String APP_TAG = MainActivity.APP_TAG;
    private PullToRefreshListView mPullRefreshListView;

    private LinkedList<String> branchesList;
    private ArrayAdapter<String> branchesAdapter;

    public LeastWaitingTimeListFragment() {
    }
    
    private void initBranchesList() {
        branchesList = new LinkedList<String>();
        branchesList.addAll(Arrays.asList(getResources().getStringArray(
                R.array.branches)));
        branchesAdapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_list_item_1, branchesList);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        initBranchesList();
        mPullRefreshListView  = this
            .getPullToRefreshListView();
        mPullRefreshListView
                .setOnRefreshListener(new OnRefreshListener<ListView>() {
                    @Override
                    public void onRefresh(
                            PullToRefreshBase<ListView> refreshView) {
                        GetLeastWaitingTimeTask task = new GetLeastWaitingTimeTask();
                        task.execute();
                    }
                });

        this.getListView().setTextFilterEnabled(true);

        this.setListAdapter(branchesAdapter);
        ListView list = this.getListView();
        list.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> av, View view, int position,
                    long id) {
                // TODO Auto-generated method stub
                // showToast("clicked id = " + id + ", pos = " + position);
                Log.d(APP_TAG, "executing task");
                RequestTask task = new RequestTask();
                task.execute("http://cutebalrog.com:8080/OCBC-QM-Server-web/webresources/Branch/GetBranch/"
                        + id);
            }
        });
        this.setEmptyText(getString(R.string.hello_world));

    }

    class RequestTask extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... uri) {
            String responseString = null;
            InputStream responseStream = null;
            HttpURLConnection urlConnection = null;
            try {
                URL url = new URL(uri[0]);
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
                String id = jObj.getString("waitingTime");
                Toast.makeText(getActivity(), id, Toast.LENGTH_SHORT).show();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private class GetLeastWaitingTimeTask extends
            AsyncTask<Void, Void, String[]> {

        @Override
        protected String[] doInBackground(Void... arg0) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
            }
            return getResources().getStringArray(R.array.branches);
        }

        @Override
        protected void onPostExecute(String[] result) {
            branchesList.addFirst("Added after refresh...");
            branchesAdapter.notifyDataSetChanged();

            // Call onRefreshComplete when the list has been refreshed.
            mPullRefreshListView.onRefreshComplete();

            super.onPostExecute(result);
        }
    }

}
