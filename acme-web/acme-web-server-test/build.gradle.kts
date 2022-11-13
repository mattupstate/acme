plugins {
  kotlin("jvm")
  kotlin("plugin.serialization")
}

dependencies {
  implementation(project(":acme-domain:acme-domain-core"))
  testImplementation(Testing.kotest.runner.junit5)
  testImplementation(project(":acme-lib:acme-lib-liquibase"))
  testImplementation(project(":acme-lib:acme-lib-serialization"))
  testImplementation(project(":acme-web:acme-web-server"))
  testImplementation(libs.json.schema.validator)
  testImplementation(libs.ktor.client.apache)
  testImplementation(libs.ktor.client.auth)
  testImplementation(libs.ktor.client.logging)
  testImplementation(libs.ktor.server.netty)
  testImplementation(libs.kotest.assertions.ktor)
  testImplementation(libs.kotest.assertions.json)
  testImplementation(libs.org.testcontainers.postgresql)
}
