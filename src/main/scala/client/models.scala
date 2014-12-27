package client

import net.liftweb.json
import org.joda.time._
import org.joda.time.format.PeriodFormatterBuilder

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
                     tags: Option[List[Any]],
                     description: String) extends Duration {

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
      case _ => Some(parse(timeEntryData))
    }
  }

  private def parse(timeEntryData: json.JValue): TimeEntry = {

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
    val timeEntriesData: json.JValue = json.parse(body)
    if(timeEntriesData.children.toList.length == 0)
      return None

    val timeEntries: List[TimeEntry] = for(entry <- timeEntriesData.children.toList) yield parse(entry)
    Some(timeEntries)
  }
}
