plugins {
  kotlin("jvm")
  kotlin("plugin.serialization")
  `java-library`
}

dependencies {
  implementation(project(":acme-lib:acme-lib-serialization"))
  implementation(libs.pipelinr)
  api(project(":acme-domain:acme-domain-core"))
  testImplementation(project(":acme-lib:acme-lib-liquibase"))
  testImplementation(Testing.kotest.runner.junit5)
}
