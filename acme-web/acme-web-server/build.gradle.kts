import org.asciidoctor.gradle.jvm.AsciidoctorTask

plugins {
  id("acme.kotlin-application-conventions")
  id("org.asciidoctor.jvm.convert") version "3.3.2"
  id("org.hidetake.swagger.generator") version "2.18.2"
  kotlin("plugin.serialization")
}

val localRuntimeOnly by configurations.creating

dependencies {
  swaggerUI("org.webjars:swagger-ui:3.50.0")

  implementation(project(":acme-data:acme-data-scheduling"))
  implementation(project(":acme-data:acme-data-sql"))
  implementation(project(":acme-domain:acme-domain-core"))
  implementation(project(":acme-domain:acme-domain-scheduling"))
  implementation(project(":acme-ktor:acme-ktor-i18n"))
  implementation(project(":acme-ktor:acme-ktor-logging"))
  implementation(project(":acme-ktor:acme-ktor-metrics"))
  implementation(project(":acme-ktor:acme-ktor-tracing"))
  implementation(project(":acme-ktor:acme-ktor-validation"))
  implementation(project(":acme-lib:acme-lib-serialization"))
  implementation(project(":acme-lib:acme-lib-validation"))
  implementation("am.ik.timeflake:timeflake4j:1.3.0")
  implementation("ch.qos.logback:logback-classic:1.2.11")
  implementation("com.michael-bull.kotlin-coroutines-jdbc:kotlin-coroutines-jdbc:1.0.2")
  implementation("com.zaxxer:HikariCP:5.0.1")
  implementation("io.ktor:ktor-client-apache:2.0.0")
  implementation("io.ktor:ktor-client-content-negotiation:2.0.0")
  implementation("io.ktor:ktor-serialization-kotlinx-json:2.0.0")
  implementation("io.ktor:ktor-server-auth:2.0.0")
  implementation("io.ktor:ktor-server-call-id:2.0.0")
  implementation("io.ktor:ktor-server-call-logging:2.0.0")
  implementation("io.ktor:ktor-server-content-negotiation:2.0.0")
  implementation("io.ktor:ktor-server-default-headers:2.0.0")
  implementation("io.ktor:ktor-server-forwarded-header:2.0.0")
  implementation("io.ktor:ktor-server-locations:2.0.0")
  implementation("io.ktor:ktor-server-metrics-micrometer:2.0.0")
  implementation("io.ktor:ktor-server-netty:2.0.0")
  implementation("io.ktor:ktor-server-status-pages:2.0.0")
  implementation("io.micrometer:micrometer-registry-prometheus:1.8.5")
  implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.0")
  implementation("org.postgresql:postgresql:42.2.18")
  runtimeOnly("org.glassfish:jakarta.el:4.0.1")

  testImplementation(project(":acme-lib:acme-lib-liquibase"))
  testImplementation("com.mattbertolini:liquibase-slf4j:4.0.0")
  testImplementation("org.testcontainers:postgresql:1.17.1")

  localRuntimeOnly(project(":acme-lib:acme-lib-liquibase"))
  localRuntimeOnly("org.testcontainers:postgresql:1.17.1")
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
    tasks.getByName<AsciidoctorTask>("asciidoctor").outputDir.parent
  )
}

application {
  mainClass.set("io.ktor.server.netty.EngineMain")

}

jib {
  container {
    ports = listOf("8080")
    mainClass = application.mainClass.get()
  }
}

tasks {
  getByName<JavaExec>("run") {
    classpath += localRuntimeOnly
    systemProperty("logback.configurationFile", projectDir.resolve("logback-dev.xml"))
  }

  getByName<AsciidoctorTask>("asciidoctor") {
    baseDirFollowsSourceDir()
    attributes = mapOf(
      "project-version" to project.version
    )
  }

  getByName("processResources") {
    dependsOn(asciidoctor)
    dependsOn(generateSwaggerUI)
  }
}
