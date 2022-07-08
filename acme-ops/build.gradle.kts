tasks {
  val devKubernetesClusterName = (project.properties["dev.kubernetes.name"] ?: "acme-dev")
  val sslCertDir = projectDir.resolve("src/k8s/cluster/ssl")
  val sslCertificateFile = sslCertDir.resolve("_wildcard.nip.io.pem")
  val sslCertificateKeyFile = sslCertDir.resolve("_wildcard.nip.io-key.pem")

  val makeCerts = register<Exec>("makeCerts") {
    outputs.files(sslCertificateFile, sslCertificateKeyFile)
    commandLine("mkcert", "*.nip.io")
    workingDir(sslCertDir)
  }

  val devKubernetesClusterStart = register<Exec>("devKubernetesClusterStart") {
    commandLine(
      "bash", "-c", """
        k3d cluster create $devKubernetesClusterName -p "80:80@loadbalancer" -p "443:443@loadbalancer" --k3s-arg "--disable=traefik@server:0"
        kubectl kustomize --enable-helm src/k8s/cluster | kubectl apply -f -
      """.trimIndent()
    )
    dependsOn(makeCerts)
  }

  register("devStart") {
    dependsOn(
      devKubernetesClusterStart,
    )
  }

  register<Exec>("devDelete") {
    commandLine("k3d", "cluster", "delete", devKubernetesClusterName)
  }
}
