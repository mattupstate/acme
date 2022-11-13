plugins {
  application
  kotlin("jvm")
  kotlin("plugin.serialization")
  id("com.google.cloud.tools.jib")
  id("com.ryandens.javaagent-jib")
  id("io.ktor.plugin") version "2.1.1"
  id("org.hidetake.swagger.generator")
}

val localRuntimeOnly by configurations.creating

dependencies {
  swaggerUI(libs.swagger.ui)
  javaagent(libs.opentelemetry.javaagent)

  implementation(platform(libs.opentelemetry.bom))
  implementation(project(":acme-data:acme-data-scheduling"))
  implementation(project(":acme-data:acme-data-sql"))
  implementation(project(":acme-domain:acme-domain-core"))
  implementation(project(":acme-domain:acme-domain-scheduling"))
  implementation(project(":acme-ktor:acme-ktor-i18n"))
  implementation(project(":acme-ktor:acme-ktor-logging"))
  implementation(project(":acme-ktor:acme-ktor-validation"))
  implementation(project(":acme-lib:acme-lib-serialization"))
  implementation(project(":acme-lib:acme-lib-validation"))
  implementation(libs.timeflake4j)
  implementation(libs.logback.classic)
  implementation(libs.kotlin.coroutines.jdbc)
  implementation(libs.hikaricp)
  implementation(libs.kotlin.logging.jvm)
  implementation(libs.ktor.client.java)
  implementation(libs.ktor.client.content.negotiation)
  implementation(libs.ktor.serialization.kotlinx.json)
  implementation(libs.ktor.server.auth)
  implementation(libs.ktor.server.call.id)
  implementation(libs.ktor.server.call.logging)
  implementation(libs.ktor.server.content.negotiation)
  implementation(libs.ktor.server.default.headers)
  implementation(libs.ktor.server.forwarded.header)
  implementation(libs.ktor.server.locations)
  implementation(libs.ktor.server.metrics.micrometer)
  implementation(libs.ktor.server.netty)
  implementation(libs.ktor.server.status.pages)
  implementation(libs.micrometer.registry.prometheus)
  implementation(libs.opentelemetry.api)
  implementation(libs.opentelemetry.sdk)
  implementation(libs.kotlinx.coroutines.core)
  implementation(libs.org.postgresql.postgresql)
  runtimeOnly(libs.jakarta.el)

  testImplementation(project(":acme-lib:acme-lib-liquibase"))
  testImplementation(Testing.kotest.runner.junit5)
  testImplementation(libs.liquibase.slf4j)
  testImplementation(libs.org.testcontainers.postgresql)

  localRuntimeOnly(project(":acme-lib:acme-lib-liquibase"))
  localRuntimeOnly(libs.org.testcontainers.postgresql)
}

swaggerSources {
  register("acme") {
    setInputFile(file("spec.yaml"))
    ui.outputDir = file("$buildDir/swagger-ui-acme/swagger-ui")
  }
}

sourceSets.main {
  resources.srcDirs(
    swaggerSources.getByName("acme").ui.outputDir.parent,
  )
}

application {
  mainClass.set("io.ktor.server.netty.EngineMain")
}

jib {
  container {
    ports = listOf("8080")
    mainClass = application.mainClass.get()
    environment = mapOf(
      "OTEL_SERVICE_NAME" to "com-acme-web-server"
    )
  }

}

tasks {
  getByName<JavaExec>("run") {
    classpath += localRuntimeOnly
    systemProperty("logback.configurationFile", projectDir.resolve("logback-dev.xml"))
  }

  getByName("processResources") {
    dependsOn(generateSwaggerUI)
  }
}
