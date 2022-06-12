plugins {
  id("acme.kotlin-application-conventions")
  kotlin("plugin.serialization")
}

dependencies {
  implementation(project(":acme-domain:acme-domain-core"))
  testImplementation(project(":acme-web:acme-web-server"))
  testImplementation(project(":acme-lib:acme-lib-liquibase"))
  testImplementation(project(":acme-lib:acme-lib-serialization"))
  testImplementation("com.github.java-json-tools:json-schema-validator:2.2.14")
  testImplementation("io.ktor:ktor-client-apache:2.0.0")
  testImplementation("io.ktor:ktor-client-auth:2.0.0")
  testImplementation("io.ktor:ktor-client-logging:2.0.0")
  testImplementation("io.ktor:ktor-server-netty:2.0.0")
  testImplementation("io.kotest.extensions:kotest-assertions-ktor:1.0.3")
  testImplementation("io.kotest:kotest-assertions-json:5.3.0")
  testImplementation("org.testcontainers:postgresql:1.17.1")
}
