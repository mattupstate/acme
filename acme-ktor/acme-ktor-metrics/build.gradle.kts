plugins {
  kotlin("jvm")
  `java-library`
}

dependencies {
  implementation(libs.ktor.server.metrics.micrometer)
  implementation(libs.micrometer.registry.prometheus)
  testImplementation(Testing.kotest.runner.junit5)
}
