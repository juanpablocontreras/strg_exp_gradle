#runs the SQL strg experiment with the given inputs

import os
import db_scripts.SQL.Truncator

#General Paths
strg_exp_gradle_path = os.path.realpath('..')
path_settings = strg_exp_gradle_path + "/exp_settings"
path_logs = strg_exp_gradle_path + "/logs"

path_controller_settings = path_settings + "/controller_settings.txt"
path_handler_settings = path_settings + "/handler_settings.txt"
path_creator_settings = path_settings + "/creator_settings.txt"
path_trsm_settings = path_settings + "/trsm_settings.txt"


#Creator Settings
crt_conn_str = ""
crt_username = ""
crt_password = ""
crt_tbl_name = ""
crt_total_num_items = ""
crt_distribution = ""

#Controller Settings
ctr_max_queue_size = ""
ctr_log_id = ""
#must include creator distribution too

#Handler settings
hnd_max_type = ""
hnd_max_type_num = ""
hnd_inter_io_proc = "" #number in milliseconds

#Transmitter Settings