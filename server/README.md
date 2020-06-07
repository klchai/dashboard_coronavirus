# Pré-requis
Executer la commande suivante dans le terminal:
__apt-get update && apt-get install -y docker-compose libsasl2-modules libsasl2-dev && pip install --upgrade pip six thrift_sasl "pyhive[hive]" flask fbprophet statsmodels pandas__

# Utilisation
1. __docker-compose up -d__ pour lancer le serveur hive
2. Attendre une dizaine de secondes pour que le serveur se lance
4. __python3 web_server.py__ pour lancer le serveur web qui va insérer les données dans Hive et les requeter
