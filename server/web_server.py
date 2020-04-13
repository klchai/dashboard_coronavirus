from pyhive import hive
from flask import Flask
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

if __name__ == "__main__":
    cursor = hive.connect(host='localhost').cursor()
    retrieve_data()
    os.system("docker cp res.csv server_hive-server_1:/opt/hive/bin/res.csv")
    # creer la table si besoin et ignorer la premiere ligne de fichier csv
    cursor.execute("""CREATE TABLE IF NOT EXISTS covid(dep STRING,sexe INT,jour STRING,hosp INT,rea INT,rad INT,dc INT) 
        row format delimited fields terminated by ';' tblproperties('skip.header.line.count'='1')""")
    cursor.execute("LOAD DATA LOCAL INPATH '/opt/hive/bin/res.csv' OVERWRITE INTO TABLE covid")
    cursor.execute("SELECT COUNT(*) FROM covid")
    print(cursor.fetchall())
    app.run()
