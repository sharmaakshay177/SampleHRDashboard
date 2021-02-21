package com.example.app.Controllers

import org.scalatest.funsuite.AnyFunSuite
import org.scalamock.scalatest.MockFactory
import org.scalatest.BeforeAndAfter
import CreateQueries.{crateQueryToAddNewUserToAttendanceSystem,
                      createQueryForAddingNewUserToPersonalDataTable,
                      createQueryForLogin,
                      createQueryToAddNewUserToCompanyRecordDataTable,
                      createQueryToAddNewUserToLoginDataTable,
                      genericInsertQuery,
                      genericSelectQuery,
                      queryToRetrieveUserJsonFromAttendanceRecord,
                      queryToUpdateAttendanceJson}

class CreateQueriesSpec extends AnyFunSuite with BeforeAndAfter with MockFactory {

  test("It should return the login query for the user with the passed params"){
    val testTableName = "testTable"
    val testUserName  = "testUser"
    val testPassword  = "testPassword"

    val expectedQuery = s""" SELECT * FROM $testTableName WHERE UserName="$testUserName" AND Password="$testPassword" """
    val returnedQuery = createQueryForLogin(testTableName, testUserName, testPassword)
    assert(returnedQuery === expectedQuery)
  }

  test("It should return the query for select statement with all columns if none passed and" +
    "with the selected Columns with the where conditions in the query"){
    val testTableName = "testTable"

    val selectingColumnsConditionEmpty = List()
    val selectingColumnsConditionFewColumns = List("testColumn1", "testColumn2")

    val conditionMapWithOneCondition         = Map("testConditionColumn" -> "testConditionValue")
    val conditionMapWithMoreThanOneCondition = Map("testConditionColumn1" -> "testConditionValue1",
                                                   "testConditionColumn2" -> "testConditionValue2")

    // Query with no selectColumn and one condition
    val expectedQuery1 = s""" SELECT * FROM $testTableName WHERE testConditionColumn="testConditionValue" """
    val returnedQuery1 = genericSelectQuery(testTableName, selectingColumnsConditionEmpty, conditionMapWithOneCondition)
    assert(returnedQuery1 === expectedQuery1)

    // Query with no selectColumn and Multiple conditions
    val expectedQuery2 = s""" SELECT * FROM $testTableName WHERE testConditionColumn1="testConditionValue1" AND testConditionColumn2="testConditionValue2" """
    val returnedQuery2 = genericSelectQuery(testTableName, selectingColumnsConditionEmpty, conditionMapWithMoreThanOneCondition)
    assert(returnedQuery2 === expectedQuery2)

    // Query with multiple selectColumn and one conditions
    val expectedQuery3 = s""" SELECT testColumn1,testColumn2 FROM $testTableName WHERE testConditionColumn="testConditionValue" """
    val returnedQuery3 = genericSelectQuery(testTableName, selectingColumnsConditionFewColumns, conditionMapWithOneCondition)
    assert(returnedQuery3 === expectedQuery3)

    //Query with multiple Selection and multiple Condition
    val expectedQuery4 = s""" SELECT testColumn1,testColumn2 FROM $testTableName WHERE testConditionColumn1="testConditionValue1" AND testConditionColumn2="testConditionValue2" """
    val returnedQuery4 = genericSelectQuery(testTableName, selectingColumnsConditionFewColumns, conditionMapWithMoreThanOneCondition)
    assert(returnedQuery4 === expectedQuery4)
  }

  test("It should return the InsertQuery when passed the params and the similar size list for values"){
    val testTableName     = "testTable"
    val allColumnsToEnter = List("column1", "column2", "column3")
    val allValuesToEnter  = List("value1", "value2", "value3")

    val expectedQuery = s""" INSERT INTO $testTableName (column1,column2,column3) VALUES ("value1", "value2", "value3"); """
    val returnedQuery = genericInsertQuery(testTableName, allColumnsToEnter, allValuesToEnter)
    assert(returnedQuery === expectedQuery)
  }

