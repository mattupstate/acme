
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
