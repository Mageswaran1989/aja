###############################################################################
#  Run : python nse_stock_exchange.py
#  
#  Output: A .csv file with entered stock name
#
#
##############################################################################
from nsetools import Nse
from pandas import Series, DataFrame
import pandas as pd
import time
import copy
import datetime

# Utility Functions

def dict_compare(d1, d2):
    d1_keys = set(d1.keys()) #set with unique keys
    d2_keys = set(d2.keys())
    intersect_keys = d1_keys.intersection(d2_keys)
    items_added = d1_keys - d2_keys
    items_removed = d2_keys - d1_keys
    #create dictionary for modyfied items
    items_modified = {key : (d1[key], d2[key]) for key in intersect_keys if d1[key] != d2[key]}
    items_same = set(key for key in intersect_keys if d1[key] == d2[key])
    return items_added, items_removed, items_modified, items_same
#added, removed, modified, same = dict_compare(x, y)

#dump all available stocks from Nse
def dump_stock_codes_to_disk(nse):
    stock_codes = nse.get_stock_codes()
    stock_codes_dataframe = DataFrame(stock_codes.items(), columns = ['NSE ID', 'Full Name'])
    stock_codes_dataframe.to_csv('stock_codes.csv')

def toCsv(stock, name, header=False):
    ts = time.time()
    timeStamp = datetime.datetime.fromtimestamp(ts).strftime('%Y-%m-%d %H:%M:%S')
    transposedEntry = DataFrame(stock.values(), index=stock.keys(), columns=[timeStamp])
    entry = transposedEntry.T
    with open(name+".csv", 'a') as file:
        if header:
           entry.to_csv(file, header=True)
        else:
           entry.to_csv(file, header=False)

#################################################################################################
#Intialize Nse
nse = Nse()

stock_name = raw_input("Enter the stock nse id (eg: infy):")

past_stock_value = nse.get_quote(stock_name)
toCsv(past_stock_value, stock_name, True)

while True:
    time.sleep(1) #assuming this delay will come from network :p
    print "Getting stock value"
    current_stock_value = nse.get_quote(stock_name)
    print current_stock_value["sellPrice1"]
    items_added, items_removed, items_modified, items_same = dict_compare(past_stock_value, current_stock_value)
    if items_modified:
       print "Stock value changed"
       toCsv(current_stock_value, stock_name)
       past_stock_value = copy.deepcopy(current_stock_value)
