name := "match-recommend"

version := "1.0"

scalaVersion := "2.11.6"
resolvers += "central" at "http://repo1.maven.org/maven2/"
resolvers ++= Seq("Codahale" at "http://repo.codahale.com")
resolvers ++= Seq("OSS" at "http://oss.sonatype.org/content/repositories/releases")
libraryDependencies += "org.apache.activemq" % "activemq-client" % "5.9.0"
libraryDependencies += "org.mongodb" % "casbah-core_2.11" % "2.8.0"
libraryDependencies += "org.json4s" % "json4s-native_2.11" % "3.2.11"
libraryDependencies += ("org.apache.mahout" % "mahout-core" % "0.9").exclude("xmlpull","xmlpull")
libraryDependencies += "log4j" % "log4j" % "1.2.17"
/*libraryDependencies ++= Seq("com.codahale" % "jerkson_2.9.1" % "0.5.0")*/
//libraryDependencies ++= Seq("co.blocke" % "scalajack_2.11" % "4.0")
mainClass in (Compile, packageBin) := Some("ListenStart")

libraryDependencies += "xmlpull" % "xmlpull" % "1.1.3.1" % "provided"
