package com.jason.ocbcapp;

import java.util.ArrayList;

import com.jason.ocbcapp.BranchAdapter.ViewHolder;

import android.app.Activity;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

public class BranchLocationAdapter extends BranchAdapter {

    protected static final String APP_TAG = MainActivity.APP_TAG;
    ArrayList<Pair<String, Double>> branchesWithDistance;

    public BranchLocationAdapter(Activity activity, ArrayList<Pair<String, Double>> branchesWithDistance) {
        super(activity, getBranchesNames(branchesWithDistance));
        this.branchesWithDistance = branchesWithDistance;
    }

    private static ArrayList<String> getBranchesNames(
            ArrayList<Pair<String, Double>> branchesWithDistance) {
        ArrayList<String> branchesNames = new ArrayList<String>();
        for (Pair<String, Double> pair : branchesWithDistance) {
            branchesNames.add(pair.first);
        }
        return branchesNames;
    }

    /*
     * (non-Javadoc)
     * Some optimization employed here. For more information,
     * @see com.jason.ocbcapp.BranchAdapter#getView(int, android.view.View, android.view.ViewGroup)
     */
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.branches_location_row, null);
            holder = initializeViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.pb.setVisibility(View.INVISIBLE);

        String branchName = branchesWithDistance.get(position).first;
        branchName = shortenBranchName(branchName);
        holder.name.setText(branchName);

        long distance = Math.round(branchesWithDistance.get(position).second) / 1000;
        holder.distance.setText(distance + " km");

        int buttonState = waitingTimes[position];
        if (buttonState != -1) {
            holder.btn.setText(buttonState + "mins");
        } else {
            holder.btn.setText("");
        }
        holder.btn.setTag(position);
        holder.btn.setOnClickListener(btnClickListener);
        return convertView;
    }

    private ViewHolder initializeViewHolder(View convertView) {
        ViewHolder holder = new ViewHolder();
        holder.name = (TextView) convertView.findViewById(R.id.branchName);
        holder.btn = (Button) convertView.findViewById(R.id.waitingTimeBtn);
        holder.pb = (ProgressBar) convertView
                .findViewById(R.id.progressBar); 
        holder.distance = (TextView) convertView.findViewById(R.id.branchDistance);
        return holder;
    }

    static class ViewHolder {
        TextView name;
        TextView distance;
        Button btn;
        ProgressBar pb; 
    }
}
