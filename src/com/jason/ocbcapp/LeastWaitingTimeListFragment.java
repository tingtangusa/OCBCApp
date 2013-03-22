package com.jason.ocbcapp;

import java.util.ArrayList;
import java.util.Arrays;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ListView;
import com.handmark.pulltorefresh.extras.listfragment.PullToRefreshListFragment;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

public class LeastWaitingTimeListFragment extends PullToRefreshListFragment {

    static final String APP_TAG = MainActivity.APP_TAG;
    private PullToRefreshListView mPullRefreshListView;

    private ArrayList<String> branchesList;
    private BranchAdapter branchesAdapter;

    public LeastWaitingTimeListFragment() {
    }
    
    private void initBranchesList() {
        branchesList = new ArrayList<String>();
        branchesList.addAll(Arrays.asList(getResources().getStringArray(
                R.array.branches)));
        branchesAdapter = new BranchAdapter(getActivity(), branchesList);
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
