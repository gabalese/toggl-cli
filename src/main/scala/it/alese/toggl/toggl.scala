package it.alese.toggl

object Toggl {
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
            case Some(entry) => println(entry)
            case None => println("No current entry")
          }

        case Array("get", "last", x, _*) =>
          val timeEntries = client.TimeEntries.getLast(x.toInt)
          timeEntries match {
            case Some(entries) => entries filterNot(_.isCurrent) foreach println
            case None => println("No latest entries")
          }

        case Array("get", "last", _*) =>
          val timeEntry = client.TimeEntries.getLast
          timeEntry match {
            case Some(entry) => println(entry)
            case None => println("No last entry")
          }

        case Array("stop", "current", _*) =>
          val timeEntry = client.TimeEntries.stopCurrent
          timeEntry match {
            case Some(entry) => println(s"Stopped entry $entry")
            case None => println("No entry to stop")
          }

        case Array("start", "last") =>
          val lastEntry = client.TimeEntries.getLast.get
          val confirmation = client.TimeEntries.resumeLast
          confirmation match {
            case Some(entry: TimeEntry) => println(entry)
            case None => throw new InvalidCommandException("No last entry available")
          }

        case Array("start", description@_*) =>
          val confirmation = client.TimeEntries.createEntry(description.mkString(" "))
          confirmation match {
            case Some(entry: TimeEntry) => println(entry)
            case None => None
          }

        case _ => throw new InvalidCommandException(s"Invalid command: ${args(0)}")
      }
    } catch {
      case ex: IndexOutOfBoundsException => println("Insert command")
      case ex: Exception => throw ex
    } finally {
      sys.exit()
    }

  }
}
