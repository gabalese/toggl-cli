package client

class ImproperlyConfiguredException(msg: String) extends Exception(msg: String)
class InvalidCommandException(msg: String) extends Exception(msg: String)

class ClientConfiguration {
  val apiKey = sys.env.get("TOGGL_KEY") match {
    case Some(key) => key
    case _ => throw new ImproperlyConfiguredException("Cannot find any key in sys.env")
  }
}

object Endpoints {
    val userData: String = "https://www.toggl.com/api/v8/me"
}
