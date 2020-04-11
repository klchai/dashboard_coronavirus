import requests
import csv
import urllib
import socket

socket.setdefaulttimeout(30)

#Recupérartion des données
url = 'https://www.data.gouv.fr/fr/datasets/r/63352e38-d353-4b54-bfd1-f1b3ee1cabd7'
DOWNLOAD_PATH ='res.csv'
# fichier = open( DOWNLOAD_PATH , "a" )
try:
    print("Download job begin.")
    urllib.request.urlretrieve(url,DOWNLOAD_PATH)
    print("Download job finished, begin parse csv.")
except socket.timeout:
    count = 1
    while count <= 5:
        try:
            urllib.urlretrieve(url,DOWNLOAD_PATH)
            break
        except socket.timeout:
            err_info = 'Reloading for %d time'%count if count ==1 else 'Reload for %d times'%count
            print(err_info)
            count += 1
    if count > 5:
            print("download job failed")
