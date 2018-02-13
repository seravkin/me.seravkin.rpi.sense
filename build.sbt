name := "me.seravkin.rpi.sense"

version := "0.1"

scalaVersion := "2.12.4"

scalacOptions += "-Ypartial-unification"

resolvers += Resolver.sonatypeRepo("releases")

libraryDependencies ++= Seq(
  "org.typelevel" %% "cats-effect" % "0.5",
  "org.typelevel" %% "cats-core" % "1.0.1",
  "org.typelevel" %% "cats-free" % "1.0.1",
  "com.github.pathikrit" %% "better-files" % "3.4.0",
  "org.scalactic" %% "scalactic" % "3.0.4" % "test",
  "org.scalatest" %% "scalatest" % "3.0.4" % "test"
)