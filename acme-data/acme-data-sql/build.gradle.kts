import nu.studer.gradle.jooq.JooqEdition
import nu.studer.gradle.jooq.JooqGenerate

plugins {
  id("acme.kotlin-library-conventions")
  id("nu.studer.jooq") version "7.1.1"
}

val jooqVersion = "3.16.4"

dependencies {
  jooqGenerator(project(":acme-lib:acme-lib-liquibase"))
  jooqGenerator("org.testcontainers:postgresql:1.17.1")
  jooqGenerator("org.postgresql:postgresql:42.2.18")
  jooqGenerator("ch.qos.logback:logback-classic:1.2.3")

  implementation(project(":acme-domain:acme-domain-core"))
  implementation("com.michael-bull.kotlin-coroutines-jdbc:kotlin-coroutines-jdbc:1.0.2")
  implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.0")
  api("org.jooq:jooq:$jooqVersion")
  api("org.jooq:jooq-kotlin:$jooqVersion")

  testImplementation(project(":acme-lib:acme-lib-liquibase"))
  testImplementation("org.postgresql:postgresql:42.2.18")
  testImplementation("org.testcontainers:postgresql:1.17.1")
}

java {
  sourceSets {
    main {
      java {
        srcDir(tasks.withType(JooqGenerate::class))
      }
    }
  }
}

jooq {
  version.set(jooqVersion)
  edition.set(JooqEdition.OSS)
  configurations {
    create("main") {
      jooqConfiguration.apply {
        logging = org.jooq.meta.jaxb.Logging.WARN
        jdbc.apply {
          driver = "org.testcontainers.jdbc.ContainerDatabaseDriver"
          url =
            "jdbc:tc:postgresql:11.5:///test?TC_INITFUNCTION=com.acme.liquibase.LiquibaseTestContainerInitializerKt::update"
        }
        generator.apply {
          name = "org.jooq.codegen.KotlinGenerator"
          database.apply {
            name = "org.jooq.meta.postgres.PostgresDatabase"
            includes = ".*"
            excludes = "databasechangelog.*|databasechangeloglock.*|pg_catalog.*|information_schema.*"
          }
          // generate.apply {
          //   isDeprecated = false
          //   isRecords = true
          //   isImmutablePojos = true
          //   isFluentSetters = true
          // }
          target.apply {
            packageName = "com.acme.sql"
          }
          strategy.name = "org.jooq.codegen.DefaultGeneratorStrategy"
        }
      }
    }
  }
}

tasks {
  val liquibaseChangelogFile = "src/main/resources/db/changelog.yaml"

  withType(JooqGenerate::class) {
    inputs.dir(file(liquibaseChangelogFile).parent).withPathSensitivity(PathSensitivity.RELATIVE)
    allInputsDeclared.set(true)
    outputs.cacheIf { true }
    javaExecSpec = Action {
      systemProperty("liquibase.changelogFile", liquibaseChangelogFile)
    }
  }

  // test {
  //   systemProperty(liquibaseChangelogProperty, liquibaseChangelogFile)
  // }
}
