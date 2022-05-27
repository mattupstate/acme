plugins {
  id("acme.kotlin-library-conventions")
}

dependencies {
  implementation("io.ktor:ktor-server-core:2.0.0")
  implementation("org.hibernate.validator:hibernate-validator:7.0.1.Final")
  implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.2.2")

  testRuntimeOnly("org.glassfish:jakarta.el:4.0.1")
}
