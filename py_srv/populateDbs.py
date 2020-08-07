#populate experiment origin database

import os



os.system('python populateSmall100.py')
os.system('python populateMed1000.py')
os.system('python populateLarge65535.py')

print("populating Dbs Threads created")

