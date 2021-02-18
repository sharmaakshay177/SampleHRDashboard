package com.example.app.Controllers

import org.yaml.snakeyaml.Yaml
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import com.fasterxml.jackson.databind.{JsonNode, ObjectMapper}

import scala.collection.mutable
import scala.jdk.CollectionConverters._

object GlobalHelpers {
  private val ConfigFileName: String = "/config.yaml"

  private def returnConfigYaml(): JsonNode ={
    val inputStream  = getClass.getResourceAsStream(ConfigFileName)
    val yamlFile     = new Yaml()

    val mapper  = new ObjectMapper().registerModules(DefaultScalaModule)
    val yamlObj = yamlFile.loadAs(inputStream, classOf[Any])

    val jsonString = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(yamlObj)
    val jsonObj    = mapper.readTree(jsonString)
    jsonObj
  }

  private def convertJsonNodeToScalaMap(jsonNode: JsonNode): Map[String, Any] ={
    // You can also get values from jsonNode using get method and then further traversing it
    // if there is container node in it.
    val map      = mutable.LinkedHashMap[String, Any]()
    val jsonIterate = jsonNode.fields()
    jsonIterate.forEachRemaining(item => {
      if(item.getValue.isBoolean)       map.update(item.getKey.toString, item.getValue.asBoolean())
      else if(item.getValue.isTextual)       map.update(item.getKey.toString, item.getValue.asText())
      else if(item.getValue.isInt)           map.update(item.getKey.toString, item.getValue.asInt())
      else if(item.getValue.isArray)         map.update(item.getKey.toString, item.getValue.asScala.toList)
      else if(item.getValue.isContainerNode) map.update(item.getKey.toString, convertJsonNodeToScalaMap(item.getValue))
    })
    map.toMap
  }

  def getConfig: Map[String, Any] ={
    val readYaml  = returnConfigYaml()
    val converted = convertJsonNodeToScalaMap(readYaml)
    converted
  }

}
