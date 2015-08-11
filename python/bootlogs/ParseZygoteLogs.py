#!/usr/bin/python
from sys import argv
from pandas import Series, DataFrame
import pandas as pd
import time as SystemTime #To avoid name space collision
import copy
import datetime

#logFileName = argv
logFile = open("logs.txt", 'r')

parsedFile = "parsed_file"

processTimeMap = dict()

def toCSV(dictData, fileName, header = False):
    ts = SystemTime.time()
    timeStamp = datetime.datetime.fromtimestamp(ts).strftime('%Y-%m-%d %H:%M:%S')
    entry = DataFrame(dictData.values(), index=dictData.keys(), columns=[timeStamp])
    with open(fileName+".csv", 'a') as file:
        if header:
           entry.T.to_csv(file, header=True)
        else:
           entry.T.to_csv(file, header=False)

for line in logFile:
    #print line
    startupInfo = line.split("started")
    #print len(startupInfo)
    if len(startupInfo) > 1:
      processInfo = startupInfo[0]
      timeInfo = startupInfo[1]
      name = processInfo.split('|')[1].strip().replace(':', '')
      time = timeInfo.split('|')[1].strip()
      processTimeMap[name] = time      
      print name, time
    domainServiceInfo = line.split("DomainServiceController")
   # print domainServiceInfo
    if len(domainServiceInfo) > 1:
      ds = domainServiceInfo[1]
      name = ds.split(':')[1].strip()
      time = ds.split('|')[-1].strip()
      processTimeMap[name] = time
      print name, time

print processTimeMap
toCSV(processTimeMap, parsedFile, True)

logFile.close()

