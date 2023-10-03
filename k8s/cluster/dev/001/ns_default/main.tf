variable "tls_cert_path" {
  type = string
}

variable "tls_key_path" {
  type = string
}

resource "kubernetes_secret" "wildcard_ssl" {

  metadata {
    namespace = "default"
    name      = "tls-io-nip-wildcard"
  }

  data = {
    "tls.crt" = file(var.tls_cert_path)
    "tls.key" = file(var.tls_key_path)
  }

  type = "kubernetes.io/tls"
}

