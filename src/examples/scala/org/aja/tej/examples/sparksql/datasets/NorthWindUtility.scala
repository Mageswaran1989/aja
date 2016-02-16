package org.aja.tej.examples.sparksql.datasets

import org.aja.tej.utils.TejUtils
import org.apache.spark.sql.types._
import org.apache.spark.sql.{Row, SQLContext}

/**
  * Created by mdhandapani on 16/2/16.
  *
  * Small SparkSQL /utility to explore NorthWind Databse
  * URL: https://northwinddatabase.codeplex.com/releases/view/71634
  */
object NorthWindUtility extends App {

  val sc = TejUtils.getSparkContext(this.getClass.getSimpleName)
  val sqlContext = new SQLContext(sc)
  import sqlContext.implicits._

  protected val employesSchema = StructType(Seq(
    StructField("EmployeeID", IntegerType, true),
    StructField("LastName",   StringType, true),
    StructField("FirstName",  StringType, true),
    StructField("Title",      StringType, true),
    StructField("BirthDate",  StringType, true),
    StructField("HireDate",   StringType, true),
    StructField("City",       StringType, true),
    StructField("State",      StringType, true),
    StructField("Zip",        StringType, true),
    StructField("Country",    StringType, true),
    StructField("ReportsTo",  StringType, true)
  ))

  /**
    * Returns the Employee CSV file as DF
    */
  val getEmployeesDF = sqlContext.read
    .format("com.databricks.spark.csv")
    .option("header", "true")
    .schema(employesSchema)
    .load("data/northwind/NW-Employees.csv")

  /**
    * Displays Employee DataFrame
    */
  def showEmployeesDF = getEmployeesDF.show()

  /**
    * Registers EmployeesDF to query with Spark SQL
    * @param name Table name to be registered
    */
  def registerEmployeeDFAs(name: String) = getEmployeesDF.registerTempTable(name)

  protected val orderDetailsSchema = StructType(Seq(
    StructField("OrderID",   IntegerType, true),
    StructField("ProductID", IntegerType, true),
    StructField("UnitPrice", DoubleType, true),
    StructField("Qty",       StringType, true),
    StructField("Discount",  DoubleType, true)
  ))

  /**
    * Returns the OrderDetail CSV file as DF
    */
  val getOrderDetailsDF = sqlContext.read
    .format("com.databricks.spark.csv")
    .option("header", "true")
    .schema(orderDetailsSchema)
    .load("data/northwind/NW-Order-Details.csv")

  /**
    * Displays OrderDetail DataFrame
    */
  def showOrderDetailsDF = getOrderDetailsDF.show()

  /**
    * Registers OrderDetailDF to query with Spark SQL
    * @param name Table name to be registered
    */
  def registerOrderDetailsDFAs(name: String) = getOrderDetailsDF.registerTempTable(name)

  protected val ordersSchema = StructType(Seq(
    StructField("OrderID", IntegerType, true),
    StructField("CustomerID", StringType, true),
    StructField("EmployeeID", IntegerType, true),
    StructField("OrderDate", StringType, true),
    StructField("ShipCuntry", StringType, true)
  ))

  /**
    * Returns the Orders CSV file as DF
    */
  val getOrdersDF = sqlContext.read
    .format("com.databricks.spark.csv")
    .option("header", "true")
    .schema(ordersSchema)
    .load("data/northwind/NW-Orders.csv")

  /**
    * Displays Orders DataFrame
    */
  def showOrdersDF = getOrdersDF.show()

  /**
    * Registers OrdersDF to query with Spark SQL
    * @param name Table name to be registered
    */
  def registergetOrdersDFAs(name: String) = getOrdersDF.registerTempTable(name)

  protected val productsSchema = StructType(Seq(
    StructField("ProductID", IntegerType, true),
    StructField("ProductName",StringType,true),
    StructField("UnitPrice",DoubleType,true),
    StructField("UnitsInStock",IntegerType,true),
    StructField("UnitsOnOrder",IntegerType,true),
    StructField("ReorderLevel",IntegerType,true),
    StructField("Discontinued",IntegerType,true)
  ))

  /**
    * Returns the Products CSV file as DF
    */
  val getProductsDF = sqlContext.read
    .format("com.databricks.spark.csv")
    .option("header", "true")
    .schema(productsSchema)
    .load("data/northwind/NW-Products.csv")

  /**
    * Displays Products DataFrame
    */
  def showProductsDF = getProductsDF.show()

  /**
    * Registers ProductsDF to query with Spark SQL
    * @param name Table name to be registered
    */
  def registerProductsDF(name: String) = getProductsDF.registerTempTable(name)
}
