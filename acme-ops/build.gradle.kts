import org.apache.tools.ant.taskdefs.condition.Os

tasks {
  val devKubernetesClusterName = (project.properties["dev.kubernetes.name"] ?: "acme-dev")
  // val devNginxConfFile = file((project.properties["dev.nginx.conf"] ?: "/usr/local/etc/nginx/conf.d/acme.conf"))
  // val ingressNginxUrlFile = buildDir.resolve("ingressnginx.url.txt")
  val sslCertDir = projectDir.resolve("src/k8s/overlays/dev/ssl")
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
        kubectl apply -f https://raw.githubusercontent.com/kubernetes/ingress-nginx/controller-v1.2.0/deploy/static/provider/cloud/deploy.yaml
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
