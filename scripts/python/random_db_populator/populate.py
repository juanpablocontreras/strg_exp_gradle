#this script will populate an SQL database given the seetings in the settings.txt file
import mysql.connector
import sys
import scipy.stats as stats
import math

#get settings
settings_file = open(sys.path[0] + "/settings.txt","r",encoding='UTF-8')

verbose = settings_file.readline()[:-1]
if verbose.upper() == 'Y':
    verbose = True
else :
    verbose = False

if verbose: print("Setttings: ")

dbhost = settings_file.readline()[:-1]
if verbose: print('host: ' + dbhost)

dbport = settings_file.readline()[:-1]
if verbose: print('port: ' + dbport)

dbuser = settings_file.readline()[:-1]
if verbose: print('user: ' + dbuser)

dbpass = settings_file.readline()[:-1]
if verbose: print('password: ' + dbpass)

dbname = settings_file.readline()[:-1]
if verbose: print('database: ' + dbname)

table_name = settings_file.readline()[:-1]
if verbose: print('table: ' + table_name)

truncate = settings_file.readline()[:-1]
if verbose: print('truncate: ' + truncate)

num_items = int(settings_file.readline()[:-1])
if verbose: print('num_items: ' + str(num_items))

mean = int(settings_file.readline()[:-1])
if verbose: print('mean: ' + str(mean))

std = int(settings_file.readline()[:-1])
if verbose: print('std: ' + str(std))

settings_file.close()


#open the connection to the database
origindb = mysql.connector.connect(
    host = dbhost, #'localhost',
    port = dbport,
    user = dbuser,
    password = dbpass,
    database = "origin",
    use_unicode = False
)

#get the database cursor
mycursor = origindb.cursor()

#truncate database
if truncate.upper() == "Y":
    if verbose: print("truncating table data...")
    mycursor.execute("TRUNCATE " + table_name)
    origindb.commit()


#create distribution of size of requests. 
#min number of characters = 1, max number of characters = 15000 (text size is limited to 64kb, and I assumed 4bytes per UTF-8 encoding)
#Truncated Random distribution to min and max number of characters. Mean and std dev are as specified in the settings file
if std != 0:
    a,b = 1,1500
    mu, sigma = mean, std
    dist = stats.truncnorm((a - mu) / sigma, (b - mu) / sigma, loc=mu, scale=sigma)
    values = dist.rvs(num_items)
else :
    #all values are the mean
    values = []
    for i in range(num_items):
        values.append(mean)

#create SQL query template
sql = "INSERT INTO " + table_name + " (id,content) VALUES (%s,%s)"

#create requests with distribution computed above
id = 0
total_data_size = 0
for val in values:
    content = 'a'*math.floor(val)  #create the content
    mycursor.execute(sql,(id,content))
    id += 1
    total_data_size += math.floor(val)

#commit changes to the database
origindb.commit()

print('total_data_size: ' + str(total_data_size) + "bytes")