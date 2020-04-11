# Démarrer le serveur web

# Pré-requis

Executer la commande suivante :
__apt-get update && apt-get install -y libsasl2-modules libsasl2-dev && pip install --upgrade pip six==1.12.0 "pyhive[hive]" flask__

1. __docker-compose up -d__ pour lancer le serveur hive
2. Attendre une dizaine de secondes pour que le serveur se lance
3. __python3 web_server.py__ pour lancer le serveur web