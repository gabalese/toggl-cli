package client

object Client {
  def main(args: Array[String]) {
    implicit val configuration: ClientConfiguration = new ClientConfiguration()
    val client = new TogglApiWrapper()

    args match {
      case Array("me", _*) =>
        val user = client.getUser
        println(s"Fullname: ${user.fullname}, Email: ${user.email}, Joined at: ${user.created_at}")

      case Array("list", _*) =>
        println("List:")

      case _ => throw new InvalidCommandException(s"Invalid command: ${args(0)}")
    }
  }
}

class JsonPrettyPrinter {

}
