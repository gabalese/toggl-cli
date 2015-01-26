package it.alese.toggl

object Main {
  def main(args: Array[String]) {

    try {
      args match {
        case Array("me", _*) =>
          GetUserInformation.handle()

        case Array("get", "current", _*) =>
          GetUserInformation.handle()

        case Array("get", "last", x, _*) =>
          GetRecentTasks.handle(x.toInt)

        case Array("get", "last", _*) =>
          GetMostRecentTask.handle()

        case Array("get", _*) =>
          GetMostRecentTask.handle()

        case Array("stop", "current", _*) =>
          StopCurrentTask.handle()

        case Array("stop", _*) =>
          StopCurrentTask.handle()

        case Array("start", "last") =>
          StartLastTask.handle()

        case Array("start", description@_*) =>
          StartNewTask.handle(description.mkString(" "))

        case Array("start") =>
          StartLastTask.handle()

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
