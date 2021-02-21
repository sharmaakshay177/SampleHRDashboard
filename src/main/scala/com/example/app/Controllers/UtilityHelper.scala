package com.example.app.Controllers

import java.sql.ResultSet
import java.time.{LocalDate, YearMonth}
import java.util.Calendar
import java.time.temporal.ChronoUnit
import java.util.stream.Collectors

import scala.collection.mutable
import org.json4s.{DefaultFormats, Formats}
import org.json4s.jackson.Serialization.write
import org.json4s.jackson.JsonMethods._
import org.slf4j.{Logger, LoggerFactory}


object UtilityHelper {
  protected implicit val jsonFormats: Formats = DefaultFormats
  private val logger: Logger = LoggerFactory.getLogger(getClass)

  def numToString(day: Int): String ={
    if(day / 10 == 0) s"0$day"
    else day.toString
  }

  //TODO: modify this method for fixing the test
  def getTheMonthDateJson(): String ={
    val dateToday = Calendar.getInstance.get(Calendar.DATE)
    val currentYear = Calendar.getInstance.get(Calendar.YEAR)
    val currentMonth = Calendar.getInstance.get(Calendar.MONTH) + 1  // as of Calender utility the month is returned from 0 so adding 1 for general.
    val noOfDays = YearMonth.of(currentYear, currentMonth).lengthOfMonth()

    val month = numToString(currentMonth)

    val hashMap = mutable.HashMap[String, String]()
    (1 to noOfDays).toList.map(item => {
      if(item <= dateToday) hashMap.put(numToString(item), "Not Required")
      else hashMap.put(numToString(item),"Not Added")
    })

    val finalMap = mutable.HashMap[String, mutable.HashMap[String, String]](month -> hashMap)
    val json = write(finalMap)
    logger.info(s"Json Created : $json")
    json
    }

  def extractJsonAndUpdate(requestManagerName: String, date: String, isPresent: Boolean, resultSet: ResultSet): String ={
    var managerName, jsonFromSql = ""
    while(resultSet.next()){
      managerName = resultSet.getString("managerName")
      jsonFromSql = resultSet.getString("dayDetails")
    }
    logger.info(s"ManagerName and Json From Result Set:  $managerName, $jsonFromSql")
    val month = date.split('/')(0)
    val day = date.split('/')(1)

    var jsonToWrite = ""
    if(requestManagerName.contentEquals(managerName)){
      val json = parse(jsonFromSql).extract[mutable.HashMap[String,mutable.HashMap[String, String]]]
      logger.info(s"Json parsed from the Result Set:  $json")

      val daysByMonth = json.getOrElse(month, mutable.HashMap.empty)
      logger.info(s"HashMap Extracted : $daysByMonth")

      if(isPresent) daysByMonth.update(day, "Present")
      else daysByMonth.update(day, "Absent")

      json.update(month, daysByMonth)
      jsonToWrite = write(json)
      logger.info(s"New Json Updated: $jsonToWrite")
    }
    jsonToWrite
  }

  def convertDate(date: String): String ={
    //given format will be mm/dd/yyyy convert to yyyy-mm-dd
    val dateSplit = date.split('/')
    val month = dateSplit(0)
    val day = dateSplit(1)
    val year = dateSplit(2)
    s"$year-$month-$day"
  }

  def getDatesBetweenFromAndToDate(startDate: String, endDate: String): List[AnyRef] ={
    val localStartDate = LocalDate.parse(startDate)
    val localEndDate   = LocalDate.parse(endDate)
    val dates = {
      localStartDate.datesUntil(localEndDate).collect(Collectors.toList()).toArray.toList
    }
    dates
  }

  def addEmployeeLeavesToAttendanceJson(startDate: String,
                                        endDate: String,
                                        resultSet: ResultSet): String ={

    val startMonth = startDate.split('/')(0)
    val startDay   = startDate.split('/')(1)


    val convertedStartDate = convertDate(startDate)
    val convertedEndDate   = convertDate(endDate)
    // get no of days between two dates
    val noOfDays = ChronoUnit.DAYS.between(LocalDate.parse(convertedStartDate), LocalDate.parse(convertedEndDate))

    var jsonFromSql = ""
    while(resultSet.next()){
      val managerName = resultSet.getString("managerName")
      // get json from result  set
      jsonFromSql = resultSet.getString("dayDetails")
    }

    val json = parse(jsonFromSql).extract[mutable.HashMap[String,mutable.HashMap[String, String]]]
    logger.info(s"Json parsed from the Result Set:  $json")

    if(noOfDays == 0) {
      val monthJson = json.getOrElse(startMonth, mutable.HashMap.empty)
      monthJson.update(startDay, "Leave")
      logger.info(s"Monthly Json Updated is : $monthJson")
      json.update(startMonth, monthJson)
      logger.info(s"The Entire Updated Json for the single Day: $json")
    }
    else {
      val listOfDaysBetweenStartAndEndDate = getDatesBetweenFromAndToDate(convertedStartDate, convertedEndDate)
      listOfDaysBetweenStartAndEndDate.foreach(item => {
        val monthLoc = item.toString.split('-')(1)
        val dayLoc   = item.toString.split('-')(2)
        val monthJson = json.getOrElse(monthLoc, mutable.HashMap.empty)
        monthJson.update(dayLoc, "Leave")
        logger.info(s"Monthly Json Updated is : $monthJson")
        json.update(monthLoc, monthJson)
      })
      logger.info(s"The Entire Updated Json for the Multiple days or Months: $json")
    }

    val jsonToWrite =  write(json)
    jsonToWrite // changed
  }


}

