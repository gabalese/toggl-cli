package client

import dispatch._

import scala.concurrent.ExecutionContext.Implicits.global

class InvalidCredentialsException(msg: String) extends Exception(msg: String)
class UnableToConnectException(msg: String) extends Exception(msg: String)

class TogglApiWrapper(implicit val configuration: ClientConfiguration) {

  val api = new Requester(configuration.apiKey)

  object Users {

    def getCurrentUserDetails: User = {
      val response = api.makeRequest(Endpoints.userData)
        val user: User = User.parse(response())
        user
    }
  }

  object TimeEntries {

    def getCurrent: Option[TimeEntry] = {
      val response = api.makeRequest(Endpoints.currentTimeEntry)
      val timeEntry = TimeEntry.parse(response())
      timeEntry
    }

    def stopCurrent: Option[TimeEntry] = {
      val currentEntry = getCurrent match {
        case Some(entry) => entry
        case None => return None
      }
      val response = api.makeRequest(Endpoints.stopEntry(currentEntry.id))
      val timeEntry = TimeEntry.parse(response())
      timeEntry
    }

    def getLast: Option[TimeEntry] = {
      getLatestTimeEntries match {
        case Some(x) => Some(x.reverse.head)
        case None => None
      }
    }

    def getLast(number: Int): Option[List[TimeEntry]] = {
      getLatestTimeEntries match {
        case Some(x) => Some(x.slice(0, number))
        case None => None
      }
    }

    private def getLatestTimeEntries: Option[List[TimeEntry]] = {
      val response = api.makeRequest(Endpoints.timeEntries)
      val timeEntries = TimeEntry.parseMultiple(response())
      timeEntries
    }

    def resumeLast: Option[TimeEntry] = {
      ???
    }

    def createEntry: TimeEntry = {
      ???
    }

    def get(id: String): TimeEntry = {
      ???
    }

  }
}

class Requester(apiKey: String) {

  def makeRequest(endpoint: String): Future[String] = {
    val svc = url(endpoint) as_!(apiKey, "api_token")
    val response: Future[String] = Http(svc OK as.String)
    response
  }
}
