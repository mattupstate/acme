plugins {
  kotlin("jvm")
  java
  jacoco
  id("com.diffplug.spotless")
}

repositories {
  mavenLocal()
  mavenCentral()
}

dependencies {
  implementation("org.jetbrains.kotlin:kotlin-stdlib:1.6.10")
  implementation("org.jetbrains.kotlin:kotlin-stdlib-common:1.6.10")
  implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.6.10")
  implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk7:1.6.10")
  implementation("org.jetbrains.kotlin:kotlin-reflect:1.6.10")
  implementation("org.jetbrains.kotlin:kotlin-script-runtime:1.6.10")
  implementation("org.slf4j:slf4j-api:1.7.31")
  implementation("io.github.microutils:kotlin-logging-jvm:2.1.20")
  testImplementation(project(":acme-lib:acme-lib-test"))
  testImplementation("io.kotest:kotest-runner-junit5:5.3.0")
  testImplementation("io.mockk:mockk:1.12.4")
}

group = "com.acme"
version = "0.1.0"

java {
  sourceCompatibility = JavaVersion.VERSION_11
  targetCompatibility = JavaVersion.VERSION_11
}

jacoco {
  toolVersion = "0.8.7"
}

spotless {
  kotlin {
    ktlint("0.44.0")
      .userData(mapOf("indent_size" to "2", "continuation_indent_size" to "2"))
  }
}

tasks {
  test {
    useJUnitPlatform()
    testLogging {
      events("passed", "skipped", "failed")
    }
    finalizedBy(jacocoTestReport)
  }

  jacocoTestReport {
    reports {
      html.required.set(true)
      xml.required.set(true)
    }
    dependsOn(test)
  }

  withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    kotlinOptions {
      jvmTarget = JavaVersion.VERSION_11.toString()
      freeCompilerArgs = listOf(
        "-Xemit-jvm-type-annotations",  // NOTE: necessary for validation annotations to work. Such as: `val things: List<@NotBlank String>`
        "-opt-in=kotlin.RequiresOptIn",
      )
    }
  }
}
