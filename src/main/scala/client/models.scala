package client

import net.liftweb.json

case class User(fullname: String, email: String, created_at: String)
case class Task(name: String, start_time: String)

object UserParser {
  implicit val formats = net.liftweb.json.DefaultFormats

  def parse(body: String): User = {
    val userData: json.JValue = json.parse(body) \ "data"

    User(
      (userData \ "fullname").extract[String],
      (userData \ "email").extract[String],
      (userData \ "created_at").extract[String]
    )

  }
}
