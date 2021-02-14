package com.example.app.Controllers

import org.slf4j.{Logger, LoggerFactory}

object CreateQueries {
  private val logger: Logger = LoggerFactory.getLogger(getClass)

  //TODO: Move column Names to a single file and create generic column names.
  //TODO: create generic queries by passing the column names.
  def createQueryForLogin(tableName: String,
                          userName: String,
                          passWord: String): String = {
     val query = s""" SELECT * FROM ${tableName} WHERE UserName="${userName}" AND Password="${passWord}" """
     logger.info(s"Query: $query")
     query

  }

  def createQueryForAddingNewUserToPersonalDataTable(tableName: String,
                                                     empID: Int,
                                                     firstName: String,
                                                     lastName: String,
                                                     age: Int): String ={

    val query = s""" INSERT INTO $tableName (`EmpID`, `FirstName`, `LastName`, `Age`) VALUES ("$empID", "$firstName", "$lastName", $age); """
    logger.info(s"Query Created: $query")
    query
  }

  def createQueryToAddNewUserToLoginDataTable(tableName: String,
                                              empId: Int,
                                              userName: String,
                                              password: String,
                                              role: String): String ={
    val query = s""" INSERT INTO $tableName (`EmployeeID`, `UserName`, `Password`, `Role`) VALUES ("$empId", "$userName", "$password", "$role"); """
    logger.info(s"Query Created: $query")
    query
  }

  def createQueryToAddNewUserToCompanyRecordDataTable(tableName: String,
                                                      empId: Int,
                                                      firstName: String,
                                                      lastName: String,
                                                      role: String,
                                                      designation: String,
                                                      managerName: String,
                                                      salary: Double): String ={

    val query = s""" INSERT INTO $tableName (`empId`, `firstname`, `lastname`, `role`, `designation`, `managername`, `salary`) VALUES ("$empId", "$firstName", "$lastName", "$role", "$designation", "$managerName", "$salary"); """
    logger.info(s"Query Created: $query")
    query
  }

  def crateQueryToAddNewUserToAttendanceSystem(tableName: String,
                                               empId: Int,
                                               managerName: String,
                                               attendanceJson: String): String ={
    val query = s""" INSERT INTO $tableName (`empId`, `managerName`, `dayDetails`) VALUES ("$empId", "$managerName", '$attendanceJson'); """
    logger.info(s"Query Created: $query")
    query
  }

  def queryToRetrieveUserJsonFromAttendanceRecord(tableName: String,
                                                  empId: Int): String ={
    val query = s""" SELECT `managerName`, `dayDetails` FROM $tableName WHERE `empId`="$empId" """
    logger.info(s"Query Created: $query")
    query
  }

  def queryToUpdateAttendanceJson(tableName: String,
                                  empId:Int,
                                  updatedJson: String): String ={
    val query = s""" UPDATE $tableName SET `dayDetails` = '$updatedJson' WHERE $tableName.`empId` = $empId; """
    logger.info(s"Query Created: $query")
    query
  }

}
