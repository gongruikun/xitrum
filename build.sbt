organization := "tv.cntt"

name := "xitrum"

version := "3.14-SNAPSHOT"

scalaVersion := "2.11.1"

scalacOptions ++= Seq("-deprecation", "-feature", "-unchecked")

// xitrum.util.FileMonitor requires Java 7
javacOptions ++= Seq("-source", "1.7", "-target", "1.7")

// Generate Version.scala from "version" above ---------------------------------

// Also check if the directory name is correct:
// src/main/resources/META-INF/resources/webjars/xitrum/<version>
sourceGenerators in Compile += Def.task {
  val versions = version.value.split('.')
  val major    = versions(0).toInt
  val minor    = versions(1).split('-')(0).toInt
  val ma_mi    = s"$major.$minor"
  val base     = (baseDirectory in Compile).value
  val resDir   = base / s"src/main/resources/META-INF/resources/webjars/xitrum/$ma_mi"
  val file     = base / "src/main/scala/xitrum/Version.scala"
  if (!resDir.exists) throw new Exception(s"Check $resDir")
  IO.write(file, s"""package xitrum
// Autogenerated by build.sbt. Do not modify this file directly.
class Version {
  val major = $major
  val minor = $minor
  /** major.minor */
  override def toString = "$ma_mi"
}
""")
  Seq(file)
}.taskValue

//------------------------------------------------------------------------------

// Most Scala projects are published to Sonatype, but Sonatype is not default
// and it takes several hours to sync from Sonatype to Maven Central
resolvers += "SonatypeReleases" at "http://oss.sonatype.org/content/repositories/releases/"

// Projects using Xitrum must provide a concrete implementation of SLF4J (Logback etc.)
libraryDependencies += "org.slf4s" %% "slf4s-api" % "1.7.7"

// An implementation of SLF4J is needed for log in tests to be output
libraryDependencies += "ch.qos.logback" % "logback-classic" % "1.1.2" % "test"

// Netty is the core of Xitrum's HTTP(S) feature
libraryDependencies += "io.netty" % "netty-all" % "4.0.19.Final"

// Javassist boosts Netty 4 speed
libraryDependencies += "org.javassist" % "javassist" % "3.18.1-GA"

// For clustering SockJS; Akka is included here
libraryDependencies += "tv.cntt" %% "glokka" % "1.9"

// Redirect Akka log to SLF4J
// (akka-slf4j version should be the same as the Akka version used by Glokka above)
libraryDependencies += "com.typesafe.akka" %% "akka-slf4j" % "2.3.3"

// For file watch
// (akka-agent is added here, should ensure same Akka version as above)
libraryDependencies += "com.beachape.filemanagement" %% "schwatcher" % "0.1.5"

// For scanning routes
libraryDependencies += "tv.cntt" %% "sclasner" % "1.6"

// For binary (de)serializing
libraryDependencies += "tv.cntt" %% "chill-scala-2-11" % "1.0"

// For JSON (de)serializing
libraryDependencies += "org.json4s" %% "json4s-jackson" % "3.2.10"

// For i18n
libraryDependencies += "tv.cntt" %% "scaposer" % "1.3"

// For jsEscape
libraryDependencies += "org.apache.commons" % "commons-lang3" % "3.3.2"

// For compiling CoffeeScript to JavaScript
libraryDependencies += "tv.cntt" % "rhinocoffeescript" % "1.7.1"

// For metrics
libraryDependencies += "nl.grons" %% "metrics-scala" % "3.2.0_a2.3"

// For metrics
libraryDependencies += "com.codahale.metrics" % "metrics-json" % "3.0.2"

// For test
libraryDependencies += "org.scalatest" %% "scalatest" % "2.1.6" % "test"

// JSON4S uses scalap 2.10.0, which in turn uses scala-compiler 2.10.0, which in
// turn uses scala-reflect 2.10.0. We need to force "scalaVersion" above, because
// Scala annotations (used by routes and Swagger) compiled by a newer version
// can't be read by an older version.
//
// Also, we must release a new version of Xitrum every time a new version of
// Scala is released.
libraryDependencies <+= scalaVersion { sv => "org.scala-lang" % "scalap" % sv }

// WebJars ---------------------------------------------------------------------

libraryDependencies += "org.webjars" % "jquery" % "2.1.1"

libraryDependencies += "org.webjars" % "jquery-validation" % "1.12.0"

libraryDependencies += "org.webjars" % "sockjs-client" % "0.3.4"

libraryDependencies += "org.webjars" % "swagger-ui" % "2.0.17"

libraryDependencies += "org.webjars" % "d3js" % "3.4.8"

// Put config directory in classpath for easier development --------------------

// For "sbt console"
unmanagedClasspath in Compile <+= (baseDirectory) map { bd => Attributed.blank(bd / "src/test/resources") }

//------------------------------------------------------------------------------

// Avoid messy Scaladoc by excluding things that are not intended to be used
// directly by normal Xitrum users.
scalacOptions in (Compile, doc) ++= Seq("-skip-packages", "xitrum.sockjs")

// Skip API doc generation to speedup "publish-local" while developing.
// Comment out this line when publishing to Sonatype.
publishArtifact in (Compile, packageDoc) := false
