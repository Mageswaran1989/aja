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
#   Logs should start with :
#   BOOT-TIME| .....
#
#   Two patterns are looked for:
#   BOOT-TIME| SystemServer startOtherServices : started at|9603
#   ("at", "|" before the process name and before the timing)
#   BOOT-TIME| DomainServiceController : com.gm.server.erasedataservice.GMEraseDataService : phase : 4 : took |14
#   ("DomainServiceController")
###############################################################

from sys import argv
from pandas import DataFrame
import glob, os, os.path
import transposer

#scripName = argv

#Directory names
beforeDir = "before"
afterDir = "after"

#Flag to inser line number / process number for ordering while using Excel
processOrderInsertFlag = True

#Outfile name
parsedFile = "GMInfo3BootTime"

#Maps to hold the individual boot data
processTimeMap = dict()
#Map to store the process name and line number/process number from 0 to n
processOrder = dict()

#Delete the old file if found
if os.path.isfile(parsedFile+"Transposed"+'.csv'):
   os.remove(parsedFile+"Transposed"+'.csv')
   processOrderInsertFlag = False

#Create or append the each boot entry to the file
def toCSV(dictData, fileName, logFileName):
    entry = DataFrame(dictData.values(), index=dictData.keys(), columns=[logFileName])
    fileExists = os.path.isfile(fileName+".csv")     
    with open(fileName+".csv", 'a') as file:
        if fileExists:
           print "Found "+fileName+".csv... inserting the entry..."
           entry.T.to_csv(file, header=False)
        else:
           entry.T.to_csv(file, header=True)
           print "Creating "+fileName+".csv... inserting the entry..."

#Look for .txt file in the diretory and filter out the process nae and timing
def processFile(dirName):
 global processOrderInsertFlag
 for logFileName in glob.glob(dirName+"/*.txt"):
    print("Processing..." + logFileName)
    logFile = open(logFileName, 'r')
    i = 1
    for line in logFile:
        #print line
        #For pattern : BOOT-TIME| SystemServer startOtherServices : started at|9603
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
        #Patern: BOOT-TIME| DomainServiceController : com.gm.server.erasedataservice.GMEraseDataService : phase : 4 : took |14
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

print "Processing the folder <before>..."
processFile(beforeDir)
print "Processing the folder <after>..."
processFile(afterDir)

if os.path.isfile(parsedFile+".csv"):
  transposer.transpose(i=parsedFile+'.csv', o=parsedFile+"Transposed"+'.csv')
  print "Found "+parsedFile+".csv, transposing it for easy analysing data"


