package org.aja.tej.examples.sparksql.datasets

import org.aja.tej.utils.TejUtils
import org.apache.spark.sql.types._
import org.apache.spark.sql.{Row, SQLContext}

/**
  * Created by mdhandapani on 16/2/16.
  *
  * Small SparkSQL utility to explore NorthWind Database
  * URL: https://northwinddatabase.codeplex.com/releases/view/71634
  */
trait NorthWindUtility {

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
  val employeesDF = sqlContext.read
    .format("com.databricks.spark.csv")
    .option("header", "true")
    .schema(employesSchema)
    .load("data/northwind/NW-Employees.csv")

  /**
    * Displays Employee DataFrame
    */
  def showEmployeesDF() = employeesDF.show()

  /**
    * Registers EmployeesDF to query with Spark SQL
    * @param name Table name to be registered
    */
  def registerEmployeeDFAs(name: String) = employeesDF.registerTempTable(name)

  protected val orderDetailsSchema = StructType(Seq(
    StructField("OrderID",   IntegerType, true),
    StructField("ProductID", IntegerType, true),
    StructField("UnitPrice", DoubleType, true),
    StructField("Qty",       IntegerType, true),
    StructField("Discount",  DoubleType, true)
  ))

  /**
    * Returns the OrderDetail CSV file as DF
    */
  val orderDetailsDF = sqlContext.read
    .format("com.databricks.spark.csv")
    .option("header", "true")
    .schema(orderDetailsSchema)
    .load("data/northwind/NW-Order-Details.csv")

  /**
    * Displays OrderDetail DataFrame
    */
  def showOrderDetailsDF = orderDetailsDF.show()

  /**
    * Registers OrderDetailDF to query with Spark SQL
    * @param name Table name to be registered
    */
  def registerOrderDetailsDFAs(name: String) = orderDetailsDF.registerTempTable(name)

  protected val ordersSchema = StructType(Seq(
    StructField("OrderID", IntegerType, true),
    StructField("CustomerID", StringType, true),
    StructField("EmployeeID", IntegerType, true),
    StructField("OrderDate", StringType, true),
    StructField("ShipCountry", StringType, true)
  ))

  /**
    * Returns the Orders CSV file as DF
    */
  val ordersDF = sqlContext.read
    .format("com.databricks.spark.csv")
    .option("header", "true")
    .schema(ordersSchema)
    .load("data/northwind/NW-Orders.csv")

  /**
    * Displays Orders DataFrame
    */
  def showOrdersDF = ordersDF.show()

  /**
    * Registers OrdersDF to query with Spark SQL
    * @param name Table name to be registered
    */
  def registergetOrdersDFAs(name: String) = ordersDF.registerTempTable(name)

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
  val productsDF = sqlContext.read
    .format("com.databricks.spark.csv")
    .option("header", "true")
    .schema(productsSchema)
    .load("data/northwind/NW-Products.csv")

  /**
    * Displays Products DataFrame
    */
  def showProductsDF = productsDF.show()

  /**
    * Registers ProductsDF to query with Spark SQL
    * @param name Table name to be registered
    */
  def registerProductsDF(name: String) = productsDF.registerTempTable(name)
}
