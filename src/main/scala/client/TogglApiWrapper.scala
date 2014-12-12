package client

import dispatch._

import scala.concurrent.ExecutionContext.Implicits.global

class InvalidCredentialsException(msg: String) extends Exception(msg: String)

class TogglApiWrapper(implicit val configuration: ClientConfiguration) {
  val apiKey: String = configuration.apiKey

  def getUser: User = {
    val svc = url(Endpoints.userData) as_! (apiKey, "api_token")
    try {
        val response: Future[String] = Http(svc OK as.String)
        val user: User = UserParser.parse(response())
        user
    } catch {
      case StatusCode(403) => throw new InvalidCredentialsException("Invalid Toggl credentials")
    }
  }

  def getCurrentTask: Option[Task] = {
    ???
  }

  def getTasks: List[Task] = {
    ???
  }

  def getLastTask: Option[Task] = {
    ???
  }

}
