package com.example.app.Controllers

import org.slf4j.{Logger, LoggerFactory}
import org.yaml.snakeyaml.Yaml

import scala.annotation.tailrec

object CreateQueries {
  private val logger: Logger = LoggerFactory.getLogger(getClass)

  //TODO: Move column Names to a single file and create generic column names.
  //TODO: create generic queries by passing the column names and column values.
  def createQueryForLogin(tableName: String,
                          userName: String,
                          passWord: String): String = {
     val query = s""" SELECT * FROM $tableName WHERE UserName="$userName" AND Password="$passWord" """
     logger.info(s"Query: $query")
     query

  }

  def genericSelectQuery[A](tableName: String,
                            selectingColumnNames: List[A],
                            conditionColumnsMap: Map[String, Any]): String ={
    @tailrec
    def createWhereConditions(h: (String, Any), t: Map[String, Any], acc: String): String =
      if(t.isEmpty) acc + s"""${h._1}="${h._2}""""
      else createWhereConditions(t.head, t.tail, acc + s""" ${h._1}="${h._2}" AND """)

    val selectionColumns = {
      if(selectingColumnNames.isEmpty) "*"
      else selectingColumnNames.mkString(",")
    }
    val whereConditionString = createWhereConditions(conditionColumnsMap.head, conditionColumnsMap.tail, "")
    val query = s""" SELECT $selectionColumns FROM $tableName WHERE $whereConditionString """
    logger.info(s"Query: $query")
    query
  }

  def genericInsertQuery[A](tableName: String,
                            allColumns: List[A],
                            allValues: List[Any]): String ={
    @tailrec
    def createString(h: Any, t: List[Any], end: String, acc: String): String = {
      if(t.isEmpty) acc + s""""$h"""" + end
      else createString(t.head, t.tail, end, acc + s""""$h", """)
    }

    val cols = allColumns.mkString("(",",",")")
    val stringValues = createString(allValues.head, allValues.tail, ");", "(")
    val query: String = s""" INSERT INTO $tableName $cols VALUES $stringValues """
    logger.info(s"Query Created: $query")
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
