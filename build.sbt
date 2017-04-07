
name := """toletagent"""

version := "1.0"

lazy val root = (project in file(".")).enablePlugins(PlayJava)

scalaVersion := "2.11.7"

libraryDependencies ++= Seq(
  javaJdbc,
  cache,
  javaWs,
  javaJpa.exclude("org.hibernate.javax.persistence", "hibernate-jpa-2.0-api"),
  "org.hibernate" % "hibernate-entitymanager" % "4.3.10.Final",
  "mysql" % "mysql-connector-java" % "5.1.36",
  "org.mongodb.morphia" % "morphia" % "1.0.0",
  "com.amazonaws" % "aws-java-sdk" % "1.9.10",
  "com.google.code.gson" % "gson" % "2.3.1",
  "org.im4java" % "im4java" % "1.4.0",
  "org.projectlombok" % "lombok" % "1.16.12",
  "net.coobird" % "thumbnailator" % "0.4.2",
  "org.infobip.oneapi" % "oneapi-java" % "0.0.1",
  "org.jsoup" % "jsoup" % "1.7.3",
  "org.apache.poi" % "poi" % "3.12",
  "org.apache.poi" % "poi-ooxml" % "3.9",
  "javax.mail" % "javax.mail-api" % "1.5.4",
  "com.sun.mail" % "javax.mail" % "1.5.4",
  "net.sf.uadetector"%"uadetector-resources"%"2014.04",
  "com.google.guava" % "guava" % "19.0",
  "com.googlecode.libphonenumber" % "libphonenumber" % "7.4.0",
  "com.rometools" % "rome" % "1.7.0",
  "org.elasticsearch" % "elasticsearch" % "2.3.4"
)

pipelineStages := Seq(digest, gzip)

PlayKeys.externalizeResources := false
