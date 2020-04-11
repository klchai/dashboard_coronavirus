from pyhive import hive
from flask import Flask

app = Flask(__name__)

@app.route("/")
def hello():
    return "Hello World!"

if __name__ == "__main__":
    cursor = hive.connect(host='localhost').cursor()
    cursor.execute("CREATE TABLE test_table(nom STRING, age INT)")
    cursor.execute("SHOW tables")
    print(cursor.fetchall())
    app.run()