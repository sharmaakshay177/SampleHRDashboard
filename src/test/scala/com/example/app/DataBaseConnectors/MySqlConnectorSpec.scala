package com.example.app.DataBaseConnectors

import java.sql.{Connection, Statement}

import org.scalamock.scalatest.MockFactory
import org.scalatest.{BeforeAndAfter}
import org.scalatest.funsuite.AnyFunSuite

class MySqlConnectorSpec extends AnyFunSuite with MockFactory with BeforeAndAfter {

  val dataBaseConnector = new MySqlConnector("localhost", 3306)

  test("It should return the databaseConnection Url with   client provided database Name"){
    val dataBaseName = "testDB"
    val expectedUrl = s"jdbc:mysql://localhost:3306/$dataBaseName"

    val returnedUrl = dataBaseConnector.createUrl(dataBaseName)
    assert(returnedUrl === expectedUrl)
  }

  test("It should return the statement when a connection object is passed"){
    val connection = mock[Connection]
    val statement = mock[Statement]
    (connection.createStatement: () => Statement).expects().returns(statement)

    val returned = dataBaseConnector.createStatement(connection)
    assert(returned.isInstanceOf[Statement])
  }

}
