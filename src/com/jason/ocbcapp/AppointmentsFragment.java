package com.jason.ocbcapp;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class AppointmentsFragment extends Fragment {

    public AppointmentsFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View myFragmentView = inflater.inflate(R.layout.fragmentlayout,
                container, false);

        return myFragmentView;
    }
}