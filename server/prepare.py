import pandas as pd

def prepare_data():
    print("Begin prepare.")
    df = pd.read_csv("covid-19-all.csv",sep=",")
    df = df.fillna(0)
    drop_list = ['Latitude','Longitude']
    df.drop(columns=drop_list,inplace=True)
    headers = ['Country','Province','Confirmed','Recovered','Deaths','Date']
    df.to_csv("res.csv",sep=";",index=False,header=headers)
    print("Prepare finished.")