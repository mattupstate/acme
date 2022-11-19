plugins {
  id("acme.kotlin-library-conventions")
  kotlin("plugin.serialization")
}

dependencies {
  api(libs.org.jetbrains.kotlinx.kotlinx.serialization.json)
}
