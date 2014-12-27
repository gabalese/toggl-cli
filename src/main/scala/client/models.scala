package client

import net.liftweb.json
import net.liftweb.json._
import net.liftweb.json.JsonDSL._
import org.joda.time._
import org.joda.time.format.{DateTimeFormat, PeriodFormatterBuilder}

trait Duration {
  def getDuration: Int
}

object DurationFormatter {
  val formatter = new PeriodFormatterBuilder()
    .appendHours()
    .appendSuffix(" hour", " hours")
    .appendSeparator(" and ")
    .appendMinutes()
    .appendSuffix(" minute", " minutes")
    .appendSeparatorIfFieldsAfter(", ")
    .appendSeconds()
    .appendSuffix(" second", " seconds")
    .toFormatter
}

object DateFormatter {
  val formatter = DateTimeFormat.forPattern("d MMMM, yyyy")
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
                     tags: Option[List[Any]],
                     description: String) extends Duration {

  def getDuration: Int = stop match {
      case Some(datetime) => (datetime.getMillis - start.getMillis).toInt
      case _ => (DateTime.now.getMillis - start.getMillis).toInt
    }

  def getFormattedDuration: String = {
    val formatter = DurationFormatter.formatter
    val formatted_interval = formatter.print(new Period(getDuration))
    formatted_interval
  }

  override def toString: String = {
    if(stop.isDefined){
      s"[$id] $description, started at $start and completed at ${stop.get}, run for $getFormattedDuration"
    } else {
      s"[$id] $description, started at $start and running for $getFormattedDuration"
    }
  }

  def toJson: String = {
    val jsonMap = ("description" -> description) ~ ("start" -> start.toString(DateFormatter.formatter))
    json.compact(json.render(jsonMap))
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

    val timeEntryData: JValue = json.parse(body) \ "data"

    timeEntryData.children.length match {
      case 0 => None
      case _ => Some(parse(timeEntryData))
    }
  }

  private def parse(timeEntryData: JValue): TimeEntry = {

    TimeEntry(
      (timeEntryData \ "id").extract[Int],
      (timeEntryData \ "pid").extract[Option[Int]],
      (timeEntryData \ "wid").extract[Option[Int]],
      (timeEntryData \ "billable").extract[Option[Boolean]],
      DateTime.parse((timeEntryData \ "start").extract[String]),
      (timeEntryData \ "stop").extract[Option[String]] match {
        case Some(date) => Some(DateTime.parse(date))
        case None => None
      },
      (timeEntryData \ "created_with").extractOpt[String],
      (timeEntryData \ "tags").extract[Option[List[Any]]],
      (timeEntryData \ "description").extract[String]
    )
  }

  def parseMultiple(body: String): Option[List[TimeEntry]] = {
    val timeEntriesData: JValue = json.parse(body)
    if(timeEntriesData.children.toList.length == 0)
      return None

    val timeEntries: List[TimeEntry] = for(entry <- timeEntriesData.children.toList) yield parse(entry)
    Some(timeEntries)
  }

  def create(description: String): TimeEntry = {
    TimeEntry(0, None, None, None, DateTime.now, None, Some("Toggl CLI"), None, description)
  }
}
