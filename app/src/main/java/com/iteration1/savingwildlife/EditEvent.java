package com.iteration1.savingwildlife;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.iteration1.savingwildlife.entities.Event;
import com.iteration1.savingwildlife.entities.Report;
import com.iteration1.savingwildlife.utils.UIUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

public class EditEvent extends AppCompatActivity {
    private Toolbar toolbar;
    private TextView toolbar_title;
    private DatePicker eventDate;
    private Spinner spinnerForEventLocation;
    private TimePicker timePickerForStartTime;
    private TimePicker timePickerForEndTime;
    private Button btnSubmmit;
    private String ky;
    private Event e;
    private int startHours;
    private int startMins;
    private int endHours;
    private int endMins;
    private String sHour;
    private String sMin;
    private String eHour;
    private String eMin;
    private int currentHourIn24Format;
    private int currentMinIn24Format;
    private String Smonth;
    private String Sday;
    private String editingKey;
    private long startDate;
    FirebaseStorage storage;
    StorageReference storageReference;
    DatabaseReference databaseReference;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_event);
        covertToLongType();
        initUI();
        e = new Event();
        btnSubmmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submmitInfo();
            }
        });
    }

    private void initUI(){
        Intent intent1 = getIntent();
        ky = intent1.getStringExtra("key");
        Log.d("event key", ky);
        editingKey = ky;
        btnSubmmit = (Button) findViewById(R.id.submmitbtn1);
        spinnerForEventLocation = (Spinner) findViewById(R.id.spinnerForEventLocation1);
        timePickerForStartTime = (TimePicker)findViewById(R.id.timePickerForStartTime1);
        timePickerForEndTime = (TimePicker)findViewById(R.id.timePickerForEndTime1);
        timePickerForStartTime.setIs24HourView(true);
        timePickerForEndTime.setIs24HourView(true);
        eventDate = (DatePicker) findViewById(R.id.datePickerForEvent1);
        eventDate.setMinDate(new Date().getTime());
        eventDate.setMaxDate(startDate);
//        storage = FirebaseStorage.getInstance();
//        storageReference = storage.getReference();
        retrieveEvent();

        toolbar_title = (TextView) findViewById(R.id.toolbar_title_for_edit_event);
        toolbar = (Toolbar) findViewById(R.id.toolbarForEditEvent);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void retrieveEvent(){
        databaseReference = FirebaseDatabase.getInstance().getReference("event");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    if (child.getKey().toString().equals(ky)){
                        e = child.getValue(Event.class);
                        String[] starttime = e.getEvent_start().split(":");
                        String[] endtime = e.getEvent_end().split(":");
                        String[] date = e.getEvent_date().split("-");
                        if (Build.VERSION.SDK_INT > 23) {
                        timePickerForStartTime.setHour(Integer.parseInt(starttime[0]));
                        timePickerForStartTime.setMinute(Integer.parseInt(starttime[1]));
                        timePickerForEndTime.setHour(Integer.parseInt(endtime[0]));
                        timePickerForEndTime.setMinute(Integer.parseInt(endtime[1]));
                        } else{
                            timePickerForStartTime.setCurrentHour(Integer.parseInt(starttime[0]));
                            timePickerForStartTime.setCurrentMinute(Integer.parseInt(starttime[1]));
                            timePickerForEndTime.setCurrentHour(Integer.parseInt(endtime[0]));
                            timePickerForEndTime.setCurrentMinute(Integer.parseInt(endtime[1]));
                        }
                        eventDate.init(Integer.parseInt(date[2]),Integer.parseInt(date[1]),Integer.parseInt(date[0]),null);
                        String[] selections = getResources().getStringArray(R.array.beach_names);
                        spinnerForEventLocation.setSelection(Arrays.asList(selections).indexOf(e.getEvent_location()));
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }



    private void submmitInfo() {
        if (!spinnerForEventLocation.getSelectedItem().toString().equals("Select one")) {
            Calendar rightNow = Calendar.getInstance();
            currentHourIn24Format = rightNow.get(Calendar.HOUR_OF_DAY);
            currentMinIn24Format = rightNow.get(Calendar.MINUTE);
            if (Build.VERSION.SDK_INT > 23) {
                startHours = timePickerForStartTime.getHour();
                startMins = timePickerForStartTime.getMinute();
                endHours = timePickerForEndTime.getHour();
                endMins = timePickerForEndTime.getMinute();
            } else {
                startHours = timePickerForStartTime.getCurrentHour();
                startMins = timePickerForStartTime.getCurrentMinute();
                endHours = timePickerForEndTime.getCurrentHour();
                endMins = timePickerForEndTime.getCurrentMinute();
            }
            String eventLocation = spinnerForEventLocation.getSelectedItem().toString();
            Integer year = eventDate.getYear();
            Integer month = eventDate.getMonth() + 1;
            Integer Day = eventDate.getDayOfMonth();
            Date today = new Date();
            SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
            String dateToStr = format.format(today);
            if (Day < 10) {
                Sday = "0" + Day.toString();
            }else{
                Sday = Day.toString();
            }
            if (month < 10) {
                Smonth = "0" + month.toString();
            }else{
                Smonth = month.toString();
            }
            String event_date = Sday + "-" + Smonth + "-" + year.toString();

            if (startHours < 10) {
                sHour = "0" + String.valueOf(startHours);
            } else {
                sHour = String.valueOf(startHours);
            }
            if (startMins < 10) {
                sMin = "0" + String.valueOf(startMins);
            } else {
                sMin = String.valueOf(startMins);
            }
            if (endHours < 10) {
                eHour = "0" + String.valueOf(endHours);
            } else {
                eHour = String.valueOf(endHours);
            }
            if (endMins < 10) {
                eMin = "0" + String.valueOf(endMins);
            } else {
                eMin = String.valueOf(endMins);
            }
            String start_time = sHour + ":" + sMin;
            String end_time = eHour + ":" + eMin;
            if((event_date.equals(dateToStr) && startHours < currentHourIn24Format) || (event_date.equals(dateToStr) && startHours == currentHourIn24Format && startMins < currentMinIn24Format)) {
                Toast.makeText(EditEvent.this, "Start time should be late than current time!", Toast.LENGTH_SHORT).show();
            }else if (startHours > endHours || (startHours == endHours && startMins > endMins) || (startHours == endHours && startMins == endMins)) {
                Toast.makeText(EditEvent.this, "Start time should be earlier than end time!", Toast.LENGTH_SHORT).show();
            }else {
                try {
                    databaseReference.child(editingKey).child("event_date").setValue(event_date);
                    databaseReference.child(editingKey).child("event_start").setValue(start_time);
                    databaseReference.child(editingKey).child("event_end").setValue(end_time);
                    databaseReference.child(editingKey).child("event_location").setValue(eventLocation);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Toast.makeText(EditEvent.this, "Your event has been modified!", Toast.LENGTH_SHORT).show();
                spinnerForEventLocation.setSelection(0);
//                Intent intent = new Intent(EditEvent.this, MainActivity.class);
//                startActivity(intent);
                finish();
            }
        } else {
            Toast.makeText(EditEvent.this, "Miss the event location!", Toast.LENGTH_SHORT).show();
        }
    }


    private void covertToLongType(){
        try {
            String dateString = "01/01/2020";
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            Date date = sdf.parse(dateString);
            startDate = date.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
}
