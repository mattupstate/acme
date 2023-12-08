plugins {
  id("acme.kotlin-library-conventions")
}

dependencies {
  implementation(project(":acme-data:acme-data-sql"))
  implementation(project(":acme-domain:acme-domain-core"))
  implementation(project(":acme-domain:acme-domain-scheduling"))
  implementation(project(":acme-lib:acme-lib-jooq"))
  implementation(project(":acme-lib:acme-lib-serialization"))
  implementation(libs.io.github.oshai.kotlin.logging.jvm)
  implementation(libs.org.jetbrains.kotlinx.kotlinx.coroutines.core)
  implementation(libs.org.jooq.jooq.kotlin.coroutines)

  testImplementation(project(":acme-lib:acme-lib-liquibase"))
  testImplementation(libs.com.zaxxer.hikariCP)
  testImplementation(libs.io.kotest.kotest.runner.junit5)
  testImplementation(libs.org.postgresql)
  testImplementation(libs.org.postgresql.r2dbc)
  testImplementation(libs.org.slf4j.slf4j.simple)
  testImplementation(libs.org.testcontainers.postgresql)
  testImplementation(libs.org.testcontainers.r2dbc)
}
