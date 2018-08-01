
resolvers += Resolver.jcenterRepo
resolvers += Resolver.bintrayRepo("neelsmith", "maven")

scalaVersion := "2.12.4"
libraryDependencies ++= Seq(
  "edu.holycross.shot.cite" %% "xcite" % "3.3.0",
  "edu.holycross.shot" %% "ohco2" % "10.8.0",
  "edu.holycross.shot"  %% "latphone" % "1.2.0",


  "edu.holycross.shot" %% "tabulae" % "1.0.0",
  "com.github.pathikrit" %% "better-files" % "3.5.0"

)
