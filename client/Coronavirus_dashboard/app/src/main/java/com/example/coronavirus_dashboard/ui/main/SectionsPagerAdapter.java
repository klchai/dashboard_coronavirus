package com.example.coronavirus_dashboard.ui.main;

import android.content.Context;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.coronavirus_dashboard.R;

//Classe manipulant le contenu du Tab Layout de l'interface
public class SectionsPagerAdapter extends FragmentPagerAdapter {

    //Noms des onglets
    @StringRes
    private static final int[] TAB_TITLES = new int[]{R.string.tab_text_1, R.string.tab_text_2};
    private final Context mContext;

    public SectionsPagerAdapter(Context context, FragmentManager fm) {
        super(fm);
        mContext = context;
    }

    //Contenu des onglets
    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                return LiveStatsFragment.newInstance();
            case 1:
                return PredictedStatsFragment.newInstance();
            default:
                return null;
        }
    }

    //Noms des onglets
    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return mContext.getResources().getString(TAB_TITLES[position]);
    }

    //Nombre d'onglets
    @Override
    public int getCount() {
        // Show 2 total pages.
        return 2;
    }
}