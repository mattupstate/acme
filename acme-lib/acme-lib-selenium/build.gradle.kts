plugins {
  id("acme.kotlin-library-conventions")
}

dependencies {
  api(libs.org.seleniumhq.selenium.selenium.java)
  api(libs.io.kotest.kotest.assertions.core)
}
