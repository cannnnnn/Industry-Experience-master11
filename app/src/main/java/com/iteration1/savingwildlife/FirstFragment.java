package com.iteration1.savingwildlife;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class FirstFragment extends Fragment{

    View thisView;
    private TextView create;
    private TextView find;
    private TextView report;
    private TextView beachlist;
    private TextView beachmap;
    private TextView myevents;
    private FragmentManager fragmentManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState) {
        thisView = inflater.inflate(R.layout.first_page, container, false);
        fragmentManager = getActivity().getSupportFragmentManager();
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("Beach Step");
        create = thisView.findViewById(R.id.create_event);
        create.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.setClass(getActivity(), CreateEvent.class);
            startActivity(intent);
        });
        find = thisView.findViewById(R.id.find_event);
        find.setOnClickListener(v -> {
//            fragmentManager.beginTransaction().replace(R.id.content_frame, new EventList()).commit();
            Intent intent = new Intent();
            intent.setClass(getActivity(), EventList.class);
            startActivity(intent);
        });
        report = thisView.findViewById(R.id.make_report);
        report.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.setClass(getActivity(), MakeReport.class);
            startActivity(intent);
        });
        beachlist = thisView.findViewById(R.id.beach_list);
        beachlist.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.setClass(getActivity(), HomeScreenFragment.class);
            startActivity(intent);
        });
        beachmap = thisView.findViewById(R.id.beach_map);
        beachmap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(getActivity(), MapFragment.class);
                startActivity(intent);
            }
        });
        myevents = thisView.findViewById(R.id.my_events);
        myevents.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(getContext(),MyEvents.class);
                startActivity(intent);
                Toast.makeText(getActivity(), "click to edit the event!", Toast.LENGTH_SHORT).show();
            }
        });
        return thisView;
    }

}
