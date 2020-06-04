from pyhive import hive
from flask import Flask,jsonify
import os
import pandas as pd
import prepare as pre
import requests
import csv
import urllib
import socket

app = Flask(__name__)

@app.route("/")
def home():
    return "Home page of the web server of the dashboard_coronavirus."

@app.route("/select")
def select():
    cursor.execute("SELECT * FROM covid WHERE country=\"France\" LIMIT 10")
    res=cursor.fetchall()
    res_json=[{"country":row[0],"prov":row[1],"confirm":row[2],"recov":row[3],"death":row[4],"jour":row[5]} for row in res]
    return jsonify(res_json)

@app.route("/deathbyday")
# Nombre de décès par jour
def deathbyday():
    cursor.execute("SELECT jour,sum(death) FROM covid WHERE country=\"France\" GROUP BY jour ORDER BY jour") 
    res=cursor.fetchall()
    res_json=[{"jour":row[0],"death":row[1]} for row in res]
    return jsonify(res_json)

@app.route("/confbyday")
# Cas confirme par jour
def confbyday():
    cursor.execute("SELECT jour,sum(confirm) FROM covid WHERE country=\"France\" GROUP BY jour ORDER BY jour")
    res=cursor.fetchall()
    res_json=[{"jour":row[0],"conf":row[1]} for row in res]
    return jsonify(res_json)

@app.route("/recovbyday")
# Cas guéris par jour
def recovbyday():
    cursor.execute("SELECT jour,sum(recov) FROM covid WHERE country=\"France\" GROUP BY jour ORDER BY jour")
    res=cursor.fetchall()
    res_json=[{"jour":row[0],"gu":row[1]} for row in res]
    return jsonify(res_json)

@app.route("/compose/<day>")
# La composition du jour (cas confirmé, guéris et décès)
def compose(day):
    cursor.execute("SELECT * FROM (SELECT sum(confirm), sum(recov), sum(death) from covid WHERE country=\"France\" group by jour ORDER BY jour)a\
    WHERE a.jour=\"%s\"" % day)
    res=cursor.fetchall()
    res_json=[{"jour":row[0],"confirm":row[1],"recov":row[2],"death":row[3]} for row in res]
    return jsonify(res_json)

@app.route("/pred_conf")
# Prédiction du nombre confirmés - Modèle Prophet
def pred_conf():
    return pre.predict_Prophet()

@app.route("/pred_conf_arima")
# Prédiction du nombre confirmés - Modèle ARIMA
def pred_conf_arima():
    return pre.predict_Arima()

@app.route("/pred_death_P")
# Prédiction du nombre décès - Modèle Prophet
def pred_death_P():
    return pre.predict_death_P()

@app.route("/pred_death_A")
# Prédiction du nombre décès - Modèle Prophet
def pred_death_A():
    return pre.predict_death_A()

if __name__ == "__main__":
    pre.prepare_data()
    cursor = hive.connect(host='localhost').cursor()
    os.system("docker cp res.csv server_hive-server_1:/opt/hive/bin/res.csv")
    # Supprime l'ancien table
    cursor.execute("""DROP TABLE IF EXISTS covid""")
    # Créer la table si besoin et ignorer la prèmiere ligne de fichier csv
    cursor.execute("""CREATE TABLE IF NOT EXISTS covid(country STRING,prov STRING,confirm INT, recov INT, death INT,jour STRING) 
        ROW FORMAT DELIMITED
        FIELDS TERMINATED BY ';'
        tblproperties('skip.header.line.count'='1')""")
    cursor.execute("LOAD DATA LOCAL INPATH '/opt/hive/bin/res.csv' OVERWRITE INTO TABLE covid")
    app.run()
