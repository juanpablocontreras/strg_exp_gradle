import mysql.connector

class Truncator:
	def truncate(self, host, database_name, username, password, table_name):
		cnx_target = mysql.connector.connect(
			user=username,
			password=password,
			host=host,
			database=database_name)

		cursor = cnx_target.cursor()

		deleteQuery = "TRUNCATE TABLE "

		cursor.execute(deleteQuery + table_name + ";")
		cnx_target.commit()

		cursor.close()
		cnx_target.close()