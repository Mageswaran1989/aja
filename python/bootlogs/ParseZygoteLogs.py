#!/usr/bin/python

#################################################################
# Pandas: 
#        http://pandas.pydata.org/pandas-docs/stable/install.html
# Transposer : 
#        Download : https://pypi.python.org/pypi/transposer/
#        $ sudo python setup.py install 
#        (utility to transpose CSV file)
#################################################################

################################################################
#
#   Preconditions:
#   Logs should start with : BOOT-TIME| .....
#
###############################################################

from sys import argv
from pandas import Series, DataFrame
import pandas as pd
import time as SystemTime #To avoid name space collision
import copy
import datetime
import glob, os, os.path
import transposer

scripName = argv

beforeDir = "before"
afterDir = "after"

processOrderInsertFlag = True

parsedFile = "GMInfo3BootTime"

processTimeMap = dict()
processOrder = dict()

if os.path.isfile(parsedFile+"Transposed"+'.csv'):
   os.remove(parsedFile+"Transposed"+'.csv')

def toCSV(dictData, fileName, logFileName):
    #ts = SystemTime.time()
    #timeStamp = datetime.datetime.fromtimestamp(ts).strftime('%m-%d %H:%M')
    entry = DataFrame(dictData.values(), index=dictData.keys(), columns=[logFileName]) # + ":" + timeStamp])
    fileExists = os.path.isfile(fileName+".csv")     
    with open(fileName+".csv", 'a') as file:
        if fileExists:
           print "Found "+fileName+".csv... inserting the entry..."
           entry.T.to_csv(file, header=False)
        else:
           entry.T.to_csv(file, header=True)
           print "Creating "+fileName+".csv... inserting the entry..."


def processFile(dirName):
 global processOrderInsertFlag
 for logFileName in glob.glob(dirName+"/*.txt"):
    print("Processing..." + logFileName)
    logFile = open(logFileName, 'r')
    i = 1
    for line in logFile:
        #print line
        startupInfo = line.split("at") # changed started -> at to handle "PowerModing HMIReady"
        if len(startupInfo) > 1:
           processInfo = startupInfo[0]
           timeInfo = startupInfo[1]
           if timeInfo.find('|') == -1:
              continue #Special case to handle "PowerModing HMIReady"
           name = processInfo.split('|')[1].strip().replace(':', '')
           time = timeInfo.split('|')[1].strip()
           processTimeMap[name] = time      
           processOrder[name] = i 
           #print name, time
        domainServiceInfo = line.split("DomainServiceController")
        if len(domainServiceInfo) > 1:
           ds = domainServiceInfo[1]
           name = ds.split(':')[1].strip()
           time = ds.split('|')[-1].strip()
           processTimeMap[name] = time
           processOrder[name] = i
           #print name, time
        i = i + 1
    #print processTimeMap
    if processOrderInsertFlag == True: #To insert the number for ease of sorting in excel
       toCSV(processOrder, parsedFile, "ProcessOrder")
       processOrderInsertFlag = False
    toCSV(processTimeMap, parsedFile, logFileName)
    logFile.close()

print "Processing folder <before>..."
processFile(beforeDir)
print "Processing folder <after>..."
processFile(afterDir)

if os.path.isfile(parsedFile+".csv"):
  transposer.transpose(i=parsedFile+'.csv', o=parsedFile+"Transposed"+'.csv')
  print "Found "+parsedFile+".csv, transposing it for easy analysing data"


