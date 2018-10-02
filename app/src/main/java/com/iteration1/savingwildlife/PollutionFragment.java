package com.iteration1.savingwildlife;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.bin.david.form.core.SmartTable;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LegendEntry;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class PollutionFragment extends Fragment {
    private View fView;
    private ArrayList<String> items;
    private ArrayList<String> counts;
    private BarChart chart;
    private PieChart chart2;
    private Button button;
    private Boolean infirstpage;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        fView = inflater.inflate(R.layout.pollution_fragment, container, false);

        items = new ArrayList<>();
        counts = new ArrayList<>();
        infirstpage = true;
        chart = (BarChart) fView.findViewById(R.id.chart);
        chart.setNoDataText("Graph is loading...");
        chart2 = (PieChart) fView.findViewById(R.id.chart2);
        button = fView.findViewById(R.id.sb);
        connectDatabase();
        ViewSwitcher simpleViewSwitcher = (ViewSwitcher) fView.findViewById(R.id.viewswitcher);
        View nextView = simpleViewSwitcher.getNextView();
        Animation in = AnimationUtils.loadAnimation(getContext(), android.R.anim.slide_in_left);
        simpleViewSwitcher.setInAnimation(in);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (infirstpage) {
                    simpleViewSwitcher.showNext();
                } else {
                    simpleViewSwitcher.showPrevious();
                }
            }
        });
        return fView;
    }

    private void connectDatabase() {
        // Get the reference of firebase instance
        DatabaseReference mReference = FirebaseDatabase.getInstance().getReference("pollutionresource");
        mReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    HashMap<String, String> map = (HashMap<String, String>) child.getValue();
                    String a = (String) map.get("item");
                    String b = (String) map.get("count");
                    items.add(a);
                    counts.add(b);
                }
                ArrayList<BarEntry> entry = new ArrayList<>();
                for (int i = 0; i < counts.size(); i++) {
                    entry.add(new BarEntry(i, Integer.valueOf(counts.get(i)), items.get(i)));
                }
                BarDataSet dataSet = new BarDataSet(entry, "");
                ArrayList<IBarDataSet> dataSets = new ArrayList<IBarDataSet>();
                dataSets.add(dataSet);
                BarData data = new BarData(dataSets);
                ArrayList<Integer> colors = new ArrayList<>();
                for (int i : ColorTemplate.COLORFUL_COLORS
                        ) {
                    colors.add(i);
                }
                for (int i : ColorTemplate.JOYFUL_COLORS
                        ) {
                    colors.add(i);
                }
                dataSet.setColors(colors);
                chart.setData(data);
                chart.invalidate();
                XAxis axis = chart.getXAxis();
                axis.setPosition(XAxis.XAxisPosition.BOTTOM);
                axis.setLabelRotationAngle(-45);
                axis.setTextSize(8f);
                axis.setDrawLabels(false);
                chart.setScaleEnabled(false);
                chart.getAxis(YAxis.AxisDependency.RIGHT).setDrawLabels(false);
                chart.getAxis(YAxis.AxisDependency.LEFT).setTextSize(5);
                chart.setDescription(null);
                chart.getAxisLeft().setDrawGridLines(false);
                chart.getAxisRight().setDrawGridLines(false);
                chart.getXAxis().setDrawGridLines(false);
                Legend legend = chart.getLegend();
                legend.setEnabled(true);
                ArrayList<LegendEntry> t = new ArrayList<>();
                for (int u = 0; u < 10; u++) {
                    t.add(new LegendEntry(items.get(u), Legend.LegendForm.DEFAULT,
                            8f, 2f, null, colors.get(u)));
                }
                legend.setCustom(t);
                legend.setOrientation(Legend.LegendOrientation.VERTICAL);
                legend.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
                legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
                legend.setDrawInside(true);
                chart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
                    @Override
                    public void onValueSelected(Entry e, Highlight h) {
                        e.getData();
                        Toast.makeText(getContext(), items.get(entry.indexOf(e)),
                                Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onNothingSelected() {
                    }
                });


                ArrayList<PieEntry> pentry = new ArrayList<>();
                for (int i = 0; i < counts.size(); i++) {
                    pentry.add(new PieEntry(Integer.valueOf(counts.get(i)), items.get(i)));
                    Log.d("count" + i, counts.get(i));
                }
                PieDataSet pdataset = new PieDataSet(pentry, "");
                pdataset.setColors(colors);
                PieData pdata = new PieData(pdataset);
                chart2.setData(pdata);
                chart2.getLegend().setEnabled(false);
                chart2.setEntryLabelTextSize(8);
                chart2.setEntryLabelColor(Color.DKGRAY);
                chart2.getDescription().setEnabled(false);
                chart2.invalidate();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getDetails());
            }
        });

    }


}
