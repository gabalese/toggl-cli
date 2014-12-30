package it.alese.toggl

class ImproperlyConfiguredException(msg: String) extends Exception(msg: String)
class InvalidCommandException(msg: String) extends Exception(msg: String)

class ClientConfiguration {
  val apiKey = sys.env.get("TOGGL_KEY") match {
    case Some(key) => key
    case None => throw new ImproperlyConfiguredException("Cannot find any key in sys.env")
  }
  val clientName: String = "Toggl CLI Client"
}

object Endpoints {
  val userData: String = "https://www.toggl.com/api/v8/me"
  val currentTimeEntry: String = "https://www.toggl.com/api/v8/time_entries/current"
  val timeEntries: String = "https://www.toggl.com/api/v8/time_entries"
  def stopEntry(id: Int): String = {
    s"https://www.toggl.com/api/v8/time_entries/$id/stop"
  }
  val createEntry: String = "https://www.toggl.com/api/v8/time_entries/start"
  def getEntry(id: Int): String = {
    s"https://www.toggl.com/api/v8/time_entries/$id"
  }
}
