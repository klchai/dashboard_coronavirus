from pyhive import hive
from flask import Flask
import os

app = Flask(__name__)
os.system("docker cp res.csv server_hive-server_1:/opt/hive/bin/res.csv")

@app.route("/")
def hello():
    return "Hello World!"

if __name__ == "__main__":
    cursor = hive.connect(host='localhost').cursor()
    # creer la table si besoin et ignorer la premiere ligne de fichier csv
    cursor.execute("CREATE TABLE IF NOT EXISTS covid(dep STRING, sexe INT, jour STRING, hosp INT, rea INT, rad INT, dc INT) row format delimited fields terminated by ';' tblproperties('skip.header.line.count'='1')")
    cursor.execute("LOAD DATA LOCAL INPATH '/opt/hive/bin/res.csv' OVERWRITE INTO TABLE covid")
    cursor.execute("SELECT COUNT(*) FROM covid")
    print(cursor.fetchall())
    app.run()
