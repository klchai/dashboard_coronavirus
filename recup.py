import pandas as pd
import requests
import csv

#Recupérartion des données
url = 'https://www.data.gouv.fr/fr/resources.csv?badge=covid-19'
DOWNLOAD_PATH ='ressources.csv'
fichier = open( DOWNLOAD_PATH , "a")
urllib.request.urlretrieve(url,DOWNLOAD_PATH)
fichier.close()

ressource = pd.read_csv('ressources.csv',sep=';')
for i in ressource['url']:
    url = i
    DOWNLOAD_PATH =i.split("/")[-1]
    fichier = open( DOWNLOAD_PATH , "a")
    urllib.request.urlretrieve(url,DOWNLOAD_PATH)
    fichier.close()
for i in ressource['url']:
    j = pd.read_csv(i.split("/")[-1])
    print(j)
    
