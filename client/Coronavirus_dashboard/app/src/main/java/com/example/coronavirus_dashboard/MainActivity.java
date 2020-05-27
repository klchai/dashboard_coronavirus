package com.example.coronavirus_dashboard;

import android.os.Bundle;

import com.example.coronavirus_dashboard.ui.main.LiveStatsFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.example.coronavirus_dashboard.ui.main.SectionsPagerAdapter;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());
        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(sectionsPagerAdapter);
        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);
        FloatingActionButton fab = findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDataAndRefreshCharts();
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    private void getDataAndRefreshCharts(){
        //Daily cases data
        String myUrl = "http://10.0.2.2:5000/confbyday";
        //String to place our result in
        String result;
        //Instantiate new instance of our class
        JSONRequests getRequest = new JSONRequests();
        try {
            result = getRequest.execute(myUrl).get();
            System.out.println(result);
            JSONArray obj = new JSONArray(result);
            LiveStatsFragment.refreshDailyCasesChart(obj);
        /*} catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();*/
        } catch (Exception e){
            e.printStackTrace();
        }

        //Daily deaths data
        //remplacer par le lien vers le nobmre de deces par jour
        myUrl = "http://10.0.2.2:5000/deathbyday";
        //String to place our result in
        result = null;
        //Instantiate new instance of our class
        getRequest = new JSONRequests();
        try {
            result = getRequest.execute(myUrl).get();
            System.out.println(result);
            JSONArray obj = new JSONArray(result);
            LiveStatsFragment.refreshDailyDeathsChart(obj);
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}