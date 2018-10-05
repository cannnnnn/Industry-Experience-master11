package com.iteration1.savingwildlife;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;


import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.iteration1.savingwildlife.utils.UIUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.io.IOException;
import java.util.Random;


public class CreateEvent extends AppCompatActivity {
    private Toolbar toolbar;
    private EditText editText;
    private TextView toolbar_title;
    private DatePicker eventDate;
    private Spinner spinnerForEventLocation;
    private TimePicker timePickerForStartTime;
    private TimePicker timePickerForEndTime;
    private Button btnSubmmit;
    private int startHours;
    private int startMins;
    private int endHours;
    private int endMins;
    private String imei;
    private String sHour;
    private String sMin;
    private String eHour;
    private String eMin;
    private int currentHourIn24Format;
    private int currentMinIn24Format;
    private String Smonth;
    private String Sday;
    private long startDate;
    FirebaseStorage storage;
    StorageReference storageReference;
    DatabaseReference databaseReference;


    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_event);
        covertToLongType();
        initUI();
        getImei(this);
        btnSubmmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submmitInfo();

            }
        });
    }



    private void initUI(){
        btnSubmmit = (Button) findViewById(R.id.submmitbtn);
        editText = (EditText) findViewById(R.id.description);
        spinnerForEventLocation = (Spinner) findViewById(R.id.spinnerForEventLocation);
        timePickerForStartTime = (TimePicker)findViewById(R.id.timePickerForStartTime);
        timePickerForEndTime = (TimePicker)findViewById(R.id.timePickerForEndTime);
        timePickerForStartTime.setIs24HourView(true);
        timePickerForEndTime.setIs24HourView(true);
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        databaseReference = FirebaseDatabase.getInstance().getReference("event");
        toolbar_title = (TextView) findViewById(R.id.toolbar_title1);
        toolbar = (Toolbar) findViewById(R.id.toolbar1);
        eventDate = (DatePicker) findViewById(R.id.datePickerForEvent);
        eventDate.setMinDate(new Date().getTime());
        eventDate.setMaxDate(startDate);
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
            String desc = editText.getText().toString();
            String event_type = "Beach Cleaning";
            String primary_registered_user = imei + ",";
            Random random = new Random();
            int length = random.nextInt(15);
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
                Toast.makeText(CreateEvent.this, "Start time should be late than current time!", Toast.LENGTH_SHORT).show();
            }else if (startHours > endHours || (startHours == endHours && startMins > endMins) || (startHours == endHours && startMins == endMins)) {
                Toast.makeText(CreateEvent.this, "Start time should be earlier than end time!", Toast.LENGTH_SHORT).show();
            }else if (desc.length() > 100) {
                Toast.makeText(CreateEvent.this, "the limit of description is 100 characters!", Toast.LENGTH_SHORT).show();
            }else {
                String id = databaseReference.push().getKey();
                databaseReference.child(id).child("description").setValue(desc);
                databaseReference.child(id).child("event_date").setValue(event_date);
                databaseReference.child(id).child("event_start").setValue(start_time);
                databaseReference.child(id).child("event_end").setValue(end_time);
                databaseReference.child(id).child("event_location").setValue(eventLocation);
                databaseReference.child(id).child("event_type").setValue(event_type);
                databaseReference.child(id).child("imei").setValue(imei);
                databaseReference.child(id).child("registered_user").setValue(primary_registered_user);
                databaseReference.child(id).child("name").setValue(UIUtils.getRandomString(length));
                databaseReference.child(id).child("timestamp").setValue(Long.toString(new Date().getTime()));
                Toast.makeText(CreateEvent.this, "Your event has been created!", Toast.LENGTH_SHORT).show();
                spinnerForEventLocation.setSelection(0);
                finish();
            }
        } else {
            Toast.makeText(CreateEvent.this, "Miss the event location!", Toast.LENGTH_SHORT).show();
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

    private void getImei(Context context) {
        TelephonyManager telephonyMgr = (TelephonyManager) context.getSystemService(TELEPHONY_SERVICE);
//        String imei = "";
        if (ActivityCompat.checkSelfPermission(context,
                android.Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            if (!ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.READ_PHONE_STATE)) {
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.READ_PHONE_STATE},
                        1);
            }
        }
        assert telephonyMgr != null;
        imei = telephonyMgr.getDeviceId();

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

}
