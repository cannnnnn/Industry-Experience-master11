package com.iteration1.savingwildlife;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.annotation.WorkerThread;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.iteration1.savingwildlife.entities.Event;
import com.iteration1.savingwildlife.utils.UIUtils;

import java.util.ArrayList;
import java.util.Objects;

import static android.content.Context.TELEPHONY_SERVICE;
import static com.firebase.ui.auth.AuthUI.getApplicationContext;

public class ViewMyEvents extends Fragment {
    View eventView;
    ArrayList<Event> eventList1;
    ArrayList<Event> eventList;
    ArrayList<Event> filterList;
    String imei;
    Event e;
    RecyclerView myView;
    EventsAdapter mAdapter;
    DatabaseReference databaseReference;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState) {
        databaseReference = FirebaseDatabase.getInstance().getReference("event");
        eventList1 = new ArrayList<>();
        eventList = new ArrayList<>();
        filterList = new ArrayList<>();
        eventView = inflater.inflate(R.layout.view_my_events, container, false);
        myView = (RecyclerView) eventView.findViewById(R.id.my_recycler_view);
        new LoadTask1().execute();
        return eventView;
    }

    private void getRelatedEvents() {
        imei = getUniqueIMEIId(getContext());
        for (int i = 0; i < eventList.size(); i++) {
            if (!imei.equals("not_found")) {
                if (eventList.get(i).getImei() != null && eventList.get(i).getImei().equals(imei)) {
                    filterList.add(eventList.get(i));
                }
            } else {
                filterList = new ArrayList<>();
            }
        }
        if (filterList.size() == 0) {

           filterList =  new ArrayList<>();
        }
        mAdapter = new EventsAdapter(filterList, new EventsAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Event event) {
                showEventDialog(event.getId(),event);

            }
        });
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this.getActivity());
        myView.setLayoutManager(mLayoutManager);
        myView.setItemAnimator(new DefaultItemAnimator());
        myView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();

    }


    public static String getUniqueIMEIId(Context context) {
        try {
            TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
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


    private void showEventDialog(String key,Event event){
        AlertDialog.Builder normalDialog =
                new AlertDialog.Builder(getActivity());
        normalDialog.setIcon(R.drawable.ic_event_note_black_24dp);
        normalDialog.setTitle("Beach Cleaning");
        normalDialog.setPositiveButton("Edit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent = new Intent();
                intent.setClass(getActivity(), EditEvent.class);
                intent.putExtra("key",key);
                startActivity(intent);

            }
        });
        normalDialog.setNeutralButton("Close", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        normalDialog.setNegativeButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                DatabaseReference dele = FirebaseDatabase.getInstance().getReference("event");
                String registered_man = event.getRegistered_user();
                if (registered_man.equals(imei + ",")) {
                    try {
                        dele.child(event.getId()).child("imei").setValue(" ");
                        dele.child(event.getId()).child("registered_user").setValue(" ");
                        UIUtils.showCenterToast(getContext(), " event has been deleted!");
                        assert getFragmentManager() != null;
                        FragmentTransaction ft = getFragmentManager().beginTransaction();
                        ft.detach(ViewMyEvents.this).attach(ViewMyEvents.this).commit();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }else {
                    Toast.makeText(getActivity(), "fail to delete,someone has register your event!", Toast.LENGTH_SHORT).show();
                }

            }
        });
        AlertDialog dialog = normalDialog.create();
        dialog.show();
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
                        assert e != null;
                        e.setId(child.getKey());
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