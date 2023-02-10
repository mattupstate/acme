#resource "kubernetes_namespace" "ops" {
#  metadata {
#    name = "ops"
#  }
#}
#
#resource "kubernetes_secret" "ops_wildcard_ssl" {
#
#  metadata {
#    namespace = kubernetes_namespace.ops.metadata[0].name
#    name      = "tls-io-nip-wildcard"
#  }
#
#  data = {
#    "tls.crt" = file("${path.module}/.secrets/_wildcard.nip.io.pem")
#    "tls.key" = file("${path.module}/.secrets/_wildcard.nip.io-key.pem")
#  }
#
#  type = "kubernetes.io/tls"
#}
#
#resource "helm_release" "loki_stack" {
#  repository = "https://grafana.github.io/helm-charts"
#  chart      = "loki-stack"
#  version    = local.loki_stack_chart_version
#  namespace  = kubernetes_namespace.ops.metadata[0].name
#  name       = "loki"
#  atomic     = true
#
#  values = [
#    file("${path.module}/helm_release.loki_stack.values.yaml")
#  ]
#}
#
#resource "helm_release" "jaeger" {
#  repository = "https://jaegertracing.github.io/helm-charts"
#  chart      = "jaeger"
#  version    = local.jaeger_chart_version
#  namespace  = kubernetes_namespace.ops.metadata[0].name
#  name       = "jaeger"
#  atomic     = true
#
#  values = [
#    file("${path.module}/helm_release.jaeger.values.yaml")
#  ]
#}
#
#resource "kubernetes_ingress_v1" "grafana" {
#  metadata {
#    name        = "grafana"
#    namespace   = kubernetes_namespace.ops.metadata[0].name
#    annotations = {
#      "kubernetes.io/ingress.class" = "nginx"
#    }
#  }
#
#  spec {
#    tls {
#      hosts = [
#        "grafana-127-0-0-1.nip.io"
#      ]
#      secret_name = "tls-io-nip-wildcard"
#    }
#
#    rule {
#      host = "grafana-127-0-0-1.nip.io"
#      http {
#        path {
#          backend {
#            service {
#              name = "loki-grafana"
#              port {
#                number = 80
#              }
#            }
#          }
#          path = "/"
#        }
#      }
#    }
#  }
#}
#
#resource "helm_release" "otel_collector" {
#  repository = "https://open-telemetry.github.io/opentelemetry-helm-charts"
#  chart      = "opentelemetry-collector"
#  version    = local.opentelemetry_collector_chart_version
#  namespace  = kubernetes_namespace.ops.metadata[0].name
#  name       = "opentelemetry-collector"
#  atomic     = true
#  values     = [
#    file("${path.module}/helm_release.opentelemetry_collector.values.yaml")
#  ]
#  depends_on = [
#    kubernetes_namespace.ops
#  ]
#}
