plugins {
  id("acme.kotlin-library-conventions")
}

dependencies {
  implementation(libs.org.liquibase.liquibase.core)
  implementation(libs.org.yaml.snakeyaml)
  testImplementation(libs.io.kotest.kotest.runner.junit5)
}
