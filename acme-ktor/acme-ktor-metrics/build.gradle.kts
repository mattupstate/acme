plugins {
  id("acme.kotlin-library-conventions")
}

dependencies {
  implementation(libs.io.ktor.ktor.server.metrics.micrometer)
  implementation(libs.io.micrometer.micrometer.registry.prometheus)
}
