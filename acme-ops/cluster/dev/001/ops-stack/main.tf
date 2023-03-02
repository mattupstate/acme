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
