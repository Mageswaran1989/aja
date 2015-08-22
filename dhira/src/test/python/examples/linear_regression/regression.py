from numpy import *

'''
 import regression
 from numpy import *
 xArr,yArr=regression.loadDataSet('ex0.txt')
'''

def loadDataSet(fileName):      #general function to parse tab -delimited floats
    numFeat = len(open(fileName).readline().split('\t')) - 1 #get number of fields 
    dataMat = []; labelMat = []
    fr = open(fileName)
    for line in fr.readlines():
        lineArr =[]
        curLine = line.strip().split('\t')
        for i in range(numFeat):
            lineArr.append(float(curLine[i]))
        dataMat.append(lineArr)
        labelMat.append(float(curLine[-1]))
    return dataMat,labelMat

def standRegres(xArr,yArr): # 200 x 2, 1 x 200
    xMat = mat(xArr);  # 200 x 2
    yMat = mat(yArr).T # 200 x 1
    xTx = xMat.T*xMat  # 2   x 2
    if linalg.det(xTx) == 0.0:
       print "This matrix is singular, cannot do inverse"
       return
    ws = xTx.I * (xMat.T*yMat) # (2 X 2) * (2 X 200 * 200 * 1) = 2 x 1
    return ws # 2 x 1
'''
 xMat=mat(xArr) # 200 x 2
 yMat=mat(yArr) # 1 X 200
 yHat = xMat*ws # () = 200 x 2 * 2 x 1 => 200 x 1
'''

'''
import matplotlib.pyplot as plt
 fig = plt.figure()
 ax = fig.add_subplot(111)
 ax.scatter(xMat[:,1].flatten().A[0], yMat.T[:,0].flatten().A[0])
 xCopy=xMat.copy()
 xCopy.sort(0)
 yHat=xCopy*ws
 ax.plot(xCopy[:,1],yHat)
 plt.show()
'''

def lwlr(testPoint,xArr,yArr,k=1.0):
    xMat = mat(xArr); yMat = mat(yArr).T
    m = shape(xMat)[0]
    weights = mat(eye((m)))
    for j in range(m):                      #next 2 lines create weights matrix
        diffMat = testPoint - xMat[j,:]     #
        weights[j,j] = exp(diffMat*diffMat.T/(-2.0*k**2))
    xTx = xMat.T * (weights * xMat)
    if linalg.det(xTx) == 0.0:
        print "This matrix is singular, cannot do inverse"
        return
    ws = xTx.I * (xMat.T * (weights * yMat))
    return testPoint * ws

def lwlrTest(testArr,xArr,yArr,k=1.0):  #loops over all the data points and applies lwlr to each one
    m = shape(testArr)[0]
    yHat = zeros(m)
    for i in range(m):
        yHat[i] = lwlr(testArr[i],xArr,yArr,k)
    return yHat

'''
 xArr,yArr=regression.loadDataSet('ex0.txt')
 yArr[0]
 regression.lwlr(xArr[0],xArr,yArr,1.0)
 regression.lwlr(xArr[0],xArr,yArr,0.001)
 yHat = regression.lwlrTest(xArr, xArr, yArr,0.003)
 xMat=mat(xArr)
 srtInd = xMat[:,1].argsort(0)
 xSort=xMat[srtInd][:,0,:]

 import matplotlib.pyplot as plt
 fig = plt.figure()
 ax = fig.add_subplot(111)
 ax.plot(xSort[:,1],yHat[srtInd])
 ax.scatter(xMat[:,1].flatten().A[0], mat(yArr).T.flatten().A[0] , s=2, c='red')
 plt.show()

'''

def rssError(yArr,yHatArr):
  return ((yArr-yHatArr)**2).sum()

'''
#Training with first 100 data set

abX,abY=regression.loadDataSet('abalone.txt')

yHat01=regression.lwlrTest(abX[0:99],abX[0:99],abY[0:99],0.1)
yHat1=regression.lwlrTest(abX[0:99],abX[0:99],abY[0:99],1)
yHat10=regression.lwlrTest(abX[0:99],abX[0:99],abY[0:99],10)

#Training Error
regression.rssError(abY[0:99],yHat01.T)
regression.rssError(abY[0:99],yHat1.T)
regression.rssError(abY[0:99],yHat10.T)


#Test data remaining 100 dataset and their Error
yHat01=regression.lwlrTest(abX[100:199],abX[0:99],abY[0:99],0.1)
regression.rssError(abY[100:199],yHat01.T)

yHat1=regression.lwlrTest(abX[100:199],abX[0:99],abY[0:99],1)
regression.rssError(abY[100:199],yHat1.T)

yHat10=regression.lwlrTest(abX[100:199],abX[0:99],abY[0:99],10)
regression.rssError(abY[100:199],yHat10.T)
'''

def ridgeRegres(xMat,yMat,lam=0.2):
    xTx = xMat.T*xMat
    denom = xTx + eye(shape(xMat)[1])*lam
    if linalg.det(denom) == 0.0:
        print "This matrix is singular, cannot do inverse"
        return
    ws = denom.I * (xMat.T*yMat)
    return ws

def ridgeTest(xArr,yArr):
    xMat = mat(xArr); yMat=mat(yArr).T
    yMean = mean(yMat,0)
    yMat = yMat - yMean     #to eliminate X0 take mean off of Y
    #regularize X's
    xMeans = mean(xMat,0)   #calc mean then subtract it off
    xVar = var(xMat,0)      #calc variance of Xi then divide by it
    xMat = (xMat - xMeans)/xVar
    numTestPts = 30
    wMat = zeros((numTestPts,shape(xMat)[1]))
    for i in range(numTestPts):
        ws = ridgeRegres(xMat,yMat,exp(i-10))
        wMat[i,:]=ws.T
    return wMat

'''
reload(regression)
abX,abY=regression.loadDataSet('abalone.txt')
ridgeWeights=regression.ridgeTest(abX,abY)

 import matplotlib.pyplot as plt
 fig = plt.figure()
 ax = fig.add_subplot(111)
 ax.plot(ridgeWeights)
 plt.show()

'''

def regularize(xMat):#regularize by columns
    inMat = xMat.copy()
    inMeans = mean(inMat,0)   #calc mean then subtract it off
    inVar = var(inMat,0)      #calc variance of Xi then divide by it
    inMat = (inMat - inMeans)/inVar
    return inMat



def stageWise(xArr,yArr,eps=0.01,numIt=100):
    xMat = mat(xArr); yMat=mat(yArr).T
    yMean = mean(yMat,0)
    yMat = yMat - yMean     #can also regularize ys but will get smaller coef
    xMat = regularize(xMat)
    m,n=shape(xMat)
    #returnMat = zeros((numIt,n)) #testing code remove
    ws = zeros((n,1)); 
    wsTest = ws.copy(); 
    wsMax = ws.copy()
    for i in range(numIt):
        print ws.T
        lowestError = inf;
        for j in range(n):
            for sign in [-1,1]:
                wsTest = ws.copy()
                wsTest[j] += eps*sign
                yTest = xMat*wsTest
                rssE = rssError(yMat.A,yTest.A)
                if rssE < lowestError:
                    lowestError = rssE
                    wsMax = wsTest
        ws = wsMax.copy()
        #returnMat[i,:]=ws.T
    #return returnMat

'''
 reload(regression)
 xArr,yArr=regression.loadDataSet('abalone.txt')
 regression.stageWise(xArr,yArr,0.01,200)


'''

