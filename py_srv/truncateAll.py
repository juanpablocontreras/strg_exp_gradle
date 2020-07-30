#delete all data from small, medium, and large tables of target db

import mysql.connector

cnx_target = mysql.connector.connect(
                               user='juan',
							   password='LapinCoquin13',
							   host='127.0.0.1',
							   database='EXP_TARGET')


cursor = cnx_target.cursor()

deleteQuery = "TRUNCATE TABLE "

cursor.execute(deleteQuery + "Small100;")
cnx_target.commit()

cursor.execute(deleteQuery + "Med1000;")
cnx_target.commit()

cursor.execute(deleteQuery + "Large65535;")
cnx_target.commit()


cursor.close()
cnx_target.close()
