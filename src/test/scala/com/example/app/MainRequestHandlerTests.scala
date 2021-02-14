package com.example.app

import org.scalatra.test.scalatest._

class MainRequestHandlerTests extends ScalatraFunSuite {

  addServlet(classOf[MainRequestHandler], "/*")

  test("GET / on MainRequestHandler should return status 200") {
    get("/") {
      status should equal (200)
    }
  }
}
