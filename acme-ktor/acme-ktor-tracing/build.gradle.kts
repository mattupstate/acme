plugins {
  id("acme.kotlin-library-conventions")
}

dependencies {
  implementation(libs.io.jaegertracing.jaeger.micrometer)
  implementation(libs.io.jaegertracing.jaeger.thrift)
  implementation(libs.io.ktor.ktor.server.core)
  api(libs.io.opentracing.opentracing.api)
}
