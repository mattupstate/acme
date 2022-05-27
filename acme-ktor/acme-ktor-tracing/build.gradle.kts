plugins {
  id("acme.kotlin-library-conventions")
}

dependencies {
  implementation("io.jaegertracing:jaeger-micrometer:1.2.0")
  implementation("io.jaegertracing:jaeger-thrift:1.2.0")
  implementation("io.ktor:ktor-server-core:2.0.0")
  api("io.opentracing:opentracing-api:0.33.0")
}
