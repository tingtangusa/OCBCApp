package com.jason.ocbcapp;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
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

    private ArrayList<String> branchesList;
    private BranchesAdapter branchesAdapter;

    public LeastWaitingTimeListFragment() {
    }
    
    private void initBranchesList() {
        branchesList = new ArrayList<String>();
        branchesList.addAll(Arrays.asList(getResources().getStringArray(
                R.array.branches)));
        branchesAdapter = new BranchesAdapter(getActivity(), branchesList);
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

        this.setEmptyText(getString(R.string.hello_world));

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
            branchesList.add(0, "Added after refresh...");
            branchesAdapter.notifyDataSetChanged();

            // Call onRefreshComplete when the list has been refreshed.
            mPullRefreshListView.onRefreshComplete();

            super.onPostExecute(result);
        }
    }

}