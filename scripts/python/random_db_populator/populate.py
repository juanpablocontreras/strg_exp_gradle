#this script will populate an SQL database given the seetings in the settings.txt file
import mysql.connector
import sys

#get settings
settings_file = open(sys.path[0] + "/settings.txt","r",encoding='UTF-8')

dbhost = settings_file.readline()
print('host: ' + dbhost)

dbport = settings_file.readline()
print('port: ' + dbport)

dbuser = settings_file.readline()
print('user: ' + dbuser)

dbpass = settings_file.readline()
print('password: ' + dbpass)

truncate = settings_file.readline()
print('truncate: ' + truncate)

num_items = settings_file.readline()
print('num_items: ' + num_items)

mean = settings_file.readline()
print('mean: ' + mean)

std = settings_file.readline()
print('std: ' + std)

settings_file.close()


#open the connection to the database
origindb = mysql.connector.connect(
    host = dbhost, #'localhost',
    port = dbport,
    user = dbuser,
    password = 'Printsessa<3',
    database = "origin",
    use_unicode = False
)

#get the database cursor
mycursor = origindb.cursor()

#create the sql statement to execute
sql = "INSERT INTO transfer_data (id,content) VALUES (%s,%s)"
val = ("0","hello world")

#execute the sql statement
mycursor.execute(sql,val)

#commit changes to the database
origindb.commit()