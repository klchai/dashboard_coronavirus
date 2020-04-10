### création d'un répertoire dans lequel on va cloner le projet git
    mkdir projet-android
    cd projet-android/

### téléchargement et installation de l'image docker
cette commande prendra un peu de temps

    git clone https://github.com/big-data-europe/docker-hive.git
    cd docker-hive/
    docker-compose up -d

### lancer hive et le bash de l'image docker
    docker exec -it docker-hive_hive-server_1 /bin/bash

### installation des bibliothèques nécessaires
tout le reste est à éxecuter à l'interieur de l'image docker

l'image contient quasiment rien à part hive et HDFS

    apt-get update
    apt-get install python3
    apt-get install -y libsasl2-modules libsasl2-dev

    apt install python3-pip
    pip3 install six==1.12.0
    
### installation de hive
    pip3 install 'pyhive[hive]'

### tester si hive fonctionne
    hive

### tester depuis python
    python3
    
    from pyhive import hive
    cursor = hive.connect('localhost').cursor()
    cursor.execute("CREATE TABLE test_table(nom STRING, age INT)")
    cursor.execute("SHOW tables")
    print(cursor.fetchall())

### quitter l'image docker
    exit
