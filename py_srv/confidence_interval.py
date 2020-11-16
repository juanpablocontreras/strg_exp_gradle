# outputs the number of times an experiment must be re-run in order to obtain confident results

import os
import Truncator

#paths
strg_exp_gradle_path = os.path.realpath('..')
path_settings = strg_exp_gradle_path + "/exp_settings"
path_logs = strg_exp_gradle_path + "/logs"

path_controller_settings = path_settings + "/controller_settings.txt"
path_handler_settings = path_settings + "/handler_settings.txt"
path_creator_settings = path_settings + "/creator_settings.txt"
path_trsm_settings = path_settings + "/trsm_settings.txt"

#ORIGIN database connectivity
orig_connection_string = "jdbc:mysql://localhost:3306/EXP_ORIG?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC&useSSL=false"
orig_username = "root"
orig_password = "root_password"
total_items_to_transmit = 100


#TARGET database connectivity
target_connection_string = "jdbc:mysql://localhost:3306/EXP_TARGET?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC&useSSL=false"
target_host = "localhost" #used for truncate
target_database_name = "EXP_TARGET" #used for truncate
target_username = "root"
target_password = "root_password"

#experiment variable settings
queueSize = 100
table = "Large65535"
min_interval_difference = 100 #once this difference b/w 2 intervals is reached, program stops


#Handler settings
max_num_type = "NUM_IO_REQUESTS"
max_num = 1
inter_io_processing_time = 10
outcome_type = "SPEED"


#build java experiment
os.system("cd .. \n ./gradlew clean build")

#truncator
truncator = Truncator.Truncator()


#settings
#open files
controller_settings_file = open(path_controller_settings, "w")
handler_settings_file = open(path_handler_settings, "w")
creator_settings_file = open(path_creator_settings, "w")
trsm_settings_file = open(path_trsm_settings, "w")

# controller settings
controller_settings_file.write(str(queueSize))
controller_settings_file.write("\n")
controller_settings_file.write("intervals")

# Handler settings
handler_settings_file.write(str(max_num))  # size of data transfer (max num)
handler_settings_file.write("\n")
handler_settings_file.write(str(inter_io_processing_time))  # inter io processing time
handler_settings_file.write("\n")
handler_settings_file.write(max_num_type)  # max num type
handler_settings_file.write("\n")
handler_settings_file.write(outcome_type)  # outcome type

#Creator settings
creator_settings_file.write(orig_connection_string)
creator_settings_file.write("\n")
creator_settings_file.write(orig_username)
creator_settings_file.write("\n")
creator_settings_file.write(orig_password)
creator_settings_file.write("\n")
creator_settings_file.write(table)
creator_settings_file.write("\n")
creator_settings_file.write(str(total_items_to_transmit))

#transmitter
trsm_settings_file.write(target_connection_string)
trsm_settings_file.write("\n")
trsm_settings_file.write(target_username)
trsm_settings_file.write("\n")
trsm_settings_file.write(target_password)
trsm_settings_file.write("\n")
trsm_settings_file.write(table)

#close file writers
controller_settings_file.close()
handler_settings_file.close()
creator_settings_file.close()
trsm_settings_file.close()






#run the intervals
last_interval_avg = 0
curr_interval_avg_speed = 0
number_of_exp_repetitions = 1

while abs(curr_interval_avg_speed - last_interval_avg) > min_interval_difference or last_interval_avg == 0:

    print("interval differance: " + str(abs(curr_interval_avg_speed - last_interval_avg)))

    #set last interval avg
    last_interval_avg = curr_interval_avg_speed

    #reset current interval average
    curr_interval_avg_speed = 0

    for i in range(1, number_of_exp_repetitions):

        # truncate databases
        truncator.truncate(target_host, target_database_name, target_username, target_password, table)

        # run java experiment
        os.system("cd .. \n ./gradlew run")

        #get batch speeds and calculate average speed (curr_avg_speed)
        batch_speed_file = open(path_logs + "/batch_speed_intervals", "r")
        lines = batch_speed_file.readlines()
        exp_avg_speed = 0
        count = 0
        for line in lines:
            exp_avg_speed += float(line)
            count += 1
        exp_avg_speed = exp_avg_speed / count

        #add exp_avg_speed to interval average
        curr_interval_avg_speed += exp_avg_speed


    curr_interval_avg_speed = curr_interval_avg_speed / number_of_exp_repetitions #find interval average speed

    #write (number_of_exp_repetitions, interval average speed)
    interval_file = open(path_logs + "/intervals", "a")
    interval_file.write(str(number_of_exp_repetitions) + "," + str(curr_interval_avg_speed))
    interval_file.write("\n")

    number_of_exp_repetitions += 1 #increment number of times the experiment is run for the following interval

print(number_of_exp_repetitions-1)
