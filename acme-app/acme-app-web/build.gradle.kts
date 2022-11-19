tasks {
  register<Exec>("install") {
    inputs.file("package.json")
    outputs.dir("node_modules")
    commandLine("npm", "install", "--prune")
  }

  register<Exec>("build") {
    inputs.file(".eslintrc.json")
    inputs.file("angular.json")
    inputs.file("tsconfig.json")
    inputs.dir("projects")
    outputs.dir("dist")
    commandLine("npm", "run", "build")
    dependsOn("install")
  }

  // register<Exec>("test") {
  //   inputs.dir("projects")
  //   outputs.dir("reports")
  //   commandLine("npm", "run", "test-ci")
  //   dependsOn("install")
  // }
}
