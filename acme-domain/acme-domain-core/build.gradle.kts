plugins {
  `java-library`
  kotlin("jvm")
  kotlin("plugin.serialization")
}

dependencies {
  implementation(project(":acme-lib:acme-lib-serialization"))
  implementation(libs.kotlin.logging.jvm)
  testImplementation(Testing.kotest.runner.junit5)
  testImplementation(project(":acme-lib:acme-lib-liquibase"))
}
