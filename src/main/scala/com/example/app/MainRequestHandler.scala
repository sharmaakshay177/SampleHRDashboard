package com.example.app


import com.example.app.Controllers.RequestHandler.{addAttendance,
                                                   addEmployee,
                                                   checkLoginCredentials,
                                                   sendRecipe,
                                                   applyOrMarkLeaveOnAttendanceJson}
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.scalatra._
import org.json4s.{DefaultFormats, Formats}
import org.scalatra.json.JacksonJsonSupport

// TODO:: Add swagger support for the API Documentation - swagger support

class MainRequestHandler extends ScalatraServlet with JacksonJsonSupport {
  protected implicit lazy val jsonFormats: Formats = DefaultFormats
  val logger: Logger = LoggerFactory.getLogger(getClass)

  before(){
    contentType = formats("json")
  }

  get("/") {
    views.html.hello()
  }

  get("/index") {
    logger.info("Request is made for index page")
    "Hi there starting scalatra webservice"
  }

  get("/home"){
    "Home page"
  }

  get("/recipe"){
    response.addHeader("ACK", "Recipe Send")
    sendRecipe
  }


  get("/extract"){
    logger.info(s"params extracted is $params")
    logger.info(s"key1 extracted is : ${params("key1")}")
    logger.info(s"key2 extracted is : ${params("key2")}")
  }

  get("/login"){
    logger.info("Request is made for login page")
    val userName: String = params("key-user")
    val password: String  = params("key-pass")

    logger.info(s"username $userName password $password")
    val isLoginCredentialGood = checkLoginCredentials(userName, password)
    //TODO: Pass the main page template here if login credentials are good
    //TODO: Send Employee role also in the template
    isLoginCredentialGood
  }
  get("/:username/logout"){
    logger.info("Request for logout is made by user")
    redirect("/home")
  }
  
  post("/:username/add"){
    logger.info("Request Made for adding the user by admin")

    val username: String = params("username")
    logger.info(s"User-name: $username is making request")

    // Extracting other details
    val nameCompleteString = params("name")
    val empID = params.getAs[Int]("empID") match {
      case Some(id) => id
      case None => -1
    }
    val empDetail = params("empDetails")
    logger.info(s"name: $nameCompleteString")
    logger.info(s"detail: $empDetail")
    // sending details for parsing
    val Response = addEmployee(username, empID, nameCompleteString, empDetail)
    response.addHeader("ACK", "Request Processed")
    Response
  }

  post("/:username/approve"){
    logger.info("Post request made for attendance approval")
    val approverName: String = params("username")

    val attendanceDetails: String = params("attendanceDetails")

    logger.info(s"ApproverName is : $approverName")
    logger.info(s"Attendance Details Name is : $attendanceDetails")
    val resp = addAttendance(approverName,attendanceDetails)
    response.addHeader("ACK", resp)
    resp
  }

  post("/:employee/leaveapply"){
    logger.info("Post Request for leave apply by the employee")
    val empId = params.getAs[Int]("employee") match {
      case Some(id) => id
      case None => halt(404, <h1>Not found. Bummer. Or employee ID is not of Type</h1>)
    }

    logger.info(s"Request made by employee: $empId")
    //extracting params

    val startDate   = params("startDate")
    val endDate     = params("endDate")
    val typeOfLeave = params("type")

    logger.info(s"params are $startDate, $endDate and $typeOfLeave")

    val resp = applyOrMarkLeaveOnAttendanceJson(empId, typeOfLeave, startDate, endDate)
    response.addHeader("ACK", resp)
    resp
  }

  notFound{
    <h1>Not found. Bummer.</h1>
  }

}