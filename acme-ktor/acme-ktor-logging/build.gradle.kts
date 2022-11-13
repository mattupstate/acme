plugins {
  kotlin("jvm")
  `java-library`
}

dependencies {
  implementation(Ktor.server.core)
  implementation(libs.logstash.logback.encoder)
  testImplementation(Testing.kotest.runner.junit5)
}
