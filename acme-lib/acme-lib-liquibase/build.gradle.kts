plugins {
  id("acme.kotlin-library-conventions")
}

dependencies {
  implementation(libs.org.liquibase.liquibase.core)
  testImplementation(libs.io.kotest.kotest.runner.junit5)
}
