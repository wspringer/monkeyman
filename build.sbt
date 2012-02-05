name := "frontman"

version := "1.0"

scalaVersion := "2.8.1"

libraryDependencies ++= Seq(
  "org.pegdown" % "pegdown" % "1.1.0",
  "net.databinder" %% "unfiltered-filter" % "0.5.0",
  "net.databinder" %% "unfiltered-jetty" % "0.5.0",
  "net.databinder" %% "unfiltered-scalate" % "0.5.0",
  "org.fusesource.scalamd" % "scalamd" % "1.5",
  "org.clapper" %% "avsl" % "0.3.6",
  "joda-time" % "joda-time" % "2.0",
  "eu.medsea.mimeutil" % "mime-util" % "2.1.3",
  "commons-io" % "commons-io" % "2.1",
  "org.joda" % "joda-convert" % "1.2",
  "org.fusesource.scalate" % "scalate-core" % "1.5.2-scala_2.8.1",
  "org.clapper" %% "argot" % "0.3.3",
  "com.ibm.icu" % "icu4j" % "4.8.1.1"
)

resolvers ++= Seq(
  "java m2" at "http://download.java.net/maven/2",
  "snapshots" at "http://scala-tools.org/repo-snapshots",
  "releases"  at "http://scala-tools.org/repo-releases"
)

initialCommands in console := "import java.io._; import nl.flotsam.frontman._"

