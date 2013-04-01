package com.jason.ocbcapp;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import android.app.Activity;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

public class AppointmentAdapter extends BaseAdapter {

    private static final String APP_TAG = MainActivity.APP_TAG;

    private Activity activity;
    private ArrayList<Appointment> appointments;
    private static LayoutInflater inflater = null;
    private static String[] branchNames = null;

    public AppointmentAdapter(Activity activity, ArrayList<Appointment> appointments) {
        this.activity = activity;
        this.appointments = appointments;
        inflater = activity.getLayoutInflater();
        branchNames = activity.getResources().getStringArray(R.array.branches);
    }

    @Override
    public int getCount() {
        return appointments.size();
    }

    @Override
    public Object getItem(int position) {
        return appointments.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.appointment_row, null);

            holder = new ViewHolder();
            holder.branchName = (TextView) convertView.findViewById(R.id.apptBranchName);
            holder.queueNumber = (TextView) convertView.findViewById(R.id.apptQueueNumber);
            holder.timeSlot = (TextView) convertView.findViewById(R.id.apptTimeSlot);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        int branchId = appointments.get(position).getBranchId();
        int queueNumber = appointments.get(position).getQueueNumber();
        long timestamp = appointments.get(position).getAppointmentDateTime();
        
        String branchName = getBranchNameFromId(branchId);
        holder.branchName.setText(branchName);

        String dateTime = getDateFromTimeStamp(timestamp) + " "+ getTimeSlotFromTimeStamp(timestamp);
        holder.timeSlot.setText(dateTime);

        String queueNumberStr = "" + queueNumber;
        holder.queueNumber.setText(queueNumberStr);

        return convertView;
    }

    private String getDateFromTimeStamp(long timestamp) {
        Calendar cal = convertTimeStampToCalendar(timestamp);
        int date = cal.get(Calendar.DATE);
        int month = cal.get(Calendar.MONTH);
        int year = cal.get(Calendar.YEAR);
        return date + "-" + month + "-" + year;
    }

    private String getTimeSlotFromTimeStamp(long timestamp) {
        Calendar cal = convertTimeStampToCalendar(timestamp);
        int hourOfDay = cal.get(Calendar.HOUR_OF_DAY);
        Log.d(APP_TAG, "timestamp = " + timestamp + ", Hour " + hourOfDay);
        SparseArray<String> timeSlotMap = AppointmentsFragment.makeTimeSlotsMap(this.activity);
        return timeSlotMap.get(hourOfDay);
    }

    /**
     * @param timestamp
     * @return
     */
    private Calendar convertTimeStampToCalendar(long timestamp) {
        Log.d(APP_TAG, "before convert = " + timestamp);
        Calendar cal = new GregorianCalendar(TimeZone.getTimeZone("GMT"));
        cal.setTimeInMillis(timestamp);
        Log.d(APP_TAG, "after convert = " + cal.getTimeInMillis());
        return cal;
    }

    private String getBranchNameFromId(int id) {
        return branchNames[id];
    }

    static class ViewHolder {
        TextView branchName;
        TextView queueNumber;
        TextView timeSlot;
    }
}
