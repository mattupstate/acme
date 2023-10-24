plugins {
  id("acme.kotlin-conventions")
  kotlin("plugin.serialization")
}

dependencies {
  implementation(project(":acme-domain:acme-domain-core"))
  implementation(libs.io.github.oshai.kotlin.logging.jvm)
  implementation(libs.org.slf4j.slf4j.api)
  testImplementation(project(":acme-web:acme-web-api"))
  testImplementation(project(":acme-lib:acme-lib-liquibase"))
  testImplementation(project(":acme-lib:acme-lib-serialization"))
  testImplementation(libs.com.github.java.json.tools.json.schema.validator)
  testImplementation(libs.io.kotest.extensions.kotest.assertions.ktor)
  testImplementation(libs.io.kotest.kotest.assertions.json)
  testImplementation(libs.io.kotest.kotest.runner.junit5)
  testImplementation(libs.io.ktor.ktor.client.apache)
  testImplementation(libs.io.ktor.ktor.client.auth)
  testImplementation(libs.io.ktor.ktor.client.logging)
  testImplementation(libs.io.ktor.ktor.server.netty)
  testImplementation(libs.org.testcontainers.postgresql)
}
