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
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LiveStatsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LiveStatsFragment extends Fragment {

    private LineChart daily_cases_lineChart, daily_deaths_lineChart;

    private static LiveStatsFragment _instance = null;

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
        _instance = this;
        /*if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
        }*/
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_live_stats, container, false);
        daily_cases_lineChart = (LineChart)(root.findViewById(R.id.live_stats_line_chart_1));
        daily_deaths_lineChart = (LineChart)(root.findViewById(R.id.live_stats_line_chart_2));
        //showLineChart1(root);
        //showLineChart2(root);
        return root;
    }

    public static void refreshDailyCasesChart(JSONArray data){
        System.out.println("refreshing daily cases");
        _instance.refreshLineChart(_instance.daily_cases_lineChart, data, "Daily Cases", "Daily Cases",
                "jour", "conf");
    }

    public static void refreshDailyDeathsChart(JSONArray data){
        System.out.println("refreshing daily deaths");
        _instance.refreshLineChart(_instance.daily_deaths_lineChart, data, "Daily Deaths", "Daily Deaths",
                "jour", "death");
    }

    private void refreshLineChart(LineChart lineChart, JSONArray data, String label, String description,
                                  String dateColumnName, String valueColumnName){
        Description d = new Description();
        d.setText(description);
        lineChart.setDescription(d);

        LineDataSet lineDataSet = new LineDataSet(getData(data, dateColumnName, valueColumnName), label);
        lineDataSet.setDrawCircles(false);
        lineDataSet.setColor(ContextCompat.getColor(getActivity(), R.color.colorPrimary));
        lineDataSet.setValueTextColor(ContextCompat.getColor(getActivity(), R.color.colorPrimaryDark));
        lineDataSet.setDrawValues(false);
        XAxis xAxis = lineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        ValueFormatter formatter = new ValueFormatter() {
            @Override
            public String getAxisLabel(float value, AxisBase axis) {
                //return months[((int) value) % months.length];
                return getDateLabel(value);
            }
        };
        xAxis.setGranularity(1f);
        xAxis.setValueFormatter(formatter);

        YAxis yAxisRight = lineChart.getAxisRight();
        yAxisRight.setEnabled(false);

        YAxis yAxisLeft = lineChart.getAxisLeft();
        yAxisLeft.setGranularity(1f);

        LineData _data = new LineData(lineDataSet);
        lineChart.setData(_data);
        lineChart.animateX(1500);
        lineChart.invalidate();
    }

    private ArrayList getData(JSONArray data, String dateColumnName, String valueColumnName){
        ArrayList<Entry> entries = new ArrayList<>();

        try {
            for (int i = 0; i < data.length(); ++i) {
                JSONObject rec = data.getJSONObject(i);
                int dc = rec.getInt(valueColumnName);
                String jour = rec.getString(dateColumnName);
                int year = Integer.parseInt(jour.substring(0, 4));
                int month = Integer.parseInt(jour.substring(5, 7));
                int day = Integer.parseInt(jour.substring(8, 10));
                Calendar cal = Calendar.getInstance();
                cal.set(year, month - 1, day, 0, 0, 0);
                Date date = cal.getTime();
                System.out.println(date.toString() + " : " + dc);
                entries.add(new Entry(date.getTime(), dc));
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }

        /*entries.add(new Entry(10f, 4f));
        entries.add(new Entry(11f, 1f));
        entries.add(new Entry(12f, 2f));
        entries.add(new Entry(13f, 4f));
        entries.add(new Entry(14f, 2f));
        entries.add(new Entry(18f, 5f));
        entries.add(new Entry(20f, 1f));
        entries.add(new Entry(21f, 3f));*/
        return entries;
    }

    private String getDateLabel(float time){
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis((long)time);
        return cal.get(Calendar.DAY_OF_MONTH) + "/" + (cal.get(Calendar.MONTH) + 1);
    }
}
