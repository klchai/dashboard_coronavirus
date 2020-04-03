# dashboard_coronavirus

## Déployer un cluster hadoop 

1. Il faut d'abord installer docker compose (https://docs.docker.com/compose/install/).
2. Dans le dossier cluster_hadoop, exécuter la commande : __docker-compose up -d__
3. Entrer dans le conteneur : __docker exec -it namenode bash__
4. Pour supprimer le cluster : __docker-compose down__
