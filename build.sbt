name := "krabbelsack"

version := "1.0"

scalaVersion := "2.9.1"

resolvers += "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/"
	 
libraryDependencies ++= Seq(
  "se.scalablesolutions.akka" % "akka-actor" % "1.2",
  "se.scalablesolutions.akka" % "akka-typed-actor" % "1.2",
  "se.scalablesolutions.akka" % "akka-amqp" % "1.2",
  "se.scalablesolutions.akka" % "akka-testkit" % "1.2"
)
