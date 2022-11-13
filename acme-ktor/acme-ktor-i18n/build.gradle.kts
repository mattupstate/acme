plugins {
  kotlin("jvm")
  `java-library`
}

dependencies {
  implementation(Ktor.server.core)
  testImplementation(Testing.kotest.runner.junit5)
}
