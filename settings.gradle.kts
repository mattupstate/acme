/*
 * The settings file is used to specify which projects to include in your build.
 *
 * Detailed information about configuring a multi-project build in Gradle can be found
 * in the user manual at https://docs.gradle.org/6.0.1/userguide/multi_project_builds.html
 */

rootProject.name = "acme"

pluginManagement {
  repositories {
    mavenCentral()
    gradlePluginPortal()
  }
}

include(":acme-app:acme-app-web")
include(":acme-app:acme-app-web-test")
include(":acme-data:acme-data-scheduling")
include(":acme-data:acme-data-sql")
include(":acme-domain:acme-domain-core")
include(":acme-domain:acme-domain-scheduling")
include(":acme-ktor:acme-ktor-i18n")
include(":acme-ktor:acme-ktor-logging")
include(":acme-ktor:acme-ktor-metrics")
include(":acme-ktor:acme-ktor-tracing")
include(":acme-ktor:acme-ktor-validation")
include(":acme-lib:acme-lib-liquibase")
include(":acme-lib:acme-lib-serialization")
include(":acme-lib:acme-lib-test")
include(":acme-lib:acme-lib-validation")
include(":acme-ops")
include(":acme-web:acme-web-server")
include(":acme-web:acme-web-server-test")
