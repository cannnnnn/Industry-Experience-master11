package com.iteration1.savingwildlife;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.WorkerThread;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.maps.android.SphericalUtil;
import com.iteration1.savingwildlife.entities.Beach;
import com.iteration1.savingwildlife.entities.Report;
import com.iteration1.savingwildlife.utils.UIUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class HomeScreenFragment extends AppCompatActivity {
    View vHome;
    private TextView title;

    private ArrayList<Beach> beachList;
    private ArrayList<ImageView> ibList;
    private Location mlocation;
    private ArrayList<Report> reports;
    // To see whether user has given the permission of using device location or not
    private boolean locationrefuse;
    private String provider;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//    @SuppressLint("CheckResult")
//    @Nullable
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
//            savedInstanceState) {
//        vHome = inflater.inflate(R.layout.home_screen, container, false);
        setContentView(R.layout.home_screen);
        beachList = new ArrayList<>();
        ibList = new ArrayList<>();
        reports = new ArrayList<>();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        provider = "";

        locationrefuse = false;

        // Get user location using locationmanager
        LocationManager mLocMan = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this.getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this.getApplicationContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            locationrefuse = ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.ACCESS_COARSE_LOCATION);
            locationrefuse = ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.ACCESS_FINE_LOCATION);
            if (!locationrefuse) {
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION},
                        1);
//                this.onResume();
            }
            UIUtils.showCenterToast(this.getApplicationContext(), "Show beach randomly...");
        } else {
            UIUtils.showCenterToast(this.getApplicationContext(), "Analyzing the closest beach...");

            assert mLocMan != null;
            List<String> list = mLocMan.getProviders(true);
            if (list.contains(LocationManager.GPS_PROVIDER)) {
                provider = LocationManager.GPS_PROVIDER;
            } else if (list.contains(LocationManager.NETWORK_PROVIDER)) {
                  provider = LocationManager.NETWORK_PROVIDER;
        } else {
            UIUtils.showCenterToast(this, "Please check internet connection or GPS permission!");
        }

            mlocation = mLocMan.getLastKnownLocation(provider);
//            Bundle bundle = this.getArguments();
//            mlocation = bundle.getParcelable("location");
            LocationListener locationListener = new LocationListener() {
                @Override
                public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
                }

                @Override
                public void onProviderEnabled(String arg0) {
                }

                @Override
                public void onProviderDisabled(String arg0) {

                }

                @SuppressLint("MissingPermission")
                @Override
                public void onLocationChanged(Location arg0) {
                    mlocation = mLocMan.getLastKnownLocation(provider);
                }
            };
            mLocMan.requestLocationUpdates(provider, 2000, 2,
                    locationListener);

        }


        initUI();
        new LoadTask().execute();
//        rxPermissions
//                .request(android.Manifest.permission.ACCESS_FINE_LOCATION)
//                .subscribe(granted -> {
//                    if (granted) { //
//                        applyRecommendSystem();
//                    } else {
//                        applyRecommendSystem();
//                    }
//                });
    }


    private void initUI() {
        ImageView ib1 = (ImageView) findViewById(R.id.image1);
        ImageView ib2 = (ImageView) findViewById(R.id.image2);
        ImageView ib3 = (ImageView) findViewById(R.id.image3);
        ImageView ib4 = (ImageView) findViewById(R.id.image4);
        ImageView ib5 = (ImageView) findViewById(R.id.image5);
        ImageView ib6 = (ImageView) findViewById(R.id.image6);
        ImageView ib7 = (ImageView) findViewById(R.id.image7);
        ImageView ib8 = (ImageView) findViewById(R.id.image8);
        ImageView ib9 = (ImageView) findViewById(R.id.image9);
        ImageView ib10 = (ImageView) findViewById(R.id.image10);
        ibList.add(ib1);
        ibList.add(ib2);
        ibList.add(ib3);
        ibList.add(ib4);
        ibList.add(ib5);
        ibList.add(ib6);
        ibList.add(ib7);
        ibList.add(ib8);
        ibList.add(ib9);
        ibList.add(ib10);
//        glow();
    }


