plugins {
  id("acme.kotlin-library-conventions")
}

dependencies {
  implementation("com.googlecode.libphonenumber:libphonenumber:8.12.24")
  api("jakarta.validation:jakarta.validation-api:3.0.0")
  testRuntimeOnly("org.glassfish:jakarta.el:4.0.1")
}
