import mysql.connector
import tweepy
import Truncator
import unicodedata


num_items = 500


####################
#### GET TWEETS ####
####################

consumer_key= 'QukK3BN5tI1maZLIXIWmOnrWE'
consumer_secret= 'FXL7LnlpKzHAlpSkSSXlAF7V2bLUBqNQNJcrZcdUy2LpV7ZqQr'
access_token= '955193142952607744-2j30pZuLoD0fHixTHJchvGCqEB8tLne'
access_token_secret= '8ezqSULMmKZ8wzAbqa1ARm0mnqbQeL1yeQXGpCGMpkwrK'

auth = tweepy.OAuthHandler(consumer_key, consumer_secret)
auth.set_access_token(access_token, access_token_secret)
api = tweepy.API(auth, wait_on_rate_limit=True)

tweets = tweepy.Cursor(api.search,
                       q = "#trudeau",
                       lang = "en",
                       since="2020-08-20").items(num_items)


#########################
### POPULATE LARGE DB ###
#########################

t = Truncator.Truncator()
t.truncate("127.0.1", "EXP_ORIG", "juan", "LapinCoquin13", "Large65535")


cnx_orig = mysql.connector.connect( user='juan', password='LapinCoquin13', host='localhost', database='EXP_ORIG')



orig_cursor = cnx_orig.cursor()

i = 0
for tweet in tweets:
    value = unicodedata.normalize('NFKD', tweet.text) #.encode('ascii','ignore')
    clean_value = ''.join([c for c in value if ord(c) < 128 and ord(c) > 20])
    clean_value = clean_value.encode("utf-8")
    print(clean_value)
    insert_row = f"INSERT INTO Large65535 (id,data_item) VALUES ({i},'{clean_value}');"
    orig_cursor.execute(insert_row)
    i = i+1

cnx_orig.commit()
orig_cursor.close()
cnx_orig.close()
