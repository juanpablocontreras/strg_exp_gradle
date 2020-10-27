import mysql.connector
import Truncator
from random import randint
from random import seed

#Experiment variables
num_items = 1000 #number of items to create in the database
item_max_size = 1000
item_min_size = 1
letter = "a"

#data base
host = 'localhost'
db_username = "juan"
db_password = "Matusalen13"
db_name = "EXP_ORIG"
db_table = "Large65535"

t = Truncator.Truncator()
t.truncate(host, db_name, db_username, db_password, db_table)


cnx_orig = mysql.connector.connect(user=db_username, password=db_password, host=host, database=db_name)
orig_cursor = cnx_orig.cursor()


seed(1)

for i in range(num_items):
    #add a random amount of letter a's, up to 1000
    num_letters = randint(item_min_size, item_max_size)
    value = ""
    for j in range(num_letters):
        value += letter

    insert_row = f"INSERT INTO {db_table} (id,data_item) VALUES ({i},'{value}');"
    orig_cursor.execute(insert_row)


cnx_orig.commit()
orig_cursor.close()
cnx_orig.close()