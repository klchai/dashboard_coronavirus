import pandas as pd
from fbprophet import Prophet
from statsmodels.tsa.arima_model import ARIMA

def prepare_data():
    print("Begin prepare.")
    df = pd.read_csv("covid-19-all.csv",sep=",")
    df = df.fillna(0)
    drop_list = ['Latitude','Longitude']
    df.drop(columns=drop_list,inplace=True)
    headers = ['Country','Province','Confirmed','Recovered','Deaths','Date']
    df.to_csv("res.csv",sep=";",index=False,header=headers)
    print("Prepare finished.")

def predict_Prophet():
    pred = pd.read_csv("covid-19-all.csv",sep=",")
    pred = pred.fillna(0)
    predgrp = pred[pred['Country/Region']=='France'].groupby('Date')['Confirmed','Recovered','Deaths'].sum().reset_index()
    pred_cnfrm = predgrp.loc[:,["Date","Confirmed"]]
    pr_data = pred_cnfrm
    pr_data.columns = ['ds','y']
    m = Prophet()
    m.fit(pr_data)
    future=m.make_future_dataframe(periods=15)
    forecast=m.predict(future)
    cnfrm = forecast.loc[:,['ds','trend']]
    cnfrm = cnfrm[cnfrm['trend']>0]
    cnfrm.columns = ['Date','Confirm']
    cnfrm[["Confirm"]] = cnfrm[["Confirm"]].astype(int)
    res = cnfrm.astype(str).to_json(orient='records',date_format="iso")
    return res