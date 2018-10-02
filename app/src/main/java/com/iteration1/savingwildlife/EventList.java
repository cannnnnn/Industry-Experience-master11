package com.iteration1.savingwildlife;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.iteration1.savingwildlife.entities.Beach;
import com.iteration1.savingwildlife.entities.Event;
import com.iteration1.savingwildlife.entities.Report;
import com.iteration1.savingwildlife.utils.EventAdapter;
import com.iteration1.savingwildlife.utils.UIUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;


public class EventList extends AppCompatActivity {
    View thisView;
    private ListView listView;
    private ArrayList<Beach> beachList;
    private ArrayList<Event> events;
    private ArrayList<Report> reports;
    private Spinner order;
//    private Button btnMyEvents;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//    }
//
//
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        thisView = inflater.inflate(R.layout.event_list, container, false);
        setContentView(R.layout.event_list);
        beachList = new ArrayList<>();
        events = new ArrayList<>();
        reports = new ArrayList<>();
        getEvents();
        initUI();
    }

    private void initUI(){
        listView = findViewById(R.id.beach_event);
        order = findViewById(R.id.select_order);
//        btnMyEvents = findViewById(R.id.btn_my_events);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        toolbar.setTitle("");
        // Back to former page
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        order.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                switch (position){
                    case 0:
                        break;
                    case 1:
                        Collections.sort(events, new Comparator<Event>() {
                            @Override
                            public int compare(Event o1, Event o2) {
                                StringBuilder sb1 = new StringBuilder();
                                StringBuilder sb2 = new StringBuilder();
                                sb1.append(o1.getEvent_date()).append(" ");
                                sb1.append(o1.getEvent_start());
                                sb2.append(o2.getEvent_date()).append(" ");
                                sb2.append(o2.getEvent_start());
                                Date d1 = UIUtils.strToDateLong(sb1.toString());
                                Date d2 = UIUtils.strToDateLong(sb2.toString());
                                if (d1.before(d2)){
                                    return -1;
                                }else if (d1.after(d2)){
                                    return 1;
                                }
                                return 0;
                            }
                        });
                        listView.setAdapter(new EventAdapter(getApplicationContext(), events));
                        break;
                    case 2:
                        Collections.sort(events, new Comparator<Event>() {
                            @Override
                            public int compare(Event o1, Event o2) {
                                return o1.getEvent_location().compareTo(o2.getEvent_location());
                            }
                        });
                        listView.setAdapter(new EventAdapter(getApplicationContext(), events));
                        break;
                    case 3:
                        Collections.sort(events, new Comparator<Event>() {
                            @Override
                            public int compare(Event o1, Event o2) {
                                return new Date(Long.parseLong(o2.getTimestamp())).compareTo(new Date(Long.parseLong(o1.getTimestamp())));
                            }
                        });
                        listView.setAdapter(new EventAdapter(getApplicationContext(), events));
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            }

        });

