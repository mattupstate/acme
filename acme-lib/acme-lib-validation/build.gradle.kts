plugins {
  id("acme.kotlin-library-conventions")
}

dependencies {
  implementation(libs.com.googlecode.libphonenumber)
  api(libs.jakarta.validation.jakarta.validation.api)
  testImplementation(libs.io.kotest.kotest.runner.junit5)
  testImplementation(libs.org.hibernate.validator.hibernate.validator)
  testRuntimeOnly(libs.org.glassfish.jakarta.el)
}
