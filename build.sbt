name := "me.seravkin.rpi.sense"

version := "0.2"

scalaVersion := "2.12.4"

scalacOptions += "-Ypartial-unification"

resolvers += Resolver.sonatypeRepo("releases")

licenses += ("MIT", url("http://opensource.org/licenses/MIT"))

addCompilerPlugin("com.olegpy" %% "better-monadic-for" % "0.2.4")
addCompilerPlugin("org.spire-math" %% "kind-projector" % "0.9.8")

libraryDependencies ++= Seq(
  "org.typelevel" %% "cats-effect" % "1.0.0",
  "org.typelevel" %% "cats-core" % "1.4.0",
  "com.github.pathikrit" %% "better-files" % "3.4.0",
  "org.scalactic" %% "scalactic" % "3.0.4" % "test",
  "org.scalatest" %% "scalatest" % "3.0.4" % "test"
)