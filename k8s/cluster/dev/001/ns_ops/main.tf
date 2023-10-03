variable "tls_cert_path" {
  type = string
}

variable "tls_key_path" {
  type = string
}

variable "metrics_server_enabled" {
  type        = bool
  description = "To enable the k8s metrics server or not"
}

locals {
  loki_chart_version                    = "4.6.1"
  opentelemetry_collector_chart_version = "0.48.2"
  promtail_chart_version                = "6.8.3"
  tempo_chart_version                   = "1.0.0"
  grafana_chart_version                 = "6.50.7"
  prometheus_chart_version              = "19.6.1"
}

resource "helm_release" "metrics_server" {
  count      = var.metrics_server_enabled ? 1 : 0
  repository = "https://kubernetes-sigs.github.io/metrics-server"
  chart      = "metrics-server"
  name       = "metrics-server"
  namespace  = "management"
  atomic     = true

  values = [
    file("${path.module}/metrics-server.values.yaml")
  ]
}

resource "kubernetes_namespace" "ops" {
  metadata {
    name = "ops"
  }
}

resource "kubernetes_secret" "ops_wildcard_ssl" {

  metadata {
    namespace = kubernetes_namespace.ops.metadata[0].name
    name      = "tls-io-nip-wildcard"
  }

  data = {
    "tls.crt" = file(var.tls_cert_path)
    "tls.key" = file(var.tls_key_path)
  }

  type = "kubernetes.io/tls"
}

resource "helm_release" "loki" {
  repository = "https://grafana.github.io/helm-charts"
  chart      = "loki"
  version    = local.loki_chart_version
  namespace  = kubernetes_namespace.ops.metadata[0].name
  name       = "loki"
  atomic     = true

  values = [
    file("${path.module}/loki.values.yaml")
  ]
}

resource "helm_release" "tempo" {
  repository = "https://grafana.github.io/helm-charts"
  chart      = "tempo"
  version    = local.tempo_chart_version
  namespace  = kubernetes_namespace.ops.metadata[0].name
  name       = "tempo"
  atomic     = true

  values = [
    file("${path.module}/tempo.values.yaml")
  ]
}

resource "helm_release" "promtail" {
  repository = "https://grafana.github.io/helm-charts"
  chart      = "promtail"
  version    = local.promtail_chart_version
  namespace  = kubernetes_namespace.ops.metadata[0].name
  name       = "promtail"
  atomic     = true

  values = [
    file("${path.module}/promtail.values.yaml")
  ]
}

resource "helm_release" "prometheus" {
  repository = "https://prometheus-community.github.io/helm-charts"
  chart      = "prometheus"
  version    = local.prometheus_chart_version
  namespace  = kubernetes_namespace.ops.metadata[0].name
  name       = "prometheus"
  atomic     = true

  values = [
    file("${path.module}/prometheus.values.yaml")
  ]
}

resource "helm_release" "grafana" {
  repository = "https://grafana.github.io/helm-charts"
  chart      = "grafana"
  version    = local.grafana_chart_version
  namespace  = kubernetes_namespace.ops.metadata[0].name
  name       = "grafana"
  atomic     = true

  values = [
    file("${path.module}/grafana.values.yaml")
  ]
}

resource "helm_release" "otel_collector" {
  repository = "https://open-telemetry.github.io/opentelemetry-helm-charts"
  chart      = "opentelemetry-collector"
  version    = local.opentelemetry_collector_chart_version
  namespace  = kubernetes_namespace.ops.metadata[0].name
  name       = "opentelemetry-collector"
  atomic     = true

  values = [
    file("${path.module}/otel.values.yaml")
  ]
}

