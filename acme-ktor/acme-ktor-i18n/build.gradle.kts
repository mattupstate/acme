plugins {
  id("acme.kotlin-library-conventions")
}

dependencies {
  implementation(libs.io.ktor.ktor.server.core)
  testImplementation(libs.io.kotest.kotest.runner.junit5)
}
