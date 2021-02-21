package com.example.app.Controllers

import org.scalatest.funsuite.AnyFunSuite
import RequestHelpers.{convertStringToAttendanceDetails, getEmployeeDetailsFromString, getEmployeePersonalDetailsFromString}

class RequestHelperSpec extends AnyFunSuite {

  test("When Employee Personal detail string is passed should return EmployeeName Object"){
    val testEmployeeString = "testFirstName,testLastName,20"
    val expectedValue = EmployeeName("testFirstName", "testLastName", 20)

    val returnValue = getEmployeePersonalDetailsFromString(testEmployeeString)
    assert(returnValue === expectedValue)
    assert(returnValue.Age.isInstanceOf[Int])
  }

  test("When Employee detail string is passed it should return the EmployeeDetails Object"){
    val testEmployeeDetail = "testEngineer,testDesignation,testManagerName"
    val expectedObject = EmployeeDetails("testEngineer", "testDesignation", "testManagerName")

    val returnedObject = getEmployeeDetailsFromString(testEmployeeDetail)
    assert(returnedObject === expectedObject)
  }

  test("When Employee Attendance details are passed as String it should return AttendanceDetails Object"){
    val testAttendanceDetailString = "1059,True,02/10/2021"
    val expectedObject = AttendanceDetails(1059, isPresent = true, "02/10/2021")

    val returnedObject = convertStringToAttendanceDetails(testAttendanceDetailString)
    assert(returnedObject === expectedObject)
    assert(returnedObject.EmpID.isInstanceOf[Int])
    assert(returnedObject.isPresent.isInstanceOf[Boolean])
  }

}

