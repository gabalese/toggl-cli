package it.alese.toggl

import dispatch._

import scala.concurrent.ExecutionContext.Implicits.global

class InvalidCredentialsException(msg: String) extends Exception(msg: String)
class UnableToConnectException(msg: String) extends Exception(msg: String)
class MalformedRequestException(msg: String) extends Exception(msg: String)

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

    def createEntry(description: String): Option[TimeEntry] = {
      val timeEntry: TimeEntry = TimeEntry.newFromDescription(description)
      val response: Future[Either[String, String]] = api.postData(Endpoints.createEntry, s""" {"time_entry": ${timeEntry.toJson}} """)
      response().fold(
        error => throw new MalformedRequestException(error),
        result => TimeEntry.parse(result)
      )
    }

    def resumeLast: Option[TimeEntry] = {
      val last = getLast.get
      val timeEntry = TimeEntry.newFromExisting(last)
      val response: Future[Either[String, String]] = api.postData(Endpoints.createEntry, s""" {"time_entry": ${timeEntry.toJson}} """)
      response().fold(
        error => throw new MalformedRequestException(error),
        result => TimeEntry.parse(result)
      )
    }

    def get(id: String): Option[TimeEntry] = {
      val response = api.makeRequest(Endpoints.getEntry(id.toInt))
      val timeEntry = TimeEntry.parse(response())
      timeEntry
    }
  }

  class Requester(apiKey: String) {

    def makeRequest(endpoint: String): Future[String] = {
      val svc = url(endpoint) as_!(apiKey, "api_token")
      Http(svc).map {
        response => response.getStatusCode match {
          case 200 => response.getResponseBody
          case _ => throw new MalformedRequestException(s"[${response.getStatusText}], ${response.getResponseBody}")
        }
      }
    }

    def postData(endpoint: String, json: String): Future[Either[String, String]] = {
      val svc = url(endpoint) as_!(apiKey, "api_token")
      Http(svc << json).map {
        response => response.getStatusCode match {
          case 200 => Right(response.getResponseBody)
          case 201 => Right(response.getResponseBody)
          case _ => Left(s"[${response.getStatusText}], ${response.getResponseBody}")
        }
      }
    }
  }
}
