package com.example.app.Controllers

object RequestHelpers{

  def getEmployeePersonalDetailsFromString(empDetailsString: String): EmployeeName ={

    val details = empDetailsString.split(',')
    val firstName = details(0)
    val lastName = details(1)
    val age = details(2).toInt
    EmployeeName(firstName, lastName, age)
  }

  def getEmployeeDetailsFromString(empDetails: String): EmployeeDetails ={
    //TODO: Add salary details to the EmployeeDetails, by extracting the params and add it here
    val details = empDetails.split(',')
    details.map(item => item.trim()) match {
      case Array(role, designation, managerName) => EmployeeDetails(role, designation, managerName)
    }
  }

  def convertStringToAttendanceDetails(attendanceDetails: String): AttendanceDetails ={
    val details = attendanceDetails.split(',')
    val empID = details(0).toInt
    val isPresent = details(1).toBoolean
    val date = details(2)
    AttendanceDetails(empID, isPresent, date)
  }

}
