
resolvers += Resolver.jcenterRepo
resolvers += Resolver.bintrayRepo("neelsmith", "maven")

scalaVersion := "2.12.4"
libraryDependencies ++= Seq(
  "edu.holycross.shot.cite" %% "xcite" % "3.7.0",
  "edu.holycross.shot" %% "ohco2" % "10.11.1",
  "edu.holycross.shot"  %% "latphone" % "2.2.2",
  "edu.holycross.shot" %% "midvalidator" % "5.2.1",

  "edu.holycross.shot" %% "tabulae" % "1.0.0",
  "com.github.pathikrit" %% "better-files" % "3.5.0",
  "com.jsuereth" %% "scala-arm" % "2.0"
)
