package com.example.app

import org.json4s.{DefaultFormats, Formats}
import org.scalatra.ScalatraServlet
import org.scalatra.json.JacksonJsonSupport
import org.scalatra._
import org.slf4j.{Logger, LoggerFactory}
import com.example.app.Controllers.TestRequestHelper.parseSchedulerBodyContentsAndSave

class TestRequestHandler extends ScalatraServlet with JacksonJsonSupport{
  protected implicit lazy val jsonFormats: Formats = DefaultFormats
  val logger: Logger = LoggerFactory.getLogger(getClass)

  // this is for testing the functionalities

  get("/schedule"){
    logger.info("providing schedule page using twirl in scalatra")
    <h1>schedule page</h1>
  }

  post("/schedule"){
    logger.info("request Made for entering the day records into the database")
    val dateForContent = params("date")
    logger.info(s"content passed for date $dateForContent")
    val bodyContents = request.body
    logger.info(s"body contents passed are $bodyContents")
    parseSchedulerBodyContentsAndSave(dateForContent ,bodyContents)
    response.addHeader("ACK", "Values recorded for entering data")
  }

  get("/dayNotes"){

  }

  post("/dayNotes"){
    
  }


  get("/login"){
    "login page"
  }

  get("/testPage"){
    ""
  }

  notFound{
    <h1>Not found. Bummer.</h1>
  }
}
