plugins {
  kotlin("jvm")
  `java-library`
}

dependencies {
  implementation(libs.jaeger.micrometer)
  implementation(libs.jaeger.thrift)
  implementation(Ktor.server.core)
  api(libs.opentracing.api)
  testImplementation(Testing.kotest.runner.junit5)
}
