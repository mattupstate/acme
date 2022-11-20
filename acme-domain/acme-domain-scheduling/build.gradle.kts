plugins {
  id("acme.kotlin-library-conventions")
  kotlin("plugin.serialization")
}

dependencies {
  implementation(project(":acme-lib:acme-lib-serialization"))
  implementation(libs.net.sizovs.pipelinr)
  api(project(":acme-domain:acme-domain-core"))
  testImplementation(project(":acme-lib:acme-lib-liquibase"))
  testImplementation(libs.io.kotest.kotest.runner.junit5)
}
