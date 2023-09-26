plugins {
  id("acme.kotlin-library-conventions")
  kotlin("plugin.serialization")
}

dependencies {
  implementation(project(":acme-lib:acme-lib-serialization"))
  implementation(libs.io.github.oshai.kotlin.logging.jvm)
  testImplementation(project(":acme-lib:acme-lib-liquibase"))
  testImplementation(libs.io.kotest.kotest.runner.junit5)
  testImplementation(libs.org.slf4j.slf4j.api)
}
