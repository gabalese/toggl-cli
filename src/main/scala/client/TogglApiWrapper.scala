package client

import dispatch._
import scala.concurrent.ExecutionContext.Implicits.global

class InvalidCredentialsException(msg: String) extends Exception(msg: String)
class UnableToConnectException(msg: String) extends Exception(msg: String)

class TogglApiWrapper(implicit val configuration: ClientConfiguration) {

  val apiKey: String = configuration.apiKey

  object Users {
    def getCurrentUserDetails: User = {
      val svc = url(Endpoints.userData) as_!(apiKey, "api_token")
      try {
        val response: Future[String] = Http(svc OK as.String)
        val user: User = User.parse(response())
        user
      } catch {
        case StatusCode(403) => throw new InvalidCredentialsException("Invalid Toggl credentials")
        case ex: java.util.concurrent.ExecutionException => throw new UnableToConnectException("Check connection")
      }
    }
  }

  object TimeEntries {
    def getLast: Option[TimeEntry] = {
      val svc = url(Endpoints.currentTimeEntry) as_!(apiKey, "api_token")
      val response: Future[String] = Http(svc OK as.String)
      val timeEntry = TimeEntry.parse(response())
      timeEntry
    }
  }

    def createEntry: TimeEntry = {
      ???
    }

    def get(id: String): TimeEntry = {
      ???
    }


}
