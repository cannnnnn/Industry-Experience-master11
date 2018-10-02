package com.iteration1.savingwildlife;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.TileOverlay;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.maps.android.heatmaps.Gradient;
import com.google.maps.android.heatmaps.HeatmapTileProvider;
import com.iteration1.savingwildlife.utils.UIUtils;

import java.util.ArrayList;
import java.util.HashMap;

public class FishPopulationFragment extends Fragment implements OnMapReadyCallback {
    private MapView mMapView;
    private View fView;
    private GoogleMap mMap;
    private LatLng center;
    private WebView wv;
    private ArrayList<LatLng> fishLocations;


    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState) {
        fView = inflater.inflate(R.layout.fish_population_fragment, container, false);
        Bundle mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle("com.google.android.geo.API_KEY");
        }
        mMapView = (MapView) fView.findViewById(R.id.basemap_map);
        mMapView.onCreate(mapViewBundle);
        mMapView.onResume();
        wv = fView.findViewById(R.id.beachtxt);
        String s = "<html><body style='text-align:justify;' bgcolor=\"#F3F7F7\">" + getString(R.string.general_info) +
                "</body></html>";
        wv.loadData(s, "text/html", "UTF-8");



        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        mMapView.getMapAsync(this);
        center = new LatLng(-37.8136, 144.9631);

        return fView;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker to default location
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(center, 4));
        connectDatabase();
    }


    private void connectDatabase() {
        // Get the reference of firebase instance
//        DatabaseReference mReference = FirebaseDatabase.getInstance().getReference("fishcluster");
        DatabaseReference mReference = FirebaseDatabase.getInstance().getReference("fishes");
        fishLocations = new ArrayList<>();
        UIUtils.showCenterToast(getContext(), "Heatmap is loading...");
        mReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    HashMap<String, Double> map = (HashMap<String, Double>) child.getValue();

                    Double a = (Double) map.get("lat");
                    Double b = (Double) map.get("lng");

                    // Add weights to points
//                    WeightedLatLng p = new WeightedLatLng(new LatLng(a, b), w * 10);
                    LatLng p = new LatLng(a, b);
                    fishLocations.add(p);
                }
                Log.d("list size", Integer.toString(fishLocations.size()));

                // Create a heat map tile provider, passing it the latlngs of the police stations.
                HeatmapTileProvider mProvider = new HeatmapTileProvider.Builder()
                        .data(fishLocations)
                        .build();
                mProvider.setRadius(30);
                int[] colors = {
                        Color.YELLOW, Color.RED, Color.BLACK};

                float[] startPoints = {
                        0.005f, 0.04f, 0.15f
                };
                mProvider.setGradient(new Gradient(colors, startPoints));
                // Add a tile overlay to the map, using the heat map tile provider.
                TileOverlay mOverlay = mMap.addTileOverlay(new TileOverlayOptions().tileProvider(mProvider));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getDetails());
            }
        });
    }
}
