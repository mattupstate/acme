import org.asciidoctor.gradle.jvm.AsciidoctorTask

plugins {
  id("org.asciidoctor.jvm.convert")
  id("org.ajoberstar.git-publish")
}

repositories {
  mavenLocal()
  mavenCentral()
}

asciidoctorj {
  modules {
    diagram.use()
    diagram.version("2.2.1")
  }
}

tasks {
  build {
    dependsOn("asciidoctor")
  }

  "asciidoctor"(AsciidoctorTask::class) {
    baseDirFollowsSourceFile()
    sourceDir(file("docs"))
  }

  gitPublish {
    repoUri.set("https://github.com/mattupstate/reference-arch-v2.git")
    branch.set("gh-pages")
    commitMessage.set("Automated commit performed by ${System.getenv("USER")}")
    contents {
      from(asciidoctor.get().outputDir)
    }
  }

  gitPublishPush {
    dependsOn(asciidoctor)
  }
}
