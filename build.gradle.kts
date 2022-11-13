buildscript {
  repositories {
    mavenCentral()
  }
  configurations.classpath {
    resolutionStrategy {
      // SEE: https://github.com/GoogleContainerTools/jib/issues/3058
      force("org.apache.httpcomponents:httpcore:_")
      force("org.apache.httpcomponents:httpclient:_")
      force("com.google.http-client:google-http-client:_")
      force("com.google.http-client:google-http-client-apache-v2:_")
    }
  }
}

allprojects {
  repositories {
    mavenLocal()
    mavenCentral()
  }
}

plugins {
  idea
  jacoco
}

tasks {
  register<Exec>("runDev") {
    commandLine("bash", "-c", "eval $(minikube --profile=acme-dev docker-env); skaffold run --kube-context=acme-dev")
    dependsOn("acme-ops:deployDev:_", "acme-app:acme-app-web:_")
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
}
