plugins {
  id("acme.kotlin-library-conventions")
}

dependencies {
  implementation(libs.org.jooq.jooq.kotlin.coroutines)
  // api(libs.org.jooq)
  // api(libs.org.jooq.jooq.kotlin)
  testImplementation(libs.io.kotest.kotest.runner.junit5)
}
