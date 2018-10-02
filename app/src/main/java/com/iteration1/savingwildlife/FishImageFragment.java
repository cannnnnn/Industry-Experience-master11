package com.iteration1.savingwildlife;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;
import com.youth.banner.Transformer;
import com.youth.banner.loader.ImageLoader;

import java.util.ArrayList;
import java.util.HashMap;

public class FishImageFragment extends Fragment {
    private View fView;
    private Banner banner;
    private ArrayList<String> names;
    private ArrayList<String> counts;
    private ArrayList<String> images;
    private ArrayList<String> details;
    private View line;
    private TextView dt;
    private WebView wv;


    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState) {
        fView = inflater.inflate(R.layout.fish_image_fragment, container, false);
        banner = fView.findViewById(R.id.banner);
        names = new ArrayList<>();
        counts = new ArrayList<>();
        images = new ArrayList<>();
        details = new ArrayList<>();
        line = fView.findViewById(R.id.line);
        dt = fView.findViewById(R.id.detail);
        line.setVisibility(View.INVISIBLE);
        dt.setVisibility(View.INVISIBLE);
        wv = fView.findViewById(R.id.fishtxt);
        wv.setVisibility(View.INVISIBLE);
        connectDatabase();
        return fView;
    }

    private void connectDatabase() {
        // Get the reference of firebase instance
        DatabaseReference mReference = FirebaseDatabase.getInstance().getReference("top_fishes");
        mReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    HashMap<String, String> map = (HashMap<String, String>) child.getValue();
                    String a = (String) map.get("scientificName");
                    String b = (String) map.get("image");
                    String c = (String) map.get("count");
                    String d = (String) map.get("detail");

                    names.add(a);
                    images.add(b);
                    counts.add(c);
                    details.add(d);
                }
                banner.setBannerStyle(BannerConfig.CIRCLE_INDICATOR_TITLE);
                banner.setBannerAnimation(Transformer.DepthPage);
                banner.setBannerTitles(names);
                banner.isAutoPlay(true);
                banner.setDelayTime(8000);
                banner.setImageLoader(new GlideImageLoader());
                banner.setImages(images);
                banner.setIndicatorGravity(View.SCROLL_INDICATOR_LEFT);


                banner.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                    @Override
                    public void onPageScrolled(int i, float v, int i1) {

                    }

                    @Override
                    public void onPageSelected(int i) {
                        line.setVisibility(View.INVISIBLE);
                        dt.setVisibility(View.INVISIBLE);
                        line.setVisibility(View.VISIBLE);
                        dt.setVisibility(View.VISIBLE);
                        StringBuilder sb = new StringBuilder(names.get(i) + ", "
                                + counts.get(i) + " has been found in the past years");
                        sb.append("\n\n");
                        sb.append(details.get(i));
                        dt.setText(sb);
                        dt.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onPageScrollStateChanged(int i) {

                    }

                });
                banner.start();
            }


            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getDetails());
            }
        });


    }


    public class GlideImageLoader extends ImageLoader {
        @Override
        public void displayImage(Context context, Object path, ImageView imageView) {
            StorageReference r = FirebaseStorage.getInstance().getReferenceFromUrl(path.toString());
            GlideApp.with(context.getApplicationContext())
                    .load(r)
                    .placeholder(R.drawable.common_full_open_on_phone)
                     .error(R.drawable.common_full_open_on_phone)
                    .into(imageView);
        }

    }

}
