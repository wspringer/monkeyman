package nl.flotsam.frontman

object Frontman {

  val tools = Map(
    "generate" -> FrontmanGenerator
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
      println("frontman " + key + " ARGS")
    }
    println()
    println("Type 'frontman TOOL [-h|--help]' for more information.")
    println()
  }

}
