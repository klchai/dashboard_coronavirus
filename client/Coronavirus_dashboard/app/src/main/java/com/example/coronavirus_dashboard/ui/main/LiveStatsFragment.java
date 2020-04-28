package com.example.coronavirus_dashboard.ui.main;

import android.app.Activity;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.coronavirus_dashboard.MainActivity;
import com.example.coronavirus_dashboard.R;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LiveStatsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LiveStatsFragment extends Fragment {

    private LineChart lineChart;

    public LiveStatsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment LiveStatsFragment.
     */
    public static LiveStatsFragment newInstance() {
        LiveStatsFragment fragment = new LiveStatsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
        }*/
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_live_stats, container, false);
        showLineChart1(root);
        showLineChart2(root);
        // Inflate the layout for this fragment
        return root;
    }

    private void showLineChart1(View root){
        lineChart = (LineChart)(root.findViewById(R.id.live_stats_line_chart_1));
        LineDataSet lineDataSet = new LineDataSet(getData(), "Total Cases");
        lineDataSet.setColor(ContextCompat.getColor(getActivity(), R.color.colorPrimary));
        lineDataSet.setValueTextColor(ContextCompat.getColor(getActivity(), R.color.colorPrimaryDark));
        XAxis xAxis = lineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        final String[] months = new String[]{"Jan", "Feb", "Mar", "Apr"};
        ValueFormatter formatter = new ValueFormatter() {
            @Override
            public String getAxisLabel(float value, AxisBase axis) {
                return months[((int) value) % months.length];
            }
        };
        xAxis.setGranularity(1f);
        xAxis.setValueFormatter(formatter);

        YAxis yAxisRight = lineChart.getAxisRight();
        yAxisRight.setEnabled(false);

        YAxis yAxisLeft = lineChart.getAxisLeft();
        yAxisLeft.setGranularity(1f);

        LineData data = new LineData(lineDataSet);
        lineChart.setData(data);
        lineChart.animateX(1500);
        lineChart.invalidate();
    }

    private void showLineChart2(View root){
        lineChart = (LineChart)(root.findViewById(R.id.live_stats_line_chart_2));
        LineDataSet lineDataSet = new LineDataSet(getData(), "Total Deaths");
        lineDataSet.setColor(ContextCompat.getColor(getActivity(), R.color.colorPrimary));
        lineDataSet.setValueTextColor(ContextCompat.getColor(getActivity(), R.color.colorPrimaryDark));
        XAxis xAxis = lineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        final String[] months = new String[]{"Jan", "Feb", "Mar", "Apr"};
        ValueFormatter formatter = new ValueFormatter() {
            @Override
            public String getAxisLabel(float value, AxisBase axis) {
                return months[((int) value) % months.length];
            }
        };
        xAxis.setGranularity(1f);
        xAxis.setValueFormatter(formatter);

        YAxis yAxisRight = lineChart.getAxisRight();
        yAxisRight.setEnabled(false);

        YAxis yAxisLeft = lineChart.getAxisLeft();
        yAxisLeft.setGranularity(1f);

        LineData data = new LineData(lineDataSet);
        lineChart.setData(data);
        lineChart.animateX(1500);
        lineChart.invalidate();
    }

    private ArrayList getData(){
        ArrayList<Entry> entries = new ArrayList<>();
        entries.add(new Entry(10f, 4f));
        entries.add(new Entry(11f, 1f));
        entries.add(new Entry(12f, 2f));
        entries.add(new Entry(13f, 4f));
        entries.add(new Entry(14f, 2f));
        entries.add(new Entry(18f, 5f));
        entries.add(new Entry(20f, 1f));
        entries.add(new Entry(21f, 3f));
        return entries;
    }
}
