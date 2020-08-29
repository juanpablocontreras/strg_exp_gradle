#populates the small items database

import mysql.connector


#SETTINGS FOR POPULATE
num_items = 1000
num_letters = 100
letter = "a"

cnx_orig = mysql.connector.connect(
                               user='juan',
							   password='Matusalen13',
							   host='localhost',
							   database='EXP_ORIG')





orig_cursor = cnx_orig.cursor()

item = letter
for s in range(num_letters-1):
    item = item + letter

print(len(item))

for i in range(num_items):
	insert_row = f"INSERT INTO Small100 (id,data_item) VALUES ({i},'{item}');"
	orig_cursor.execute(insert_row)

cnx_orig.commit()
orig_cursor.close()
cnx_orig.close()
