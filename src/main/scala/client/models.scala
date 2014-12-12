package client

import net.liftweb.json
import java.text.{ParsePosition, FieldPosition, DateFormat}

import org.joda.time.DateTime

case class User(fullname: String, email: String, created_at: DateTime)
case class Task(name: String, start_time: String)

object UserParser {
  implicit val formats = net.liftweb.json.DefaultFormats

  def parse(body: String): User = {
    val userData: json.JValue = json.parse(body) \ "data"

    User(
      (userData \ "fullname").extract[String],
      (userData \ "email").extract[String],
      DateTime.parse((userData \ "created_at").extract[String])
    )

  }
}
