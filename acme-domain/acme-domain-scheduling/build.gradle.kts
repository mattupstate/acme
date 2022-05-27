plugins {
  id("acme.kotlin-library-conventions")
  kotlin("plugin.serialization")
}

dependencies {
  implementation(project(":acme-lib:acme-lib-serialization"))
  implementation("net.sizovs:pipelinr:0.6")
  api(project(":acme-domain:acme-domain-core"))
  testImplementation(project(":acme-lib:acme-lib-liquibase"))
}
