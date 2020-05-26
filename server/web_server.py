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
    cursor.execute("SELECT * FROM covid LIMIT 10")
    res=cursor.fetchall()
    res_json=[{"jour":row[0],"confirm":row[1],"dc":row[2],"rea":row[3],"hosp":row[4],"gu":row[5],"sus":row[6]} for row in res]
    return jsonify(res_json)

@app.route("/dcjour")
# Nombre de deces par jour
def dcjour():
    cursor.execute("SELECT jour,dc FROM covid")
    res=cursor.fetchall()
    res_json=[{"jour":row[0],"dc":row[1]} for row in res]
    return jsonify(res_json)

@app.route("/dc/<jour>")
# Nombre de deces par jour
def dc(jour):
    cursor.execute("SELECT dc FROM covid WHERE jour='\"%s\"' " % jour) 
    res=cursor.fetchall()
    res_json=[{"dc":row[0]} for row in res]
    return jsonify(res_json)

@app.route("/confjour")
# Cas confirme par jour
def confjour():
    cursor.execute("SELECT jour,confirm FROM covid")
    res=cursor.fetchall()
    res_json=[{"jour":row[0],"conf":row[1]} for row in res]
    return jsonify(res_json)

@app.route("/gujour")
# Recovered par jour
def gujour():
    cursor.execute("SELECT jour,gu FROM covid")
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
    cursor.execute("""CREATE TABLE IF NOT EXISTS covid(jour string,confirm int,dc int,rea int,hosp int,gu int,sus int) 
        ROW FORMAT DELIMITED
        FIELDS TERMINATED BY ';'
        tblproperties('skip.header.line.count'='1')""")
    cursor.execute("LOAD DATA LOCAL INPATH '/opt/hive/bin/res.csv' OVERWRITE INTO TABLE covid")
    app.run()
