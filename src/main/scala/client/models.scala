package client

import org.scala_tools.time.Imports._

import scala.util.parsing.json.JSON


case class User(fullname: String, email: String, created_at: String)

case class Task(name: String, start_time: DateTime)

object UserParser {
  def parse(body: String): User = {
    val json: Option[Any] = JSON.parseFull(body)
    val map: Map[String, Any] = json.get.asInstanceOf[Map[String, Any]]
    val data: Map[String, Any] = map.get("data").get.asInstanceOf[Map[String, String]]

    User(
      data.get("fullname").get.asInstanceOf[String],
      data.get("email").get.asInstanceOf[String],
      data.get("created_at").get.asInstanceOf[String]
    )

  }
}
