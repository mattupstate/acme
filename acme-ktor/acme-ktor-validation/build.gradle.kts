plugins {
  id("acme.kotlin-library-conventions")
}

dependencies {
  implementation(libs.io.ktor.ktor.server.core)
  implementation(libs.org.hibernate.validator.hibernate.validator)
  implementation(libs.org.jetbrains.kotlinx.kotlinx.serialization.json)
  testRuntimeOnly(libs.org.glassfish.jakarta.el)
}
