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

def prediction():
    pred = pd.read_csv("covid-19-all.csv",sep=",")
    pred = pred.fillna(0)
    predgrp = pred[pred['Country/Region']=='France'].groupby('Date')['Confirmed','Recovered','Deaths'].sum().reset_index()
    return predgrp

def predict_Prophet():
    predgrp = prediction()
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
    res = cnfrm.astype(str).to_json(orient='records')
    return res

def predict_Arima():
    predgrp = prediction()
    pred_cnfrm = predgrp.loc[:,["Date","Confirmed"]]
    confirm_cs = pred_cnfrm.cumsum()
    confirm_cs['date1'] = pred_cnfrm['Date']
    confirm_cs = confirm_cs.drop('Date',axis=1)
    arima_data = confirm_cs
    arima_data.columns = ['count','confirmed_date']
    model = ARIMA(arima_data['count'].values, order=(1, 2, 1))
    fit_model = model.fit(trend='c', full_output=True, disp=True)
    forcast = fit_model.forecast(steps=30)
    pred_y = forcast[0].tolist()
    pred = pd.DataFrame(pred_y)
    pred['pred']=  pred - pred.shift(1)
    predict = pd.DataFrame({'Date':pd.date_range("20200526",periods=29).astype(str),'Confirmed':pred.drop(0)['pred'].astype(int)})
    pred_cnfrm["Confirmed"] = pred_cnfrm["Confirmed"].astype(int)
    res = pd.concat([pred_cnfrm, predict])
    res.reset_index(drop=True,inplace=True)
    res = res.to_json(orient='records')
    return res