  test("It should return the query to add new user to personal dateTable"){
    val testTableName = "testTable"
    val testEmpId     = 1000
    val testFirstName = "testFirstName"
    val testLastName  = "testLastName"
    val testAge       = 20

    val expectedQuery = s""" INSERT INTO $testTableName (`EmpID`, `FirstName`, `LastName`, `Age`) VALUES ("$testEmpId", "$testFirstName", "$testLastName", $testAge); """
    val returnedQuery = createQueryForAddingNewUserToPersonalDataTable( testTableName,
                                                                        testEmpId,
                                                                        testFirstName,
                                                                        testLastName,
                                                                        testAge)
    assert(returnedQuery === expectedQuery)
  }

  test("It should return the query to add the new user to login data table"){
    val testTableName = "testTable"
    val testEmpID = 1234
    val userName = "testUserName"
    val password = "testPassword"
    val role = "testRole"

    val expectedQuery = s""" INSERT INTO $testTableName (`EmployeeID`, `UserName`, `Password`, `Role`) VALUES ("$testEmpID", "$userName", "$password", "$role"); """
    val returnedQuery = createQueryToAddNewUserToLoginDataTable(testTableName, testEmpID, userName, password, role)

    assert(returnedQuery === expectedQuery)
  }

  test("It Should return the query to add new user to company record table query"){
    val testTableName = "testTable"
    val testEmpID = 1234
    val firstName = "testFirstName"
    val lastName  = "testLastName"
    val role = "testRole"
    val designation = "testDesignation"
    val managerName = "testManagerName"
    val Salary = 0.0

    val expectedQuery = s""" INSERT INTO $testTableName (`empId`, `firstname`, `lastname`, `role`, `designation`, `managername`, `salary`) VALUES ("$testEmpID", "$firstName", "$lastName", "$role", "$designation", "$managerName", "$Salary"); """
    val returnedQuery = createQueryToAddNewUserToCompanyRecordDataTable(testTableName, testEmpID, firstName, lastName, role, designation, managerName, Salary)

    assert(returnedQuery === expectedQuery)
  }

  test("It should return the query to add user to attendanceSystem with given params"){
    val tableName = "testTable"
    val empId = 1000
    val managerName = "testManagerName"
    val attendanceJson = """{"02":{"22":"Not Added","01":"Not Required","23":"Not Added","02":"Not Required","24":"Not Added","03":"Not Required","25":"Not Added","04":"Not Required","26":"Not Added","05":"Not Required","27":"Not Added","06":"Not Required","28":"Not Added","07":"Not Required","08":"Not Required","09":"Not Required","10":"Not Required","11":"Not Required","12":"Not Required","13":"Not Required","14":"Not Required","15":"Not Required","16":"Not Required","17":"Not Required","18":"Not Required","19":"Not Required","20":"Not Added","21":"Not Added"}}"""

    val expectedQuery = s""" INSERT INTO $tableName (`empId`, `managerName`, `dayDetails`) VALUES ("$empId", "$managerName", '$attendanceJson'); """
    val returnedQuery = crateQueryToAddNewUserToAttendanceSystem(tableName, empId, managerName, attendanceJson)

    assert(returnedQuery ===  expectedQuery)
  }

  test("It should return the query to extract the attendance JSon from table"){
    val tableName = "testTable"
    val empId = 1000

    val expectedQuery = s""" SELECT `managerName`, `dayDetails` FROM $tableName WHERE `empId`="$empId" """
    val returnedQuery = queryToRetrieveUserJsonFromAttendanceRecord(tableName, empId)
    assert(returnedQuery === expectedQuery)
  }

  test("It should return the query to update the given attendance Json"){
    val tableName = "testTableName"
    val empId = 1000
    val updatedJson = """{"02":{"22":"Not Added","01":"Not Required","23":"Not Added","02":"Not Required","24":"Not Added","03":"Not Required","25":"Not Added","04":"Not Required","26":"Not Added","05":"Not Required","27":"Not Added","06":"Not Required","28":"Not Added","07":"Not Required","08":"Not Required","09":"Not Required","10":"Not Required","11":"Not Required","12":"Not Required","13":"Not Required","14":"Not Required","15":"Not Required","16":"Not Required","17":"Not Required","18":"Not Required","19":"Not Required","20":"Not Added","21":"Not Added"}}"""

    val expectedQuery = s""" UPDATE $tableName SET `dayDetails` = '$updatedJson' WHERE $tableName.`empId` = $empId; """
    val returnedQuery = queryToUpdateAttendanceJson(tableName, empId, updatedJson)
    assert(returnedQuery === expectedQuery)
  }

}
