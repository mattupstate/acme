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

  val devKubernetesClusterStart = register<Exec>("devMinikubeStart") {
    commandLine(
      "bash", "-c", """
        k3d cluster create $devKubernetesClusterName -p "8888:80@loadbalancer" -p "4433:443@loadbalancer" --k3s-arg "--disable=traefik@server:0"
        kubectl apply -f https://raw.githubusercontent.com/kubernetes/ingress-nginx/controller-v1.2.0/deploy/static/provider/cloud/deploy.yaml
      """.trimIndent()
    )
    dependsOn(makeCerts)
  }

  // val devMinikubeAddons = register<Exec>("devMinikubeAddons") {
  //   commandLine("minikube", "--profile", devKubernetesClusterName, "addons", "enable", "ingress")
  //   dependsOn(devKubernetesClusterStart)
  // }

  // val ingressNginxUrl = register<Exec>("ingressNginxUrl") {
  //   commandLine("bash", "-c", """
  //     mkdir -p $(dirname $ingressNginxUrlFile)
  //     minikube --profile $devKubernetesClusterName service --namespace ingress-nginx ingress-nginx-controller --url | \
  //       tail -n 1 | \
  //       sed -e 's/http:\/\//https:\/\//' > $ingressNginxUrlFile
  //     echo "Ingress Nginx URL written to $ingressNginxUrlFile"
  //   """.trimIndent())
  //   dependsOn(devMinikubeAddons)
  // }

  // val devExpandNginxConfig = register<Copy>("devExpandNginxConfig") {
  //   from("src/nginx/dev")
  //   into(devNginxConfFile.parent)
  //   include("acme.conf.template")
  //   rename { it.replace(".template", "") }
  //   expand(
  //     "sslCertificate" to "$sslCertificateFile",
  //     "sslCertificateKey" to "$sslCertificateKeyFile",
  //     "proxyPass" to ingressNginxUrlFile
  //   )
  //   dependsOn(ingressNginxUrl)
  //   doLast {
  //     exec {
  //       commandLine("echo", """
  //         Nginx configuration may have changed. Reload the service:
  //         [Ubuntu] $ sudo systemctl reload nginx.service
  //         [macOS]  $ sudo brew services restart nginx
  //       """.trimIndent())
  //     }
  //   }
  // }

  register("devStart") {
    dependsOn(
      devKubernetesClusterStart,
      // devMinikubeAddons,
      // devExpandNginxConfig,
    )
  }

  register<Exec>("devDelete") {
    commandLine("k3d", "cluster", "delete", devKubernetesClusterName)
    // commandLine("minikube", "--profile", devKubernetesClusterName, "delete")
  }
}
