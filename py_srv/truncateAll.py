#delete all data from small, medium, and large tables of target db
import Truncator

t = Truncator.Truncator()

#ORIGIN
t.truncate("127.0.1", "EXP_ORIG", "juan", "LapinCoquin13", "Small100")
t.truncate("127.0.1", "EXP_ORIG", "juan", "LapinCoquin13", "Med1000")
t.truncate("127.0.1", "EXP_ORIG", "juan", "LapinCoquin13", "Large65535")

t.truncate("127.0.1", "EXP_TARGET", "juan", "LapinCoquin13", "Small100")
t.truncate("127.0.1", "EXP_TARGET", "juan", "LapinCoquin13", "Med1000")
t.truncate("127.0.1", "EXP_TARGET", "juan", "LapinCoquin13", "Large65535")










