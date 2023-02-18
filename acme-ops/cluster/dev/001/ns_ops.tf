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
    "tls.crt" = file("${path.module}/.certs/_wildcard.nip.io.pem")
    "tls.key" = file("${path.module}/.certs/_wildcard.nip.io-key.pem")
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
    file("${path.module}/helm_release.loki.values.yaml")
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
    file("${path.module}/helm_release.tempo.values.yaml")
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
    file("${path.module}/helm_release.promtail.values.yaml")
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
    file("${path.module}/helm_release.prometheus.values.yaml")
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
    file("${path.module}/helm_release.grafana.values.yaml")
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
    file("${path.module}/helm_release.opentelemetry_collector.values.yaml")
  ]
}
