plugins {
  id("acme.kotlin-library-conventions")
  kotlin("plugin.serialization")
}

dependencies {
  api(libs.org.jetbrains.kotlinx.kotlinx.serialization.json)
  testImplementation(libs.io.kotest.kotest.runner.junit5)
}
