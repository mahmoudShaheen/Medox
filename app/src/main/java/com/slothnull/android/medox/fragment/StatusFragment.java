package com.slothnull.android.medox.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.slothnull.android.medox.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class StatusFragment extends Fragment  implements View.OnClickListener {

    View view;
    Button button;
    public StatusFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_status, container, false);

        button = (Button) view.findViewById(R.id.button2);
        button.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View v) {
        //do what you want to do when button is clicked
        switch (v.getId()) {
            case R.id.button2:
                hello();
                break;
        }
    }
    public void hello(){
        Toast.makeText(getActivity(), "Hello!!!", Toast.LENGTH_LONG).show();
    }
}
