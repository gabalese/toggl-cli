package client

import net.liftweb.json
import org.joda.time._

trait Duration {
  def getDuration: Int
}

case class User(fullname: String,
                email: String,
                created_at: DateTime)

case class TimeEntry(id: Int,
                     pid: Option[Int],
                     wid: Option[Int],
                     billable: Option[Boolean],
                     start: DateTime, stop: Option[DateTime],
                     created_with: Option[String],
                     tags: Option[List[Any]]) extends Duration {

  def getDuration: Int = stop match {
      case Some(datetime) => (datetime.getMillis - start.getMillis).toInt
      case _ => 0
    }
}


object User {
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

object TimeEntry {
  implicit val formats = net.liftweb.json.DefaultFormats

  def parse(body: String): Option[TimeEntry] = {

    val timeEntryData: json.JValue = json.parse(body) \ "data"

    timeEntryData.children.length match {
      case 0 => None
      case _ => Some(
          TimeEntry(
          (timeEntryData \ "id").extract[Int],
          (timeEntryData \ "pid").extract[Option[Int]],
          (timeEntryData \ "wid").extract[Option[Int]],
          (timeEntryData \ "billable").extract[Option[Boolean]],
          DateTime.parse((timeEntryData \ "start").extract[String]),
          (timeEntryData \ "stop").extract[Option[DateTime]],
          (timeEntryData \ "created_with").extractOpt[String],
          (timeEntryData \ "tags").extract[Option[List[Any]]]
        )
      )
    }
  }
}
