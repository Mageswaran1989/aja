
package org.aja.se

trait SEBase {

  //Variables to hold Url
  var getQuoteUrl: String
  var stocksCsvUrl: String 
  var getTopGainersUrl: String
  var getTopLosersUrl: String

  //Abstract methods to be implemented by Nse & Bse classes
  def getStockCodes
  def isValidCode
  def getQuote
  def getTopGainers
  def getTopLosers
  def getMostActive
  def getTopVolume
  def getPeerCompanies
  def isMarketOpen
}
