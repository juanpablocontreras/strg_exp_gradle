import mysql.connector
import Truncator
from random import randint
from random import seed

num_items = 500 #number of items to create in the database
letter = "a"

t = Truncator.Truncator()
t.truncate("127.0.1", "EXP_ORIG", "juan", "LapinCoquin13", "Large65535")


cnx_orig = mysql.connector.connect(user='juan', password='LapinCoquin13', host='localhost', database='EXP_ORIG')
orig_cursor = cnx_orig.cursor()

print(len(range(num_items)))

seed(1)

for i in range(num_items):
    #add a random amount of letter a's, up to 1000
    num_letters = randint(1, 1000)
    value = ""
    for j in range(num_letters):
        value += letter

    insert_row = f"INSERT INTO Large65535 (id,data_item) VALUES ({i},'{value}');"
    orig_cursor.execute(insert_row)


cnx_orig.commit()
orig_cursor.close()
cnx_orig.close()