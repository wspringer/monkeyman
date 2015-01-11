name := "monkeyman"

version := "0.3.1"

scalaVersion := "2.10.4"

libraryDependencies ++= Seq(
  "com.google.guava" % "guava" % "18.0",
  "org.pegdown" % "pegdown" % "1.1.0",
  "org.fusesource.scalamd" % "scalamd" % "1.5",
  "joda-time" % "joda-time" % "2.0",
  "commons-io" % "commons-io" % "2.4",
  "org.joda" % "joda-convert" % "1.2",
  "org.scalatra.scalate" % "scalate-core_2.10" % "1.7.0",
  "org.scala-lang" % "scala-library" % "2.10.4",
  "org.clapper" %% "argot" % "1.0.3",
  "com.ibm.icu" % "icu4j" % "4.8.1.1",
  "ch.qos.logback" % "logback-core" % "1.0.0",
  "ch.qos.logback" % "logback-classic" % "1.0.0",
  "com.asual.lesscss" % "lesscss-engine" % "1.1.5",
  "org.yaml" % "snakeyaml" % "1.10",
  "org.jsoup" % "jsoup" % "1.6.3",
  "net.java.dev.jets3t" % "jets3t" % "0.9.0"
)

initialCommands in console := "import java.io._; import nl.flotsam.monkeyman._"

mainClass in (Compile, run) := Some("nl.flotsam.monkeyman.Monkeyman")

mainClass in (Compile, packageBin) := Some("nl.flotsam.monkeyman.Monkeyman")

mainClass in assembly := Some("nl.flotsam.monkeyman.Monkeyman")

jarName in assembly := "monkeyman.jar"

fork in run := true

connectInput in run := true

compileOrder := CompileOrder.JavaThenScala
