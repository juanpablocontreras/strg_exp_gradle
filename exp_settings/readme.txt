README

Each settings file holds the values that will be read by the corresponding _setter java file. Settings are separated by a new line.

Controller:
maxQueueSize
logIdentifier
Scenario (integer)

Creator:
connectionStr
username
password
table_name
total_number_of_items
Period

Handler:
Max type: ENUM (SIZE, NUM_IO_REQUESTS, PERIOD)
max type num: number of IO requests per data transfer/ OR  / SIZE before transfer in bytes / OR / Fixed Period
inter io processing time



Transmitter:
connectionStr
username
password
table_name
