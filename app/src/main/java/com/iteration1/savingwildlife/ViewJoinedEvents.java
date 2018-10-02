package com.iteration1.savingwildlife;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.MainThread;
import android.support.annotation.WorkerThread;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.iteration1.savingwildlife.entities.Event;

import java.util.ArrayList;

public class ViewJoinedEvents extends Fragment {
    View eventView;
    ArrayList<Event> eventList1;
    ArrayList<Event> eventList;
    ArrayList<Event> filterList;
    String imei;
    Event e;
    RecyclerView myView;
    JoinedEventsAdapter mAdapter;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState) {
//        e = new Event();
//        e.setEvent_date("No Data");
//        e.setEvent_start("No Data");
//        e.setEvent_end("No Data");
//        e.setEvent_type("You have not joined any events");
//        e.setEvent_location("No Data");
//        e.setImei(" ");
//        e.setName(" ");
//        e.setRegistered_user(" ");
        eventList1 = new ArrayList<>();
        eventList = new ArrayList<>();
        filterList = new ArrayList<>();
        eventView = inflater.inflate(R.layout.view_joined_events, container, false);
        myView = (RecyclerView) eventView.findViewById(R.id.my_recycler_view1);
        new LoadTask1().execute();
        return eventView;
    }

    private void getRelatedEvents() {
        imei = getUniqueIMEIId(getContext());
        for (int i = 0; i < eventList.size(); i++) {
            if (!imei.equals("not_found")) {
                if (eventList.get(i).getRegistered_user().contains(imei) && eventList.get(i).getImei() != null) {
                    filterList.add(eventList.get(i));
                }
            } else {
                filterList = new ArrayList<>();
            }
        }
        if (filterList.size() == 0) {

            filterList = new ArrayList<>();
        }
        mAdapter = new JoinedEventsAdapter(filterList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this.getActivity());
        myView.setLayoutManager(mLayoutManager);
        myView.setItemAnimator(new DefaultItemAnimator());
        //myView.addItemDecoration(new DividerItemDecoration(Objects.requireNonNull(this.getActivity()), LinearLayoutManager.VERTICAL));
        myView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();

    }

    public static String getUniqueIMEIId(Context context) {
        try {
            TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return "";
            }
            assert telephonyManager != null;
            String imei = telephonyManager.getDeviceId();
            Log.e("imei", "=" + imei);
            if (imei != null && !imei.isEmpty()) {
                return imei;
            } else {
                return android.os.Build.SERIAL;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "not_found";
    }



    private class LoadTask1 extends AsyncTask<String, Integer, String> {

        @MainThread
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.d("pre", "onPreExecute() called");
        }

        @WorkerThread
        @Override
        protected String doInBackground(String... params) {

            // Get the reference of firebase instance

            DatabaseReference mReference = FirebaseDatabase.getInstance().getReference("event");

            mReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot child : dataSnapshot.getChildren()) {
                        Event e = child.getValue(Event.class);
                        eventList1.add(e);
                    }
                    eventList = eventList1;
                    getRelatedEvents();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    System.out.println("The read failed: " + databaseError.getDetails());
                }
            });

            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... progresses) {
            Log.d("mprogress", "onProgressUpdate(Progress... progresses) called");
        }

        @MainThread
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute("finish");
            Log.d("post", "onPostExecute(Result result) called");
        }

        @Override
        protected void onCancelled() {
            Log.d("cancelled", "onCancelled() called");
        }

    }
}