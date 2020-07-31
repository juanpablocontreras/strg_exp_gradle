#run the experiment with number of IO per data transfer as the independent variable
#Range: 1,10,25,50,100 IO requests per data transfer
#Queue size: 20,60,100
#tables: Small100, Med1000, Large65535

import os

strg_exp_gradle_path = os.path.realpath('..')
settings_path = strg_exp_gradle_path + "/exp_settings/exp_settings.txt"

queueSizes = [20] #[20, 60, 100]
variableIOperDT = [1] #[1, 10, 25, 50, 100]
tables = ["Small100"] #["Small100", "Med1000", "Large65535"]

#build java experiment
os.system("cd .. \n ./gradlew clean build")

for tableName in tables:
    #for each table in origin database, transfer all the data to the target database table with the same name

    for maxQueueSize in queueSizes:

        for numIOrequestsPerDataTransfer in variableIOperDT:

            #write settings
            settings_file = open(settings_path, "w")
            settings_file.write(str(maxQueueSize))
            settings_file.write("\n")
            settings_file.write(str(numIOrequestsPerDataTransfer))
            settings_file.write("\n")
            settings_file.write(tableName)
            settings_file.close()

            #truncate databases
            os.system("cd .. \n python py_srv/truncateAll.py")

            #run java experiment
            os.system("cd .. \n ./gradlew run")

            #copy logs to results folder
            

    print("table: " + tableName)