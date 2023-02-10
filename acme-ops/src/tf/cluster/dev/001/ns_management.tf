resource "kubernetes_namespace" "management" {
  metadata {
    name = "management"
  }
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

resource "kubernetes_secret" "management_wildcard_ssl" {

  metadata {
    namespace = kubernetes_namespace.management.metadata[0].name
    name      = "tls-io-nip-wildcard"
  }

  data = {
    "tls.crt" = file("${path.module}/.secrets/_wildcard.nip.io.pem")
    "tls.key" = file("${path.module}/.secrets/_wildcard.nip.io-key.pem")
  }

  type = "kubernetes.io/tls"

  depends_on = [
    kubernetes_namespace.management
  ]
}
