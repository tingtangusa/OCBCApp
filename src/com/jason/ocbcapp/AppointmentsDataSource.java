package com.jason.ocbcapp;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class AppointmentsDataSource {

    private String APP_TAG = MainActivity.APP_TAG;
    // Database fields
    private SQLiteDatabase database;
    private MySQLiteHelper dbHelper;
    private String[] allColumns = { MySQLiteHelper.COLUMN_ID,
            MySQLiteHelper.COLUMN_APPT_DATETIME,
            MySQLiteHelper.COLUMN_BRANCH_ID, MySQLiteHelper.COLUMN_QUEUE_NO };

    public AppointmentsDataSource(Context context) {
        dbHelper = new MySQLiteHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public Appointment createAppointment(long appointmentDateTime,
            int branchId, int queueNumber) {
        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.COLUMN_APPT_DATETIME, appointmentDateTime);
        values.put(MySQLiteHelper.COLUMN_BRANCH_ID, branchId);
        values.put(MySQLiteHelper.COLUMN_QUEUE_NO, queueNumber);
        long insertId = database.insert(MySQLiteHelper.TABLE_APPOINTMENTS,
                null, values);
        Cursor cursor = database.query(MySQLiteHelper.TABLE_APPOINTMENTS,
                allColumns, MySQLiteHelper.COLUMN_ID + " = " + insertId, null,
                null, null, null);
        cursor.moveToFirst();
        Appointment newAppointment = convertCursorFieldsToAppointment(cursor);
        cursor.close();
        return newAppointment;
    }

    public void deleteAppointment(Appointment appointment) {
        int branchId = appointment.getBranchId();
        int queueNumber = appointment.getQueueNumber();
        database.delete(MySQLiteHelper.TABLE_APPOINTMENTS, MySQLiteHelper.COLUMN_BRANCH_ID
                + " = " + branchId + " and " + MySQLiteHelper.COLUMN_QUEUE_NO + " = " + queueNumber, null);
        Log.d(APP_TAG, "Appointment deleted with branch id = " + branchId + ", queue number = " + queueNumber);
    }

    public List<Appointment> getAllAppointments() {
        List<Appointment> appointments = new ArrayList<Appointment>();

        Cursor cursor = database.query(MySQLiteHelper.TABLE_APPOINTMENTS,
                allColumns, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Appointment appointment = convertCursorFieldsToAppointment(cursor);
            appointments.add(appointment);
            cursor.moveToNext();
        }
        // Make sure to close the cursor
        cursor.close();
        return appointments;
    }

    public void deleteAllAppointments() {
        database.delete(MySQLiteHelper.TABLE_APPOINTMENTS, null, null);
    }

    private Appointment convertCursorFieldsToAppointment(Cursor cursor) {
        long appointmentDateTime = cursor.getLong(1);
        int branchId = cursor.getInt(2);
        int queueNumber = cursor.getInt(3);
        Appointment appointment = new Appointment(appointmentDateTime, branchId, queueNumber);
        Log.d(APP_TAG, "retrived from curson appointment = " + appointment.toString());
        return appointment;
    }

    /**
     * @param dateTimeUnix
     */
    private Calendar convertUnixTimeStampToCalendar(int dateTimeUnix) {
        Date date = new Date(dateTimeUnix);
        Calendar cal = new GregorianCalendar();
        cal.setTime(date);
        return cal;
    }
}