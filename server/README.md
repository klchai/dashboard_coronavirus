# Démarrer le serveur web

# Pré-requis

Executer la commande suivante :
__apt-get update && apt-get install -y libsasl2-modules libsasl2-dev && pip install --upgrade pip six==1.12.0 "pyhive[hive]" flask__

1. __docker-compose up -d__ pour lancer le serveur hive
2. Attendre une dizaine de secondes pour que le serveur se lance
4. __python3 web_server.py__ pour lancer le serveur web qui va insérer les données dans Hive et les requeter

# Hive
Si vous souhaitez tester Hive, procédez comme suit:
1. copier le fichier csv dans le conteneur docker `docker cp res.csv {nom_du_conteneur}:/opt/res.csv`
2. Lancer l'image de docker `docker exec -it {nom_du_conteneur} /bin/bash`
3. Lancer hive `hive`
4. Créer la table dans hive
`CREATE TABLE IF NOT EXISTS covid(country STRING,prov STRING,confirm INT, recov INT, death INT,jour STRING) 
        ROW FORMAT DELIMITED
        FIELDS TERMINATED BY ';'
        tblproperties('skip.header.line.count'='1')`
5. Lire les données depuis fichier csv
`LOAD DATA LOCAL INPATH '/opt/hive/bin/res.csv' OVERWRITE INTO TABLE covid`
6. Affichier la table `select * from covid limit 10;`
`SELECT * FROM covid WHERE country=\"France\" LIMIT 10`
