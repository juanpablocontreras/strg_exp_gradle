#run the experiment with number of IO per data transfer as the independent variable
#Range: 1,10,25,50,100 IO requests per data transfer
#Queue size: 20,60,100
#tables: Small100, Med1000, Large65535

import os
import Truncator

#paths
strg_exp_gradle_path = os.path.realpath('..')
path_settings = strg_exp_gradle_path + "/exp_settings"

path_controller_settings = path_settings + "/controller_settings.txt"
path_handler_settings = path_settings + "/handler_settings.txt"
path_creator_settings = path_settings + "/creator_settings.txt"
path_trsm_settings = path_settings + "/trsm_settings.txt"

#ORIGIN database connectivity
orig_connection_string = "jdbc:mysql://localhost:3306/EXP_ORIG?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC&useSSL=false"
orig_username = "juan"
orig_password = "Matusalen13"
total_items_to_transmit = 1000


#TARGET database connectivity
target_connection_string = "jdbc:mysql://localhost:3306/EXP_TARGET?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC&useSSL=false"
target_host = "localhost" #used for truncate
target_database_name = "EXP_TARGET" #used for truncate
target_username = "juan"
target_password = "Matusalen13"

#experiment variable settings
queueSizes = [100] #[2, 3, 4, 6, 7, 8]
sizeof_data_transfer = list(range(1,50,1)) + list(range(50,100,10)) + list(range(150,1000,50))
tables = ["Large65535"] #["Small100", "Med1000", "Large65535"]
inter_io_processing_time = 10
handler_max_type = "NUM_IO_REQUESTS"
outcome_type = "SPEED"

#build java experiment
os.system("cd .. \n ./gradlew clean build")

#truncator
truncator = Truncator.Truncator()



for tableName in tables:
    #for each table in origin database, transfer all the data to the target database table with the same name

    for maxQueueSize in queueSizes:

        for curr_size in sizeof_data_transfer:

            #settings
            #open files
            controller_settings_file = open(path_controller_settings, "w")
            handler_settings_file = open(path_handler_settings, "w")
            creator_settings_file = open(path_creator_settings, "w")
            trsm_settings_file = open(path_trsm_settings, "w")

            #controller settings
            controller_settings_file.write(str(maxQueueSize))
            controller_settings_file.write("\n")

            size_io_identifier = ""
            if handler_max_type == "SIZE":
                size_io_identifier = "S"
            elif handler_max_type == "NUM_IO_REQUESTS":
                size_io_identifier = "IO"

            controller_settings_file.write(tableName + "Q" + str(maxQueueSize) + size_io_identifier + str(curr_size)) #tableQueueSizeIOperDT

            #Handler settings
            handler_settings_file.write(str(curr_size)) #size of data transfer (max num)
            handler_settings_file.write("\n")
            handler_settings_file.write(str(inter_io_processing_time)) #inter io processing time
            handler_settings_file.write("\n")
            handler_settings_file.write(handler_max_type) #max num type
            handler_settings_file.write("\n")
            handler_settings_file.write(outcome_type) #outcome type

            #Creator settings
            creator_settings_file.write(orig_connection_string)
            creator_settings_file.write("\n")
            creator_settings_file.write(orig_username)
            creator_settings_file.write("\n")
            creator_settings_file.write(orig_password)
            creator_settings_file.write("\n")
            creator_settings_file.write(tableName)
            creator_settings_file.write("\n")
            creator_settings_file.write(str(total_items_to_transmit))

            #transmitter
            trsm_settings_file.write(target_connection_string)
            trsm_settings_file.write("\n")
            trsm_settings_file.write(target_username)
            trsm_settings_file.write("\n")
            trsm_settings_file.write(target_password)
            trsm_settings_file.write("\n")
            trsm_settings_file.write(tableName)

            #close file writers
            controller_settings_file.close()
            handler_settings_file.close()
            creator_settings_file.close()
            trsm_settings_file.close()

            #truncate databases
            truncator.truncate(target_host, target_database_name, target_username, target_password, tableName)

            #run java experiment
            os.system("cd .. \n ./gradlew run")

    print("table: " + tableName)