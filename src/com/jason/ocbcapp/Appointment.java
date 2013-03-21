package com.jason.ocbcapp;

import java.util.ArrayList;
import java.util.Calendar;

import com.jason.ocbcapp.AppointmentsFragment.Services;

public class Appointment {

    // datetime is in unix timestamp
    private long appointmentDateTime;
    private int branchId;
    private int queueNumber;

    Appointment(long appointmentDateTime, int branchId, int queueNumber) {
        this.appointmentDateTime = appointmentDateTime;
        this.branchId = branchId;
        this.queueNumber = queueNumber;
    }

    public long getAppointmentDateTime() {
        return appointmentDateTime;
    }

    public int getBranchId() {
        return branchId;
    }

    public int getQueueNumber() {
        return queueNumber;
    }

    @Override
    public String toString() {
        return "dateTime = " + appointmentDateTime + ", branchID = " + branchId + ", q NO = " + queueNumber;
    }
}