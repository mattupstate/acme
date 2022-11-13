plugins {
  kotlin("jvm")
  `java-library`
}

dependencies {
  implementation(libs.libphonenumber)
  api(libs.jakarta.validation.api)
  testRuntimeOnly(libs.jakarta.el)
  testImplementation(Testing.kotest.runner.junit5)
}
