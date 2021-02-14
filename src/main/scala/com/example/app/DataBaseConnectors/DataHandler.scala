package com.example.app.DataBaseConnectors

import java.sql.{ResultSet, Statement}
import org.slf4j.{Logger, LoggerFactory}

object DataHandler {

  private val logger: Logger = LoggerFactory.getLogger(getClass)

  def executeQueryOnDataBase(statement: Statement, query: String): ResultSet ={
    logger.debug(s"query passed : $query")
    val resultSet = statement.executeQuery(query)
    resultSet
  }

  def executeUpdateQueryOnDataBase(statement: Statement, query: String): Int ={
    // use this only the sql statement for which you expect a row expected count, like Insert, update and delete statements
    logger.debug(s"query passed : $query executing it")
    val result = statement.executeUpdate(query)
    result
  }

}
