plugins {
  id("acme.kotlin-library-conventions")
}

dependencies {
  implementation(kotlin("reflect"))
  api(libs.org.seleniumhq.selenium.selenium.java)
  api(libs.io.kotest.kotest.assertions.core)
  testImplementation(libs.io.kotest.kotest.runner.junit5)
}
