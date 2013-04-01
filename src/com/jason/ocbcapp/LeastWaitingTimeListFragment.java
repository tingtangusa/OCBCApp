package com.jason.ocbcapp;

import java.util.ArrayList;
import java.util.Arrays;

import android.app.ListFragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ListView;
import com.handmark.pulltorefresh.extras.listfragment.PullToRefreshListFragment;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

public class LeastWaitingTimeListFragment extends ListFragment {

    static final String APP_TAG = MainActivity.APP_TAG;

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

        this.getListView().setTextFilterEnabled(true);

        this.setListAdapter(branchesAdapter);

        this.setEmptyText(getString(R.string.hello_world));

    }
}
