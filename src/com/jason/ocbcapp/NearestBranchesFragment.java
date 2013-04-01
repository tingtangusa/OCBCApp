package com.jason.ocbcapp;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.Scanner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.handmark.pulltorefresh.extras.listfragment.PullToRefreshListFragment;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

/**
 * @author jason List the 15 nearest branches.
 */
public class NearestBranchesFragment extends PullToRefreshListFragment {

    private ArrayList<Branch> branches;

    private ArrayList<Branch> nearestBranchesList;
    private ArrayAdapter<String> nearestBranchesAdapter;

    private LocationManager manager;

    // number of branches to show
    private static final int NUM_BRANCHES = 15;

    private static final int MIN_UPDATE_INTERVAL = 0;
    private static final int MIN_DISTANCE = 0;

    private PullToRefreshListView nearestBranchesListView;

    private String APP_TAG = MainActivity.APP_TAG;

    private Location currentLocation;

    static final Comparator<Branch> DIST_ORDER = new Comparator<Branch>() {
        public int compare(Branch b1, Branch b2) {
            double d1 = b1.getDistFromUser();
            double d2 = b2.getDistFromUser();
            if (d1 < d2) return -1;
            if (d1 > d2) return 1;
            return 0;
        }
    };

    public NearestBranchesFragment() {

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setupLocationManager();
        setupListView();
    }

    /**
     * 
     */
    private void setupListView() {
        nearestBranchesListView = this.getPullToRefreshListView();
        nearestBranchesListView
                .setOnRefreshListener(new OnRefreshListener<ListView>() {
                    @Override
                    public void onRefresh(
                            PullToRefreshBase<ListView> refreshView) {
                        //User pulled list to refresh, get fresh location, refresh list
                        setupLocationManager();
                    }
                });
    }

    /**
     * 
     */
    private void setupLocationManager() {
        manager = (LocationManager) getActivity().getSystemService(
                Context.LOCATION_SERVICE);
        LocationListener locationListener = new LocationListener() {
            /**
             * Received new location, refresh branches list
             */
            public void onLocationChanged(Location location) {
                Log.d(APP_TAG, "location changed");
                currentLocation = location;
                refreshNearestBranchesList();
            }

            public void onProviderDisabled(String provider) {
            }

            public void onProviderEnabled(String provider) {
            }

            public void onStatusChanged(String provider, int status,
                    Bundle extras) {
            }
        };
        manager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                MIN_UPDATE_INTERVAL, MIN_DISTANCE, locationListener);
    }

    protected void refreshNearestBranchesList() {
        Boolean isNotSetup = branches == null || branches.isEmpty();
        if (isNotSetup)
            setupBranchesList();
        else {
            setupNearestBranchestList();
            nearestBranchesAdapter.notifyDataSetChanged();
            // Call onRefreshComplete when the list has been refreshed.
            nearestBranchesListView.onRefreshComplete();
        }
    }

    /**
     * Makes a copy of the branches list and sorts the copy ordered by the
     * distance from the branch to the user.
     */
    private void setupNearestBranchestList() {
        nearestBranchesList = new ArrayList<Branch>(branches);
        calculateDistanceForAllBranches();
        Collections.sort(nearestBranchesList, DIST_ORDER);
        nearestBranchesList = take(NUM_BRANCHES, nearestBranchesList);
    }

    private <E> ArrayList<E> take(int n, ArrayList<E> list) {
        ArrayList<E> result = new ArrayList<E>();
        for (int i = 0; i < n && i < list.size(); i ++) {
            result.add(list.get(i));
        }
        return result;
    }

    private void calculateDistanceForAllBranches() {
        for (Branch branch : branches) {
            double distFromUser = getUserAndBranchCoordinatesCalculateDist(branch);
            branch.setDistFromUser(distFromUser);
        }
    }

    /**
     * @param branch
     * @return distance between branch and user
     */
    private double getUserAndBranchCoordinatesCalculateDist(Branch branch) {
        double branchLat = branch.getLatitude();
        double branchLong = branch.getLongitude();
        double userLat = currentLocation.getLatitude();
        double userLong = currentLocation.getLongitude();
        double distFromUser = calculateDistance(branchLat, branchLong, userLat, userLong);
        return distFromUser;
    }

    /**
     * Returns the distance between the two coordinates in kilometers.
     * @param lat1
     * @param lng1
     * @param lat2
     * @param lng2
     * @return distance in kilometers
     */
    private static double calculateDistance(double lat1, double lng1, double lat2, double lng2) {
        double earthRadius = 3958.75;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLng = Math.toRadians(lng2 - lng1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1))
                * Math.cos(Math.toRadians(lat2)) * Math.sin(dLng / 2)
                * Math.sin(dLng / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double dist = earthRadius * c;

        int meterConversion = 1609;

        return dist * meterConversion;
    }

    private void setupNearestBranchesAdapter() {
        Log.d(APP_TAG, "Branches after sort = " + branches);
        ArrayList<String> names = new ArrayList<String>();
        for (Branch branch : nearestBranchesList) {
            names.add(branch.getName());
        }
        nearestBranchesAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, names);
        this.setListAdapter(nearestBranchesAdapter);
    }

    private void setupBranchesList() {
        branches = new ArrayList<Branch>();
        GetAllBranchesTask task = new GetAllBranchesTask();
        task.execute();
    }

    public class GetAllBranchesTask extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void...args) {
            // init our variables for the http connection
            String responseString = null;
            InputStream responseStream = null;
            HttpURLConnection urlConnection = null;
            OutputStreamWriter writer = null;

            try {
                URL url = new URL("http://cutebalrog.com:8080/OCBC-QM-Server-web/webresources/Branch/GetAllBranches");
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.addRequestProperty("Content-type", "application/json");
                urlConnection.connect();

                responseStream = new BufferedInputStream( urlConnection.getInputStream());
                Log.d(APP_TAG, "response code: " + urlConnection.getResponseCode());
                responseString = readStream(responseStream);
            } catch (Exception e) {
                Log.e(APP_TAG, e.getMessage());
                return "";
            }
            return responseString;
        }

        @Override
        protected void onPostExecute(String result) {
            if (TextUtils.isEmpty(result)) {
                Log.w(APP_TAG, "no result from GetAllBranchesTask");
            } else {
                Log.d(APP_TAG, "got response for GetAllBranchesTask: " + result);
                try {
                    JSONObject jobj = new JSONObject(result);
                    JSONArray allBranchesInfo = jobj.getJSONArray("branchEntireInfo");
                    for (int i = 0; i < allBranchesInfo.length(); i ++) {
                        JSONObject branchJsonObject = allBranchesInfo.getJSONObject(i);
                        Branch branch = makeBranchFromJson(branchJsonObject);
                        branches.add(branch);
                    }
                    Log.d(APP_TAG, "branches = " + branches.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                setupNearestBranchestList();
                setupNearestBranchesAdapter();
            }
        }

        private Branch makeBranchFromJson(JSONObject branchJsonObject) {
            int id = 0;
            String name = null;
            int waitingTime = 0;
            double latitude = 0;
            double longitude = 0;
            try {
                JSONObject branchInfoObject = branchJsonObject.getJSONObject("branch");
                id = branchInfoObject.getInt("branchId");
                name = branchInfoObject.getString("name");
                waitingTime = branchJsonObject.getInt("waitingTime");
                latitude = branchInfoObject.getDouble("latitude");
                longitude = branchInfoObject.getDouble("longitude");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Branch branch = new Branch(id, name, waitingTime, latitude, longitude);
            return branch;
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

    }
}
