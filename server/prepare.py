import pandas as pd
from fbprophet import Prophet
from statsmodels.tsa.arima_model import ARIMA

# Nettoyage des données.
# 1. remplissez toutes les valeurs vides avec 0, en rendant les données continues
# 2. éliminer les colonnes non souhaitées et les renommer
def prepare_data():
    print("Begin prepare.")
    df = pd.read_csv("covid-19-all.csv",sep=",")
    df = df.fillna(0)
    drop_list = ['Latitude','Longitude']
    df.drop(columns=drop_list,inplace=True)
    headers = ['Country','Province','Confirmed','Recovered','Deaths','Date']
    df.to_csv("res.csv",sep=";",index=False,header=headers)
    print("Prepare finished.")

# Seules les données françaises ont été sélectionnées
def pred_data():
    df = pd.read_csv("covid-19-all.csv",sep=",")
    df = df.fillna(0)
    res = df[df['Country/Region']=='France'].groupby('Date')['Confirmed','Recovered','Deaths'].sum().reset_index()
    return res

# Prédiction - Modèle Prophet
def predict_Prophet():
    data = pred_data()
    # Renommé le nom du colones pour modèle
    train = data.loc[:,["Date","Confirmed"]].rename(columns={"Date":"ds","Confirmed":"y"})
    m = Prophet()
    m.fit(train)
    # Prédiction pour les 30 prochains jours
    forecast = m.predict(m.make_future_dataframe(periods=30))
    # Date et prédiction
    f_data = forecast.loc[:,['ds','trend']]
    # Prenez que les bonnes prédictions(trend>0)
    predict = f_data[f_data['trend']>0].rename(columns={"ds":"Date","trend":"Confirm"})
    predict[["Confirm"]] = predict[["Confirm"]].astype(int)
    res = predict.astype(str).to_json(orient='records')
    return res

# Prédiction - Modèle Arima
def predict_Arima():
    data = pred_data()
    conf = data.loc[:,["Date","Confirmed"]]
    # Calculer la valeur cumulée
    train = conf.cumsum()
    train["NDate"] = train["Date"]
    train = train.drop("Date",axis=1)
    model = ARIMA(train["Confirmed"].values, order=(2, 2, 1))
    fit = model.fit(trend='c', full_output=True, disp=True)
    forcast = fit.forecast(steps=31)
    pred = pd.DataFrame(forcast[0].tolist())
    pred['pred'] = pred - pred.shift(1)
    # Concaténation des anciennes et des nouvelles données
    predict = pd.DataFrame({'Date':pd.date_range("20200526",periods=30).astype(str),'Confirmed':pred.drop(0)['pred'].astype(int)})
    conf["Confirmed"] = conf["Confirmed"].astype(int)
    res = pd.concat([conf, predict])
    res.reset_index(drop=True,inplace=True)
    res = res.to_json(orient='records')
    return res

# Prédiction décès - Modèle Prophet
def predict_death_P():
    data = pred_data()
    # Renommé le nom du colones pour modèle
    train = data.loc[:,["Date","Deaths"]].rename(columns={"Date":"ds","Deaths":"y"})
    m = Prophet()
    m.fit(train)
    # Prédiction pour les 30 prochains jours
    forecast = m.predict(m.make_future_dataframe(periods=30))
    # Date et prédiction
    f_data = forecast.loc[:,['ds','trend']]
    # Prenez que les bonnes prédictions(trend>0)
    predict = f_data[f_data['trend']>0].rename(columns={"ds":"Date","trend":"Deaths"})
    predict[["Deaths"]] = predict[["Deaths"]].astype(int)
    res = predict.astype(str).to_json(orient='records')
    return res

# Prédiction décès - Modèle Arima
def predict_death_A():
    data = pred_data()
    conf = data.loc[:,["Date","Deaths"]]
    # Calculer la valeur cumulée
    train = conf.cumsum()
    train["NDate"] = train["Date"]
    train = train.drop("Date",axis=1)
    model = ARIMA(train["Deaths"].values, order=(2, 2, 1))
    fit = model.fit(trend='c', full_output=True, disp=True)
    forcast = fit.forecast(steps=31)
    pred = pd.DataFrame(forcast[0].tolist())
    pred['pred'] = pred - pred.shift(1)
    # Concaténation des anciennes et des nouvelles données
    predict = pd.DataFrame({'Date':pd.date_range("20200526",periods=30).astype(str),'Deaths':pred.drop(0)['pred'].astype(int)})
    conf["Deaths"] = conf["Deaths"].astype(int)
    res = pd.concat([conf, predict])
    res.reset_index(drop=True,inplace=True)
    res = res.to_json(orient='records')
    return res
