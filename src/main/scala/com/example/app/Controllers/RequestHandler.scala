package com.example.app.Controllers

import com.example.app.Controllers.UtilityHelper.{extractJsonAndUpdate, getTheMonthDateJson}
import org.json4s.{DefaultFormats, Formats}
import org.json4s.jackson.Serialization.write
import com.example.app.DataBaseConnectors.MySqlConnector
import com.example.app.DataBaseConnectors.DataHandler
import org.slf4j.{Logger, LoggerFactory}


case class LoginResponse(isCredentialsGood: Boolean, comments: String)
case class EmployeeName(FirstName: String, LastName: String, Age: Int)
case class EmployeeDetails(Role: String, Designation: String, ManagerName: String, Salary: Double = 0.0)
case class AttendanceDetails(EmpID: Int, isPresent: Boolean, date: String)


case class Recipe(title: String,
                  details: RecipeDetails,
                  ingredients: List[IngredientLine],
                  steps: List[String])

case class RecipeDetails(cuisine: String, vegetarian: Boolean,
                         diet: Option[String])

case class IngredientLine(label: String, quantity: String)


object RequestHandler {

  private val logger: Logger = LoggerFactory.getLogger(getClass)

  protected implicit val jsonFormats: Formats = DefaultFormats
  protected val Host: String = "localhost"
  protected val Port: Int = 3306
  protected val DataBaseName: String = "employee"
  protected val sqlConnector = new MySqlConnector(Host, Port)

  def checkLoginCredentials(userName: String, password: String): String ={
    val url: String = sqlConnector.createUrl(DataBaseName)
    val connection = sqlConnector.createConnection(url) match {
      case Left(conn) => conn
      case _ =>  return "Bummer: SQl Server Not Up"
    }
    //TODO: Remove the table name from here and use it from the config file.
    val statement = sqlConnector.createStatement(connection)
    val query = CreateQueries.createQueryForLogin("login", userName, password)
    val result = DataHandler.executeQueryOnDataBase(statement, query)

    var credentialCheck: Boolean = false
    var comments: String = "Not Matching"

    if(result.first() && !result.next()){
      credentialCheck = true
      comments = "Matching credentials"
    }

    val response = LoginResponse(credentialCheck, comments)
    connection.close()
    write(response)
  }

  def sendRecipe: String ={

    val details = RecipeDetails(cuisine = "italian", vegetarian = true, diet = None)
    val ingredients = List(IngredientLine(label = "penne", quantity = "250g"),
                        IngredientLine(label = "Cocktail tomatoes", quantity = "300g"),
                        IngredientLine(label = "Rucola", quantity = "250g"),
                        IngredientLine(label = "Goat cheese", quantity = "250g"),
                        IngredientLine(label = "Garlic cloves", quantity = "2 tsps"))
    val stepsTotal = List("Cook noodles until aldente.",
                          "Quarter the tomatoes, wash the rucola, dice" +
                            "the goat's cheese and cut the garlic.",
                          "Heat olive oil in a pan, add the garlic and the tomatoes and" +
                          "steam short (approx. for 5 minutes).",
                          "Shortly before the noodles are ready add the rucola" +
                          "to the tomatoes.",
                          "Drain the noodles and mix with the tomatoes, "+
                          "finally add the goat's cheese and serve.")

    val recipeToSend = Recipe(title = "Penne with cocktail tomatoes, Rucola and Goat cheese",
                              details = details,
                              ingredients = ingredients,
                              steps = stepsTotal)

    write(recipeToSend)
  }

