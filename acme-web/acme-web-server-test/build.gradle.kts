plugins {
  id("acme.kotlin-conventions")
  kotlin("plugin.serialization")
}

dependencies {
  implementation(project(":acme-domain:acme-domain-core"))
  testImplementation(project(":acme-web:acme-web-server"))
  testImplementation(project(":acme-lib:acme-lib-liquibase"))
  testImplementation(project(":acme-lib:acme-lib-serialization"))
  testImplementation(libs.com.github.java.json.tools.json.schema.validator)
  testImplementation(libs.io.ktor.ktor.client.apache)
  testImplementation(libs.io.ktor.ktor.client.auth)
  testImplementation(libs.io.ktor.ktor.client.logging)
  testImplementation(libs.io.ktor.ktor.server.netty)
  testImplementation(libs.io.kotest.extensions.kotest.assertions.ktor)
  testImplementation(libs.io.kotest.kotest.assertions.json)
  testImplementation(libs.org.testcontainers.postgresql)
}
