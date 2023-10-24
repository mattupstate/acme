resource "kubernetes_manifest" "oathkeeper_rules_kratos" {
  manifest = yamldecode(file("${path.module}/oathkeeper.rules.kratos.yaml"))
}

resource "kubernetes_manifest" "oathkeeper_rules_kratos_selfservice_ui" {
  manifest = yamldecode(file("${path.module}/oathkeeper.rules.kratos-selfservice-ui.yaml"))
}

resource "helm_release" "oathkeeper" {
  chart      = "oathkeeper"
  repository = "https://k8s.ory.sh/helm/charts"
  name       = "oathkeeper"
  namespace  = "default"

  values = [
    file("${path.module}/helm_release.oathkeeper.values.yaml")
  ]

  depends_on = [
    kubernetes_manifest.oathkeeper_rules_kratos,
    kubernetes_manifest.oathkeeper_rules_kratos_selfservice_ui,
  ]
}
