plugins {
  id("acme.kotlin-library-conventions")
}

dependencies {
  api(libs.org.jooq)
  api(libs.org.jooq.jooq.kotlin)
  api(libs.org.jooq.jooq.kotlin.coroutines)
  testImplementation(libs.io.kotest.kotest.runner.junit5)
}
