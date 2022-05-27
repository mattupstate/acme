plugins {
  id("acme.kotlin-library-conventions")
  kotlin("plugin.serialization")
}

dependencies {
  implementation(project(":acme-lib:acme-lib-serialization"))
  testImplementation(project(":acme-lib:acme-lib-liquibase"))
}
