package com.jason.ocbcapp;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

public class AppointmentsFragment extends Fragment {

    public AppointmentsFragment() {

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Setup spinner for branch form item
        Spinner branchesSpinner = (Spinner) getView().findViewById(
                R.id.branchesSpinner);
        ArrayAdapter<CharSequence> branchesAdapter = ArrayAdapter
                .createFromResource(getView().getContext(), R.array.branches,
                        android.R.layout.simple_spinner_dropdown_item);
        branchesSpinner.setAdapter(branchesAdapter);
        
        // Setup spinner for services requested form item
        Spinner servicesSpinner = (Spinner) getView().findViewById(
                R.id.serviceSpinner);
        ArrayAdapter<CharSequence> servicesAdapter = ArrayAdapter
                .createFromResource(getView().getContext(), R.array.services,
                        android.R.layout.simple_spinner_dropdown_item);
        servicesSpinner.setAdapter(servicesAdapter);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View apptView = inflater.inflate(R.layout.fragmentlayout, container,
                false);

        return apptView;
    }
}