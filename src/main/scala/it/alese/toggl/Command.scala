package it.alese.toggl

abstract class Command {
  implicit val configuration: ClientConfiguration = new ClientConfiguration()
  val client = new TogglApiWrapper()
  def handle(): Unit
}

object Command {
  def mkFromString(cmd: String): Command = {
    ???
  }
}

object GetUserInformation extends Command {
  def handle(): Unit = {
    val user = client.Users.getCurrentUserDetails
    println(s"Fullname: ${user.fullname}, Email: ${user.email}, Joined at: ${user.created_at}")
  }
}

object GetCurrentTask extends Command {
  def handle(): Unit = {
    val timeEntry = client.TimeEntries.getCurrent
    timeEntry match {
      case Some(entry) => println(entry)
      case None => println("No current entry")
    }
  }
}

object GetMostRecentTask extends Command {
  def handle(): Unit = {
    // Fetches the most recent task
    val timeEntry = client.TimeEntries.getLast
    timeEntry match {
      case Some(entry) => println(entry)
      case None => println("No last entry")
    }
  }
}

object GetRecentTasks extends Command {
  def handle(num: Int = 0): Unit = {
    val timeEntries = client.TimeEntries.getLast(num)
    timeEntries match {
      case Some(entries) => entries filterNot (_.isCurrent) foreach println
      case None => println("No latest entries")
    }
  }
  def handle() = () // Eeewww
}

object StopCurrentTask extends Command {
  def handle() = {
    val timeEntry = client.TimeEntries.stopCurrent
    timeEntry match {
      case Some(entry) => println(s"Stopped entry $entry")
      case None => println("No entry to stop")
    }
  }
}

object StartLastTask extends Command {
  def handle() = {
    val lastEntry = client.TimeEntries.getLast.get
    val confirmation = client.TimeEntries.resumeLast
    confirmation match {
      case Some(entry: TimeEntry) => println(entry)
      case None => throw new InvalidCommandException("No last entry available")
    }
  }
}

object StartNewTask extends Command {
  def handle() = () // Eeeww^2
  def handle(description: String) = {
    val confirmation = client.TimeEntries.createEntry(description)
    confirmation match {
      case Some(entry: TimeEntry) => println(entry)
      case None => None
    }
  }
}
