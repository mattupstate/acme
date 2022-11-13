plugins {
  kotlin("jvm")
  `java-library`
}

dependencies {
  implementation(Kotlin.stdlib.jdk8)
  implementation(KotlinX.coroutines.core)
  implementation(libs.kotlin.logging.jvm)
  implementation(project(":acme-data:acme-data-sql"))
  implementation(project(":acme-domain:acme-domain-core"))
  implementation(project(":acme-domain:acme-domain-scheduling"))
  implementation(project(":acme-lib:acme-lib-serialization"))

  testImplementation(Testing.kotest.runner.junit5)
  testImplementation(project(":acme-lib:acme-lib-liquibase"))
  testImplementation(libs.hikaricp)
  testImplementation(libs.org.postgresql.postgresql)
  testImplementation(libs.org.testcontainers.postgresql)
}
