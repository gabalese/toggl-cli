package client

object Client {
  def main(args: Array[String]) {
    implicit val configuration: ClientConfiguration = new ClientConfiguration()
    val client = new TogglApiWrapper()

    try {
      args match {
        case Array("me", _*) =>
          val user = client.Users.getCurrentUserDetails
          println(s"Fullname: ${user.fullname}, Email: ${user.email}, Joined at: ${user.created_at}")

        case Array("list", _*) =>
          println("List:")

        case Array("get", "current", _*) =>
          val timeEntry = client.TimeEntries.getCurrent
          timeEntry match {
            case Some(entry) => println(timeEntry)
            case None => println("No current entry")
          }

        case Array("get", "last", _*) =>
          val timeEntry = client.TimeEntries.getLast
          timeEntry match {
            case Some(entry) => println(entry)
            case None => println("No last entry")
          }

        case _ => throw new InvalidCommandException(s"Invalid command: ${args(0)}")
      }
    } catch {
      case ex: Exception => throw ex
    }

  }
}

class JsonPrettyPrinter {

}
