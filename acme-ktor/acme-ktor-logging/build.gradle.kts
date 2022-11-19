plugins {
  id("acme.kotlin-library-conventions")
}

dependencies {
  implementation(libs.io.ktor.ktor.server.core)
  implementation(libs.net.logstash.logback.logstash.logback.encoder)
}
