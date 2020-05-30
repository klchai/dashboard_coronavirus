package com.example.coronavirus_dashboard.ui.main;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

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

//Une classe qui contient des fonction statiques de manipulation des graphiques
//utilisées fréquemment par LiveStatsFragment et PredictedStatsFragment
public class ChartUtils {
    //Prend un float qui représente le timestamp d'une date
    //et retourne un String qui représente un label sur l'axe X des dates (comme par ex : "21/01")
    public static String getDateLabel(float time){
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis((long)time);
        //On retourne le format : "JJ/MM"
        return cal.get(Calendar.DAY_OF_MONTH) + "/" + (cal.get(Calendar.MONTH) + 1);
    }

    //Prend un JSONArray et retourne une liste de Entry de MPAndroid qui sera utilisé par les fonctions
    //d'affichage de graphiques
    //dateColumnName : le nom du champs "Date" dans le JSON
    //valueColumnName : le nom du champs "Valeur" dans le JSON, qui peut etre le nombre de cas ou de deces
    //Exemple : JSON = [{dateColumnName : "JJ-MM-YYYY", valueColumnName : 1234}, ... ]
    public static ArrayList getLineChartData(JSONArray data, String dateColumnName, String valueColumnName){
        ArrayList<Entry> entries = new ArrayList<>();
        try {
            for (int i = 0; i < data.length(); ++i) {
                JSONObject rec = data.getJSONObject(i);
                //parser le champs "valeur" en int
                int dc = rec.getInt(valueColumnName);
                String jour = rec.getString(dateColumnName);
                //Parser la date en type Date
                int year = Integer.parseInt(jour.substring(0, 4));
                int month = Integer.parseInt(jour.substring(5, 7));
                int day = Integer.parseInt(jour.substring(8, 10));
                Calendar cal = Calendar.getInstance();
                cal.set(year, month - 1, day, 0, 0, 0);
                Date date = cal.getTime();
                //System.out.println(date.toString() + " : " + dc);
                entries.add(new Entry(date.getTime(), dc));
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }

        return entries;
    }

    //Raffraichir le contenu d'un LineChart donné, avec les données dans un JSONArray
    public static void refreshLineChart(FragmentActivity fragmentActivity, LineChart lineChart, JSONArray data,
                                  String label, String description,
                                  String dateColumnName, String valueColumnName){
        Description d = new Description();
        d.setText(description);
        lineChart.setDescription(d);

        LineDataSet lineDataSet = new LineDataSet(getLineChartData(data, dateColumnName, valueColumnName), label);
        lineDataSet.setDrawCircles(false);
        lineDataSet.setColor(ContextCompat.getColor(fragmentActivity, R.color.colorPrimary));
        lineDataSet.setValueTextColor(ContextCompat.getColor(fragmentActivity, R.color.colorPrimaryDark));
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
}
