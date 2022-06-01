plugins {
  id("acme.kotlin-library-conventions")
}

dependencies {
  implementation(project(":acme-data:acme-data-sql"))
  implementation(project(":acme-domain:acme-domain-core"))
  implementation(project(":acme-domain:acme-domain-scheduling"))
  implementation(project(":acme-lib:acme-lib-serialization"))
  implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.0")

  testImplementation(project(":acme-lib:acme-lib-liquibase"))
  testImplementation("com.zaxxer:HikariCP:5.0.1")
  testImplementation("org.postgresql:postgresql:42.2.18")
  testImplementation("org.testcontainers:postgresql:1.17.1")
}
