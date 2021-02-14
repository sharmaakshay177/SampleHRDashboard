package com.example.app.DataBaseConnectors

import java.sql.{Connection, DriverManager, Statement}

import org.slf4j.{Logger, LoggerFactory}

class MySqlConnector(Host: String, Port: Int) {

  private val logger: Logger = LoggerFactory.getLogger(getClass)

  private val Driver: String = "com.mysql.jdbc.Driver"
  //TODO - Move it to config file and extract from there
  private val userName: String = "root"
  private val password: String = "root"

  def createUrl(dataBaseName: String): String ={
    logger.info(s"creating Url for provided host:$Host, port:$Port and dataBase:$dataBaseName")
    s"jdbc:mysql://$Host:$Port/$dataBaseName"
  }

  def createConnection(url: String): Either[Connection, Exception] ={
    try{
      Class.forName(Driver)
      val connection: Connection = DriverManager.getConnection(url, userName, password)
      Left(connection)
    }catch{
      case exception: Exception =>
        logger.error("Error encountered During Connection creation")
        Right(exception)
    }
  }

  def createStatement(connection: Connection): Statement = connection.createStatement()

}
