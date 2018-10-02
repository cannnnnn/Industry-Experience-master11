package com.iteration1.savingwildlife;

import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LegendEntry;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.iteration1.savingwildlife.entities.Beach;
import com.iteration1.savingwildlife.utils.UIUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Set;

public class InfoStatisticFragment extends Fragment {
    private View parentView;
    private Beach selected;
    private BarChart chart;
    private ArrayList<HashMap> pollutions;
    private Button button;
    private ArrayList<Integer> colors;
    private TextView tv1;
    private int index;
    private int nowat;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        parentView = inflater.inflate(R.layout.beach_statistic_fragment, container, false);
        Bundle bundle = this.getArguments();
        assert bundle != null;
        selected = (Beach) bundle.getSerializable("selected");
        chart = (BarChart) parentView.findViewById(R.id.chart);
        pollutions = new ArrayList<>();
        index = 0;
        nowat = 0;
        connectDatabase();
        chart.setNoDataText("Get involved today!");
        chart.getAxis(YAxis.AxisDependency.RIGHT).setDrawLabels(false);
        button = parentView.findViewById(R.id.sb);
        tv1 = parentView.findViewById(R.id.instruction);
        colors = new ArrayList<>();
        for (int i : ColorTemplate.COLORFUL_COLORS
                ) {
            colors.add(i);
        }
        colors.add(Color.GRAY);
        return parentView;
    }

    private void nextYear(int i){
            HashMap thisyear = pollutions.get(i);
            ArrayList<String> names = new ArrayList<>();
            ArrayList<BarEntry> entry = new ArrayList<>();
            Map<String, Integer> vals = new HashMap<>();
            int position = 0;
            if (!thisyear.get("Cigarette butts").equals(0)){
                vals.put("Cigarette butts", Integer.valueOf(thisyear.get("Cigarette butts").toString()));
//                entry.add(new BarEntry(position,Integer.valueOf(thisyear.get("Cigarette butts").toString())));
//                names.add("Cigarette butts");
//                position++;
            }
            if (!thisyear.get("Metals").equals(0)){
                vals.put("Metals", Integer.valueOf(thisyear.get("Metals").toString()));
//                entry.add(new BarEntry(position,Integer.valueOf(thisyear.get("Metals").toString())));
//                names.add("Metals");
//                position++;
            }
            if (!thisyear.get("Others garbage").equals(0)){
                vals.put("Others garbage", Integer.valueOf(thisyear.get("Others garbage").toString()));
//                entry.add(new BarEntry(position,Integer.valueOf(thisyear.get("Others garbage").toString())));
//                names.add("Others garbage");
//                position++;
            }
            if (!thisyear.get("Plastic waste").equals(0)){
                vals.put("Plastic waste", Integer.valueOf(thisyear.get("Plastic waste").toString()));
//                entry.add(new BarEntry(position,Integer.valueOf(thisyear.get("Plastic waste").toString())));
//                names.add("Plastic waste");
//                position++;
            }
            if (!thisyear.get("Recyclables").equals(0)){
                vals.put("Recyclables", Integer.valueOf(thisyear.get("Recyclables").toString()));
//                entry.add(new BarEntry(position,Integer.valueOf(thisyear.get("Recyclables").toString())));
//                names.add("Recyclables");
//                position++;
            }
            if (!thisyear.get("Rubber waste").equals(0)){
                vals.put("Rubber waste", Integer.valueOf(thisyear.get("Rubber waste").toString()));
//                entry.add(new BarEntry(position,Integer.valueOf(thisyear.get("Rubber waste").toString())));
//                names.add("Rubber waste");
            }
            vals = UIUtils.sortByValue(vals);
        for (Map.Entry<String, Integer> e : vals.entrySet())
        {
            entry.add(new BarEntry(position, e.getValue()));
            position++;
            names.add(e.getKey());
        }
            BarDataSet dataSet = new BarDataSet(entry, thisyear.get("Year").toString());
            ArrayList<IBarDataSet> dataSets = new ArrayList<IBarDataSet>();
            dataSets.add(dataSet);
            BarData data = new BarData(dataSets);

            dataSet.setColors(colors);
            chart.setData(data);

            Legend legend = chart.getLegend();
            legend.setEnabled(true);
            ArrayList<LegendEntry> t = new ArrayList<>();
            for (int u = 0; u < names.size(); u++) {
                t.add(new LegendEntry(names.get(u), Legend.LegendForm.DEFAULT,
                        6f, 1f, null, colors.get(u)));
            }
            legend.setCustom(t);
            legend.setTextSize(9f);
            legend.setOrientation(Legend.LegendOrientation.VERTICAL);
            legend.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
            legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
            legend.setDrawInside(true);
            chart.getDescription().setText("Year: " + thisyear.get("Year").toString());
            chart.getAxisLeft().setDrawGridLines(false);
            chart.getAxisRight().setDrawGridLines(false);
            chart.getXAxis().setDrawGridLines(false);
            chart.getXAxis().setDrawLabels(false);
            chart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
            chart.getAxisRight().setEnabled(false);
            chart.invalidate();
    }

    private void connectDatabase(){
        DatabaseReference mReference = FirebaseDatabase.getInstance().getReference("beachpollution");
        mReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    HashMap<String, String> map = (HashMap<String, String>) child.getValue();
                    HashMap thismap = new HashMap<String, Integer>();
                    if (selected.getName().toLowerCase().contains(map.get("Beach"))){
                        Integer a = (Integer) Integer.valueOf(map.get("Year"));
                        Integer b = (Integer) Integer.valueOf(map.get("Cigarette butts"));
                        Integer c = (Integer) Integer.valueOf(map.get("Metals"));
                        Integer d = (Integer) Integer.valueOf(map.get("Others garbage"));
                        Integer e = (Integer) Integer.valueOf(map.get("Plastic waste"));
                        Integer f = (Integer) Integer.valueOf(map.get("Recyclables"));
                        Integer g = (Integer) Integer.valueOf(map.get("Rubber waste"));
                        thismap.put("Year", a);
                        thismap.put("Cigarette butts", b);
                        thismap.put("Metals", c);
                        thismap.put("Others garbage", d);
                        thismap.put("Plastic waste", e);
                        thismap.put("Recyclables", f);
                        thismap.put("Rubber waste", g);
                        pollutions.add(thismap);
                    }
                }
                index = pollutions.size();
                button.setVisibility(View.INVISIBLE);
                StringBuilder sb = new StringBuilder();
                if (index == 0){
                    sb.append("No clean up event recorded! \n\nCome and participate!");
                }else{
                    sb.append("In the past clean up events on "+ selected.getName().toLowerCase() + " , those things were found...");
                }
                tv1.setText(sb);
                if (index > 1){
                    button.setVisibility(View.VISIBLE);
                    button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            int i = 0;
                            if (nowat == index-1){i = 0;}else{i = nowat + 1;}
                            nowat = i;
                            nextYear(i);
                        }
                    });
                }
                if (index != 0) {
                    nowat = index-1;
                    nextYear(nowat);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}
