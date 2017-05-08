package com.slothnull.android.medox.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.slothnull.android.medox.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class StatusFragment extends Fragment {

    private static final String TAG = "StatusFragment";

    View view;
    //Button button;
    public StatusFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_status, container, false);
        return view;
    }
}
