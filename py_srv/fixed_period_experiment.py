#experiment for Scenario 2: Fixed Period

#imports
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
orig_username = "root"
orig_password = "root_password"
total_items_to_transmit = 1000


#TARGET database connectivity
target_connection_string = "jdbc:mysql://localhost:3306/EXP_TARGET?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC&useSSL=false"
target_host = "localhost" #used for truncate
target_database_name = "EXP_TARGET" #used for truncate
target_username = "root"
target_password = "root_password"

#experiment variable settings
queueSizes = [100] #[2, 3, 4, 6, 7, 8]
periods = list(range(50,1100, 50))
tables = ["Large65535"] #["Small100", "Med1000", "Large65535"]
inter_io_processing_time = 10
handler_max_type = "PERIOD"
outcome_type = "SPEED"

#build java experiment
os.system("cd .. \n ./gradlew clean build")

#truncator
truncator = Truncator.Truncator()


for tableName in tables:
    for maxQueueSize in queueSizes:
        for period in periods:
            # settings
            # open files
            controller_settings_file = open(path_controller_settings, "w")
            handler_settings_file = open(path_handler_settings, "w")
            creator_settings_file = open(path_creator_settings, "w")
            trsm_settings_file = open(path_trsm_settings, "w")

            # controller settings
            controller_settings_file.write(str(maxQueueSize))
            controller_settings_file.write("\n")
            controller_settings_file.write(tableName + "Q" + str(maxQueueSize) + "P" + str(period))
            controller_settings_file.write("\n")
            controller_settings_file.write(str(2))

            # Handler settings
            handler_settings_file.write(str(period))
            handler_settings_file.write("\n")
            handler_settings_file.write(str(inter_io_processing_time))
            handler_settings_file.write("\n")
            handler_settings_file.write(handler_max_type)
            handler_settings_file.write("\n")
            handler_settings_file.write(outcome_type)

            # Creator settings
            creator_settings_file.write(orig_connection_string)
            creator_settings_file.write("\n")
            creator_settings_file.write(orig_username)
            creator_settings_file.write("\n")
            creator_settings_file.write(orig_password)
            creator_settings_file.write("\n")
            creator_settings_file.write(tableName)
            creator_settings_file.write("\n")
            creator_settings_file.write(str(total_items_to_transmit))

            # transmitter
            trsm_settings_file.write(target_connection_string)
            trsm_settings_file.write("\n")
            trsm_settings_file.write(target_username)
            trsm_settings_file.write("\n")
            trsm_settings_file.write(target_password)
            trsm_settings_file.write("\n")
            trsm_settings_file.write(tableName)

            # close file writers
            controller_settings_file.close()
            handler_settings_file.close()
            creator_settings_file.close()
            trsm_settings_file.close()

            # truncate databases
            truncator.truncate(target_host, target_database_name, target_username, target_password, tableName)

            # run java experiment
            os.system("cd .. \n ./gradlew run")

