plugins {
  id("acme.kotlin-library-conventions")
}

dependencies {
  implementation(libs.com.googlecode.libphonenumber)
  api(libs.jakarta.validation.jakarta.validation.api)
  testRuntimeOnly(libs.org.glassfish.jakarta.el)
}
