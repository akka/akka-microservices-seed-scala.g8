name := "$name$"
version := "1.0"

organization := "com.lightbend.akka.samples"
organizationHomepage := Some(url("https://akka.io"))
licenses := Seq(("CC0", url("https://creativecommons.org/publicdomain/zero/1.0")))

scalaVersion := "2.13.3"

Compile / scalacOptions ++= Seq("-deprecation", "-feature", "-unchecked", "-Xlog-reflective-calls", "-Xlint")
Compile / javacOptions ++= Seq("-Xlint:unchecked", "-Xlint:deprecation")

Test / parallelExecution := false
Test / testOptions += Tests.Argument("-oDF")
Test / logBuffered := false

run / fork := false
Global / cancelable := false // ctrl-c


// For akka management snapshot
resolvers += Resolver.bintrayRepo("akka", "snapshots")
// For akka nightlies
resolvers += "Akka Snapshots" at "https://repo.akka.io/snapshots/"


// 1. Basic dependencies for a clustered application
// FIXME once akka 2.6.9 is released
val AkkaVersion = "2.6.8+71-57fb9e90"
val AkkaHttpVersion = "10.2.0"
// FIXME once akka management 1.0.9 is released
val AkkaManagementVersion = "1.0.8+35-9feaa689+20200825-1429"
libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-stream" % AkkaVersion,
  "com.typesafe.akka" %% "akka-actor-testkit-typed" % AkkaVersion % Test,
  "com.typesafe.akka" %% "akka-stream-testkit" % AkkaVersion % Test,
  "com.typesafe.akka" %% "akka-cluster-typed" % AkkaVersion,
  "com.typesafe.akka" %% "akka-cluster-sharding-typed" % AkkaVersion,

  // Akka Management powers Health Checks and Akka Cluster Bootstrapping
  "com.lightbend.akka.management" %% "akka-management" % AkkaManagementVersion, // uses Akka HTTP and Spray JSON
  "com.typesafe.akka" %% "akka-http" % AkkaHttpVersion,
  "com.typesafe.akka" %% "akka-http-spray-json" % AkkaHttpVersion,
  "com.lightbend.akka.management" %% "akka-management-cluster-http" % AkkaManagementVersion, // cluster health-checks
  "com.lightbend.akka.management" %% "akka-management-cluster-bootstrap" % AkkaManagementVersion, // cluster bootstrap
  "com.typesafe.akka" %% "akka-discovery" % AkkaVersion, // used in cluster bootstrapping

  // Common dependencies for logging and testing
  "com.typesafe.akka" %% "akka-slf4j" % AkkaVersion,
  "ch.qos.logback" % "logback-classic" % "1.2.3",
  "org.scalatest" %% "scalatest" % "3.1.2" % Test,
)

// 2. Using gRPC and/or protobuf
enablePlugins(AkkaGrpcPlugin)
libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-http2-support" % AkkaHttpVersion, // HTTP/2 is required for gRPC
)

// 3. Using Akka Persistence (assumes the app is clustered so event-sourced behaviors can be sharded)
val AkkaPersistenceCassandraVersion = "1.0.1"
libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-persistence-typed" % AkkaVersion, // Akka Persistence API
  "com.typesafe.akka" %% "akka-serialization-jackson" % AkkaVersion, // Recommended serializers for Persistence (CBOR)
  "com.typesafe.akka" %% "akka-persistence-cassandra" % AkkaPersistenceCassandraVersion, // plugin implementation
  "com.typesafe.akka" %% "akka-persistence-testkit" % AkkaVersion % Test,
)

// 4. Querying or projecting data from Akka Persistence (assumes the app is clustered so projections can be sharded)
val AlpakkaKafkaVersion = "2.0.4"
val AkkaProjectionVersion = "1.0.0-RC3"
libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-persistence-query" % AkkaVersion, // low-level querying the event journal
  "com.lightbend.akka" %% "akka-projection-eventsourced" % AkkaProjectionVersion, // journal as a projection source
  "com.lightbend.akka" %% "akka-projection-cassandra" % AkkaProjectionVersion,
  "com.lightbend.akka" %% "akka-projection-testkit" % AkkaProjectionVersion % Test,
  "com.typesafe.akka" %% "akka-stream-kafka" % AlpakkaKafkaVersion, // Kafka as Source and Sink
)