  def addEmployee(managerName: String, empID: Int, name: String, employeeDetailsString: String): String ={
     val personal = RequestHelpers.getEmployeePersonalDetailsFromString(name)
     val employeeDetail = RequestHelpers.getEmployeeDetailsFromString(employeeDetailsString)


    //TODO: need to add the new employee in login
    //TODO: need to add the new employee to personal record
    //TODO: need to add the new employee in companyRecord
    //TODO: need to add the new employee in attendanceSystem to update the attendance
    val url: String = sqlConnector.createUrl(DataBaseName)
    val connection = sqlConnector.createConnection(url) match {
      case Left(conn) => conn
      case _ =>  return "Bummer: SQl Server Not Up"
    }
    val statement = sqlConnector.createStatement(connection)

    //creating Queries
    //TODO: remove the tableNames from hardcode to yaml file
    val queryToAddNewEmployeeToLogin = CreateQueries.createQueryToAddNewUserToLoginDataTable(tableName = "login",
                                                                                              empID,
                                                                                              userName = "",
                                                                                              password = "",
                                                                                              employeeDetail.Role)

    val queryToAddNewEmployeeToPersonalRecord = CreateQueries.createQueryForAddingNewUserToPersonalDataTable(tableName = "personalrecord",
                                                                                                              empID,
                                                                                                              personal.FirstName,
                                                                                                              personal.LastName,
                                                                                                              personal.Age)

    val queryToAddNewEmployeeToCompanyRecord = CreateQueries.createQueryToAddNewUserToCompanyRecordDataTable(tableName = "companyrecord",
                                                                                                              empID,
                                                                                                              personal.FirstName,
                                                                                                              personal.LastName,
                                                                                                              employeeDetail.Role,
                                                                                                              employeeDetail.Designation,
                                                                                                              managerName,
                                                                                                              employeeDetail.Salary)

    val initialAttendanceJson = getTheMonthDateJson()
    val queryToAddEmployeeToAttendanceSystem = CreateQueries.crateQueryToAddNewUserToAttendanceSystem(tableName = "attendancesystem",
                                                                                                      empID,
                                                                                                      employeeDetail.ManagerName,
                                                                                                      initialAttendanceJson)

    val resultLogin =                   DataHandler.executeUpdateQueryOnDataBase(statement, queryToAddNewEmployeeToLogin)
    val resultAddEmployee =             DataHandler.executeUpdateQueryOnDataBase(statement, queryToAddNewEmployeeToPersonalRecord)
    val resultAddEmployeeToCompany =    DataHandler.executeUpdateQueryOnDataBase(statement, queryToAddNewEmployeeToCompanyRecord)
    val resultAddEmployeeToAttendance = DataHandler.executeUpdateQueryOnDataBase(statement, queryToAddEmployeeToAttendanceSystem)

    connection.close()
    var response: String = "No able to add employee"

    //TODO: Add conditions to return comments according, if any of the queries fail to enter data
    if (resultLogin == 1 && resultAddEmployee == 1 && resultAddEmployeeToCompany == 1 && resultAddEmployeeToAttendance == 1){
      response = "Employee added successfully"
    }

    val resp = Map("Message" -> response)
    connection.close()
    write(resp)
  }

  def addAttendance(managerName: String, attendanceDetails: String): String ={

    val url: String = sqlConnector.createUrl(DataBaseName)
    val connection = sqlConnector.createConnection(url) match {
      case Left(conn) => conn
      case _ =>  return "Bummer: SQl Server Not Up"
    }
    val statement = sqlConnector.createStatement(connection)

    // get the json for the user from database
    // update the value for the date
    // add the json back to the user record.
    val attendanceDetailsObject = RequestHelpers.convertStringToAttendanceDetails(attendanceDetails)
    //TODO: Remove the table Name/ Use table name from config.yaml file
    val queryToGetEmployeeJson = CreateQueries.queryToRetrieveUserJsonFromAttendanceRecord(tableName = "attendancesystem",
                                                                                           attendanceDetailsObject.EmpID)
    logger.info(s"Result Set Acquired after the query")
    val resultSet = DataHandler.executeQueryOnDataBase(statement, queryToGetEmployeeJson)

    //result set will return only a single row of managerName, json
    val jsonToWrite = extractJsonAndUpdate(managerName,
                                           attendanceDetailsObject.date,
                                           attendanceDetailsObject.isPresent,
                                           resultSet)
    val updateJsonQuery = CreateQueries.queryToUpdateAttendanceJson(tableName = "attendancesystem",
                                                                    attendanceDetailsObject.EmpID,
                                                                    jsonToWrite)
    logger.info(s"Updating the Json")
    val resultOfExecution = DataHandler.executeUpdateQueryOnDataBase(statement, updateJsonQuery)
    statement.close()
    //sending response dict
    var comments = "Attendance Not Updated"
    if(resultOfExecution == 1) comments = "Attendance Updated"

    write(Map("comments" -> comments))
  }

  def applyOrMarkLeaveOnAttendanceJson(empId: Int, leaveType: String, startDate: String, endDate: String): String ={

    val url: String = sqlConnector.createUrl(DataBaseName)
    val connection = sqlConnector.createConnection(url) match {
      case Left(conn) => conn
      case _ =>  return "Bummer: SQl Server Not Up"
    }
    val statement = sqlConnector.createStatement(connection)

    val queryToGetEmployeeJson = CreateQueries.queryToRetrieveUserJsonFromAttendanceRecord(tableName = "attendancesystem",
                                                                                           empId = empId)
    val resultSet = DataHandler.executeQueryOnDataBase(statement, queryToGetEmployeeJson)
    logger.info(s"Result Set Acquired after the query")

    val newJsonToWrite = UtilityHelper.addEmployeeLeavesToAttendanceJson(startDate,
                                                                         endDate,
                                                                         resultSet)
    val queryToWriteJson = CreateQueries.queryToUpdateAttendanceJson(tableName = "attendancesystem",
                                                                     empId,
                                                                     newJsonToWrite)

    logger.info(s"Updating the Json for Leave Record")
    val resultOfExecution = DataHandler.executeUpdateQueryOnDataBase(statement, queryToWriteJson)
    connection.close()

    var comments = "Leaves Not Marked For the Dates"
    if(resultOfExecution == 1) comments = "Marked Leaves for The Provided Dates"
    write(Map("comments" -> comments))
  }

}
