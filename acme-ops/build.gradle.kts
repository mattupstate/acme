tasks {
  val devKubernetesClusterName = (project.properties["dev.kubernetes.name"] ?: "acme-dev")
  val sslCertDir = projectDir.resolve("src/tf/cluster/dev/001/.secrets")
  val sslCertificateFile = sslCertDir.resolve("_wildcard.nip.io.pem")
  val sslCertificateKeyFile = sslCertDir.resolve("_wildcard.nip.io-key.pem")

  val makeCerts = register<Exec>("makeCerts") {
    outputs.files(sslCertificateFile, sslCertificateKeyFile)
    commandLine("bash", "-c", "mkcert *.nip.io")
    workingDir(sslCertDir)
  }

  val devKubernetesClusterCreate = register<Exec>("devKubernetesClusterCreate") {
    commandLine(
      "bash", "-c", """
        k3d cluster create $devKubernetesClusterName -p "80:80@loadbalancer" -p "443:443@loadbalancer" --k3s-arg "--disable=traefik@server:0"
      """.trimIndent()
    )
    dependsOn(makeCerts)
  }

  val devSecretsFetch = register<Exec>("devSecretsFetch") {
    commandLine("./fetch-secrets.sh")
    workingDir(projectDir.resolve("src/tf/cluster/dev/002"))
  }

  val devKubernetesClusterProvision = register<Exec>("devKubernetesClusterProvision") {
    commandLine(
      "bash", "-c", """
        terraform -chdir=src/tf/cluster/dev/001 apply -auto-approve
        terraform -chdir=src/tf/cluster/dev/002 apply -auto-approve
      """.trimIndent()
    )
    dependsOn(devSecretsFetch, devKubernetesClusterCreate)
  }

  register("devStart") {
    dependsOn(
      devKubernetesClusterProvision,
    )
  }

  register<Exec>("devDelete") {
    commandLine("k3d", "cluster", "delete", devKubernetesClusterName)
  }
}
