package com.example.coronavirus_dashboard.ui.main;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.coronavirus_dashboard.JSONRequests;
import com.example.coronavirus_dashboard.R;
import com.github.mikephil.charting.charts.LineChart;

import org.json.JSONArray;

//Classe manipulant le contenu de l'onglet : Predicted Stats
public class PredictedStatsFragment extends Fragment {

    //Les 2 Line Plot :
        //Nombre de cas prédit par jour
        //Nombre de deces prédit par jour
    private LineChart daily_cases_lineChart, daily_deaths_lineChart;

    //Une instance statique de cette classe pour permettre l'accès au graphiques à partir des autres classes
    private static PredictedStatsFragment _instance = null;

    public PredictedStatsFragment() {
        // Required empty public constructor
    }

    //Création d'une nouvelle instance, le contenu de cette fonction est comme par défaut
    public static PredictedStatsFragment newInstance() {
        PredictedStatsFragment fragment = new PredictedStatsFragment();
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
        //Utiliser le contenu graphique de fragment_predicted_stats.xml
        View root = inflater.inflate(R.layout.fragment_predicted_stats, container, false);
        //Trouver les graphiques depuis le contenu chargé du fichier XML
        daily_cases_lineChart = (LineChart)(root.findViewById(R.id.predicted_stats_line_chart_1));
        daily_deaths_lineChart = (LineChart)(root.findViewById(R.id.predicted_stats_line_chart_2));
        //Actualiser le contenu des graphiques
        refreshAll();
        return root;
    }

    //Fonction publique statique qui, quand elle est appelée, actualise le contenu de tous les graphiques
    //de cet onglet
    public static void refreshAll(){
        //Daily cases
        //URL vers le JSON de nombre prédit de cas par jour
        String myUrl = "http://10.0.2.2:5000/pred_conf";
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
        } catch (Exception e){
            //En cas d'erreur de téléchargment, de parsing, ou autre
            e.printStackTrace();
        }

        //Daily deaths data
        //On refait la meme chose, pour le nombre prédit de deces par jour
        //il suffit de changer l'URL
        myUrl = "http://10.0.2.2:5000/pred_death_P";
        getRequest = new JSONRequests();
        try {
            result = getRequest.execute(myUrl).get();
            //System.out.println(result);
            JSONArray obj = new JSONArray(result);
            //On raffraichit le Line Plot avec le contenu reçu
            refreshDailyDeathsChart(obj);
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    //Rafraichir le contenu du Line Plot de nombre prédit de cas par jour, à partir d'un JSONArray
    public static void refreshDailyCasesChart(JSONArray data){
        System.out.println("refreshing predicted daily cases");
        ChartUtils.refreshLineChart(_instance.getActivity(), _instance.daily_cases_lineChart,
                data, "Predicted Daily Cases", "Predicted Daily Cases",
                "Date", "Confirm");
    }

    //Rafraichir le contenu du Line Plot de nombre prédit de deces par jour, à partir d'un JSONArray
    public static void refreshDailyDeathsChart(JSONArray data){
        System.out.println("refreshing predicted daily deaths");
        ChartUtils.refreshLineChart(_instance.getActivity(), _instance.daily_deaths_lineChart,
                data, "Predicted Daily Deaths", "Predicted Daily Deaths",
                "Date", "Deaths");
    }
}
