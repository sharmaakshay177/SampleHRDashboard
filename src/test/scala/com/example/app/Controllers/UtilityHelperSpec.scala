package com.example.app.Controllers

import java.sql.ResultSet
import java.time.LocalDate

import org.scalatest.funsuite.AnyFunSuite
import org.scalamock.scalatest.MockFactory
import UtilityHelper.{addEmployeeLeavesToAttendanceJson, convertDate, extractJsonAndUpdate, getDatesBetweenFromAndToDate, getTheMonthDateJson, numToString}

class UtilityHelperSpec extends AnyFunSuite with MockFactory {

  test("When a number passed should convert it to String if less" +
    "then 10 should append 0 before the number"){
    val testNumber1 = 9
    val testNumber2 = 23
    //Expected
    val expectedString1 = "09"
    val expectedString2 = "23"
    //Returned
    val returnedString1 = numToString(testNumber1)
    val returnedString2 = numToString(testNumber2)

    assert(returnedString1 === expectedString1)
    assert(returnedString2 === expectedString2)
  }

  test("When the date is passed in mm/dd/yyyy format it should convert to  yyyy/mm/dd string"){
    val testDate = "02/10/2021"
    val expectedFormat = "2021-02-10"

    val returnedFormat = convertDate(testDate)
    assert(returnedFormat === expectedFormat)
  }

  test("When two dates in the string formats are passed it should return the list of all the " +
    "dates in between that range exclusive of the end date"){
    val startDateString = "2021-02-19"
    val endDateString   = "2021-02-21"

    val expectedObject: List[AnyRef] = List[AnyRef](LocalDate.parse(startDateString),
                                                    LocalDate.parse("2021-02-20"))

    val returnedObject = getDatesBetweenFromAndToDate(startDateString, endDateString)
    assert(returnedObject === expectedObject)
  }

  test("When the method is called it should return the json containing all the days " +
    "and marked Not added for all the dates after the current date and Not required for all the dates" +
    "before the current date"){
    //TODO: this method will break, modify this method to receive the date and then generate the json
    val expectedJsonResult = """{"02":{"22":"Not Added","01":"Not Required","23":"Not Added","02":"Not Required","24":"Not Added","03":"Not Required","25":"Not Added","04":"Not Required","26":"Not Added","05":"Not Required","27":"Not Added","06":"Not Required","28":"Not Added","07":"Not Required","08":"Not Required","09":"Not Required","10":"Not Required","11":"Not Required","12":"Not Required","13":"Not Required","14":"Not Required","15":"Not Required","16":"Not Required","17":"Not Required","18":"Not Required","19":"Not Required","20":"Not Added","21":"Not Added"}}"""

    val returnedObject = getTheMonthDateJson()
    assert(returnedObject === expectedJsonResult)
  }
  //TODO: Move json to resource file extract from there
  test("It should update the attendance of the employee in the json and create json again to write" +
    "for the passed date by the given manager"){
    val testManagerName  = "testManager"
    val testDate         = "02/20/2021"
    val testPresentParam = true
    val resultSetMock    = mock[ResultSet]

    val testDayDetails     = """{"02":{"22":"Not Added","01":"Not Required","23":"Not Added","02":"Not Required","24":"Not Added","03":"Not Required","25":"Not Added","04":"Not Required","26":"Not Added","05":"Not Required","27":"Not Added","06":"Not Required","28":"Not Added","07":"Not Required","08":"Not Required","09":"Not Required","10":"Not Required","11":"Not Required","12":"Not Required","13":"Not Required","14":"Not Required","15":"Not Required","16":"Not Required","17":"Not Required","18":"Not Required","19":"Not Required","20":"Not Added","21":"Not Added"}}"""
    val expectedJsonString = """{"02":{"22":"Not Added","01":"Not Required","23":"Not Added","02":"Not Required","24":"Not Added","03":"Not Required","25":"Not Added","04":"Not Required","26":"Not Added","05":"Not Required","27":"Not Added","06":"Not Required","28":"Not Added","07":"Not Required","08":"Not Required","09":"Not Required","10":"Not Required","11":"Not Required","12":"Not Required","13":"Not Required","14":"Not Required","15":"Not Required","16":"Not Required","17":"Not Required","18":"Not Required","19":"Not Required","20":"Present","21":"Not Added"}}"""

    (resultSetMock.next: () => Boolean).expects().returning(true).once()
    (resultSetMock.getString(_: String)).expects("managerName").returning(testManagerName)
    (resultSetMock.getString(_: String)).expects("dayDetails").returning(testDayDetails)
    (resultSetMock.next: () => Boolean).expects().returning(false)

    val returnedJsonString = extractJsonAndUpdate(testManagerName, testDate, testPresentParam, resultSetMock)
    assert(returnedJsonString === expectedJsonString)
  }

  test("It Should return the Leave Applied/Marked Json when passed the startDate and endDate and recordJson"){
    val startDateString = "02/20/2021"
    val endDateString   = "02/22/2021"
    val expectedJsonResult = """{"02":{"22":"Not Added","01":"Not Required","23":"Not Added","02":"Not Required","24":"Not Added","03":"Not Required","25":"Not Added","04":"Not Required","26":"Not Added","05":"Not Required","27":"Not Added","06":"Not Required","28":"Not Added","07":"Not Required","08":"Not Required","09":"Not Required","10":"Not Required","11":"Not Required","12":"Not Required","13":"Not Required","14":"Not Required","15":"Not Required","16":"Not Required","17":"Not Required","18":"Not Required","19":"Not Required","20":"Leave","21":"Leave"}}"""

    val testManagerName  = "testManager"
    val resultSetMock    = mock[ResultSet]
    val testDayDetails     = """{"02":{"22":"Not Added","01":"Not Required","23":"Not Added","02":"Not Required","24":"Not Added","03":"Not Required","25":"Not Added","04":"Not Required","26":"Not Added","05":"Not Required","27":"Not Added","06":"Not Required","28":"Not Added","07":"Not Required","08":"Not Required","09":"Not Required","10":"Not Required","11":"Not Required","12":"Not Required","13":"Not Required","14":"Not Required","15":"Not Required","16":"Not Required","17":"Not Required","18":"Not Required","19":"Not Required","20":"Not Added","21":"Not Added"}}"""
    (resultSetMock.next: () => Boolean).expects().returning(true).once()
    (resultSetMock.getString(_: String)).expects("managerName").returning(testManagerName)
    (resultSetMock.getString(_: String)).expects("dayDetails").returning(testDayDetails)
    (resultSetMock.next: () => Boolean).expects().returning(false)

    val returnedJsonString = addEmployeeLeavesToAttendanceJson(startDateString, endDateString, resultSetMock)
    assert(returnedJsonString === expectedJsonResult)
  }

}
