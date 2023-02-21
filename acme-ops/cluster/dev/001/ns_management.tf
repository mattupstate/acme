resource "kubernetes_namespace" "management" {
  metadata {
    name = "management"
  }
}

resource "kubernetes_secret" "management_wildcard_ssl" {

  metadata {
    namespace = kubernetes_namespace.management.metadata[0].name
    name      = "tls-io-nip-wildcard"
  }

  data = {
    "tls.crt" = file("${path.module}/.certs/_wildcard.nip.io.pem")
    "tls.key" = file("${path.module}/.certs/_wildcard.nip.io-key.pem")
  }

  type = "kubernetes.io/tls"

  depends_on = [
    kubernetes_namespace.management
  ]
}

resource "helm_release" "cert_manager" {
  repository = "https://charts.jetstack.io/"
  chart      = "cert-manager"
  version    = local.cert_manager_version
  namespace  = kubernetes_namespace.management.metadata[0].name
  name       = "cert-manager"
  atomic     = true

  set {
    name  = "replicas"
    value = 1
  }

  set {
    name  = "installCRDs"
    value = "true"
  }

}

resource "helm_release" "ingress_nginx" {
  repository = "https://kubernetes.github.io/ingress-nginx"
  chart      = "ingress-nginx"
  name       = "ingress-nginx"
  namespace  = kubernetes_namespace.management.metadata[0].name
  version    = local.ingress_nginx_version
  atomic     = true

  values = [
    file("${path.module}/helm_release.ingress_nginx.values.yaml")
  ]
}

resource "kubernetes_config_map" "vault_post_start_script" {
  metadata {
    name      = "vault-post-start-script"
    namespace = kubernetes_namespace.management.metadata[0].name
  }

  binary_data = {
    "vault-post-start.sh" = "${filebase64("${path.module}/vault-post-start.sh")}"
  }
}

resource "helm_release" "vault" {
  repository = "https://helm.releases.hashicorp.com"
  chart      = "vault"
  version    = local.vault_chart_version
  name       = "vault"
  namespace  = kubernetes_namespace.management.metadata[0].name
  atomic     = true

  values = [
    file("${path.module}/helm_release.vault.values.yaml")
  ]
}
