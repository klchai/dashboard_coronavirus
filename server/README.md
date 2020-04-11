# Lire les données dans Hive
`hive`
`create table if not exists covid(dep STRING, sexe INT, jour STRING, hosp INT, rea INT, rad INT, dc INT) row format delimited fields terminated by ';' tblproperties("skip.header.line.count"="1");`
`load data local inpath '/opt/res.csv' overwrite into table covid;`

# Démarrer le serveur web

# Pré-requis

Executer la commande suivante :
__apt-get update && apt-get install -y libsasl2-modules libsasl2-dev && pip install --upgrade pip six==1.12.0 "pyhive[hive]" flask__

1. __docker-compose up -d__ pour lancer le serveur hive
2. Attendre une dizaine de secondes pour que le serveur se lance
3. __python3 web_server.py__ pour lancer le serveur web
