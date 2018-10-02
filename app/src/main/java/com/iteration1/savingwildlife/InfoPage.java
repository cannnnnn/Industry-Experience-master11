package com.iteration1.savingwildlife;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.iteration1.savingwildlife.entities.Beach;
import com.lwj.widget.viewpagerindicator.ViewPagerIndicator;

import java.util.ArrayList;
import java.util.List;

public class InfoPage extends AppCompatActivity {


    private TextView toolbar_title;
//    private TextView beachtitle;

    private FloatingActionButton direction;
    private Toolbar toolbar;
    private Beach selected;
    private ViewPagerIndicator mViewPagerIndicator;
    private ArrayList<String> reports;


    private ViewPager mViewPager;
    private SectionsPagerAdapter mPagerAdapter;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.info_page);
        initUI();
    }

    public void initUI() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar_title = (TextView) findViewById(R.id.toolbar_title);
//        beachtitle = (TextView) findViewById(R.id.beachtitle);

        direction = (FloatingActionButton) findViewById(R.id.direction_button);
        mViewPagerIndicator = (ViewPagerIndicator) findViewById(R.id.indicator_line);

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

        mPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.viewpager);
        SectionsPagerAdapter adapter = new SectionsPagerAdapter(getSupportFragmentManager());
        InfoTextFragment infotxt = new InfoTextFragment();
        InfoStatisticFragment infosta = new InfoStatisticFragment();
        InfoImageFragment infoimg = new InfoImageFragment();
        adapter.addFragment(infotxt, "Beach Introduction");
        adapter.addFragment(infosta, "Beach cleanup");
        adapter.addFragment(infoimg, "Beach gallery");
        mViewPager.setAdapter(adapter);
        mViewPagerIndicator.setViewPager(mViewPager);


        Intent intent = getIntent();
        // Use bundle to receive params
        Bundle bundle = intent.getExtras();
        assert bundle != null;
        selected = (Beach) bundle.getSerializable("beach");
        reports = bundle.getStringArrayList("reports");

        toolbar_title.setText(selected.getName());
//        StringBuilder sb = new StringBuilder("Learn about ");
//        sb.append(selected.getName());
//        beachtitle.setText(sb);
        Bundle bundle1 = new Bundle();
        bundle1.putSerializable("selected", selected);
        bundle1.putStringArrayList("reports", reports);

        infotxt.setArguments(bundle1);
        infosta.setArguments(bundle1);
        infoimg.setArguments(bundle1);


        direction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String uri = "http://maps.google.com/maps?daddr=" + selected.getLatitude() + "," + selected.getLongitude() + " (" + selected.getName() + ")";
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                intent.setPackage("com.google.android.apps.maps");

                Log.d("lat", Double.toString(selected.getLatitude()));
                startActivity(intent);
            }
        });

    }


    // When user click the back button of their own phone
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }


    // For horizontal slide
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        private final List<Fragment> fragmentList = new ArrayList<>();
        private final List<String> fragmenTitleList = new ArrayList<>();

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {

            return fragmentList.get(position);
        }

        @Override
        public int getCount() {
            return fragmentList.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return fragmenTitleList.get(position);
        }

        public void addFragment(Fragment fragment, String title) {
            fragmentList.add(fragment);
            fragmenTitleList.add(title);
        }
    }


}
