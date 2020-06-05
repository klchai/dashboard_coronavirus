package com.example.coronavirus_dashboard.ui.main;

import android.app.Activity;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.coronavirus_dashboard.JSONRequests;
import com.example.coronavirus_dashboard.MainActivity;
import com.example.coronavirus_dashboard.R;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
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

//Classe manipulant le contenu de l'onglet : Live Stats
public class LiveStatsFragment extends Fragment {

    //Les 2 Line Plot :
    //Nombre de cas par jour
    //Nombre de deces par jour
    private LineChart daily_cases_lineChart, daily_deaths_lineChart;
    //TODO : Ajouter des variables pour chacuns des graphiques nouvellement créés
    private PieChart daily_cases_PieChart;


    //Une instance statique de cette classe pour permettre l'accès au graphiques à partir des autres classes
    private static LiveStatsFragment _instance = null;

    public LiveStatsFragment() {
        // Required empty public constructor
    }

    //Création d'une nouvelle instance, le contenu de cette fonction est comme par défaut
    public static LiveStatsFragment newInstance() {
        LiveStatsFragment fragment = new LiveStatsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    //à la création d'une instance
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Marquer le contenu de _instance comme l'instance de LiveStatsFragment qui a été créée en premier
        _instance = this;
    }

    //Au chargement de la vue
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //Utiliser le contenu graphique de fragment_live_stats.xml
        View root = inflater.inflate(R.layout.fragment_live_stats, container, false);
        //Trouver les graphiques depuis le contenu chargé du fichier XML
        daily_cases_lineChart = (LineChart) (root.findViewById(R.id.live_stats_line_chart_1));
        daily_deaths_lineChart = (LineChart) (root.findViewById(R.id.live_stats_line_chart_2));
        //TODO : Affecter pour chacunes des variables des graphiques créées; leurs éléments correspondants
        daily_cases_PieChart = (PieChart) (root.findViewById(R.id.live_stats_pie_chart_1));


        //Actualiser le contenu des graphiques
        refreshAll();
        return root;
    }

    //Fonction publique statique qui, quand elle est appelée, actualise le contenu de TOUS les graphiques
    //de cet onglet
    public static void refreshAll() {
        //Daily cases data
        //URL vers le JSON de nombre de cas par jour
        String myUrl = "http://10.0.2.2:5000/confbyday";
        //String qui va contenir la réponse JSON
        String result;
        //Utilisation de la classe JSONRequests pour télécharger le JSON avec un nouveau thread
        JSONRequests getRequest = new JSONRequests();
        //Lancement du téléchargement
        try {
            result = getRequest.execute(myUrl).get();
            //System.out.println(result);
            //En cas de succès, on parse la réponse en JSONArray
            JSONArray obj = new JSONArray(result);
            //On raffraichit le Line Plot avec le contenu reçu
            refreshDailyCasesChart(obj);
        } catch (Exception e) {
            //En cas d'erreur de téléchargment, de parsing, ou autre
            e.printStackTrace();
        }

        //Daily deaths data
        //On refait la meme chose, pour le nombre de deces par jour
        //il suffit de changer l'URL
        myUrl = "http://10.0.2.2:5000/deathbyday";
        getRequest = new JSONRequests();
        try {
            result = getRequest.execute(myUrl).get();
            //System.out.println(result);
            JSONArray obj = new JSONArray(result);
            //On raffraichit le Line Plot avec le contenu reçu
            refreshDailyDeathsChart(obj);
        } catch (Exception e) {
            e.printStackTrace();
        }

        /*TODO : Faire la meme chose pour chacuns des nouveaux graphiques
         *  -Préciser l'URL du JSON
         *  -Instancier JSONRequests
         *  -Utiliser l'instance de JSONRequests pour télécharger le JSON
         *  -Parser le String reçu en JSONArray
         *  -Envoyer le résultat à une nouvelle fonction qui raffraichit le contenu du graphique à partir d'un JSONArray
         * */
        myUrl = "http://10.0.2.2:5000/compose/2020-05-25";
        getRequest = new JSONRequests();
        try {
            result = getRequest.execute(myUrl).get();
            System.out.println(result);

            JSONArray obj = new JSONArray(result);
            //On raffraichit le Line Plot avec le contenu reçu


            refreshDailyDeathsPieChart(obj);
            System.out.println("ccccccc");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void refreshDailyDeathsPieChart(JSONArray data) {

        System.out.println("refreshing daily cases pie");
        ArrayList<String> champs = new ArrayList();
        champs.add("Confirmed");
        champs.add("Recovered");
        champs.add("Deaths");
        ChartUtils.refreshPieChart(_instance.getActivity(), _instance.daily_cases_PieChart, data, " ", " ",
                "jour", champs);
    }

    //Rafraichir le contenu du Line Plot de nombre de cas par jour, à partir d'un JSONArray
    public static void refreshDailyCasesChart(JSONArray data) {
        System.out.println("refreshing daily cases");
        ChartUtils.refreshLineChart(_instance.getActivity(), _instance.daily_cases_lineChart,
                data, "Daily Cases", "Daily Cases",
                "jour", "conf");
    }

    //Rafraichir le contenu du Line Plot de nombre de deces par jour, à partir d'un JSONArray
    public static void refreshDailyDeathsChart(JSONArray data) {
        System.out.println("refreshing daily deaths");
        ChartUtils.refreshLineChart(_instance.getActivity(), _instance.daily_deaths_lineChart,
                data, "Daily Deaths", "Daily Deaths",
                "jour", "death");
    }
}
