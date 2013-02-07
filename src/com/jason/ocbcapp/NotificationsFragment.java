package com.jason.ocbcapp;

import java.util.LinkedList;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.handmark.pulltorefresh.extras.listfragment.PullToRefreshListFragment;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

public class NotificationsFragment extends PullToRefreshListFragment {

    private LinkedList<String> notifsList;
    private ArrayAdapter<String> notifsAdapter;

    private PullToRefreshListView notifsListView;

    public NotificationsFragment() {

    }

    private void initializeNotifsList(FragmentActivity activity) {
        notifsList = new LinkedList<String>();
        notifsList
                .add("Appointment Reminder: It's your turn! Please proceed to counter 8.");
        notifsList
                .add("Appointment Reminder: Your appointment is starting in one hour time.");
        notifsAdapter = new ArrayAdapter<String>(activity,
                android.R.layout.simple_list_item_1, notifsList);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initializeNotifsList(getActivity());
        notifsListView = this.getPullToRefreshListView();
        notifsListView.setOnRefreshListener(new OnRefreshListener<ListView>() {
            @Override
            public void onRefresh(PullToRefreshBase<ListView> refreshView) {
                GetNotificationsTask task = new GetNotificationsTask();
                task.execute();
            }
        });

        this.getListView().setTextFilterEnabled(true);

        this.setListAdapter(notifsAdapter);
        this.setEmptyText(getString(R.string.hello_world));

    }

    private class GetNotificationsTask extends AsyncTask<Void, Void, String[]> {

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
            notifsList.addFirst("Added after refresh...");
            notifsAdapter.notifyDataSetChanged();

            // Call onRefreshComplete when the list has been refreshed.
            notifsListView.onRefreshComplete();

            super.onPostExecute(result);
        }
    }
}
