package com.iteration1.savingwildlife;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.lwj.widget.viewpagerindicator.ViewPagerIndicator;

import java.util.ArrayList;
import java.util.List;

public class MyEvents extends AppCompatActivity {
    private TextView toolbar_title;
    private Toolbar toolbar;
    private ViewPagerIndicator mViewPagerIndicator;
    private ViewPager mViewPager;
    private SectionsPagerAdapter mPagerAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.myevents);
        initUI();
    }

    public void initUI() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar_title = (TextView) findViewById(R.id.toolbar_title);
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
        mViewPagerIndicator = (ViewPagerIndicator) findViewById(R.id.indicator_line);
        mPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.viewpager);
        SectionsPagerAdapter adapter = new SectionsPagerAdapter(getSupportFragmentManager());
        ViewMyEvents viewMyEvents = new ViewMyEvents();
        ViewJoinedEvents viewJoinedEvents = new ViewJoinedEvents();
        adapter.addFragment(viewMyEvents,"Events Created by Me");
        adapter.addFragment(viewJoinedEvents,"Events Joined by Me");
        mViewPager.setAdapter(adapter);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i2) {

            }

            @Override
            public void onPageSelected(int i) {
                if (i == 1){

                }
                // do your stuff
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
//        mViewPager.addOnPageChangeListener(this);
//        Objects.requireNonNull(mViewPager.getAdapter()).notifyDataSetChanged();
        mViewPagerIndicator.setViewPager(mViewPager);
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

        @Override
        public int getItemPosition(Object object) {
            // POSITION_NONE makes it possible to reload the PagerAdapter
            return POSITION_NONE;
        }

        public void addFragment(Fragment fragment, String title) {
            fragmentList.add(fragment);
            fragmenTitleList.add(title);
        }
    }

}
