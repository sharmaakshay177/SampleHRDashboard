package com.example.app.Controllers

import com.example.app.Controllers.UtilityHelper.{extractJsonAndUpdate, getTheMonthDateJson}
import org.json4s.{DefaultFormats, Formats}
import org.json4s.jackson.Serialization.write
import com.example.app.DataBaseConnectors.MySqlConnector
import com.example.app.DataBaseConnectors.DataHandler
import org.slf4j.{Logger, LoggerFactory}
import com.example.app.Models.LoginResponse

case class Recipe(title: String,
                  details: RecipeDetails,
                  ingredients: List[IngredientLine],
                  steps: List[String])

case class RecipeDetails(cuisine: String, vegetarian: Boolean,
                         diet: Option[String])

case class IngredientLine(label: String, quantity: String)


object RequestHandler {

  protected implicit val jsonFormats: Formats = DefaultFormats
  private val logger: Logger = LoggerFactory.getLogger(getClass)
  private val config = GlobalHelpers.getConfig

  protected val Host: String = config.getOrElse("Host", "Not Found").toString
  protected val Port: Int = config.getOrElse("Port", "Not Found").toString.toInt
  protected val DataBaseName: String = config.getOrElse("DataBaseName", "Not Found").toString
  protected val sqlConnector = new MySqlConnector(Host, Port)

  private val Tables: Map[String, Any] = config.get("Tables") match {
    case Some(value) => value.asInstanceOf[Map[String, Any]]
  }

  def checkLoginCredentials(userName: String, password: String): String ={
    val url: String = sqlConnector.createUrl(DataBaseName)
    val connection = sqlConnector.createConnection(url) match {
      case Left(conn) => conn
      case _ =>  return "Bummer: SQl Server Not Up"
    }

    val statement = sqlConnector.createStatement(connection)
    val query = CreateQueries.createQueryForLogin(Tables.getOrElse("LoginTableName","Not Found").toString,
                                                  userName,
                                                  password)
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

    val url: String = sqlConnector.createUrl(DataBaseName)
    val connection = sqlConnector.createConnection(url) match {
      case Left(conn) => conn
      case _ =>  return "Bummer: SQl Server Not Up"
    }
    val statement = sqlConnector.createStatement(connection)

    //creating Queries
    val queryToAddNewEmployeeToLogin = CreateQueries.createQueryToAddNewUserToLoginDataTable(Tables.getOrElse("LoginTableName","Not Found").toString,
                                                                                              empID,
                                                                                              userName = "",
                                                                                              password = "",
                                                                                              employeeDetail.Role)

    val queryToAddNewEmployeeToPersonalRecord = CreateQueries.createQueryForAddingNewUserToPersonalDataTable(Tables.getOrElse("PersonalDataTableName","Not Found").toString,
                                                                                                              empID,
                                                                                                              personal.FirstName,
                                                                                                              personal.LastName,
                                                                                                              personal.Age)

    val queryToAddNewEmployeeToCompanyRecord = CreateQueries.createQueryToAddNewUserToCompanyRecordDataTable(Tables.getOrElse("CompanyRecordTableName","Not Found").toString,
                                                                                                              empID,
                                                                                                              personal.FirstName,
                                                                                                              personal.LastName,
                                                                                                              employeeDetail.Role,
                                                                                                              employeeDetail.Designation,
                                                                                                              managerName,
                                                                                                              employeeDetail.Salary)

    val initialAttendanceJson = getTheMonthDateJson()
    val queryToAddEmployeeToAttendanceSystem = CreateQueries.crateQueryToAddNewUserToAttendanceSystem(Tables.getOrElse("AttendanceTableName","Not Found").toString,
                                                                                                      empID,
                                                                                                      employeeDetail.ManagerName,
                                                                                                      initialAttendanceJson)

    val resultLogin =                   DataHandler.executeUpdateQueryOnDataBase(statement, queryToAddNewEmployeeToLogin)
    val resultAddEmployee =             DataHandler.executeUpdateQueryOnDataBase(statement, queryToAddNewEmployeeToPersonalRecord)
    val resultAddEmployeeToCompany =    DataHandler.executeUpdateQueryOnDataBase(statement, queryToAddNewEmployeeToCompanyRecord)
    val resultAddEmployeeToAttendance = DataHandler.executeUpdateQueryOnDataBase(statement, queryToAddEmployeeToAttendanceSystem)

    connection.close()
    var response: String = "No able to add employee"

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

    val queryToGetEmployeeJson = CreateQueries.queryToRetrieveUserJsonFromAttendanceRecord(Tables.getOrElse("AttendanceTableName","Not Found").toString,
                                                                                           attendanceDetailsObject.EmpID)
    logger.info(s"Result Set Acquired after the query")
    val resultSet = DataHandler.executeQueryOnDataBase(statement, queryToGetEmployeeJson)

    //result set will return only a single row of managerName, json
    val jsonToWrite = extractJsonAndUpdate(managerName,
                                           attendanceDetailsObject.date,
                                           attendanceDetailsObject.isPresent,
                                           resultSet)
    val updateJsonQuery = CreateQueries.queryToUpdateAttendanceJson(Tables.getOrElse("AttendanceTableName","Not Found").toString,
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

    val queryToGetEmployeeJson = CreateQueries.queryToRetrieveUserJsonFromAttendanceRecord(Tables.getOrElse("AttendanceTableName","Not Found").toString,
                                                                                           empId = empId)
    val resultSet = DataHandler.executeQueryOnDataBase(statement, queryToGetEmployeeJson)
    logger.info(s"Result Set Acquired after the query")

    val newJsonToWrite = UtilityHelper.addEmployeeLeavesToAttendanceJson(startDate,
                                                                         endDate,
                                                                         resultSet)
    val queryToWriteJson = CreateQueries.queryToUpdateAttendanceJson(Tables.getOrElse("AttendanceTableName","Not Found").toString,
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
