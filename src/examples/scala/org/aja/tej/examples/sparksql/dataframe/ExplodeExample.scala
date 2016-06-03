package org.aja.tej.examples.sparksql.dataframe

import org.aja.tej.utils.TejUtils
import org.apache.spark.sql._

import org.apache.spark.sql.types._

/**
  * Created by mageswaran on 6/2/16.
  */
object ExplodeExample extends App {


  val sc = TejUtils.getSparkContext(this.getClass.getSimpleName)

  val sqlContext = new SQLContext(sc)
  import sqlContext.implicits._

  case class Employee(firstName: String, lastName: String, email: String)
  case class Department(id: String, name: String)
  case class DepartmentWithEmployees(department: Department, employees: Seq[Employee])

  val employee1 = new Employee("michael", "armbrust", "abc123@prodigy.net")
  val employee2 = new Employee("chris", "fregly", "def456@compuserve.net")

  val department1 = new Department("123456", "Engineering")
  val department2 = new Department("123456", "Psychology")

  val departmentWithEmployees1 = new DepartmentWithEmployees(department1, Seq(employee1, employee2))
  val departmentWithEmployees2 = new DepartmentWithEmployees(department2, Seq(employee1, employee2))

  val departmentWithEmployeesRDD = sc.parallelize(Seq(departmentWithEmployees1, departmentWithEmployees2))
  departmentWithEmployeesRDD.toDF().saveAsParquetFile("dwe.parquet")

  val departmentWithEmployeesDF = sqlContext.parquetFile("dwe.parquet")

  departmentWithEmployeesDF.printSchema()
  //  root
  //  |-- department: struct (nullable = true)
  //  |    |-- id: string (nullable = true)
  //  |    |-- name: string (nullable = true)
  //  |-- employees: array (nullable = true)
  //  |    |-- element: struct (containsNull = true)
  //  |    |    |-- firstName: string (nullable = true)
  //  |    |    |-- lastName: string (nullable = true)
  //  |    |    |-- email: string (nullable = true)
  departmentWithEmployeesDF.show()
  //  +--------------------+--------------------+
  //  |          department|           employees|
  //  +--------------------+--------------------+
  //  | [123456,Psychology]|[[michael,armbrus...|
  //  |[123456,Engineering]|[[michael,armbrus...|
  //  +--------------------+--------------------+

  // This would be replaced by explodeArray()
  val explodedDepartmentWithEmployeesDF = departmentWithEmployeesDF.explode(departmentWithEmployeesDF("employees")) {
    case Row(employee: Seq[Row]) => employee.map(employee =>
      Employee(employee(0).asInstanceOf[String], employee(1).asInstanceOf[String], employee(2).asInstanceOf[String])
    )
  }

  explodedDepartmentWithEmployeesDF.foreach(println)
  //    [[123456,Psychology],WrappedArray([michael,armbrust,abc123@prodigy.net], [chris,fregly,def456@compuserve.net]),michael,armbrust,abc123@prodigy.net]
  //  [[123456,Engineering],WrappedArray([michael,armbrust,abc123@prodigy.net], [chris,fregly,def456@compuserve.net]),michael,armbrust,abc123@prodigy.net]
  //  [[123456,Psychology],WrappedArray([michael,armbrust,abc123@prodigy.net], [chris,fregly,def456@compuserve.net]),chris,fregly,def456@compuserve.net]
  //  [[123456,Engineering],WrappedArray([michael,armbrust,abc123@prodigy.net], [chris,fregly,def456@compuserve.net]),chris,fregly,def456@compuserve.net]

  // println(explodedDepartmentWithEmployeesDF.getClass.getTypeName)
  //  org.apache.spark.sql.DataFrame

  explodedDepartmentWithEmployeesDF.printSchema()
  //  root
  //  |-- department: struct (nullable = true)
  //  |    |-- id: string (nullable = true)
  //  |    |-- name: string (nullable = true)
  //  |-- employees: array (nullable = true)
  //  |    |-- element: struct (containsNull = true)
  //  |    |    |-- firstName: string (nullable = true)
  //  |    |    |-- lastName: string (nullable = true)
  //  |    |    |-- email: string (nullable = true)
  //  |-- firstName: string (nullable = true)
  //  |-- lastName: string (nullable = true)
  //  |-- email: string (nullable = true)
  explodedDepartmentWithEmployeesDF.show()
  //  +--------------------+--------------------+---------+--------+--------------------+
  //  |          department|           employees|firstName|lastName|               email|
  //  +--------------------+--------------------+---------+--------+--------------------+
  //  | [123456,Psychology]|[[michael,armbrus...|  michael|armbrust|  abc123@prodigy.net|
  //  | [123456,Psychology]|[[michael,armbrus...|    chris|  fregly|def456@compuserve...|
  //  |[123456,Engineering]|[[michael,armbrus...|  michael|armbrust|  abc123@prodigy.net|
  //  |[123456,Engineering]|[[michael,armbrus...|    chris|  fregly|def456@compuserve...|
  //  +--------------------+--------------------+---------+--------+--------------------+

  println("explodedDepartmentWithEmployeesDF1")
  departmentWithEmployeesDF.explode($"employees") {
    case Row(employee: Seq[Row]) => employee.map(employee =>
      Employee(employee(0).asInstanceOf[String], employee(1).asInstanceOf[String], employee(2).asInstanceOf[String])
    )
  }.show()


  //  +--------------------+--------------------+---------+--------+--------------------+
  //  |          department|           employees|firstName|lastName|               email|
  //  +--------------------+--------------------+---------+--------+--------------------+
  //  | [123456,Psychology]|[[michael,armbrus...|  michael|armbrust|  abc123@prodigy.net|
  //  | [123456,Psychology]|[[michael,armbrus...|    chris|  fregly|def456@compuserve...|
  //  |[123456,Engineering]|[[michael,armbrus...|  michael|armbrust|  abc123@prodigy.net|
  //  |[123456,Engineering]|[[michael,armbrus...|    chris|  fregly|def456@compuserve...|
  //  +--------------------+--------------------+---------+--------+--------------------+
}
