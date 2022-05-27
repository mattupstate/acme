plugins {
  id("acme.kotlin-library-conventions")
  kotlin("plugin.serialization")
}

dependencies {
  api("org.jetbrains.kotlinx:kotlinx-serialization-json:1.2.2")
}