//    private void glow() {
//        title.setShadowLayer(50, 0, 0, Color.CYAN);
//    }


    @Override
    public void onResume() {
        super.onResume();
    }




    // Simulate the recomsys, but now just sort randomly instead of real algorithm
    private void applyRecommendSystem() {

        if (mlocation != null) {
            // If has the user location, apply the recommend algorithm based on distance.(Bubble sort)
            for (int i = 0; i < beachList.size() + 1; i++) {
                for (int j = 0; j < beachList.size() - 1; j++) {
//                        Location iLocation = new Location(provider);
//                        iLocation.setLatitude(beachList.get(j).getLatitude());
//                        iLocation.setLongitude(beachList.get(j).getLongitude());
//                        Location nextLocation = new Location(provider);
//                        nextLocation.setLatitude(beachList.get(j + 1).getLatitude());
//                        nextLocation.setLongitude(beachList.get(j + 1).getLongitude());
//                        if (mlocation.distanceTo(nextLocation) < mlocation.distanceTo(iLocation)) {


                    // The above is using the distance calculating mechod of android itself, now the below one is google api's calculation
                    LatLng thislatlng = new LatLng(mlocation.getLatitude(), mlocation.getLongitude());
                    LatLng nextlatlng = new LatLng(beachList.get(j + 1).getLatitude(),beachList.get(j + 1).getLongitude());
                    LatLng ilatlng = new LatLng(beachList.get(j).getLatitude(), beachList.get(j).getLongitude());
                    if (SphericalUtil.computeDistanceBetween(thislatlng, nextlatlng) <
                            SphericalUtil.computeDistanceBetween(thislatlng, ilatlng)) {
                        Beach tb = beachList.get(j + 1);
                        beachList.set(j + 1, beachList.get(j));
                        beachList.set(j, tb);
                    }
                }
            }
        } else {
            // When user do not provide location, shuffle the beaches
            Collections.shuffle(beachList);
        }

        for (int i = 0; i < ibList.size(); i++) {
            ImageView iv = ibList.get(i);

            // This block is for taking picts from cloud
            StorageReference imageRef = FirebaseStorage.getInstance().getReferenceFromUrl(beachList.get(i).getBanner());
            GlideApp.with(Objects.requireNonNull(this).getApplicationContext())
                    .load(imageRef)
                    .placeholder(R.drawable.common_full_open_on_phone)
                    .error(R.drawable.common_full_open_on_phone)
                    .into(iv);
            iv.setTag(beachList.get(i).getName());

            //@Override
            iv.setOnClickListener((View v) -> {
                Intent intent = new Intent();
                intent.setClass(this, InfoPage.class);
                String ns = (String) v.getTag();
                Beach selected;
                for (Beach b : beachList) {
                    if (b.getName().toUpperCase().replaceAll(" ", "")
                            .contains(ns.toUpperCase().trim().replaceAll(" ", ""))) {
                        selected = b;
                        ArrayList<String> relatedReport = new ArrayList<>();
                        if (reports != null) {
                            for (Report e : reports) {
                                if (e.getBeach_name().toUpperCase().replaceAll(" ", "")
                                        .equals(b.getName().toUpperCase().replaceAll(" ", ""))) {
                                    relatedReport.add(e.getEvent_type());
                                }
                            }
                        }
                        // intent cannot be used to parse integer, so use bundle to pack the params
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("beach", selected);
                        Log.d("related report size", Integer.toString(relatedReport.size()));
                        bundle.putStringArrayList("reports", relatedReport);
                        intent.putExtras(bundle);
                        startActivity(intent);
                    }
                }
            });
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        Log.d("granted", "inside");
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    ViewGroup vg = findViewById(R.id.content);
                    vg.invalidate();
                } else {

                }
                return;
            }

        }
    }



    // This is a new thread to load data from database
    private class LoadTask extends AsyncTask<String, Integer, String> {

        @MainThread
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
//            ss.show(2000);
            Log.d("pre", "onPreExecute() called");
        }

        @WorkerThread
        @Override
        protected String doInBackground(String... params) {

            // Get the reference of firebase instance
            DatabaseReference mReference = FirebaseDatabase.getInstance().getReference("beaches");

            mReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot child : dataSnapshot.getChildren()) {
                        Beach b = child.getValue(Beach.class);
                        beachList.add(b);
                    }
                    applyRecommendSystem();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    System.out.println("The read failed: " + databaseError.getDetails());
                }
            });

            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("report");
            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot child : dataSnapshot.getChildren()) {
                        Report e = child.getValue(Report.class);
                        reports.add(e);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

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
//            ss.hide();
            Log.d("post", "onPostExecute(Result result) called");
        }

        @Override
        protected void onCancelled() {
            Log.d("cancelled", "onCancelled() called");
        }

    }


}
