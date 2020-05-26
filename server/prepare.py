import numpy as np
import socket
import urllib
import pandas as pd
import requests

def data_france(df):
    return df[df["maille_code"] == "FRA"]


def clean_confirmed(df):
    return df.drop(df[df["cas_confirmes"].isna()].index)

def fill_rea(df):
    return df.fillna(value=0)

def keep_only_first_row_by_date(df):
    return df.groupby("date", as_index=False).first()

# Il y a des valeurs NAN dans la colonne des guéris, d'où les petits trous par endroits.
def clean_recov(df, feature):
    """
    replaces nan in given feature by linear interpolation
    """
    col = df[feature]
    nan_index = np.where(col.isna())
    xp = np.setdiff1d(col.index, nan_index)
    yp = col[xp]
    interp = np.interp(nan_index, xp, yp)

    df_copy = df.copy()

    for num, idx in enumerate(nan_index):
        df_copy.loc[idx, feature] = interp[num]
        
    return df_copy

def cast_type(df):
    cols = ["cas_confirmes","deces","reanimation","hospitalises","gueris","susceptible"]
    for i in cols:
        df[[i]]=df[[i]].astype(int)

def prepare_data():
    socket.setdefaulttimeout(30)
    # URL_DATA_SRC = 'https://www.data.gouv.fr/fr/datasets/r/0b66ca39-1623-4d9c-83ad-5434b7f9e2a4'
    URL_DATA_SRC = 'https://raw.githubusercontent.com/opencovid19-fr/data/master/dist/chiffres-cles.csv'
    file_downloaded ='data.csv'
    try:
        print("Download job begin.")
        urllib.request.urlretrieve(URL_DATA_SRC,file_downloaded)
        print("Download job finished, begin parse csv.")
        df = pd.read_csv("data.csv",sep=",")
        df = data_france(df)
        confirmed = clean_confirmed(df)
        ev_byday = keep_only_first_row_by_date(confirmed)
        ev_byday = confirmed.groupby("date", as_index=False).first()
        ev_byday = clean_recov(ev_byday,"gueris")
        pop_fr=67_000_000
        ev_byday["susceptible"] = pop_fr-(ev_byday["cas_confirmes"]+ev_byday["deces"]+ev_byday["gueris"])
        drop_list = ['granularite','maille_code','maille_nom','cas_ehpad','cas_confirmes_ehpad','cas_possibles_ehpad','deces_ehpad','depistes','source_nom','source_url','source_archive','source_type']
        ev_byday.drop(columns=drop_list,inplace=True)
        ev_byday.set_index(["date"], inplace=True)
        ev_byday = fill_rea(ev_byday)
        cast_type(ev_byday)
        ev_byday.to_csv("res.csv",sep=";")
        print("Parse finished.")
    except socket.timeout:
        count = 1
        while count <= 5:
            try:
                urllib.urlretrieve(URL_DATA_SRC,file_downloaded)
                break
            except socket.timeout:
                err_info = 'Reloading for %d time'%count if count ==1 else 'Reload for %d times'%count
                print(err_info)
                count += 1
        if count > 5:
                print("download job failed")