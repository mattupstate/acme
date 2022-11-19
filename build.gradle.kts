buildscript {
  repositories {
    mavenCentral()
  }
  configurations.classpath {
    resolutionStrategy {
      // SEE: https://github.com/GoogleContainerTools/jib/issues/3058
      force("org.apache.httpcomponents:httpcore:4.4.12")
      force("org.apache.httpcomponents:httpclient:4.5.10")
      force("com.google.http-client:google-http-client:1.34.0")
      force("com.google.http-client:google-http-client-apache-v2:1.34.0")
    }
  }
}

repositories {
  mavenLocal()
  mavenCentral()
}

plugins {
  idea
  jacoco
  alias(libs.plugins.com.github.ben.manes.versions)
  alias(libs.plugins.nl.littlerobots.version.catalog.update)
}

tasks {
  register<Exec>("runDev") {
    commandLine("bash", "-c", "eval $(minikube --profile=acme-dev docker-env); skaffold run --kube-context=acme-dev")
    dependsOn("acme-ops:deployDev", "acme-app:acme-app-web:build")
  }

  register<JacocoReport>("mergeJacocoReports") {
    val javaSourceSets = subprojects.mapNotNull {
      it.extensions.findByType(JavaPluginExtension::class)?.sourceSets?.getByName("main")
    }

    sourceSets(*javaSourceSets.toTypedArray())
    executionData.setFrom(fileTree(rootDir).include("**/test.exec"))
    classDirectories.setFrom(files(javaSourceSets.map { it.output }))
    sourceDirectories.setFrom(files(javaSourceSets.map { it.allSource.srcDirs }))

    reports {
      xml.apply {
        required.set(true)
        outputLocation.set(file("$buildDir/reports/jacoco/xml/coverage.xml"))
      }
      html.apply {
        required.set(true)
        outputLocation.set(file("$buildDir/reports/jacoco/html"))
      }
    }
  }

  register<Exec>("buildAntoraSite") {
    commandLine("bash", "-c", "npm install && npm run buildDocs")
  }

  register<Exec>("publishAntoraSite") {
    commandLine("npm", "run", "publishDocs")
  }
}
