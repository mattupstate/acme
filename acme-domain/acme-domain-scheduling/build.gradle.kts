plugins {
  id("acme.kotlin-library-conventions")
  kotlin("plugin.serialization")
}

dependencies {
  implementation(kotlin("reflect"))
  implementation(project(":acme-lib:acme-lib-serialization"))
  api(project(":acme-domain:acme-domain-core"))
  testImplementation(project(":acme-lib:acme-lib-liquibase"))
  testImplementation(libs.io.kotest.kotest.runner.junit5)
  testImplementation(libs.org.slf4j.slf4j.api)
}
