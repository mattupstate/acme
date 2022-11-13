plugins {
  `java-library`
  kotlin("jvm")
  kotlin("plugin.serialization")
}

dependencies {
  api(KotlinX.serialization.json)
  testImplementation(Testing.kotest.runner.junit5)
}
