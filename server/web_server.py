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
# Nombre de deces par jour
def deathbyday():
    cursor.execute("SELECT jour,sum(death) FROM covid WHERE country=\"France\" GROUP BY jour ORDER BY jour") 
    res=cursor.fetchall()
    res_json=[{"dc":row[0]} for row in res]
    return jsonify(res_json)

@app.route("/confbyday")
# Cas confirme par jour
# select jour,sum(confirm) from covid where country="France" group by jour order by jour;
def confbyday():
    cursor.execute("SELECT jour,sum(confirm) FROM covid WHERE country=\"France\" GROUP BY jour ORDER BY jour")
    res=cursor.fetchall()
    res_json=[{"jour":row[0],"conf":row[1]} for row in res]
    return jsonify(res_json)

@app.route("/recovbyday")
# Recovered par jour
def recovbyday():
    cursor.execute("SELECT jour,sum(recov) FROM covid WHERE country=\"France\" GROUP BY jour ORDER BY jour")
    res=cursor.fetchall()
    res_json=[{"jour":row[0],"gu":row[1]} for row in res]
    return jsonify(res_json)

# @app.route("/dcdep/<jour>")
# def dcdep(jour):
#     cursor.execute("SELECT jour,sum(dc) AS dc_total FROM covid WHERE jour='\"%s\"' GROUP BY dep,jour" % jour)
#     res=cursor.fetchall()
#     res_json=[{"jour":row[0],"confirm":row[2]} for row in res]
#     return jsonify(res_json)
    
if __name__ == "__main__":
    pre.prepare_data()
    cursor = hive.connect(host='localhost').cursor()
    os.system("docker cp res.csv server_hive-server_1:/opt/hive/bin/res.csv")
    # creer la table si besoin et ignorer la premiere ligne de fichier csv
    cursor.execute("""CREATE TABLE IF NOT EXISTS covid(country STRING,prov STRING,confirm INT, recov INT, death INT,jour STRING) 
        ROW FORMAT DELIMITED
        FIELDS TERMINATED BY ';'
        tblproperties('skip.header.line.count'='1')""")
    cursor.execute("LOAD DATA LOCAL INPATH '/opt/hive/bin/res.csv' OVERWRITE INTO TABLE covid")
    app.run()
