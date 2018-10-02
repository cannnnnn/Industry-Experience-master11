package com.iteration1.savingwildlife;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.iteration1.savingwildlife.entities.Beach;
import com.iteration1.savingwildlife.entities.Report;

import java.util.ArrayList;

public class InfoImageFragment extends Fragment implements OnMapReadyCallback {
    private View parentView;
    private Beach selected;
    private MapView mMapView;
    private GoogleMap mMap;
    private ArrayList<Report> allreport;
    private ArrayList<Report> thisreport;
    private TextView textView;
    private LatLng center;
    private TextView tv2;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        parentView = inflater.inflate(R.layout.beach_image_fragment, container, false);
        Bundle bundle = this.getArguments();
        assert bundle != null;
        selected = (Beach) bundle.getSerializable("selected");
//        reports = (ArrayList<String>) bundle.getStringArrayList("reports");
        allreport = new ArrayList<>();
        thisreport = new ArrayList<>();
        Bundle mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle("com.google.android.geo.API_KEY");
        }
        Double lat = selected.getLatitude();
        Double lng = selected.getLongitude();
        center = new LatLng(lat, lng);
        textView = (TextView) parentView.findViewById(R.id.txt);
        tv2 = (TextView) parentView.findViewById(R.id.txt2);
        mMapView = (MapView) parentView.findViewById(R.id.mapView);
        mMapView.onCreate(mapViewBundle);
        mMapView.onResume(); // needed to get the map to display immediately
        connectDatabase();

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }
        mMapView.getMapAsync(this);
        return parentView;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        // Add a marker to default location
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(center, 10));
        mMap.addMarker(new MarkerOptions()
                .position(center)
                .title(selected.getName()));
    }



    private void connectDatabase(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("report");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                allreport = new ArrayList<>();
                thisreport = new ArrayList<>();
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    Report e = child.getValue(Report.class);
                    allreport.add(e);
                }

                for (Report e : allreport) {
                    if (e.getBeach_name().toUpperCase().replaceAll(" ", "")
                            .equals(selected.getName().toUpperCase().replaceAll(" ", ""))) {
                        thisreport.add(e);
                    }
                }

                StringBuilder sb = new StringBuilder();
                if (thisreport == null) {
                    sb.append("No");
                } else {
                    sb.append(thisreport.size());
                }
                sb.append(" incident report(s) so far has been made in this beach area!");
                textView.setText(sb);

                StringBuilder sb2 = new StringBuilder();
                int f = 0;
                int d = 0;
                int o = 0;
                for (Report e:thisreport) {
                    if (e.getEvent_type() != null) {
                        if (e.getEvent_type().toLowerCase().contains("fish")) {
                            f++;
                        } else if (e.getEvent_type().toLowerCase().contains("dump")) {
                            d++;
                        } else if (e.getEvent_type().toLowerCase().contains("other")) {
                            o++;
                        }
                    }
                }
                if (f!=0){
                    sb2.append(f + " illegal fishing" + " | ");
                }
                if (d!=0){
                    sb2.append(d + " rubbish dumping" + " | ");
                }
                if (o!=0){
                    sb2.append(o + " other incidents" + " | ");
                }
                if (!sb2.toString().equals("")){
                    sb2.setLength(sb2.length()-2);
                    tv2.setText(sb2);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }


}
