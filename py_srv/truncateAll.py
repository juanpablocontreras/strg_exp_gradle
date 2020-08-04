#delete all data from small, medium, and large tables of target db

import mysql.connector

cnx_target = mysql.connector.connect(
                               user='admin',
							   password='Matusalen13',
							   host='target-instance.cauebsweajza.us-east-2.rds.amazonaws.com',
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
