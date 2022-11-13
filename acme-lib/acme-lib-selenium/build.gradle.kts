plugins {
  kotlin("jvm")
  `java-library`
}

dependencies {
  implementation(Kotlin.stdlib.jdk8)
  implementation(libs.kotlin.reflect)
  api(libs.selenium.java)
  api(Testing.kotest.assertions.core)
  testImplementation(Testing.kotest.runner.junit5)
}
