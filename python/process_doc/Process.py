#####################################################################
# sudo apt-get install libxml2
# sudo apt-get install libxml2-dev libxslt1-dev python-dev
# sudo apt-get install python-lxml
# Install docx: sudo pip install docx
#  
#
####################################################################

from pandas import DataFrame
import glob, os, os.path
import transposer
import sys

if len(sys.argv) < 2:
   print "Usage "+ sys.argv[0]+ " <PID section number>"
   print "Eg:"+python+" " +sys.argv[0]+" 17"
   sys.exit(0)

scriptName, pidSectionNumber = sys.argv

textFileName = "output.txt"
pidOutFileName  = "DiagnosticsValidationPID.csv"
didOutFileName  = "DiagnosticsValidationDID.csv"
cpidOutFileName = "DiagnosticsValidationCPID.csv"
dtcOutFileName  = "DiagnosticsValidationDTC.csv"


pidMap = dict()
didMap = dict()
cpidMap = dict()
dtcMap = dict()

def toCSV(dictData, fileName):
    entry = DataFrame(dictData.values(), index=dictData.keys(), columns=[fileName])
    fileExists = os.path.isfile(fileName)
    with open(fileName, 'a') as file:
       if fileExists:
          print "Found "+fileName+"... inserting the entry..."
          entry.T.to_csv(file, header=False)
       else:
          entry.T.to_csv(file, header=True)
          print "Creating "+fileName+"... inserting the entry..."
    file.close()

def processTexFileForPIDs(textFileName):
    textFile = open(textFileName, 'r')
    i = 1
    for line in textFile:
        #print line
        if line.find("PID $") != -1:
           pid = "PID $" + line.split("PID $")[1].strip()
           print pid
           pidMap[i] = pid
           i = i + 1
        if line.find("Introduction") == 0:
           toCSV(pidMap, pidOutFileName)
           if os.path.isfile(pidOutFileName):
              transposer.transpose(i=pidOutFileName, o="DiagnosticsValidationPIDTransposed"+'.csv')
              print "Found "+pidOutFileName+" transposing it for easy analysing data"
           return
        #print pidMapi

def processTexFileForDIDs(textFileName):
    textFile = open(textFileName, 'r')
    i = 1
    for line in textFile:
        #print line
        if line.find("DID $") != -1:
           did = "DID $" + line.split("DID $")[1].strip()
           print did
           didMap[i] = did
           i = i + 1
        if line.find("Introduction") == 0:
           toCSV(didMap, didOutFileName)
           if os.path.isfile(didOutFileName):
              transposer.transpose(i=didOutFileName, o="DiagnosticsValidationDIDTransposed"+'.csv')
              print "Found "+didOutFileName+" transposing it for easy analysing data"
           return
        #print pidMap

def processTexFileForCPIDs(textFileName):
    textFile = open(textFileName, 'r')
    i = 1
    for line in textFile:
        #print line
        if line.find("CPID $") != -1:
           cpid = "CPID $" + line.split("CPID $")[1].strip()
           print cpid
           cpidMap[i] = cpid
           i = i + 1
        if line.find("Introduction") == 0:
           toCSV(cpidMap, cpidOutFileName)
           if os.path.isfile(cpidOutFileName):
              transposer.transpose(i=cpidOutFileName, o="DiagnosticsValidationCPIDTransposed"+'.csv')
              print "Found "+cpidOutFileName+" transposing it for easy analysing data"
           return


processTexFileForPIDs(textFileName)
processTexFileForDIDs(textFileName)
processTexFileForCPIDs(textFileName)
#processTexFileForDTCs(textFileName)
