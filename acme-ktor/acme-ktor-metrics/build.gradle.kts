plugins {
  id("acme.kotlin-library-conventions")
}

dependencies {
  implementation("io.ktor:ktor-server-metrics-micrometer:2.0.0")
  implementation("io.micrometer:micrometer-registry-prometheus:1.8.5")
}
