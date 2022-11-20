plugins {
  id("acme.kotlin-library-conventions")
  kotlin("plugin.serialization")
}

dependencies {
  implementation(kotlin("reflect"))
  implementation(project(":acme-lib:acme-lib-serialization"))
  implementation(libs.io.github.microutils.kotlin.logging.jvm)
  testImplementation(project(":acme-lib:acme-lib-liquibase"))
  testImplementation(libs.io.kotest.kotest.runner.junit5)
}
