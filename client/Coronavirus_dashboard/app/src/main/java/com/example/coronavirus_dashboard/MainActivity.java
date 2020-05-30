package com.example.coronavirus_dashboard;

import android.os.Bundle;

import com.example.coronavirus_dashboard.ui.main.LiveStatsFragment;
import com.example.coronavirus_dashboard.ui.main.PredictedStatsFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.coronavirus_dashboard.ui.main.SectionsPagerAdapter;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.concurrent.ExecutionException;

//Classe principale
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Affichage de l'interface principale de l'application
        //définie dans activity_main.xml
        setContentView(R.layout.activity_main);
        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());
        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(sectionsPagerAdapter);
        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);

        //Ajouter un bouton permettant de raffraichir le contenu de l'interface
        FloatingActionButton fab = findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            //evenement de clique
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "Loading Data ...", Toast.LENGTH_LONG);
                //Actualiser l'onglet : Live Stats
                LiveStatsFragment.refreshAll();
                //Actualiser l'onglet : Predicted Stats
                PredictedStatsFragment.refreshAll();
            }
        });
    }
}