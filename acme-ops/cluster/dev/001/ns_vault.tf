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
    "tls.crt" = file("${path.module}/.certs/_wildcard.nip.io.pem")
    "tls.key" = file("${path.module}/.certs/_wildcard.nip.io-key.pem")
  }

  type = "kubernetes.io/tls"
}

resource "helm_release" "vault" {
  repository = "https://helm.releases.hashicorp.com"
  chart      = "vault"
  version    = local.vault_chart_version
  name       = "vault"
  namespace  = kubernetes_namespace.vault.metadata[0].name
  atomic     = true

  values = [
    file("${path.module}/helm_release.vault.values.yaml")
  ]
}

