variable "tls_cert_path" {
  type = string
}

variable "tls_key_path" {
  type = string
}


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
    "tls.crt" = file(var.tls_cert_path)
    "tls.key" = file(var.tls_key_path)
  }

  type = "kubernetes.io/tls"

  depends_on = [
    kubernetes_namespace.management
  ]
}

resource "helm_release" "cert_manager" {
  repository = "https://charts.jetstack.io/"
  chart      = "cert-manager"
  version    = "1.13.1"
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
  version    = "4.8.2"
  atomic     = true

  values = [
    file("${path.module}/helm_release.ingress_nginx.values.yaml")
  ]
}
