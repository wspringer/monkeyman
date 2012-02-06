package nl.flotsam.monkeyman

object Monkeyman {

  val tools = Map(
    "generate" -> MonkeymanGenerator
  )

  def main(args: Array[String]) {
    if (args.length == 0) {
      printUsage
    } else {
      tools.get(args(0)) match {
        case Some(tool) => tool.main(args.tail)
        case None => printUsage
      }
    }
  }

  def printUsage {
    println("Usage:")
    println()
    for (key <- tools.keys) {
      println("monkeyman " + key + " ARGS")
    }
    println()
    println("Type 'monkeyman TOOL [-h|--help]' for more information.")
    println()
  }

}
