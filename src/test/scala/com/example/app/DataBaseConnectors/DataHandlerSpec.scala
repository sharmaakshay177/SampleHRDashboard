package com.example.app.DataBaseConnectors

import java.sql.{ResultSet, Statement}

import org.scalatest.funsuite.AnyFunSuite
import org.scalamock.scalatest.MockFactory
import org.scalatest.BeforeAndAfter

import DataHandler.{executeUpdateQueryOnDataBase, executeQueryOnDataBase}

class DataHandlerSpec extends AnyFunSuite with MockFactory with BeforeAndAfter{

  test("It should return the resultSet after the executing query on table"){
    val statement = mock[Statement]
    val query     = "testQuery"
    val returnResultSet = mock[ResultSet]
    (statement.executeQuery(_: String)).expects(query).returning(returnResultSet)

    val returned = executeQueryOnDataBase(statement, query)
    assert(returned.isInstanceOf[ResultSet])
  }

  test("It should return the status Int after the execute Update query on table"){
    val statement = mock[Statement]
    val query     = "testQuery"
    val returnResultSet = mock[ResultSet]
    (statement.executeUpdate(_: String)).expects(query).returning(1)

    val returned = executeUpdateQueryOnDataBase(statement, query)
    assert(returned.isInstanceOf[Int])
  }
}
