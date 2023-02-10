tasks {
  register<Exec>("install") {
    inputs.file("package.json")
    outputs.dir("node_modules")
    commandLine(
      "/bin/bash", "-c", """
        npm install --prune
      """.trimIndent()
    )
  }

  register<Exec>("build") {
    inputs.file(".eslintrc.json")
    inputs.file("angular.json")
    inputs.file("tsconfig.json")
    inputs.dir("projects")
    outputs.dir("dist")
    commandLine(
      "/bin/bash", "-c", """
        npm run build
      """.trimIndent()
    )
    dependsOn("install")
  }

  register<Exec>("test") {
    inputs.dir("projects")
    outputs.dir("reports")
    commandLine(
      "/bin/bash", "-c", """
        npm run test-ci
      """.trimIndent()
    )
    dependsOn("install")
  }
}
