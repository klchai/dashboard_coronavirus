from pyhive import hive
from flask import Flask,jsonify
import os
import requests
import csv
import urllib
import socket

def retrieve_data():
    socket.setdefaulttimeout(30)
    URL_DATA_SRC = 'https://www.data.gouv.fr/fr/datasets/r/63352e38-d353-4b54-bfd1-f1b3ee1cabd7'
    file_downloaded ='res.csv'
    try:
        print("Download job begin.")
        urllib.request.urlretrieve(URL_DATA_SRC,file_downloaded)
        print("Download job finished, begin parse csv.")
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

app = Flask(__name__)

@app.route("/")
def home():
    return "Home page of the web server of the dashboard_coronavirus."

@app.route("/select")
def select():
    cursor.execute("SELECT * FROM covid LIMIT 10")
    res=cursor.fetchall()
    res_json=[{"dep":row[0],"sexe":row[1],"jour":row[2],"hosp":row[3],"rea":row[4],"rad":row[5],"dc":row[6]} for row in res]
    return jsonify(res_json)

@app.route("/dcjour")
# Nombre de deces par jour
def dcjour():
    cursor.execute("SELECT jour,sum(dc) AS dc_total FROM covid WHERE sexe=0 GROUP BY jour")
    res=cursor.fetchall()
    res_json=[{"jour":row[0],"dc":row[1]} for row in res]
    return jsonify(res_json)

@app.route("/dcdep/<jour>")
def dcdep(jour):
    cursor.execute("SELECT dep,jour,sum(dc) AS dc_total FROM covid WHERE jour='\"%s\"' GROUP BY dep,jour" % jour)
    res=cursor.fetchall()
    res_json=[{"dep":row[0],"jour":row[1],"dc":row[2]} for row in res]
    return jsonify(res_json)

@app.route("/dep/<dep>")
def dep(dep):
    cursor.execute("SELECT jour,sum(dc) AS dc_total FROM covid WHERE dep='\"%s\"' GROUP BY jour" % dep)
    res=cursor.fetchall()
    res_json=[{"jour":row[0],"dc":row[1]} for row in res]
    return jsonify(res_json)
    
if __name__ == "__main__":
    retrieve_data()
    cursor = hive.connect(host='localhost').cursor()
    os.system("docker cp res.csv server_hive-server_1:/opt/hive/bin/res.csv")
    # creer la table si besoin et ignorer la premiere ligne de fichier csv
    cursor.execute("""CREATE TABLE IF NOT EXISTS covid(dep STRING,sexe INT,jour STRING,hosp INT,rea INT,rad INT,dc INT) 
        row format delimited fields terminated by ';' tblproperties('skip.header.line.count'='1')""")
    cursor.execute("LOAD DATA LOCAL INPATH '/opt/hive/bin/res.csv' OVERWRITE INTO TABLE covid")
    app.run()
