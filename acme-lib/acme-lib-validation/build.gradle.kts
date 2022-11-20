plugins {
  id("acme.kotlin-library-conventions")
}

dependencies {
  implementation(libs.com.googlecode.libphonenumber)
  api(libs.jakarta.validation.jakarta.validation.api)
  testImplementation(libs.io.kotest.kotest.runner.junit5)
  testRuntimeOnly(libs.org.glassfish.jakarta.el)
}
