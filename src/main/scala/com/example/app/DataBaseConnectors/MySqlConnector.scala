package com.example.app.DataBaseConnectors

import java.sql.{Connection, DriverManager, Statement}
import org.slf4j.{Logger, LoggerFactory}

import com.example.app.Controllers.GlobalHelpers

class MySqlConnector(Host: String, Port: Int) {

  private val logger: Logger = LoggerFactory.getLogger(getClass)
  private val config = GlobalHelpers.getConfig
  private val AllDrivers = config.get("Drivers") match {
    case Some(value) => value.asInstanceOf[Map[String, Any]]
  }
  private val Driver = AllDrivers.getOrElse("SQLDriver", "None Found").toString
  private val userName = config.getOrElse("UserName", "None").toString
  private val password = config.getOrElse("Password", "None").toString

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
