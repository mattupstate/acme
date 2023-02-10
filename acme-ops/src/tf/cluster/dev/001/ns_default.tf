resource "kubernetes_secret" "wildcard_ssl" {

  metadata {
    namespace = "default"
    name      = "tls-io-nip-wildcard"
  }

  data = {
    "tls.crt" = file("${path.module}/.secrets/_wildcard.nip.io.pem")
    "tls.key" = file("${path.module}/.secrets/_wildcard.nip.io-key.pem")
  }

  type = "kubernetes.io/tls"
}

