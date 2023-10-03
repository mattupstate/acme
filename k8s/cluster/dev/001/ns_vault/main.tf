variable "tls_cert_path" {
  type = string
}

variable "tls_key_path" {
  type = string
}

resource "kubernetes_namespace" "vault" {
  metadata {
    name = "vault"
  }
}

resource "kubernetes_secret" "vault_wildcard_ssl" {

  metadata {
    namespace = kubernetes_namespace.vault.metadata[0].name
    name      = "tls-io-nip-wildcard"
  }

  data = {
    "tls.crt" = file(var.tls_cert_path)
    "tls.key" = file(var.tls_key_path)
  }

  type = "kubernetes.io/tls"

  depends_on = [
    kubernetes_namespace.vault
  ]
}

resource "kubernetes_config_map" "vault_post_start_script" {
  metadata {
    name      = "vault-post-start-script"
    namespace = kubernetes_namespace.vault.metadata[0].name
  }

  binary_data = {
    "vault-post-start.sh" = "${filebase64("${path.module}/vault-post-start.sh")}"
  }
}

resource "helm_release" "vault" {
  repository = "https://helm.releases.hashicorp.com"
  chart      = "vault"
  version    = "0.21.0"
  name       = "vault"
  namespace  = kubernetes_namespace.vault.metadata[0].name
  atomic     = true

  values = [
    file("${path.module}/helm_release.vault.values.yaml")
  ]
}