//        btnMyEvents.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent();
//                intent.setClass(EventList.this,MyEvents.class);
//                startActivity(intent);
//            }
//        });

    }



    private void getEvents(){
        // Get the reference of firebase instance
        DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference("beaches");

        reference1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    Beach b = child.getValue(Beach.class);
                    beachList.add(b);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getDetails());
            }
        });

        DatabaseReference reference2 = FirebaseDatabase.getInstance().getReference("event");
        reference2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                events = new ArrayList<>();
                Date now = new Date();
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    Event e = child.getValue(Event.class);
                    e.setId(child.getKey());
                    StringBuilder sb = new StringBuilder();
                    sb.append(e.getEvent_date()).append(" ");
                    sb.append(e.getEvent_start());
                    Date thisdate = UIUtils.strToDateLong(sb.toString());
                    if (e.getEvent_start() != null) {
                        if (thisdate.after(now)) {
                            events.add(e);
                        }
                    }else {
                        events = new ArrayList<>();
                    }
                }
                listView.setAdapter(new EventAdapter(getApplicationContext(), events));
                ViewGroup.LayoutParams params = findLayoutParams(listView.getLayoutParams());
                listView.setLayoutParams(params);
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                            long arg3) {
                        Event e = events.get(arg2);
                        showEventDialog(e);
                    }

                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getDetails());
            }
        });

        DatabaseReference reference3 = FirebaseDatabase.getInstance().getReference("report");
        reference3.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                reports = new ArrayList<>();
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    Report e = child.getValue(Report.class);
                    reports.add(e);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }



    // Dialog of event detail & register
    private void showEventDialog(Event event){
        AlertDialog.Builder normalDialog =
                new AlertDialog.Builder(this);
        normalDialog.setIcon(R.drawable.ic_event_note_black_24dp);
        normalDialog.setTitle(event.getEvent_type());
        StringBuilder sb = new StringBuilder();
        sb.append(event.getEvent_date().substring(0,5).replace("-","/"));
        sb.append("/");
        sb.append(event.getEvent_date().substring(event.getEvent_date().length() - 2,event.getEvent_date().length()));
        sb.append(" · ");
        sb.append(event.getEvent_start() + " - " + event.getEvent_end());
        sb.append(" · ");
        sb.append(event.getEvent_location() + "\n\n");
        ArrayList<String> participants = new ArrayList<>();
        if (!event.getRegistered_user().trim().equals("")){
            participants = new ArrayList<String>(Arrays.asList(event.getRegistered_user().split(",")));
            for (String g: participants) {
                if(g.equals("")){
                    participants.remove(g);
                }
            }
        }
        int count = participants.size();
        sb.append(Integer.toString(count) + " people registered");
        normalDialog.setMessage(sb.toString());
        normalDialog.setPositiveButton("Register",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String my_imei = getImei(getApplicationContext());
                        if (!event.getRegistered_user().trim().equals("")){
                            ArrayList<String> p = new ArrayList<String>(Arrays.asList(event.getRegistered_user().split(",")));
                            for (String g: p) {
                                if(g.equals("")){
                                    p.remove(g);
                                }
                            }
                            if (p.contains(my_imei)){
                                UIUtils.showCenterToast(getApplicationContext(), "You have already registered to this event!");
                            }else{
                                event.setRegistered_user(event.getRegistered_user() + my_imei + ",");
                                DatabaseReference dr = FirebaseDatabase.getInstance().getReference("event");
                                dr.child(event.getId()).child("registered_user").setValue(event.getRegistered_user());
                                UIUtils.showCenterToast(getApplicationContext(), "Register sucessful!");
                                listView.setAdapter(new EventAdapter(getApplicationContext(), events));
                            }
                        }else{
                            event.setRegistered_user(event.getRegistered_user() + my_imei + ",");
                            DatabaseReference dr = FirebaseDatabase.getInstance().getReference("event");
                            dr.child(event.getId()).child("registered_user").setValue(event.getRegistered_user());
                            UIUtils.showCenterToast(getApplicationContext(), "Register sucessful!");
                        }
                    }
                });
        normalDialog.setNeutralButton("See beach",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        for (Beach b:beachList) {
                            if (b.getName().toUpperCase().replaceAll(" ", "")
                                    .contains(event.getEvent_location().toUpperCase().trim().replaceAll(" ", ""))){
                                ArrayList<String> relatedReport = new ArrayList<>();
                                if (reports != null) {
                                    for (Report e : reports) {
                                        if (e.getBeach_name().toUpperCase().replaceAll(" ", "")
                                                .equals(b.getName().toUpperCase().replaceAll(" ", ""))) {
                                            relatedReport.add(e.getEvent_type());
                                        }
                                    }
                                }
                                Intent intent = new Intent();
                                intent.setClass(getApplicationContext(), InfoPage.class);
                                Bundle bundle = new Bundle();
                                bundle.putSerializable("beach", b);
                                bundle.putStringArrayList("reports",relatedReport);
                                intent.putExtras(bundle);
                                startActivity(intent);
                            }
                        }
                    }
                });
        normalDialog.setNegativeButton("Close", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        AlertDialog dialog = normalDialog.create();
        dialog.show();
        TextView textView = (TextView) dialog.findViewById(android.R.id.message);
        textView.setTextSize(13);
    }


    // The get imei method
    public String getImei(Context context) {
        TelephonyManager telephonyMgr = (TelephonyManager) context.getSystemService(TELEPHONY_SERVICE);
        String imei = "";
        if (ActivityCompat.checkSelfPermission(context,
                android.Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            if (!ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.READ_PHONE_STATE)) {
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.READ_PHONE_STATE},
                        1);
            }
        }
        imei = telephonyMgr.getDeviceId();
        return imei;
    }

    private ViewGroup.LayoutParams findLayoutParams(ViewGroup.LayoutParams params){
        DisplayMetrics metrics = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        params.height = (int) Math.floor(metrics.heightPixels * 0.8);
        return params;
    }

}
