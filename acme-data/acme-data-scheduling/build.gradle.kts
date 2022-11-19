plugins {
  id("acme.kotlin-library-conventions")
}

dependencies {
  implementation(project(":acme-data:acme-data-sql"))
  implementation(project(":acme-domain:acme-domain-core"))
  implementation(project(":acme-domain:acme-domain-scheduling"))
  implementation(project(":acme-lib:acme-lib-serialization"))
  implementation(libs.org.jetbrains.kotlinx.kotlinx.coroutines.core)

  testImplementation(project(":acme-lib:acme-lib-liquibase"))
  testImplementation(libs.com.zaxxer.hikariCP)
  testImplementation(libs.org.postgresql)
  testImplementation(libs.org.testcontainers.postgresql)
}
