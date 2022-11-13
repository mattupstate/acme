plugins {
  kotlin("jvm")
  `java-library`
}

dependencies {
  implementation(Ktor.server.core)
  implementation(KotlinX.serialization.json)
  implementation(libs.hibernate.validator)

  testImplementation(Testing.kotest.runner.junit5)
  testRuntimeOnly(libs.jakarta.el)
}
