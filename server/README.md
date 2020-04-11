# Démarrer le serveur web

# Pré-requis

Executer la commande suivante :
__apt-get update && apt-get install -y libsasl2-modules libsasl2-dev && pip install --upgrade pip six==1.12.0 "pyhive[hive]" flask__

1. __docker-compose up -d__ pour lancer le serveur hive
2. Attendre une dizaine de secondes pour que le serveur se lance
3. __python3 recup.py__ pour télécharger le fichier csv et renommé comme `res.csv`
4. __python3 web_server.py__ pour lire les données dans Hive puis lancer le serveur web

# Hive
Si vous souhaitez tester Hive, procédez comme suit:
1. copier le fichier csv dans le conteneur docker `docker cp res.csv {nom_du_conteneur}:/opt/res.csv`
2. Lancer l'image de docker `docker exec -it {nom_du_conteneur} /bin/bash`
3. Lancer hive `hive`
4. Créer la table dans hive
`create table if not exists covid(dep STRING, sexe INT, jour STRING, hosp INT, rea INT, rad INT, dc INT) row format delimited fields terminated by ';' tblproperties("skip.header.line.count"="1");`
5. Lire les données depuis fichier csv
`load data local inpath '/opt/res.csv' overwrite into table covid;`
6. Affichier la table `select * from covid limit 10;`
`select * from covid where dep='"75"' and dc>500;`
